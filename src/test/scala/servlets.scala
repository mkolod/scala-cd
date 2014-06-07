package scalacd

import org.scalatest._

class ServletTests extends FunSuite {

  def get(url: String): String = {

    import java.io._
    import java.net._
    import scala.io.Source.fromInputStream

    val conn = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("GET")
    fromInputStream(conn.getInputStream).getLines().mkString("\n")
  }

  test("hello, world") {
    val res = get("http://localhost:8080/")
    assert(res === """<html><body><h1>Hello, world!</h1></body></html>""")
  }

}
