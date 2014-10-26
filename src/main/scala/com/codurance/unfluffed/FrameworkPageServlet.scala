package com.codurance.unfluffed

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import com.codurance.unfluffed.FrameworkPageServlet.DOCTYPE

class FrameworkPageServlet(configuration: ApplicationConfiguration) extends HttpServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    if (request.getPathTranslated != null) {
      super.doGet(request, response)
      return
    }

    for (writer <- IO.managed(response.getWriter)) {
      writer.println(DOCTYPE)
      writer.print(html().toString())
    }
  }

  private def html() = {
    <html>
      <head>
        <meta charset="utf-8"/>

        {configuration.assets.css.map { asset =>
        <link rel="stylesheet" type="text/css" href={asset.uri.toString()}/>
        }}

        <script type="text/javascript" src="/framework/faye/faye-browser-min.js"></script>
        <script type="text/javascript" src="/framework/unfluffed.js"></script>

        {configuration.processes.map { process =>
        <script type="text/javascript" src={process.uri.toString()}></script>
        }}

        {configuration.assets.js.map { asset =>
        <script type="text/javascript" src={asset.uri.toString()}></script>
        }}
      </head>

      <body>
      </body>
    </html>
  }
}

object FrameworkPageServlet {
  val DOCTYPE = "<!DOCTYPE html>"
}
