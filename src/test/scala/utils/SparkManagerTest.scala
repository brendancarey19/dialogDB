package utils

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SparkManagerTest extends AnyFlatSpec with Matchers {
  
  "SparkManager" should "initialize SparkSession correctly" in {
    SparkManager.spark should not be null
    SparkManager.spark.sparkContext.isStopped shouldBe false
  }

  it should "configure SparkConf correctly" in {
    SparkManager.sparkConf.get("spark.app.name") shouldBe "DialogDB"
    SparkManager.sparkConf.get("spark.master") shouldBe "local[*]"
  }

  it should "stop SparkSession correctly" in {
    SparkManager.spark.stop()
    SparkManager.spark.sparkContext.isStopped shouldBe true
  }
}