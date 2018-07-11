package com.pharbers.max

import com.pharbers.search.{phMaxCompanyDashboard, phMaxNativeDashboard}
import org.scalatest.FunSuite

class MaxDashboardSuite extends FunSuite {

    val company: String = "5afa53bded925c05c6f69c54"
    val user: String = "5afaa333ed925c30f8c066d1"
    val ym = "201703"
    val mkt = "麻醉市场"

    test("max dashboard company module"){
        val dashboard = phMaxCompanyDashboard(company, ym)
//        val severalYMlist = dashboard.getLastSeveralYearYM(2, ym)
//        severalYMlist.foreach(println)
        val lastSeasonYM = dashboard.getLastSeveralMonthYM(4, ym).last
        val currCompanySales = dashboard.getCurrMonthCompanySales
        val currFullYearCompanySales = dashboard.getCurrFullYearCompanySales
        val currCurrYearSalesAvg = dashboard.getCurrYearCompanySalesAvg
        val yoy = dashboard.getCompanyYearOnYear
        val mom = dashboard.getCompanyMonthOnMonth
        val lstMonthSales = dashboard.getListMonthCompanySales
        val fastestGrowingMkt = dashboard.getFastestGrowingMkt
        val fastestSaleGrowingProd = dashboard.getCompanyFastestSaleGrowingProd
        val fastestSaleDeclineProd = dashboard.getCompanyFastestSaleDeclineProd
        val fastestShareGrowingProd = dashboard.getCompanyFastestShareGrowingProd
        val fastestShareDeclineProd = dashboard.getCompanyFastestShareDeclineProd
        val companyProdMap = dashboard.getCompanyProdCurrSalesGrowth
        println("currYM\t" + ym)
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
    }

    test("max dashboard nation module"){
        val dashboard = phMaxNativeDashboard(company, ym, mkt)

        val currMonthNationSales = dashboard.getCurrMonthNationSales
        val currMonthCompanySales = dashboard.getCurrMonthCompanySales
        val currMonthCompanyShare = dashboard.getCurrMonthCompanyShare
        val listMonthTrend = dashboard.getListMonthTrend

        println("currMonthNationSales\t" + currMonthNationSales)
        println("currMonthCompanySales\t" + currMonthCompanySales)
        println("currMonthCompanyShare\t" + currMonthCompanyShare)
        println("listMonthTrend\t" + listMonthTrend)
    }

}
