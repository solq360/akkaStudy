package org.solq.spark.route

import org.solq.spark.model.QuerySpark
import org.solq.spark.util.JsonParse

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._segmentStringToPathMatcher
import akka.http.scaladsl.server.Directives.as
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Directives.entity
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Directives.post
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller

class SparkRoute {
  implicit val um: Unmarshaller[HttpEntity, QuerySpark] =
    Unmarshaller.byteStringUnmarshaller.mapWithCharset { (data, charset) =>
      JsonParse.objManager.readValue(data.toArray, classOf[QuerySpark])
    }
  val route1: Route = (post & path("query")) {
    entity(as[QuerySpark]) {
      querySpark => complete(querySpark.toString())
    }
  }
}