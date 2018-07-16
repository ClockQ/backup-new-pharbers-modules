package com.pharbers.max

import com.pharbers.common.algorithm.phDealRGB
import com.pharbers.search.{phMaxCompanyDashboard, phMaxNativeDashboard, phMaxProvinceDashboard}
import org.scalatest.FunSuite

class MaxDashboardSuite extends FunSuite with phDealRGB {

    val company: String = "5afa53bded925c05c6f69c54"
    val user: String = "5afaa333ed925c30f8c066d1"
    val ym = "201703"
    val mkt = "麻醉市场"

    test("max dashboard company module"){
        val dashboard = phMaxCompanyDashboard(company, ym)
//        val severalYMlist = dashboard.getLastSeveralYearYM(2, ym)
//        severalYMlist.foreach(println)
        val dashboardStartYM = dashboard.dashboardStartYM
        val dashboardEndYM = dashboard.dashboardEndYM
        val lastSeasonYM = dashboard.getLastSeveralMonthYM(4, ym).last
        val currCompanySales = dashboard.getCurrMonthCompanySales
        val currFullYearCompanySales = dashboard.getCurrFullYearCompanySales
        val currCurrYearSalesAvg = dashboard.getCurrYearCompanySalesAvg
        val yoy = dashboard.getFormatShare(dashboard.getCompanyYearOnYear)
        val mom = dashboard.getFormatShare(dashboard.getCompanyMonthOnMonth)
        val lstMonthSales = dashboard.getListMonthCompanySales
        val fastestGrowingMkt = dashboard.getFastestGrowingMkt
        val fastestSaleGrowingProd = dashboard.getCompanyFastestSaleGrowingProd
        val fastestSaleDeclineProd = dashboard.getCompanyFastestSaleDeclineProd
        val fastestShareGrowingProd = dashboard.getCompanyFastestShareGrowingProd
        val fastestShareDeclineProd = dashboard.getCompanyFastestShareDeclineProd
        val companyProdMap = dashboard.getCompanyProdCurrSalesGrowth



        println("currYM\t" + ym)
        println("dashboardStartYM\t" + dashboardStartYM)
        println("dashboardEndYM\t" + dashboardEndYM)
        println("lastSeasonYM\t" + lastSeasonYM)
        println("currCompanySales\t" + currCompanySales)
        println("currFullYearCompanySales\t" + currFullYearCompanySales)
        println("currCurrYearSalesAvg\t" + currCurrYearSalesAvg)
        println("yoy\t" + yoy)
        println("mom\t" + mom)
        println("lstMonthSales\t" + lstMonthSales)
        println("fastestGrowingMkt\t" + fastestGrowingMkt)
        println("fastestSaleGrowingProd\t" + fastestSaleGrowingProd)
        println("fastestSaleDeclineProd\t" + fastestSaleDeclineProd)
        println("fastestShareGrowingProd\t" + fastestShareGrowingProd)
        println("fastestShareDeclineProd\t" + fastestShareDeclineProd)
        println("companyProdMap\t" + companyProdMap)



//        val colorStep = companyProdMap.length
//        val test = companyProdMap.zipWithIndex.map(m => {
//            val color = getIndexColor(m._2, colorStep, "#000000", "#FFFFFF").toUpperCase()
//            m._1 ++ Map("color" -> color)
//        })
//        println("test\t" + test.map(m => m("color")))

    }

    test("max dashboard nation module"){
        val dashboard = phMaxNativeDashboard(company, ym, mkt)

        val currMonthNationSales = dashboard.getCurrMonthNationSales
        val currMonthCompanySales = dashboard.getCurrMonthCompanySales
        val currMonthCompanyShare = dashboard.getCurrMonthCompanyShare
        val listMonthTrend = dashboard.getListMonthTrend
        val competingProductCount = dashboard.getCompetingProductCount
        val prodSalesGrowth = dashboard.getProdSalesGrowthByYM(ym)
        val salesSorted = dashboard.getCurrMonthProdSortByKey("sales")
        val salesGrowth = dashboard.getCurrMonthProdSortByKey("salesGrowth")
        val prodShare = dashboard.getCurrMonthProdSortByKey("prodShare")
        val prodShareGrowth = dashboard.getCurrMonthProdSortByKey("prodShareGrowth")

//        println("currMonthNationSales\t" + currMonthNationSales)
//        println("currMonthCompanySales\t" + currMonthCompanySales)
//        println("currMonthCompanyShare\t" + currMonthCompanyShare)
//        println("listMonthTrend\t" + listMonthTrend)
//        println("competingProductCount\t" + competingProductCount)
//        println("prodSalesGrowth\t" + prodSalesGrowth)

//        prodShareGrowth.foreach(println)

    }

    test("max dashboard province module"){
        val dashboard = phMaxProvinceDashboard(company, ym, mkt, "安徽")
        val testListData = dashboard.getCurrProvinceSeveralMonthProdeSalesMap
        testListData.foreach(println)
        println(testListData.size)
    }



}
