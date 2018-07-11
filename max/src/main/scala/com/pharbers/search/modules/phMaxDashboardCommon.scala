package com.pharbers.search.modules

import java.text.SimpleDateFormat
import java.util.{Base64, Date}

import com.pharbers.driver.PhRedisDriver
import com.pharbers.search.phMaxSearchTrait
import com.pharbers.sercuity.Sercurity

import scala.collection.mutable

trait phMaxDashboardCommon extends phMaxSearchTrait {

    val ym : String
    val company : String

    val dateformat = new SimpleDateFormat("yyyyMM")
    val currYM: String = dateformat.format(new Date())

    val currYear: String = currYM.take(4)
    val currMonth: String = currYM.takeRight(2)

    val dashboardYear: String = ym.take(4)
//    val dashboardMonth: String = if (currYear.toInt == dashboardYear.toInt) currMonth else "12"
    val dashboardMonth: String = "12"
    val dashboardYM: String = dashboardYear + dashboardMonth

    val lastMonthYM: String = getLastMonthYM(ym)
    val lastSeasonYM: String = getLastSeveralMonthYM(4, ym).last
    val lastYearYM: String = getLastYearYM(ym)

    val rd = new PhRedisDriver()
    val maxSingleDayJobsKey: String = Sercurity.md5Hash("Pharbers")
    val allCollections: mutable.Set[String] = getAllCollections
    val todaySingleJobKeySet: Set[String] = rd.getSetAllValue(maxSingleDayJobsKey)
    val allSingleJobKeySet: Set[String] = todaySingleJobKeySet match {
        case s if s.isEmpty => allCollections.toSet
        case s => s.foreach(allCollections.add); allCollections.toSet
    }

    def getLstKeySales(list: List[String], scope: String): List[Double] = list match {
        case Nil => 0.0 :: Nil
        case lst => lst.map(x => getHistorySalesByRange(scope, x))
    }

    def getSalesByScopeYM(scope: String, yearMonth: String, market: String = "all"): Double = filterJobKeySet(todaySingleJobKeySet, yearMonth, company, market) match {
        case Nil => getLstKeySales(filterJobKeySet(allSingleJobKeySet, yearMonth, company, market).map(x => x._4), scope).sum
        case lst => lst.map(x => {
            rd.getMapValue(x._4, scope).toDouble
        }).sum
    }

    def getLstKeySalesMap(list: List[String], scope: String): List[Map[String, String]] = list match {
        case Nil => List.empty
        case lst => lst.map(x => {
            val mkt = new String(Base64.getDecoder.decode(x)).split("#")(2)
            Map("market" -> mkt, "sales" -> getHistorySalesByRange(scope, x).toString)
        })
    }

    def getLstProductSalesMap(list: List[String], scope: String): List[Map[String, String]] = list match {
        case Nil => List.empty
        case lst => lst.flatMap(x => {
            val mkt = new String(Base64.getDecoder.decode(x)).split("#")(2)
            getAreaSalesByRange(scope, x).map(m => Map("market" -> mkt, "product" -> m("Area"), "sales" -> m("Sales")))
        })
    }

    def filterJobKeySet(jobKeySet: Set[String], ym: String, company: String, market: String): List[(String, String, String, String)] = {
        jobKeySet
            .map(singleJobKey => {
                val singleJobInfoArr = new String(Base64.getDecoder.decode(singleJobKey)).split("#")
                (
                    singleJobInfoArr(0),
                    singleJobInfoArr(1),
                    singleJobInfoArr(2),
                    singleJobKey
                )
            })
            .filter(x => x._1 == company)
            .filter(x => x._2.contains(ym))
            .filter(x => if(market == "all") true else x._3 == market)
            .toList
    }

    def getSalesGrowthByScopeYM(scope: String, yearMonth: String, market: String = "all"): Double =  filterJobKeySet(allSingleJobKeySet, yearMonth, company, market) match {
        case Nil => 0.0
        case lst =>
            val lastPeriodCompanySales = getLstKeySales(lst.map(x => x._4), scope).sum
            (getSalesByScopeYM(ym, scope, market) - lastPeriodCompanySales)/lastPeriodCompanySales
    }

    def getMktSalesMapByYM(yearMonth: String, market: String = "all"): List[Map[String, String]] = filterJobKeySet(todaySingleJobKeySet, yearMonth, company, market) match {
        case Nil => getLstKeySalesMap(filterJobKeySet(allSingleJobKeySet, yearMonth, company, market).map(x => x._4), "NATION_SALES")
        case lst => lst.map(x => {
            Map("market" -> x._3, "sales" -> rd.getMapValue(x._4, "NATION_SALES"))
        })
    }

    def getProdSalesMapByScopeYM(scope: String, yearMonth: String, market: String = "all"): List[Map[String, String]] = filterJobKeySet(todaySingleJobKeySet, yearMonth, company, market) match {
        case Nil => getLstProductSalesMap(filterJobKeySet(allSingleJobKeySet, yearMonth, company, market).map(x => x._4), scope)
        case lst => lst.flatMap(x => {
            rd.getListAllValue(x._4 + scope).map({y =>
                val temp = y.replace("[","").replace("]","").split(",")
                Map("market" -> x._3, "product" -> temp(0), "sales" -> temp(1))
            })
        })
    }

}
