package com.codurance.unfluffed

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import com.codurance.unfluffed.FrameworkPageServlet._

class FrameworkPageServlet(configuration: ApplicationConfiguration) extends HttpServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    if (request.getPathTranslated != null) {
      super.doGet(request, response)
      return
    }

    for (writer <- IO.managed(response.getWriter)) {
      writer.println(DocType)
      writer.print(HtmlPrettyPrinter.format(html()))
    }
  }

  private def html() = {
    <html>
      <head>
        <meta charset="utf-8"/>

        <title>{configuration.title}</title>

        {configuration.assets.css.map { asset =>
        <link rel="stylesheet" type="text/css" href={asset.uri.toString()}/>
        }}

        <script type="text/javascript" src="/framework/faye-browser-js/js/faye-browser-min.js"></script>
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
  val DocType = "<!DOCTYPE html>"

  val HtmlPrettyPrinter = new xml.PrettyPrinter(120, 2)
}
