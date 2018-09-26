import com.pharbers.spark.phSparkDriver
import com.pharbers.xlsxToCsvHeader
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DoubleType, IntegerType}
import org.scalatest.FunSuite

object test extends App {
    //        startTest().doTest()
//    lazy val sparkDriver: phSparkDriver = phSparkDriver()
//    import sparkDriver.ss.implicits._
//    val cpa = phSparkDriver().readCsv("hdfs:///data/nhwa/pha_config_repository1804/Nhwa_201804_CPA_.csv")
//            .na.fill(value = "0", cols = Array("VALUE", "STANDARD_UNIT"))
//            .withColumn("PRODUCT_NAME", when(col("PRODUCT_NAME").isNull, col("MOLE_NAME"))
//                    .otherwise(col("PRODUCT_NAME")))
//            .withColumn("MONTH", 'MONTH.cast(IntegerType))
//            .withColumn("MONTH", when(col("MONTH").>=(10), col("MONTH"))
//                    .otherwise(concat(col("MONTH").*(0).cast("int"), col("MONTH"))))
//            .withColumn("PRODUCT_NAME", trim(col("PRODUCT_NAME")))
//            .withColumn("DOSAGE", trim(col("DOSAGE")))
//            .withColumn("PACK_DES", trim(col("PACK_DES")))
//            .withColumn("PACK_NUMBER", trim(col("PACK_NUMBER")))
//            .withColumn("CORP_NAME", trim(col("CORP_NAME")))
//            .withColumn("min1", concat(col("PRODUCT_NAME"), col("DOSAGE"), col("PACK_DES"), col("PACK_NUMBER"), col("CORP_NAME")))
//            .withColumn("ym", concat(col("YEAR"), col("MONTH")))
//    cpa.show(false)
    
//    val cpaSum = cpa.count()
//    println("cpaSum: " + cpaSum)
//
//    val groupedSum = cpa.select("ym", "HOSP_ID")
//            .groupBy("ym")
//            .count()
////    groupedSum.show(false)
//    val result1 = groupedSum.collect()
//    result1.foreach(println)


//    val ~= = (d1: String, d2: String) => {
//        (d1.toDouble - d2.toDouble).abs < 1.0E-3
//    }
//
//    val addBooleanCol = udf(~=)
//
//    cpa.withColumn("HOSP_ID", 'HOSP_ID.cast(DoubleType))
//            .withColumn("ym", 'ym.cast(DoubleType))
//            .withColumn("aaa", addBooleanCol(col("HOSP_ID"), col("ym"))).show(false)
//
//    val result = groupedSum.collect().head.getLong(0)
//    println(result)
//    println("groupedSum: " + groupedSum)
    val maxDF = phSparkDriver().readCsv("hdfs:///workData/Max/HTN_Factorized_Units&Sales_WITH_OT1804.csv", delimiter = 31.toChar.toString)
    maxDF.show(false)
}
