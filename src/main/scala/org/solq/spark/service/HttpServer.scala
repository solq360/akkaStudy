package org.solq.spark.service

import scala.io.StdIn

import org.solq.spark.route.RouteManager

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

class HttpServer {

  def start() {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route: Route = RouteManager.route;
    val port = 9988;
    val bindingFuture = Http().bindAndHandle(route, "localhost", port)

    println(s"Server online at http://localhost:${port}/\nPress RETURN to stop...")
    while (true) {
      val command = StdIn.readLine()
      command match {
        case "stop" => {
          bindingFuture.flatMap(_.unbind()) // trigger unbinding from the port
            .onComplete(_ => system.terminate()) // and shutdown when done
        }
      }
    }
  }
}