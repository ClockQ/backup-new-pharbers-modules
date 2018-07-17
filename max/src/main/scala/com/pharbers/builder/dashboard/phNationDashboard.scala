package com.pharbers.builder.dashboard

import com.pharbers.common.algorithm.phDealRGB
import com.pharbers.search.{phMaxNativeDashboard, phMaxSearchTrait}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait phNationDashboard extends phMaxSearchTrait with phDealRGB {

    def getNationSaleShare(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxNativeDashboard(company_id, ym, market)

        val saleShareCard: List[JsValue] = List(
            toJson(Map(
                "title" -> toJson("市场总销售额"),
                "subtitle" -> toJson(time),
                "area" -> toJson("全国"),
                "num" -> toJson(getFormatSales(dashboard.getCurrMonthNationSales)),
                "tag" -> toJson("mil"),
                "yearOnYear" -> toJson(getFormatShare(dashboard.getNationSalesYearOnYear)),
                "ringRatio" -> toJson(getFormatShare(dashboard.getNationSalesMonthOnMonth))
            )),
            toJson(Map(
                "title" -> toJson("产品销售额"),
                "subtitle" -> toJson(time),
                "area" -> toJson("全国"),
                "num" -> toJson(getFormatSales(dashboard.getCurrMonthCompanySales)),
                "tag" -> toJson("mil"),
                "yearOnYear" -> toJson(getFormatShare(dashboard.getCompanySalesYearOnYear)),
                "ringRatio" -> toJson(getFormatShare(dashboard.getCompanySalesMonthOnMonth))
            )),
            toJson(Map(
                "title" -> toJson("产品份额"),
                "subtitle" -> toJson(time),
                "area" -> toJson("全国"),
                "num" -> toJson(getFormatShare(dashboard.getCurrMonthCompanyShare)),
                "tag" -> toJson("%"),
                "yearOnYear" -> toJson(getFormatShare(dashboard.getCompanyShareYearOnYear)),
                "ringRatio" -> toJson(getFormatShare(dashboard.getCompanyShareMonthOnMonth))
            ))
        )

        (Some(Map("saleShareCard" -> toJson(saleShareCard))), None)
    }

    def getNationMarketTrend(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxNativeDashboard(company_id, ym, market)

        val tableSale: Map[String, JsValue] = Map(
            "prodSalesOverview" -> toJson(Map(
                "title" -> toJson(s"市场规模 & ${company_name}产品销售趋势"),
                "timeStart" -> toJson(getFormatYM(dashboard.dashboardStartYM)),
                "timeOver" -> toJson(getFormatYM(dashboard.dashboardEndYM)),
                "area" -> toJson("全国")
            )),
            "multiData" -> toJson(dashboard.getListMonthTrend.map(x => {
                toJson(Map(
                    "ym" -> toJson(getFormatYM(x("ym"))),
                    "marketSales" -> toJson(getFormatSales(x("NationSales").toDouble)),
                    "prodSales" -> toJson(getFormatSales(x("CompanySales").toDouble)),
                    "share" -> toJson(getFormatShare(x("CompanyShare").toDouble))
                ))
            }))
        )

        (Some(Map("tableSale" -> toJson(tableSale))), None)
    }

    def getNationMostWord(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxNativeDashboard(company_id, ym, market)
        val prodSalesGrowthLst = dashboard.getProdSalesGrowthByYM(ym)

        val prodShareMax: Map[String, String] = prodSalesGrowthLst match {
            case Nil => Map.empty
            case lst => lst.maxBy(x => x("prodShare").toDouble)
        }

        val prodShareGrowing: Map[String, String] = prodSalesGrowthLst match {
            case Nil => Map.empty
            case _ => dashboard.getFastestShareGrowingProd
        }

        val prodShareDecline: Map[String, String] = prodSalesGrowthLst match {
            case Nil => Map.empty
            case _ => dashboard.getFastestShareDeclineProd
        }

        val saleShareCard: List[JsValue] = List(
            toJson(Map(
                "title" -> toJson("竟品数量"),
                "subtitle" -> toJson(time),
                "area" -> toJson("全国"),
                "name" -> toJson(dashboard.getCompetingProductCount),
                "tag" -> toJson("count")
            )),
            toJson(Map(
                "title" -> toJson("份额最大"),
                "subtitle" -> toJson(time),
                "area" -> toJson("全国"),
                "name" -> toJson(prodShareMax.getOrElse("product", "无")),
                "subname" -> toJson(prodShareMax.getOrElse("corp", "无")),
                "tag" -> toJson("%"),
                "value" -> toJson(getFormatShare(prodShareMax.getOrElse("prodShare", "0.0").toDouble)),
                "percent" -> toJson(getFormatShare(prodShareMax.getOrElse("prodShareGrowth", "0.0").toDouble))
            )),
            toJson(Map(
                "title" -> toJson("份额增长最快"),
                "subtitle" -> toJson(time),
                "area" -> toJson("全国"),
                "name" -> toJson(prodShareGrowing.getOrElse("product", "无")),
                "subname" -> toJson(prodShareGrowing.getOrElse("corp", "无")),
                "tag" -> toJson("mil"),
                "value" -> toJson(getFormatSales(prodShareGrowing.getOrElse("sales", "0.0").toDouble)),
                "percent" -> toJson(getFormatShare(prodShareGrowing.getOrElse("salesGrowth", "0.0").toDouble))
            )),
            toJson(Map(
                "title" -> toJson("份额下降最多"),
                "subtitle" -> toJson(time),
                "area" -> toJson("全国"),
                "name" -> toJson(prodShareDecline.getOrElse("product", "无")),
                "subname" -> toJson(prodShareDecline.getOrElse("corp", "无")),
                "tag" -> toJson("%"),
                "value" -> toJson(getFormatShare(prodShareDecline.getOrElse("prodShare", "0.0").toDouble)),
                "percent" -> toJson(getFormatShare(prodShareDecline.getOrElse("prodShareGrowth", "0.0").toDouble))
            ))
        )

        (Some(Map("saleShareCard" -> toJson(saleShareCard))), None)
    }

    def getNationProductShare(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxNativeDashboard(company_id, ym, market)
        val prodSalesGrowthLst = dashboard.getProdSalesGrowthByYM(ym)

        val colorStep = prodSalesGrowthLst.length
        val prodLstMapWithColor = prodSalesGrowthLst.sortBy(x => x("prodShare").toDouble).reverse.zipWithIndex.map(m => {
            val color = getIndexColor(m._2, colorStep).toUpperCase()
            m._1 ++ Map("color" -> color)
        })

        val prodSalesOverview = Map(
            "title" -> toJson(company_name + "各产品销售份额"),
            "subtitle" -> toJson(time),
            "area" -> toJson("全国")
        )
        val pie = prodLstMapWithColor.map(m => {
            Map(
                "prod" -> toJson(m.getOrElse("product", "无")),
                "sales" -> toJson(getFormatSales(m.getOrElse("sales", "0.0").toDouble)),
                "share" -> toJson(getFormatShare(m.getOrElse("prodShare", "0.0").toDouble)),
                "color" -> toJson(m.getOrElse("color", "#FFFFFF"))
            )
        })


        (Some(Map("prodSalesOverview" -> toJson(prodSalesOverview), "pie" -> toJson(pie))), None)
    }

    def getNationProductRank(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val tag = (jv \ "condition" \ "tag").asOpt[String].getOrElse(throw new Exception("Illegal tag"))
        val ym = time.replaceAll("-", "")

        val unit = tag match {
            case t if t.toLowerCase().contains("share") => "%"
            case t if t.toLowerCase().contains("grow") => "%"
            case t if t.toLowerCase().contains("sale") => "mil"
            case _ => "undefined"
        }

        val dashboard = phMaxNativeDashboard(company_id, ym, market)

        val ranking = dashboard.getCurrMonthProdSortByKey(tag).map(m => {
            val value = m.getOrElse(tag, "0.0").toDouble
            val formatValue: Double = tag match {
                case t if t.toLowerCase().contains("share") => getFormatShare(value)
                case t if t.toLowerCase().contains("grow") => getFormatShare(value)
                case t if t.toLowerCase().contains("sale") => getFormatSales(value)
                case _ => 0.0
            }
            Map(
                "no" -> toJson(m.getOrElse(s"${tag}Rank", "0").toInt),
                "prod" -> toJson(m.getOrElse("product", "无")),
                "manu" -> toJson(m.getOrElse("corp", "无")),
                "growth" -> toJson(m.getOrElse(s"${tag}RankChanges", "0").toInt),
                "value" -> toJson(formatValue)
            )
        })

        (Some(Map("unit" -> toJson(unit), "ranking" -> toJson(ranking))), None)
    }

    def getNationProductTable(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxNativeDashboard(company_id, ym, market)
        val prodSalesGrowthLst = dashboard.getProdSalesGrowthByYM(ym)

        val prodSalesOverview = Map(
            "title" -> toJson(company_name + "各产品销售概况"),
            "subtitle" -> toJson(time),
            "area" -> toJson("全国")
        )
        val prodSalesValue = prodSalesGrowthLst.map(m => {
            Map(
                "prod" -> toJson(m.getOrElse("product", "无")),
                "manufacturer" -> toJson(m.getOrElse("corp", "无")),
                "market_sale" -> toJson(getFormatSales(m.getOrElse("sales", "0.0").toDouble)),
                "sales_growth" -> toJson(getFormatShare(m.getOrElse("salesGrowth", "0.0").toDouble)),
                "ev_value" -> toJson(getFormatShare(m.getOrElse("EV", "0.0").toDouble)),
                "share" -> toJson(getFormatShare(m.getOrElse("prodShare", "0.0").toDouble)),
                "share_growth" -> toJson(getFormatShare(m.getOrElse("prodShareGrowth", "0.0").toDouble))
            )
        })


        (Some(Map("prodSalesOverview" -> toJson(prodSalesOverview), "prodSalesValue" -> toJson(prodSalesValue))), None)
    }

}
