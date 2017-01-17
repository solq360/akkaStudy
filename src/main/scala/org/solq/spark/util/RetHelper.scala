package org.solq.spark.util

object RefHepler {
  import scala.reflect.runtime._
  import scala.reflect.runtime.universe._
  val runtimeMirror: RuntimeMirror = universe.runtimeMirror(getClass.getClassLoader)
  def getFieldValue(im: InstanceMirror, term: MethodSymbol): Any = {
    val rawValue = im.reflectMethod(term)()
    //    val tpe = term.returnType
    //    val symbol = tpe.typeSymbol.asClass
    //    if (symbol.isDerivedValueClass) {
    //      val ctor = tpe.declaration(nme.CONSTRUCTOR).asMethod
    //      val ctorMirror = mirror.reflectClass(symbol).reflectConstructor(ctor)
    //      ctorMirror(rawValue)
    //    } else rawValue
  }

  def getConstructor[T: TypeTag] {
    val ot = typeOf[T]

    val methodSymbol: MethodSymbol = ot.decl(universe.termNames.CONSTRUCTOR).asMethod
    val constructorMethod = runtimeMirror.reflectClass(ot.typeSymbol.asClass).reflectConstructor(methodSymbol)
  }
}