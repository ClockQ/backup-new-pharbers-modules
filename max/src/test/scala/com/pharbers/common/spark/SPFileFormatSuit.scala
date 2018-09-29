//package com.pharbers.common.spark
//
//import com.pharbers.common.algorithm.max_data_sync_mongo_obj
//import com.pharbers.pactions.actionbase.NULLArgs
//import com.pharbers.pactions.generalactions.xlsxReadingAction
//import com.pharbers.panel.astellas.format.phAstellasCpaFormat
//import com.pharbers.spark.phSparkDriver
//import org.apache.spark.rdd.RDD
//import org.scalatest.FunSuite
//import com.mongodb.spark.sql._
//
//class SPFileFormatSuit extends FunSuite {
//    test("Spark File Convert") {
//        val cpa = xlsxReadingAction[phAstellasCpaFormat]("resource/8ee0ca24796f9b7f284d931650edbd4b/Client/171215恩华2017年10月检索.xlsx", "cpa").perform(NULLArgs)
//        val cpaRDD = cpa.get.asInstanceOf[RDD]
//        val pd = phSparkDriver().ss
//        import pd.implicits._
//        val b = pd.createDataFrame(cpaRDD)
//        println(cpaRDD.count())
//    }
//
//    test("rdd to mongo") {
//        val resultLocation = "hdfs:///workData/Max/c1b34bb6-94b2-428e-9f24-1d0a284c986aa492604d-bd4a-4973-839a-d5f792600e91"
//        val delimiter = 31.toChar.toString
//        val sd = phSparkDriver()
//        val singleJobDF = sd.csv2RDD(resultLocation, delimiter)
//        //            val singleJobDF = sd.readCsv(resultLocation, delimiter)
//
//        singleJobDF.write.format("com.mongodb.spark.sql.DefaultSource").mode("overwrite")
////            .option("uri", s"mongodb://${max_data_sync_mongo_obj.mongodbHost}:${max_data_sync_mongo_obj.mongodbPort}/")
////            .option("database", max_data_sync_mongo_obj.databaseName)
//            .option("uri", s"mongodb://192.168.100.174:27017/")
//            .option("database", "jeo_test")
//            .option("collection", "test1644")
//            .save()
//        sd.sc.stop()
//    }
//}
