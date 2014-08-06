package com.earldouglas.scalacd

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AddServlet extends HttpServlet {

  override def doGet(req: HttpServletRequest, res: HttpServletResponse) {
    Option(req.getQueryString) foreach { q =>
      val nums = q.split(",") map { _.toInt }
      res.getWriter.write(nums.sum.toString)
    }
  }

}
