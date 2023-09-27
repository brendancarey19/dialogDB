package utils

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkManager {
  val sparkConf: SparkConf = new SparkConf()
    .setAppName("DialogDB")
    .setMaster("local[*]") // Use all available cores on your machine

  val spark: SparkSession = SparkSession.builder()
    .config(sparkConf)
    .getOrCreate()

  sys.addShutdownHook {
    spark.stop()
  }
}