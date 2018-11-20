package com.pharbers.unitTest.action

import akka.actor.Actor
import com.pharbers.pactions.actionbase._
import com.pharbers.unitTest.common.readJsonTrait
import com.pharbers.builder.phMarketTable.Builderimpl
import com.pharbers.spark.phSparkDriver

case class executeMaxAction(override val defaultArgs : pActionArgs)
                           (implicit _actor: Actor) extends pActionTrait with readJsonTrait {
    override val name: String = "max_result"

    val company: String = defaultArgs.asInstanceOf[MapArgs].get("company").asInstanceOf[StringArgs].get
    val mkt: String = defaultArgs.asInstanceOf[MapArgs].get("mkt").asInstanceOf[StringArgs].get
    val user: String = defaultArgs.asInstanceOf[MapArgs].get("user").asInstanceOf[StringArgs].get
    val job_id: String = defaultArgs.asInstanceOf[MapArgs].get("job_id").asInstanceOf[StringArgs].get
    val panel_file: String = defaultArgs.asInstanceOf[MapArgs].get("panel_file").asInstanceOf[StringArgs].get
    val universe_file: String = defaultArgs.asInstanceOf[MapArgs].get("universe_file").asInstanceOf[StringArgs].get
    val cpa: String = defaultArgs.asInstanceOf[MapArgs].get("cpa").asInstanceOf[StringArgs].get
    val gycx: String = defaultArgs.asInstanceOf[MapArgs].get("gycx").asInstanceOf[StringArgs].get
    val ym: String = defaultArgs.asInstanceOf[MapArgs].get("ym").asInstanceOf[StringArgs].get

    val builderimpl = Builderimpl(company)
    import builderimpl._

    override def perform(args : pActionArgs): pActionArgs = {

        val mapping: Map[String, String] = Map(
            "user_id" -> user,
            "company_id" -> company,
            "job_id" -> job_id,
            "ym" -> ym,
            "mkt" -> mkt,
            "p_total" -> "0",
            "p_current" -> "0",
            "cpa" -> cpa,
            "gycx" -> gycx
        )

        // 执行Panel
        val panel = doPanel(mapping)

        // 执行Max
        val maxResult = doMax(mapping, panel)
        StringArgs(maxResult)
    }

    def doPanel(mapping: Map[String, String]): String = {
        //        val panelInstMap = getPanelInst(mkt)
        //        val ckArgLst = panelInstMap("source").split("#").toList ::: panelInstMap("args").split("#").toList ::: Nil
        //        val args = mapping ++ panelInstMap ++ testData.find(x => company == x("company") && mkt == x("market")).get
        //
        //        if(!parametCheck(ckArgLst, args)(m => ck_base(m) && ck_panel(m)))
        //            throw new Exception("input wrong")
        //
        //        val clazz: String = panelInstMap("instance")
        //        val result = impl(clazz, args).perform(MapArgs(Map().empty))
        //                .asInstanceOf[MapArgs]
        //                .get("phSavePanelJob")
        //                .asInstanceOf[StringArgs].get
        //        phSparkDriver().sc.stop()
        val saveName = mapping("mkt") + "_test"
        val savePath = "hdfs:///workData/Panel/" + saveName
        val ph =  phSparkDriver()
//        ph.readCsv(universe_file, ",").createOrReplaceTempView("universe")
//        ph.readCsv(panel_file, ",").createOrReplaceTempView("panel")
        //        ph.ss.sql("select distinct panel.* from panel inner join universe on panel.HOSP_ID = universe.PHA_HOSP_ID and universe.IF_PANEL_ALL = 1")
        //                .write
        //                .format("com.databricks.spark.csv")
        //                .option("header", value = true)
        //                .option("delimiter", 31.toChar.toString)
        //                .option("codec", "org.apache.hadoop.io.compress.GzipCodec")
        //                .save(savePath)
        ph.readCsv(panel_file, ",").write
                .format("com.databricks.spark.csv")
                .option("header", value = true)
                .option("delimiter", 31.toChar.toString)
                .option("codec", "org.apache.hadoop.io.compress.GzipCodec")
                .save(savePath)
        ph.stopCurrConn
        saveName
    }

    def doMax(mapping: Map[String, String], panel: String): String = {
        //        val maxInstMap = getMaxInst(mkt)
        //        val ckArgLst = maxInstMap("args").split("#").toList ::: Nil
        val args = mapping ++ Map("panel_name" -> panel) ++ testData.find(x => company == x("company") && mkt == x("market")).get

        //        if(!parametCheck(ckArgLst, args)(m => ck_base(m) && ck_panel(m) && ck_max(m)))
        //            throw new Exception("input wrong")

        val clazz: String = args("maxInstance")
        val result = impl(clazz, args).perform(MapArgs(Map().empty))
                .asInstanceOf[MapArgs]
                .get("max_persistent_action")
                .asInstanceOf[StringArgs].get
        //        phSparkDriver().sc.stop()
        result
    }
}
