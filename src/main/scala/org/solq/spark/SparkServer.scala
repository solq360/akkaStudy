package org.solq.spark

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.solq.spark.model.ISparkServer
import org.solq.spark.model.QuerySpark
import org.solq.spark.model.SourceType

class SparkServer extends ISparkServer {
  var sc = SparkSession.builder().master("local").appName("Word Count")
    .config("spark.sql.hive.metastore.sharedPrefixes", "com.mysql.jdbc")
    .config("spark.sql.shuffle.partitions", 5)
    .getOrCreate();

  def query(querySpark: QuerySpark): Unit = {

    querySpark.st match {
      case SourceType.MYSQL => {
        val sqlContext = sc.sqlContext;
        val df = sqlContext.read.format("jdbc").options(querySpark.options).load();
        df.createTempView(querySpark.sql);

        val ret: Dataset[Row] = sqlContext.sql(querySpark.sql)

        ret.show();
      }
      case SourceType.CSV => {
          
      }
    }


  }
}