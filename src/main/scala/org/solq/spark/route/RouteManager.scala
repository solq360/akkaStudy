package org.solq.spark.route

import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.MethodSymbol
import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.typeOf

import org.solq.spark.util.RefHepler

import akka.http.scaladsl.server.Directives._enhanceRouteWithConcatenation
import akka.http.scaladsl.server.Route

object RouteManager {
  val data: ArrayBuffer[Any] = new ArrayBuffer[Any];
  var route: Route = null;
  def register[T: TypeTag] = {

    val ot = typeOf[T]
    val rClass = RefHepler.runtimeMirror.runtimeClass(ot);
    val tmpObj = rClass.newInstance();
    if (data.contains(tmpObj)) {
      None
    }
    data.append(tmpObj)
    ot.members.collect {
      case m: MethodSymbol if m.isMethod & m.returnType == typeOf[Route] => {
        val method = rClass.getDeclaredMethod(m.asMethod.name.decoded);
        val ret = method.invoke(tmpObj);
        val addRoute: Route = ret.asInstanceOf[Route];
        route = if (route == null) addRoute else route ~ addRoute;
        m
      }
    }.toList
  }
  
  
  RouteManager.register[SparkRoute]
  RouteManager.register[IndexRoute]
}