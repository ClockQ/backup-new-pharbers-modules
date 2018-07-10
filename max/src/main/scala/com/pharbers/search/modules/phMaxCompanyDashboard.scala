package com.pharbers.search.modules

import java.text.SimpleDateFormat
import java.util.Date

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
    val lastSeasonYM: String = getLastSeveralMonthYM(4, ym).last
    val lastYearYM: String = getLastYearYM(ym)

    val rd = new PhRedisDriver()
    val maxSingleDayJobsKey: String = Sercurity.md5Hash("Pharbers")
    val allCollections: mutable.Set[String] = getAllCollections
    val todaySingleJobKeySet: Set[String] = rd.getSetAllValue(maxSingleDayJobsKey)
    val totalSingleJobKeySet: Set[String] = todaySingleJobKeySet match {
        case s if s.isEmpty => allCollections.toSet
        case s => s.foreach(allCollections.add); allCollections.toSet
    }

    def getCurrMonthCompanySales: Double = getCompanySalesByYM(ym)

    def getListMonthCompanySales: List[Map[String, Any]] = (dashboardYM :: getLastSeveralMonthYM(dashboardMonth.toInt, dashboardYM)).map(x => Map("ym" -> x, "sales" -> getCompanySalesByYM(x)))

    def getCurrFullYearSales: Double = getCompanySalesByYM(dashboardYear)

    def getCurrYearSalesAvg: Double = getCompanySalesByYM(dashboardYear)/dashboardMonth.toInt

    def getYearOnYear: Double = getGrowthByYM(lastYearYM)

    def getMonthOnMonth: Double = getGrowthByYM(lastMonthYM)

    def getMktCurrSalesGrowth: List[Map[String, String]] = getMktSalesMapByYM(ym).map(x => {
        Map("market" -> x("market").toString, "sales" -> x("sales").toString, "growth" -> (x("sales").toString.toDouble - getMktSalesMapByYM(lastMonthYM).find(y => y("market")==x("market")).get("sales").toString.toDouble).toString)
    })

    private val mktCurrSalesGrowthMap = getMktCurrSalesGrowth

    def getCompanyProdCurrSalesGrowth: List[Map[String, String]] = getCompanyProdSalesMapByYM(ym).map(x => {
        val mktSales = mktCurrSalesGrowthMap.find(m => m("market") == x("market")).get("sales")
        val mktGrowth = mktCurrSalesGrowthMap.find(m => m("market") == x("market")).get("growth")
        val prodLastMonthSales = getCompanyProdSalesMapByYM(lastMonthYM).find(y => y("market")==x("market") && y("product")==x("product")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val prodLastSeasonSales = getCompanyProdSalesMapByYM(lastSeasonYM).find(y => y("market")==x("market") && y("product")==x("product")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val prodLastYearSales = getCompanyProdSalesMapByYM(lastYearYM).find(y => y("market")==x("market") && y("product")==x("product")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val prodGrowth = ((x("sales").toDouble - prodLastMonthSales)/prodLastMonthSales).toString
        val EV = prodGrowth.toDouble/mktGrowth.toDouble * 100
        val companyProdShare = x("sales").toDouble/mktSales.toDouble
        val companyProdShareGrowth = (prodGrowth.toDouble + 1)/(mktGrowth.toDouble + 1) - 1
        val contribution = x("sales").toDouble/getCurrMonthCompanySales
        val lastMonthContribution = prodLastMonthSales/getCompanySalesByYM(lastMonthYM)
        val lastSeasonContribution = prodLastSeasonSales/getCompanySalesByYM(lastSeasonYM)
        val lastYearContribution = prodLastYearSales/getCompanySalesByYM(lastYearYM)
        Map(
            "product" -> getFormatProdFromMin1(x("product")),
//            "product" -> x("product"),
            "market" -> x("market"),
            "marketSales" -> mktSales,
            "marketGrowth" -> mktGrowth,
            "sales" -> x("sales"),
            "productGrowth" -> prodGrowth,
            "EV" -> EV.toString,
            "companyProdShare" -> companyProdShare.toString,
            "companyProdShareGrowth" -> companyProdShareGrowth.toString,
            "contribution" -> contribution.toString,
            "lastMonthContribution" -> lastMonthContribution.toString,
            "lastSeasonContribution" -> lastSeasonContribution.toString,
            "lastYearContribution" -> lastYearContribution.toString
        )
    })

    private val companyProdCurrSalesGrowthMap = getCompanyProdCurrSalesGrowth

    def getFastestGrowingMkt: String = mktCurrSalesGrowthMap.maxBy(x => x("growth").toString.toDouble) match {
        case m if m("growth").toString.toDouble < 0 => "无"
        case m => m("market").toString
    }

    def getFastestSaleGrowingProd: String = companyProdCurrSalesGrowthMap.maxBy(x => x("productGrowth").toString.toDouble) match {
        case m if m("productGrowth").toString.toDouble < 0 => "无"
        case m => m("product").toString
    }

    def getFastestSaleDeclineProd: String = companyProdCurrSalesGrowthMap.minBy(x => x("productGrowth").toString.toDouble) match {
        case m if m("productGrowth").toString.toDouble > 0 => "无"
        case m => m("product").toString
    }

    def getFastestShareGrowingProd: String = companyProdCurrSalesGrowthMap.maxBy(x => x("companyProdShareGrowth").toString.toDouble) match {
        case m if m("companyProdShareGrowth").toString.toDouble < 0 => "无"
        case m => m("product").toString
    }

    def getFastestShareDeclineProd: String = companyProdCurrSalesGrowthMap.minBy(x => x("companyProdShareGrowth").toString.toDouble) match {
        case m if m("companyProdShareGrowth").toString.toDouble > 0 => "无"
        case m => m("product").toString
    }

    //TO BE DISSOCIATION

    def getGrowthByYM(yearMonth: String): Double =  filterJobKeySet(totalSingleJobKeySet, yearMonth, company) match {
        case Nil => 0.0
        case lst =>
            val lastPeriodCompanySales = getLstKeySales(lst.map(x => x._4), "NATION_COMPANY_SALES").sum
            (getCurrMonthCompanySales - lastPeriodCompanySales)/lastPeriodCompanySales
    }

    def getCompanySalesByYM(yearMonth: String): Double = filterJobKeySet(todaySingleJobKeySet, yearMonth, company) match {
        case Nil => getLstKeySales(filterJobKeySet(totalSingleJobKeySet, yearMonth, company).map(x => x._4), "NATION_COMPANY_SALES").sum
        case lst => lst.map(x => {
            rd.getMapValue(x._4, "max_company_sales").toDouble
        }).sum
    }

    def getMktSalesMapByYM(yearMonth: String): List[Map[String, String]] = filterJobKeySet(todaySingleJobKeySet, yearMonth, company) match {
        case Nil => getLstKeySalesMap(filterJobKeySet(totalSingleJobKeySet, yearMonth, company).map(x => x._4), "NATION_SALES")
        case lst => lst.map(x => {
            Map("market" -> x._3, "sales" -> rd.getMapValue(x._4, "max_sales"))
        })
    }

    def getCompanyProdSalesMapByYM(yearMonth: String): List[Map[String, String]] = filterJobKeySet(todaySingleJobKeySet, yearMonth, company) match {
        case Nil => getLstProductSalesMap(filterJobKeySet(totalSingleJobKeySet, yearMonth, company).map(x => x._4), "PRODUCT_COMPANY_SALES")
        case lst => lst.flatMap(x => {
            rd.getListAllValue(x._4 + "_PRODUCT_COMPANY_SALES").map({y =>
                val temp = y.replace("[","").replace("]","").split(",")
                Map("market" -> x._3, "product" -> temp(0), "sales" -> temp(1))
            })
        })
    }



}
