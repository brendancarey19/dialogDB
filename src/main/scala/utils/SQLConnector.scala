package utils

import utils.SparkManager
import org.apache.spark.sql.{DataFrame}
import org.apache.spark.sql.types.{StructType, StructField, StringType, IntegerType}

object SQLConnector {

    val spark = SparkManager.spark

    def get_data(file: String, query: String): DataFrame = {
        val dataframe = get_dataframe(file)
        println("DF")
        dataframe.show()
        println(s"here is view name $file")
        if (!spark.catalog.tableExists(file)) {
            dataframe.createOrReplaceTempView(file)
        }

        val x = spark.sql(query)
        x.show()
        println("BRENDAN")
        println(s"$query")
        x
    }

    def get_dataframe(file: String): DataFrame = {
        val dirPath = s"./data/parquet/$file"

        spark.read.parquet(dirPath)
    }

    def get_schema(file: String): String = {
        val dataframe = get_dataframe(file)
        val schema = dataframe.schema
        schema.toString
    }

}