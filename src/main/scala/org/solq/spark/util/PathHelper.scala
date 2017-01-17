package org.solq.spark.util

object PathHelper {
  //获取class path
  val classLoader: ClassLoader = PathHelper.getClass.getClassLoader()

  def getPath(name: String): String = {
    val resource = classLoader.getResource(name)
    val path = resource.getPath();
    path
  }
}