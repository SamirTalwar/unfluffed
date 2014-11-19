package com.codurance.unfluffed

import scala.reflect.{ClassTag, classTag}

import java.net.InetAddress
import java.nio.file.Paths
import javax.servlet.Servlet

import com.typesafe.config.ConfigFactory
import io.undertow.servlet.Servlets
import io.undertow.servlet.api.{InstanceFactory, InstanceHandle}
import io.undertow.websockets.jsr.WebSocketDeploymentInfo
import io.undertow.{Handlers, Undertow}
import org.cometd.server.CometDServlet

object App {
  def main(args: Array[String]) {
    val applicationDirectory = Paths.get(args.head)
    val config =
      ConfigFactory.defaultOverrides()
        .withFallback(ConfigFactory.parseFile(applicationDirectory.resolve("application.conf").toFile))
        .withFallback(ConfigFactory.load())
    val applicationConfiguration = ApplicationConfiguration.from(config.getConfig("application"))

    val bayeuxServlet = new CometDServlet

    val servlets = Servlets.deployment()
      .setDeploymentName("Unfluffed")
      .setClassLoader(getClass.getClassLoader)
      .addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, new WebSocketDeploymentInfo)
      .setContextPath("/")
      .addServlet(servlet("FrameworkPage", () => new FrameworkPageServlet(applicationConfiguration))
        .addMapping("/"))
      .addServlet(servlet("Bayeux", () => bayeuxServlet)
        .addMapping("/bayeux")
        .addInitParam("ws.cometdURLMapping", "/bayeux/*")
        .setLoadOnStartup(0))

    val deploymentManager = Servlets.defaultContainer().addDeployment(servlets)
    deploymentManager.deploy()

    val handler = Handlers.path(deploymentManager.start())
      .addPrefixPath("/framework", StaticResources.frameworkResources)
      .addPrefixPath("/application", StaticResources.applicationResources(applicationDirectory))

    val host = InetAddress.getLocalHost.getHostName
    val port = config.getInt("unfluffed.port")
    val undertow = Undertow.builder()
      .addHttpListener(port, host)
      .addHttpListener(port, "localhost")
      .setHandler(handler)
      .build()

    undertow.start()
    println(s"Server started on http://$host:$port/.")
  }

  def servlet[S <: Servlet: ClassTag](name: String, servletConstructor: () => S)
    = Servlets.servlet(
          name,
          classTag[S].runtimeClass.asSubclass(classOf[Servlet]),
          factoryFor(servletConstructor))
        .setAsyncSupported(true)

  def factoryFor[S <: Servlet](servletFactory: () => S): InstanceFactory[S] =
    new InstanceFactory[S] {
      override def createInstance(): InstanceHandle[S] = {
        val servlet = servletFactory()
        new InstanceHandle[S] {
          override def getInstance(): S = servlet

          override def release() { }
        }
      }
    }
}
