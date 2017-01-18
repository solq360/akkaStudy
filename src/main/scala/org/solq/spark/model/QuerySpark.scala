package org.solq.spark.model

import java.util.HashMap
import java.util.Map

/**
 * <p>var 变量默认生成getter和setter方法 </p>
 * <p>val默认只生成getter方法</p>
 * @author solq
 */
final case class QuerySpark(
  /** dbsource type */
  val st: SourceType,
  /** url */
  val url: List[String],

  /** sql dsl */
  val sql: String,

  /** connect options */
  val options: Map[String, String] = new HashMap[String, String],

  /** option tmp table */
  val tmpTable: String,

  /** savePath */
  val savePath: String) 