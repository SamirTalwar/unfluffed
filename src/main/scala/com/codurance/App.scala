package com.codurance

import org.cometd.server.CometDServlet
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.util.resource.Resource

object App {
  def main(args: Array[String]) {
    val server = new Server(8080)

    val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
    context.setContextPath("/")
    server.setHandler(context)

    val staticContext = new ResourceHandler
    staticContext.setBaseResource(Resource.newClassPathResource("static"))
    staticContext.setWelcomeFiles(Array("index.html"))
    context.setHandler(staticContext)

    val servlet = new CometDServlet
    context.addServlet(new ServletHolder(servlet), "/bayeux")

    server.start()

    val bayeux = servlet.getBayeux
    new HelloService(bayeux)
  }
}
