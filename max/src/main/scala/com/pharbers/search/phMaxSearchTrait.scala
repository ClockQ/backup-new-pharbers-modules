package com.pharbers.search

import com.mongodb.casbah.Imports._
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsNumber, JsString, JsValue}
import com.pharbers.builder.phMarketTable.MongoDBPool._

import scala.collection.mutable
import scala.util.matching.Regex

/**
  * Created by jeorch on 18-5-16.
  */
trait phMaxSearchTrait {

    val pattern = new Regex("[a-zA-Z0-9]")

    def getLastMonthYM(yearMonth: String): String = yearMonth.takeRight(2) match {
        case "01" => (yearMonth.take(4).toInt - 1) + "12"
        case month => if (month.toInt == 10) yearMonth.take(4) + "09" else  yearMonth.take(5) + (yearMonth.takeRight(1).toInt - 1)
    }

    def getLastSeveralMonthYM(severalCount: Int, yearMonth: String): List[String] = {
        var tempYM = yearMonth
        (1 until severalCount).map(x => {
            tempYM = getLastMonthYM(tempYM)
            tempYM
        }).toList
    }

    def getLastSeveralYearYM(severalCount: Int, yearMonth: String): List[String] = {
        val severalMonth = severalCount * 12 + yearMonth.takeRight(2).toInt
        (yearMonth :: getLastSeveralMonthYM(severalMonth, yearMonth)).reverse
    }

    def getShare(partialData: Double, totalData: Double): Double = if (totalData == 0.0) 0.0 else partialData/totalData

    def getLastYearYM(yearMonth: String): String = (yearMonth.toInt - 100).toString

    def getFormatSales(originValue: Double): String = f"${originValue/1.0E6}%.2f"

    def getFormatShare(originValue: Double): Double = f"$originValue%.4f".toDouble

    def getFormatProdFromMin1(min1: String): String = (pattern split min1).head

    def getFormatCorpFromMin1(min1: String): String = (pattern split min1).last

    def getAllCollections : mutable.Set[String] = MongoPool.queryDBInstance("data").get.getOneDBAllCollectionNames

    def getHistorySalesByRange(range: String, tempSingleJobKey: String) : Double = {
        val db = MongoPool.queryDBInstance("aggregation").get

        val query: DBObject = DBObject()

        val output: DBObject => Map[String, JsValue] = { obj =>
            Map(
                "Sales" -> toJson(obj.as[Double]("value"))
            )
        }

        val tmp = db.queryObject(query, s"${tempSingleJobKey}_$range")(output)
        tmp match {
            case None => 0.0
            case Some(x) => x.getOrElse("Sales", 0.0).asInstanceOf[JsNumber].value.doubleValue()
        }
    }

    def getAreaSalesByRange(range: String, tempSingleJobKey: String) : List[Map[String, String]] = {
        val db = MongoPool.queryDBInstance("aggregation").get

        val query: DBObject = DBObject()

        val output: DBObject => Map[String, JsValue] = { obj =>
            Map(
                "Area" -> toJson(obj.as[String]("_id")),
                "Sales" -> toJson(obj.as[Double]("value"))
            )
        }

        val tmp = db.queryMultipleObject(query, s"${tempSingleJobKey}_$range", "value", 0, 1000)(output)
        tmp match {
            case Nil => Nil
            case lst => lst.map(x => Map(
                "Area" -> x.getOrElse("Area", "").asInstanceOf[JsString].value,
                "Sales" -> x.getOrElse("Sales", 0.0).asInstanceOf[JsNumber].value.doubleValue().toString
            ))
        }
    }

}
