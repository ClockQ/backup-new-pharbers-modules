package com.pharbers.max

import com.pharbers.builder.SearchFacade
import com.pharbers.pactions.actionbase._
import com.pharbers.search.{phHistorySearchJob, phMaxDashboard, phMaxResultInfo, phPanelResultInfo}
import org.scalatest.FunSuite
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.util.matching.Regex

/**
  * Created by jeorch on 18-5-9.
  */
class MaxSearchSuite extends FunSuite {

    val company: String = "5afa53bded925c05c6f69c54"
    val user: String = "5afaa333ed925c30f8c066d1"
    val jobId: String = "20180709test001"
    val ym = "201703"
    val mkt = "麻醉市场"

    test("history search"){

        val args: Map[String, String] = Map(
            "company" -> company,
            "user" -> user,
            "ym_condition" -> "201804-201804",
            "mkt" -> mkt,
            "pageIndex" -> "1",
            "singlePageSize" -> "10"
        )

        val searchResult =  phHistorySearchJob(args).perform().asInstanceOf[MapArgs]
        val searchResult1 =  searchResult.get("return_page_cache_action").asInstanceOf[ListArgs].get
        println(searchResult1.length)
        searchResult1.foreach(x => println(s"### => ${x}"))
    }

    test("get panel info"){
        val panelInfo = phPanelResultInfo(user, company, ym, mkt)
        println(panelInfo.getHospCount)
        println(panelInfo.getProdCount)
        println(panelInfo.getPanelSales)
        println(panelInfo.getCurrCompanySales)
        println(panelInfo.getCurrCompanyShare)
        println(panelInfo.getNotPanelHospLst)
        println(panelInfo.getNotPanelHospLst.take(10))
    }

    test("get max info"){
        val maxResultInfo = phMaxResultInfo(company, ym, mkt)

//        println(maxResultInfo.getMaxResultSales)
//        println(maxResultInfo.getCurrCompanySales)

        println(maxResultInfo.getLastYearResultSales)
        println(maxResultInfo.getLastYearCurrCompanySales)
        println(maxResultInfo.getAreaSalesByRange("CITY_SALES",maxResultInfo.lastYearSingleJobKey).length)
        println(maxResultInfo.getAreaSalesByRange("CITY_SALES",maxResultInfo.lastYearSingleJobKey))
        println(maxResultInfo.getCityLstMap)
//        println(maxResultInfo.getLastSeveralMonthResultSalesLst(12))
//        println(maxResultInfo.getLastYearResultSalesPercentage)
//        println(maxResultInfo.getLastYearCurrCompanySalesPercentage)
//        println(maxResultInfo.getLastYearCurrCompanySales)
//        println(maxResultInfo.getCityLstMap.take(10))
//        println(maxResultInfo.getProvLstMap.length)
    }

    test("history search of Facade") {
        val condition = toJson {
            Map(
                "condition" -> toJson(Map(
                    "user_id" -> toJson(user),
                    "startTime" -> toJson("201701"),
                    "endTime" -> toJson("201801"),
                    "currentPage" -> toJson(1),
                    "pageSize" -> toJson(10),
                    "market" -> toJson(mkt)
                )),
                "user" -> toJson(Map("company" -> toJson(Map("company_id" -> toJson(company)))))
            )
        }

        val search = new SearchFacade
        println(search.searchHistory(condition)._1.get.get("data").get.as[List[JsValue]].length)
        println(search.searchHistory(condition)._1.get.get("page").get)
        search.searchHistory(condition)._1.get.get("data").get.as[List[JsValue]].foreach(println)
    }

    test("export data of Facade") {
        val condition = toJson {
            Map(
                "condition" -> toJson(Map(
                    "startTime" -> toJson("201701"),
                    "endTime" -> toJson("201801"),
                    "market" -> toJson(mkt)
                )),
                "user" -> toJson(Map("company" -> toJson(Map("company_id" -> toJson(company)))))
            )
        }

        val search = new SearchFacade
        println(search.exportData(condition)._1.get.get("export_file_name").get)
    }

    test("max dashboard"){
        val dashboard = phMaxDashboard(company, ym)
//        val severalYMlist = dashboard.getLastSeveralYearYM(2, ym)
//        severalYMlist.foreach(println)
        val lastSeasonYM = dashboard.getLastSeveralMonthYM(4, ym).last
        val currCompanySales = dashboard.getCurrMonthCompanySales
        val currFullYearCompanySales = dashboard.getCurrFullYearSales
        val currCurrYearSalesAvg = dashboard.getCurrYearSalesAvg
        val yoy = dashboard.getYearOnYear
        val mom = dashboard.getMonthOnMonth
        val lstMonthSales = dashboard.getListMonthCompanySales
        val fastestGrowingMkt = dashboard.getFastestGrowingMkt
        val fastestSaleGrowingProd = dashboard.getFastestSaleGrowingProd
        val fastestSaleDeclineProd = dashboard.getFastestSaleDeclineProd
        val fastestShareGrowingProd = dashboard.getFastestShareGrowingProd
        val fastestShareDeclineProd = dashboard.getFastestShareDeclineProd
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
    }

    test("Regex"){
        val pattern = new Regex("[a-zA-Z0-9]")
        val str = "格列宁片剂150MG1上海诺华制药有限公司"
        val product = pattern.split(str).head
        val manufacturer = pattern.split(str).last
        println(s"product = $product")
        println(s"manufacturer = $manufacturer")
    }

}
