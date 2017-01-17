package org.solq.spark.route
import akka.http.scaladsl.server.Directives._
import org.solq.spark.util.PathHelper
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.HttpEntity
import scala.io.Source
import akka.http.scaladsl.model.ContentTypes
class IndexRoute {
  
   val route1: Route = (get & (path("index") | path(""))) {
      val path = PathHelper.getPath("web/html/index.html");
      val indexHtml = Source.fromFile(path, "UTF-8").getLines().mkString("\n")
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, indexHtml))
  }
}