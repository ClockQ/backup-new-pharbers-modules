package com.pharbers.processSuit

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.pharbers.processSuit.MaxTestHeader.{max, panel, panelBayer}
import akka.pattern.ask
import com.pharbers.excel.format.input.writable.phExcelWritable
import com.pharbers.pactions.generalactions.xlsxReadingAction
import com.pharbers.panel.astellas.format.phAstellasCpaFormat
import com.pharbers.spark.phSparkDriver
import org.apache.spark.rdd.RDD

import scala.concurrent.duration._
import org.scalatest.FunSuite
import play.api.libs.json.JsValue

import scala.concurrent.Await

class MaxProcess_BayerSuit extends FunSuite {
    val system = ActorSystem("maxActor")
    val company: String = "5be9751faa1d5df8ab305858"
    val user: String = "dcs"
    val jobId: String = "20181113bayer002"
    val testActor: ActorRef = system.actorOf(MaxTestHeader.props(company, user, jobId))

    test("bayer max process test") {
        val dateformat = new SimpleDateFormat("MM-dd HH:mm:ss")
        println(s"开始时间" + dateformat.format(new Date()))
        println()
        val panel = "AHP-Panel-201806.csv"
        val yms = "201806"

       val hf = phSparkDriver().ss.read.csv("/home/alfred/Documents/对数bayer1806/AHP-Panel-201806.csv")
        val rdd = xlsxReadingAction[phAstellasCpaFormat]("hdfs:///test/AHP_Panel+201806.xlsx","xlsx").perform(null).get.asInstanceOf[RDD[phAstellasCpaFormat]]


        implicit val t: Timeout = 600 minutes

        val r1 = testActor ? panelBayer(panel, yms)
        val result1 = Await.result(r1.mapTo[JsValue], t.duration)
        println("panel result2 = " + result1)
        println(s"panel 结束时间" + dateformat.format(new Date()))

        val r2 = testActor ? max()
        val result2 = Await.result(r2.mapTo[JsValue], t.duration)
        println("max result3 = " + result2)
        println(s"max 结束时间" + dateformat.format(new Date()))


        println()
        println(s"结束时间" + dateformat.format(new Date()))

    }
}
