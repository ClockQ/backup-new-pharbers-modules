package com.pharbers.search.modules

import java.text.SimpleDateFormat
import java.util
import java.util.{Base64, Date}

import com.pharbers.driver.PhRedisDriver
import com.pharbers.search.phMaxSearchTrait
import com.pharbers.sercuity.Sercurity

import scala.collection.mutable

trait phMaxCompanyDashboard extends phMaxSearchTrait with phMaxDashboardCommon {

    val ym : String
    val company : String

    val dateformat = new SimpleDateFormat("yyyyMM")
    val currYM: String = dateformat.format(new Date())

    val currYear: String = currYM.take(4)
    val currMonth: String = currYM.takeRight(2)

    val dashboardYear: String = ym.take(4)
    val dashboardMonth: String = if (currYear.toInt == dashboardYear.toInt) currMonth else "12"
    val dashboardYM: String = dashboardYear + dashboardMonth

    val lastMonthYM: String = getLastMonthYM(ym)
    val lastYearYM: String = getLastYearYM(ym)

    val rd = new PhRedisDriver()
    val maxSingleDayJobsKey: String = Sercurity.md5Hash("Pharbers")
    val allCollections: mutable.Set[String] = getAllCollections
    val todaySingleJobKeySet: Set[String] = rd.getSetAllValue(maxSingleDayJobsKey)
    val totalSingleJobKeySet: Set[String] = todaySingleJobKeySet match {
        case s if s.isEmpty => allCollections.toSet
        case s => s.foreach(allCollections.add); allCollections.toSet
    }

    def getCurrMonthSales: Double = getSalesByYM(ym)

    def getListMonthSales: List[Map[String, Any]] = (dashboardYM :: getLastSeveralMonthYM(dashboardMonth.toInt, dashboardYM)).map(x => Map("ym" -> x, "sales" -> getSalesByYM(x)))

    def getCurrFullYearSales: Double = getSalesByYM(dashboardYear)

    def getCurrYearSalesAvg: Double = getSalesByYM(dashboardYear)/dashboardMonth.toInt

    def getYearOnYear: Double = getShareByYM(lastYearYM)

    def getMonthOnMonth: Double = getShareByYM(lastMonthYM)

    def getShareByYM(yearMonth: String): Double =  filterJobKeySet(totalSingleJobKeySet, yearMonth, company) match {
        case Nil => 0.0
        case lst =>
            val lastPeriodCompanySales = getLstKeySales(lst.map(x => x._4), "NATION_COMPANY_SALES").sum
            (getCurrMonthSales - lastPeriodCompanySales)/lastPeriodCompanySales
    }

    def getSalesByYM(yearMonth: String): Double = filterJobKeySet(todaySingleJobKeySet, yearMonth, company) match {
        case Nil => getLstKeySales(filterJobKeySet(totalSingleJobKeySet, yearMonth, company).map(x => x._4), "NATION_COMPANY_SALES").sum
        case lst => lst.map(x => {
            rd.getMapValue(x._4, "max_company_sales").toDouble
        }).sum
    }

    def getMktSalesMapByYM(yearMonth: String): List[Map[String, String]] = filterJobKeySet(todaySingleJobKeySet, yearMonth, company) match {
        case Nil => getLstKeySalesMap(filterJobKeySet(totalSingleJobKeySet, yearMonth, company).map(x => x._4), "NATION_COMPANY_SALES")
        case lst => lst.map(x => {
            Map("market" -> x._3, "sales" -> rd.getMapValue(x._4, "max_company_sales"))
        })
    }

    def getProdSalesMapByYM(yearMonth: String): List[Map[String, String]] = filterJobKeySet(todaySingleJobKeySet, yearMonth, company) match {
        case Nil => getLstProductSalesMap(filterJobKeySet(totalSingleJobKeySet, yearMonth, company).map(x => x._4), "PRODUCT_SALES")
        case lst => lst.flatMap(x => {
            rd.getListAllValue(x._4 + "_PRODUCT_COMPANY_SALES").map({y =>
                val temp = y.replace("[","").replace("]","").split(",")
                Map("market" -> x._3, "product" -> temp(0), "sales" -> temp(1))
            })
        })
    }

    def getMktCurrSalesGrowth: List[Map[String, String]] = getMktSalesMapByYM(ym).map(x => {
        Map("market" -> x("market").toString, "sales" -> x("sales").toString, "growth" -> (x("sales").toString.toDouble - getMktSalesMapByYM(lastMonthYM).find(y => y("market")==x("market")).get("sales").toString.toDouble).toString)
    })

    def getProdCurrSalesGrowth: List[Map[String, String]] = getProdSalesMapByYM(ym).map(x => {
        Map("market" -> x("market").toString, "product" -> x("product").toString, "sales" -> x("sales").toString, "growth" -> (x("sales").toString.toDouble - getProdSalesMapByYM(lastMonthYM).find(y => y("market")==x("market") && y("product")==x("product")).getOrElse(Map("sales" -> 0.0))("sales").toString.toDouble).toString)
    })

    def getFastestGrowingMkt: String = getMktCurrSalesGrowth.maxBy(x => x("growth").toString.toDouble) match {
        case m if m("growth").toString.toDouble < 0 => "无"
        case m => m("market").toString
    }

    def getFastestGrowingProd: String = getProdCurrSalesGrowth.maxBy(x => x("growth").toString.toDouble) match {
        case m if m("growth").toString.toDouble < 0 => "无"
        case m => m("product").toString
    }

    def getFastestDeclineProd: String = getProdCurrSalesGrowth.minBy(x => x("growth").toString.toDouble) match {
        case m if m("growth").toString.toDouble > 0 => "无"
        case m => m("product").toString
    }

    //TO BE CONTINUED

}
