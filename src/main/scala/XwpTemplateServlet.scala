package com.earldouglas.xwptemplate

import scala.xml.NodeSeq
import javax.servlet.http.HttpServlet

class XwpTemplateServlet extends HttpServlet {

  import javax.servlet.http.HttpServletRequest
  import javax.servlet.http.HttpServletResponse

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {

    response.setContentType("text/html")
    response.setCharacterEncoding("UTF-8")

    val responseBody: NodeSeq =
      <html>
        <head>
          <title>continuous deployment for scala</title>
        </head>
        <body>
          <h1>continuous deployment for scala</h1>
          <p>
            This is a sample application deployed to Heroku via the steps
            outlined in <a href="https://github.com/earldouglas/scala-cd#continuous-deployment-for-scala">Continuous deployment for Scala</a>.
          </p>
        </body>
      </html>
    response.getWriter.write(responseBody.toString)
  }
}
