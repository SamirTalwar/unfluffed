package com.codurance

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import org.eclipse.jetty.util.resource.Resource

class SingleResourceServlet(resource: Resource) extends HttpServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    if (request.getPathTranslated != null) {
      super.doGet(request, response)
      return
    }
    IO.copyAndClose(resource.getInputStream, response.getOutputStream)
  }
}
