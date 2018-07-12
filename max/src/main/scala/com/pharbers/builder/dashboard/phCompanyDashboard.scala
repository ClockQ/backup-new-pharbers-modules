package com.pharbers.builder.dashboard

import com.pharbers.common.algorithm.phDealRGB
import com.pharbers.search.{phMaxCompanyDashboard, phMaxSearchTrait}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait phCompanyDashboard extends phMaxSearchTrait with phDealRGB {

    def saleData(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxCompanyDashboard(company_id, ym)

        val tableSale: Map[String, JsValue] = Map(
            "prodSalesOverview" -> toJson(Map(
                "title" -> toJson(company_name + "产品销售额"),
                "timeStart" -> toJson(getFormatYM(dashboard.dashboardStartYM)),
                "timeOver" -> toJson(getFormatYM(dashboard.dashboardEndYM)),
                "curMoSales" -> toJson(getFormatSales(dashboard.getCurrMonthCompanySales)),
                "yearYear" -> toJson(getFormatShare(dashboard.getCompanyYearOnYear)),
                "ring" -> toJson(getFormatShare(dashboard.getCompanyMonthOnMonth)),
                "totle" -> toJson(getFormatSales(dashboard.getCurrFullYearCompanySales)),
                "ave" -> toJson(getFormatSales(dashboard.getCurrYearCompanySalesAvg))
            )),
            "prodSalesTable" -> toJson(dashboard.getListMonthCompanySales)
        )

        (Some(Map("tableSale" -> toJson(tableSale))), None)
    }

    def keyWord(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxCompanyDashboard(company_id, ym)

        val mktGrowthLst = dashboard.getMktCurrSalesGrowth
        val fastestGrowingMkt: Map[String, String] = mktGrowthLst match {
            case Nil => Map.empty
            case _ => dashboard.getFastestGrowingMkt
        }

        val companyProdSalesGrowthLst = dashboard.getCompanyProdCurrSalesGrowth
        val fastestSaleGrowingProd: Map[String, String] = companyProdSalesGrowthLst match {
            case Nil => Map.empty
            case _ => dashboard.getCompanyFastestSaleGrowingProd
        }
        val fastestSaleDeclineProd: Map[String, String] = companyProdSalesGrowthLst match {
            case Nil => Map.empty
            case _ => dashboard.getCompanyFastestSaleDeclineProd
        }
        val maxShareProd: Map[String, String] = companyProdSalesGrowthLst match {
            case Nil => Map.empty
            case _ => dashboard.getCompanyMaxShareProd
        }
        val fastestShareGrowingProd: Map[String, String] = companyProdSalesGrowthLst match {
            case Nil => Map.empty
            case _ => dashboard.getCompanyFastestShareGrowingProd
        }
        val fastestShareDeclineProd: Map[String, String] = companyProdSalesGrowthLst match {
            case Nil => Map.empty
            case _ => dashboard.getCompanyFastestShareDeclineProd
        }

        val cards: List[JsValue] = List(
            toJson(Map(
                "title" -> toJson("市场规模增长最快"),
                "subtitle" -> toJson(time),
                "name" -> toJson(fastestGrowingMkt.getOrElse("market", "无")),
                "subname" -> toJson(company_name),
                "value" -> toJson(fastestGrowingMkt.getOrElse("sales", "0.0")),
                "percent" -> toJson(fastestGrowingMkt.getOrElse("growth", "0.0"))
            )),
            toJson(Map(
                "title" -> toJson("产品销售增长最快"),
                "subtitle" -> toJson(time),
                "name" -> toJson(fastestSaleGrowingProd.getOrElse("product", "无")),
                "subname" -> toJson(fastestSaleGrowingProd.getOrElse("market", "无")),
                "value" -> toJson(fastestSaleGrowingProd.getOrElse("sales", "0.0")),
                "percent" -> toJson(fastestSaleGrowingProd.getOrElse("productGrowth", "0.0"))
            )),
            toJson(Map(
                "title" -> toJson("产品销售下滑最多"),
                "subtitle" -> toJson(time),
                "name" -> toJson(fastestSaleDeclineProd.getOrElse("product", "无")),
                "subname" -> toJson(fastestSaleDeclineProd.getOrElse("market", "无")),
                "value" -> toJson(fastestSaleDeclineProd.getOrElse("sales", "0.0")),
                "percent" -> toJson(fastestSaleDeclineProd.getOrElse("productGrowth", "0.0"))
            )),
            toJson(Map(
                "title" -> toJson("份额最多"),
                "subtitle" -> toJson(time),
                "name" -> toJson(maxShareProd.getOrElse("product", "无")),
                "subname" -> toJson(maxShareProd.getOrElse("market", "无")),
                "value" -> toJson(maxShareProd.getOrElse("companyProdShare", "0.0")),
                "percent" -> toJson(maxShareProd.getOrElse("companyProdShareGrowth", "0.0"))
            )),
            toJson(Map(
                "title" -> toJson("产品份额增长最快"),
                "subtitle" -> toJson(time),
                "name" -> toJson(fastestShareGrowingProd.getOrElse("product", "无")),
                "subname" -> toJson(fastestShareGrowingProd.getOrElse("market", "无")),
                "value" -> toJson(fastestShareGrowingProd.getOrElse("companyProdShare", "0.0")),
                "percent" -> toJson(fastestShareGrowingProd.getOrElse("companyProdShareGrowth", "0.0"))
            )),
            toJson(Map(
                "title" -> toJson("份额下滑最多"),
                "subtitle" -> toJson(time),
                "name" -> toJson(fastestShareDeclineProd.getOrElse("product", "无")),
                "subname" -> toJson(fastestShareDeclineProd.getOrElse("market", "无")),
                "value" -> toJson(fastestShareDeclineProd.getOrElse("companyProdShare", "0.0")),
                "percent" -> toJson(fastestShareDeclineProd.getOrElse("companyProdShareGrowth", "0.0"))
            ))
        )

        (Some(Map("cards" -> toJson(cards))), None)
    }

    def overView(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxCompanyDashboard(company_id, ym)
        val companyProdLstMap = dashboard.getCompanyProdCurrSalesGrowth

        val overView: Map[String, JsValue] = Map(
            "prodSalesOverview" -> toJson(Map(
                "title" -> toJson(company_name + "各产品销售概况"),
                "subtitle" -> toJson(time)
            )),
            "prodSalesValue" -> toJson(companyProdLstMap.map(m => {
                Map(
                    "prod" -> toJson(m.getOrElse("product", "无")),
                    "market" -> toJson(m.getOrElse("market", "无")),
                    "market_scale" -> toJson(m.getOrElse("marketSales", "无")),
                    "market_growth" -> toJson(m.getOrElse("marketGrowth", "无")),
                    "sales" -> toJson(m.getOrElse("sales", "无")),
                    "sales_growth" -> toJson(m.getOrElse("productGrowth", "无")),
                    "ev_value" -> toJson(m.getOrElse("EV", "无")),
                    "share" -> toJson(m.getOrElse("companyProdShare", "无")),
                    "share_growth" -> toJson(m.getOrElse("companyProdShareGrowth", "无"))
                )
            }))
        )

        (Some(Map("overView" -> toJson(overView))), None)
    }

    def contribution(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxCompanyDashboard(company_id, ym)
        val companyProdLstMap = dashboard.getCompanyProdCurrSalesGrowth
        val colorStep = companyProdLstMap.length
        val companyProdLstMapWithColor = companyProdLstMap.sortBy(x => x("contribution").toString.toDouble).reverse.zipWithIndex.map(m => {
            val color = getIndexColor(m._2, colorStep).toUpperCase()
            m._1 ++ Map("color" -> color)
        })

        val tableSale: Map[String, JsValue] = Map(
            "prodSalesOverview" -> toJson(Map(
                "title" -> toJson(company_name + "产品销售贡献度"),
                "subtitle" -> toJson(time)
            )),
            "pie" -> toJson(companyProdLstMapWithColor.map(m => {
                Map(
                    "prod" -> toJson(m.getOrElse("product", "无")),
                    "sales" -> toJson(m.getOrElse("sales", "无")),
                    "cont" -> toJson(m.getOrElse("contribution", "无")),
                    "color" -> toJson(m.getOrElse("color", "#FFFFFF"))
                )
            })),
            "prodContValue" -> toJson(companyProdLstMapWithColor.map(m => {
                Map(
                    "prod" -> toJson(m.getOrElse("product", "无")),
                    "market" -> toJson(m.getOrElse("market", "无")),
                    "sales" -> toJson(m.getOrElse("sales", "无")),
                    "cont" -> toJson(m.getOrElse("contribution", "无")),
                    "cont-month" -> toJson(m.getOrElse("lastMonthContribution", "无")),
                    "cont-season" -> toJson(m.getOrElse("lastSeasonContribution", "无")),
                    "cont-year" -> toJson(m.getOrElse("lastYearContribution", "无")),
                    "color" -> toJson(m.getOrElse("color", "#FFFFFF"))
                )
            }))
        )

        (Some(Map("tableSale" -> toJson(tableSale))), None)
    }

}
