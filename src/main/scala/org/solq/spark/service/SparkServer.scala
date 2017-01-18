package org.solq.spark.service

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.solq.spark.model.ISparkServer
import org.solq.spark.model.QuerySpark
import org.solq.spark.model.SourceType

/***
 * spark 查询简单实现 
 * @author solq
 * */
object SparkServer {
  val sparkServer = new SparkServer;
}

private[service] class SparkServer extends ISparkServer {
  val sqlit = ",";
  val `"` = "\"";
  val dbsc = SparkSession.builder().master("local").appName("Word Count")
    .config("spark.sql.hive.metastore.sharedPrefixes", "com.mysql.jdbc")
    .config("spark.sql.shuffle.partitions", 5)
    //.config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .getOrCreate();
  val sc = SparkSession.builder()
    .master("local")
    .appName("csv session example")
    .getOrCreate();
  def query(querySpark: QuerySpark): Unit = {

    querySpark.st match {
      case SourceType.MYSQL => {
        val sqlContext = dbsc.sqlContext;
        val df = sqlContext.read.format("jdbc").options(querySpark.options).load();
        sqlContext.dropTempTable(querySpark.tmpTable)
        df.createTempView(querySpark.tmpTable);

        val ret: Dataset[Row] = sqlContext.sql(querySpark.sql)
        writeFile(ret, querySpark.savePath)

      }
      case SourceType.CSV => {
        val sqlContext = sc.sqlContext;
        val df = sqlContext.read
          .format("com.databricks.spark.csv")
          .options(querySpark.options)
          .load(querySpark.url: _*)
        sqlContext.dropTempTable(querySpark.tmpTable)
        df.createTempView(querySpark.tmpTable);
        val ret: Dataset[Row] = sqlContext.sql(querySpark.sql)
        writeFile(ret, querySpark.savePath)
      }
      case SourceType.JSON => {
        val sqlContext = sc.sqlContext;
        val df = sqlContext.read.json(querySpark.url: _*);
        sqlContext.dropTempTable(querySpark.tmpTable)
        df.createTempView(querySpark.tmpTable);
        val ret: Dataset[Row] = sqlContext.sql(querySpark.sql)
        writeFile(ret, querySpark.savePath)

      }
    }

  }

  def writeFile(ret: Dataset[Row], savePath: String) = {
    var f: File = new File(savePath);
    if (f.canExecute()) {
      if (f.getParentFile.canExecute()) {
        f.getParentFile.mkdirs();
      }
    }
    val head = new StringBuilder

    for (field <- ret.schema.fields) {
      head.append(field.name)
      head.append(sqlit)
    }
    val builder = new StringBuilder

    val writer = new PrintWriter(new FileWriter(f, false));
    writer.println(head)

    try {
      val takeResult = ret.take(ret.count().intValue() + 1)
      takeResult.foreach { r =>
        val line = new StringBuilder
        val len = r.length - 1;
        for (i <- 0 to len) {
          val str = if (null == r.get(i)) "" else r.get(i)
          line.append(`"`)
          line.append(str)
          line.append(`"`)
          if (i != len) {
            line.append(sqlit)
          }
        }
        writer.println(line)
      }
    } catch {
      case t: Throwable => t.printStackTrace()
    }

    writer.flush()
    writer.close()
  }
}