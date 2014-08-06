package scalacd

import org.scalatest._

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.webapp.WebAppContext

class TestServer(war: String, port: Int, context: String) {

  val server = new Server()

  val conn = new ServerConnector(server)
  conn.setHost("localhost")
  conn.setPort(port)
  server.addConnector(conn)
 
  val webapp = new WebAppContext()
  webapp.setContextPath(context)
  webapp.setWar(war)
  server.setHandler(webapp)

  server.start()

  def stop() {
    server.stop()
  }

}

class ServletTests extends FunSuite
                   with BeforeAndAfterAll
                   with DefaultPageTests
                   with AddServletTests {

  val testServer = new TestServer("src/main/webapp", 8080, "/")

  override def afterAll() {
    testServer.stop()
  }

  def get(url: String): String = {

    import java.io._
    import java.net._
    import scala.io.Source.fromInputStream

    val host = "http://localhost:8080"
    val conn = (new URL(host + url)).openConnection.asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("GET")
    fromInputStream(conn.getInputStream).getLines().mkString("\n")
  }

}

trait AddServletTests { self: ServletTests =>

  test("add a bunch of numbers") {
    val nums = List(1,2,3,4,5,6,6,7,8)
    val sum = get("/add?" + nums.mkString(","))
    assert(sum === "42")
  }

  test("don't add no numbers") {
    val sum = get("/add")
    assert(sum === "")
  }

}

trait DefaultPageTests { self: ServletTests =>

  test("see header") {
    val res = get("/")
    assert(res.contains("""<h1>scala-cd</h1>"""))
  }

  test("see link to /add service") {
    val res = get("/")
    val xml = scala.xml.XML.loadString(res)
    val hrefs =
      for {
        a    <- xml \\ "a"
        href <- a attribute "href"
      } yield href.text
    assert(hrefs contains "/add?1,2,3,4,5,6,6,7,8")
  }

}
