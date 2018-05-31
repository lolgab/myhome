package myhome

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import upickleModelImplicits._

object Router extends autowire.Server[String, upickle.default.Reader, upickle.default.Writer] {
  def read[Result: upickle.default.Reader](p: String)  = upickle.default.read[Result](p)
  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}

object Server {
  implicit val system           = ActorSystem("my-system")
  implicit val materializer     = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val serverSocketActor = system.actorOf(Props[ServerSocketActor])

  def main(args: Array[String]): Unit = {
    val routes = pathSingleSlash(getFromResource("index.html")) ~ getFromResourceDirectory("") ~
      post {
        path("ajax" / Segments) { s =>
          entity(as[String]) { e =>
            complete {
              println(s"ajax request: ${s.mkString("/")}/" +
                s"$e")
              Router.route[Api](ApiImpl)(
                autowire.Core.Request(
                  s,
                  upickle.default.read[Map[String, String]](e)
                )
              )
            }
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(routes, "0.0.0.0", 8080)

    println(s"Server online at http://0.0.0.0:8080/")
  }
}
