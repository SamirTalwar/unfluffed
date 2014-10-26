package com.codurance.unfluffed

import scala.reflect.{ClassTag, classTag}

import java.nio.file.Paths
import javax.servlet.Servlet

import com.typesafe.config.ConfigFactory
import io.undertow.servlet.Servlets
import io.undertow.servlet.api.{InstanceFactory, InstanceHandle}
import io.undertow.{Handlers, Undertow}
import org.cometd.server.CometDServlet
import org.eclipse.jetty.util.resource.Resource.newClassPathResource

object App {
  val STATIC_RESOURCE_PATH = "com/codurance/unfluffed/static"

  def main(args: Array[String]) {
    val applicationDirectory = Paths.get(args.head)
    val config = ConfigFactory.parseFile(applicationDirectory.resolve("application.conf").toFile)
      .withFallback(ConfigFactory.load())

    val bayeuxServlet = new CometDServlet

    val servlets = Servlets.deployment()
      .setDeploymentName("Unfluffed")
      .setClassLoader(getClass.getClassLoader)
      .setContextPath("/")
      .addServlet(servlet("FrameworkPage", () => new FrameworkPageServlet(newClassPathResource(STATIC_RESOURCE_PATH + "/index.html"), config.getConfig("application")))
        .addMapping("/"))
      .addServlet(servlet("FrameworkResources", () => new StaticResourceServlet(newClassPathResource(STATIC_RESOURCE_PATH).getURI))
        .addMapping("/framework/*"))
      .addServlet(servlet("Processes", () => new StaticResourceServlet(applicationDirectory.resolve("processes").toUri))
        .addMapping("/application/*"))
      .addServlet(servlet("Bayeux", () => bayeuxServlet)
        .addMapping("/bayeux")
        .setLoadOnStartup(0))

    val deploymentManager = Servlets.defaultContainer().addDeployment(servlets)
    deploymentManager.deploy()

    val path = Handlers.path(deploymentManager.start())

    Undertow.builder()
      .addHttpListener(config.getInt("unfluffed.port"), "localhost")
      .setHandler(path)
      .build()
      .start()

    val bayeux = bayeuxServlet.getBayeux
    Services.initialize(bayeux)
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
