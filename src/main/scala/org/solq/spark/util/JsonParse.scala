package org.solq.spark.util

import scala.reflect.runtime.universe.TypeTag
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.unmarshalling.Unmarshaller
import scala.reflect.runtime.universe.typeOf
object JsonParse {
  val objManager = new ObjectMapper;
  objManager.registerModule(DefaultScalaModule)

  def reader[T: TypeTag] =
    Unmarshaller.byteStringUnmarshaller.mapWithCharset { (data, charset) =>
      objManager.readValue(data.toArray, typeOf[T].getClass)
    }
}
 