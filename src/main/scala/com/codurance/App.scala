package com.codurance

import com.typesafe.config.ConfigFactory
import org.cometd.server.CometDServlet
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.util.resource.Resource.newClassPathResource

object App {
  def main(args: Array[String]) {
    val config = ConfigFactory.load()

    val server = new Server(config.getInt("unfluffed.port"))

    val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
    context.setContextPath("/")
    server.setHandler(context)

    val rootServlet = new SingleResourceServlet(newClassPathResource("static/index.html"))
    context.addServlet(new ServletHolder(rootServlet), "/")

    val staticResourceServlet = new StaticResourceServlet(newClassPathResource("static"))
    context.addServlet(new ServletHolder(staticResourceServlet), "/_framework/*")

    val bayeuxServlet = new CometDServlet
    context.addServlet(new ServletHolder(bayeuxServlet), "/bayeux")

    server.start()

    val bayeux = bayeuxServlet.getBayeux
    Services.initialize(bayeux)
  }
}
