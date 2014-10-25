package com.codurance.unfluffed

import java.nio.file.Paths

import com.typesafe.config.ConfigFactory
import org.cometd.server.CometDServlet
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.util.resource.Resource.newClassPathResource

object App {
  val STATIC_RESOURCE_PATH = "com/codurance/unfluffed/static"

  def main(args: Array[String]) {
    val applicationDirectory = Paths.get(args.head)
    val config = ConfigFactory.parseFile(applicationDirectory.resolve("application.conf").toFile)
      .withFallback(ConfigFactory.load())

    val server = new Server(config.getInt("unfluffed.port"))

    val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
    context.setContextPath("/")
    server.setHandler(context)

    val rootServlet = new FrameworkPageServlet(newClassPathResource(STATIC_RESOURCE_PATH + "/index.html"), config.getConfig("application"))
    context.addServlet(new ServletHolder(rootServlet), "/")

    val staticResourceServlet = new StaticResourceServlet(newClassPathResource(STATIC_RESOURCE_PATH).getURI)
    context.addServlet(new ServletHolder(staticResourceServlet), "/framework/*")

    val applicationServlet = new StaticResourceServlet(applicationDirectory.resolve("processes").toUri)
    context.addServlet(new ServletHolder(applicationServlet), "/application/*")

    val bayeuxServlet = new CometDServlet
    context.addServlet(new ServletHolder(bayeuxServlet), "/bayeux")

    server.start()

    val bayeux = bayeuxServlet.getBayeux
    Services.initialize(bayeux)
  }
}
