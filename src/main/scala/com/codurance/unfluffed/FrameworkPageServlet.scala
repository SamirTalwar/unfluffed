package com.codurance.unfluffed

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import com.codurance.unfluffed.FrameworkPageServlet.DOCTYPE
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

class FrameworkPageServlet(applicationConfiguration: Config) extends HttpServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    if (request.getPathTranslated != null) {
      super.doGet(request, response)
      return
    }

    val processes = applicationConfiguration.as[List[String]]("processes")
    for (writer <- IO.managed(response.getWriter)) {
      writer.println(DOCTYPE)
      writer.print(html(processes).toString())
    }
  }

  private def html(processes: Seq[String]) = {
    <html>
      <head>
        <meta charset="utf-8"/>
        <script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>

        <script type="text/javascript" src="/framework/faye/faye-browser-min.js"></script>
        <script type="text/javascript" src="/framework/unfluffed.js"></script>

        {processes.map { process =>
          <script type="text/javascript" src={"/application/processes/" + process + ".js"}></script>
        }}
      </head>

      <body>
        <div id="body"></div>
      </body>
    </html>
  }
}

object FrameworkPageServlet {
  val DOCTYPE = "<!DOCTYPE html>"
}
