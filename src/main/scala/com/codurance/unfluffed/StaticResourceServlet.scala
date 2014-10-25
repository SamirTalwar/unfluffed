package com.codurance.unfluffed

import java.io.FileNotFoundException
import java.net.URI
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

class StaticResourceServlet(root: URI) extends HttpServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val relativePath = Option(request.getPathInfo).map(_.replaceFirst("^/", ""))
    val resource = relativePath.map(root.resolve)
    if (resource.isEmpty) {
      super.doGet(request, response)
      return
    }

    try {
      IO.copyAndClose(resource.get.toURL.openStream(), response.getOutputStream)
    } catch {
      case e: FileNotFoundException => super.doGet(request, response)
    }
  }
}
