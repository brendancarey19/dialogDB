package converter

import java.nio.file.Path
import org.apache.spark.sql.{SparkSession, SaveMode}
import cats.effect.IO
import utils.SparkManager

object ParquetConverter {
    
    val spark = SparkManager.spark

    def toParquet(csvPath: Path, parquetPath: Path): IO[Unit] = IO {
        val df = spark.read.option("header", "true").option("delimiter", ";").csv(csvPath.toString)
        df.write.mode(SaveMode.Overwrite).parquet(parquetPath.toString)
    }
}