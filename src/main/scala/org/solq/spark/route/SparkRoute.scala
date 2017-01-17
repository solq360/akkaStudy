package org.solq.spark.route

import org.solq.spark.model.QuerySpark
import org.solq.spark.util.JsonParse

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._segmentStringToPathMatcher
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Directives.entity
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Directives.post
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller
import javax.inject.Inject
import org.solq.spark.service.SparkServer

class SparkRoute {
  implicit val um: Unmarshaller[HttpEntity, QuerySpark] =
    Unmarshaller.byteStringUnmarshaller.mapWithCharset { (data, charset) =>
      println(new String(data.toArray))
      JsonParse.objManager.readValue(data.toArray, classOf[QuerySpark])
    }
  val route1: Route = (post & path("query") & entity(as[QuerySpark])) {
    querySpark =>
      SparkServer.sparkServer.query(querySpark);
      complete(querySpark.toString())
  }
  val route2: Route = (post & pathPrefix("query" / LongNumber) & entity(as[QuerySpark])) {
    (id, querySpark) =>
      SparkServer.sparkServer.query(querySpark);
      complete(querySpark.toString())
  }
}