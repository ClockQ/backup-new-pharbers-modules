//package dataCleanDemo
//
//import com.pharbers.pactions.actionbase._
//import com.pharbers.spark.phSparkDriver
//import org.apache.spark.sql.DataFrame
//import org.apache.spark.sql.functions.udf
//import com.pharbers.common.algorithm.ph_alg.edit_distance
//
//object dataCleanAction {
//    def apply(args: MapArgs): pActionTrait = new dataCleanAction(args)
//}
//
//class dataCleanAction(override val defaultArgs: pActionArgs) extends pActionTrait {
//    override val name: String = "data_Clean"
//    lazy val sparkDriver: phSparkDriver = phSparkDriver()
//    import sparkDriver.ss.implicits._
//
//    override def perform(args: pActionArgs): pActionArgs = {
//        val source_file = defaultArgs.asInstanceOf[MapArgs].get("source_file").asInstanceOf[DFArgs].get
//        val standard_file = defaultArgs.asInstanceOf[MapArgs].get("standard_file").asInstanceOf[DFArgs].get
//
//        val checkedDF: DataFrame = {
//            val Splicing_sourceDF = source_file.withColumn("MIN_PRODUCT_UNIT", source_file("PRODUCT_NAME") + source_file("APP2_COD") + source_file("PACK_DES") + source_file("PACK_NUMBER") + source_file("CORP_NAME"))
//            val totalDF = Splicing_sourceDF.withColumn("flag", compare_product(Splicing_sourceDF("MIN_PRODUCT_UNIT"), standard_file("MIN_PRODUCT_UNIT_STANDARD")))
//            totalDF.filter($"flag" === true)
//        }
//        DFArgs(checkedDF)
//    }
//
//    val compare_product = udf((d1: String, d2: String) => {
//        edit_distance(d1, d2) / Math.max(d1.length, d2.length) == 0
//    })
//}
