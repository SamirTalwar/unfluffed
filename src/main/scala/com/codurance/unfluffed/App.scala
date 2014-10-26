package com.codurance.unfluffed

import scala.reflect.{ClassTag, classTag}

import java.nio.file.Paths
import javax.servlet.Servlet

import com.typesafe.config.ConfigFactory
import io.undertow.server.handlers.resource.{ClassPathResourceManager, FileResourceManager}
import io.undertow.servlet.Servlets
import io.undertow.servlet.api.{InstanceFactory, InstanceHandle}
import io.undertow.{Handlers, Undertow}
import org.cometd.server.CometDServlet

object App {
  val STATIC_RESOURCE_PATH = "com/codurance/unfluffed/static"

  val DIRECT_FILE_TRANSFER_LIMIT_IN_BYTES = 1024

  def main(args: Array[String]) {
    val applicationDirectory = Paths.get(args.head)
    val config = ConfigFactory.parseFile(applicationDirectory.resolve("application.conf").toFile)
      .withFallback(ConfigFactory.load())

    val bayeuxServlet = new CometDServlet

    val servlets = Servlets.deployment()
      .setDeploymentName("Unfluffed")
      .setClassLoader(getClass.getClassLoader)
      .setContextPath("/")
      .addServlet(servlet("FrameworkPage", () => new FrameworkPageServlet(config.getConfig("application")))
        .addMapping("/"))
      .addServlet(servlet("Bayeux", () => bayeuxServlet)
        .addMapping("/bayeux")
        .setLoadOnStartup(0))

    val deploymentManager = Servlets.defaultContainer().addDeployment(servlets)
    deploymentManager.deploy()

    val handler = Handlers.path(deploymentManager.start())
      .addPrefixPath("/framework", Handlers.resource(new ClassPathResourceManager(
        getClass.getClassLoader,
        STATIC_RESOURCE_PATH)))
      .addPrefixPath("/application", Handlers.resource(new FileResourceManager(
        applicationDirectory.resolve("processes").toFile,
        DIRECT_FILE_TRANSFER_LIMIT_IN_BYTES)))

    Undertow.builder()
      .addHttpListener(config.getInt("unfluffed.port"), "localhost")
      .setHandler(handler)
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
