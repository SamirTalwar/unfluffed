package com.codurance

import javax.servlet.http.{HttpServletResponse, HttpServlet, HttpServletRequest}

import org.eclipse.jetty.util.resource.Resource

class StaticResourceServlet(root: Resource) extends HttpServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val resource = Option(request.getPathInfo).map(root.addPath)
    if (resource.isEmpty || !resource.get.exists()) {
      super.doGet(request, response)
      return
    }
    IO.copyAndClose(resource.get.getInputStream, response.getOutputStream)
  }
}
