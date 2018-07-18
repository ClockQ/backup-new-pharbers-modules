package com.pharbers.builder.dashboard

import com.pharbers.common.algorithm.phDealRGB
import com.pharbers.search.{phMaxProvinceDashboard, phMaxSearchTrait}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

trait phProvinceDashboard extends phMaxSearchTrait with phDealRGB {

    def getProvinceCardData(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxProvinceDashboard(company_id, ym, market)
        val provinceSalesLst = dashboard.getProvinceSalesLstMap(ym)

        val provSalesMax: Map[String, String] = provinceSalesLst match {
            case Nil => Map.empty
            case _ => dashboard.getMaxProvinceSalesMap
        }
        val provSalesGrowingFastest: Map[String, String] = provinceSalesLst match {
            case Nil => Map.empty
            case _ => dashboard.getFastProvinceGrowingMap
        }
        val provCompanySalesMax: Map[String, String] = provinceSalesLst match {
            case Nil => Map.empty
            case _ => dashboard.getMaxCompanySalesMap
        }
        val provCompanySalesGrowingFastest: Map[String, String] = provinceSalesLst match {
            case Nil => Map.empty
            case _ => dashboard.getFastCompanySalesGrowingMap
        }
        val provCompanyShareMax: Map[String, String] = provinceSalesLst match {
            case Nil => Map.empty
            case _ => dashboard.getMaxCompanyShareMap
        }
        val provCompanyShareGrowingFastest: Map[String, String] = provinceSalesLst match {
            case Nil => Map.empty
            case _ => dashboard.getFastCompanyShareGrowingMap
        }

        val provinceWord: List[JsValue] = List(
            toJson(Map(
                "title" -> toJson("市场规模最大"),
                "subtitle" -> toJson(time),
                "name" -> toJson(provSalesMax.getOrElse("province", "无")),
                "tag" -> toJson("mil"),
                "value" -> toJson(getFormatSales(provSalesMax.getOrElse("provinceSales", "0.0").toDouble)),
                "percent" -> toJson(getFormatShare(provSalesMax.getOrElse("provMomGrowth", "0.0").toDouble))
            )),
            toJson(Map(
                "title" -> toJson("市场规模增长最快"),
                "subtitle" -> toJson(time),
                "name" -> toJson(provSalesGrowingFastest.getOrElse("province", "无")),
                "tag" -> toJson("mil"),
                "value" -> toJson(getFormatSales(provSalesGrowingFastest.getOrElse("provinceSales", "0.0").toDouble)),
                "percent" -> toJson(getFormatShare(provSalesGrowingFastest.getOrElse("provMomGrowth", "0.0").toDouble))
            )),
            toJson(Map(
                "title" -> toJson("产品销售额最高"),
                "subtitle" -> toJson(time),
                "name" -> toJson(provCompanySalesMax.getOrElse("province", "无")),
                "tag" -> toJson("mil"),
                "value" -> toJson(getFormatSales(provCompanySalesMax.getOrElse("companySales", "0.0").toDouble)),
                "percent" -> toJson(getFormatShare(provCompanySalesMax.getOrElse("companySalesMomGrowth", "0.0").toDouble))
            )),
            toJson(Map(
                "title" -> toJson("产品销售额增长最快"),
                "subtitle" -> toJson(time),
                "name" -> toJson(provCompanySalesGrowingFastest.getOrElse("province", "无")),
                "tag" -> toJson("mil"),
                "value" -> toJson(getFormatSales(provCompanySalesGrowingFastest.getOrElse("companySales", "0.0").toDouble)),
                "percent" -> toJson(getFormatShare(provCompanySalesGrowingFastest.getOrElse("companySalesMomGrowth", "0.0").toDouble))
            )),
            toJson(Map(
                "title" -> toJson("产品份额最高"),
                "subtitle" -> toJson(time),
                "name" -> toJson(provCompanyShareMax.getOrElse("province", "无")),
                "tag" -> toJson("%"),
                "value" -> toJson(getFormatShare(provCompanyShareMax.getOrElse("companyShare", "0.0").toDouble)),
                "percent" -> toJson(getFormatShare(provCompanyShareMax.getOrElse("companyShareMomGrowth", "0.0").toDouble))
            )),
            toJson(Map(
                "title" -> toJson("产品份额增长最快"),
                "subtitle" -> toJson(time),
                "name" -> toJson(provCompanyShareGrowingFastest.getOrElse("province", "无")),
                "tag" -> toJson("%"),
                "value" -> toJson(getFormatShare(provCompanyShareGrowingFastest.getOrElse("companyShare", "0.0").toDouble)),
                "percent" -> toJson(getFormatShare(provCompanyShareGrowingFastest.getOrElse("companyShareMomGrowth", "0.0").toDouble))
            ))
        )

        (Some(Map("provinceWord" -> toJson(provinceWord))), None)
    }

    def getProvinceTableOverview(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxProvinceDashboard(company_id, ym, market)
        val provinceSalesLst = dashboard.getProvinceSalesLstMap(ym)

        val proTableOverview: Map[String, JsValue] = Map(
            "title" -> toJson(s"${market}市场各省销售概况")
        )

        val prodSalesValue = provinceSalesLst match {
            case Nil => List.empty
            case lst => lst.map(m => {
                Map(
                    "province" -> toJson(m.getOrElse("province", "无")),
                    "market_size" -> toJson(getFormatSales(m.getOrElse("provinceSales", "0.0").toDouble)),
                    "market_groth" -> toJson(getFormatShare(m.getOrElse("provMomGrowth", "0.0").toDouble)),
                    "sales_amount" -> toJson(getFormatSales(m.getOrElse("companySales", "0.0").toDouble)),
                    "sales_growth" -> toJson(getFormatShare(m.getOrElse("companySalesMomGrowth", "0.0").toDouble)),
                    "ev_value" -> toJson(getFormatShare(m.getOrElse("EV", "0.0").toDouble)),
                    "share" -> toJson(getFormatShare(m.getOrElse("companyShare", "0.0").toDouble)),
                    "share_growth" -> toJson(getFormatShare(m.getOrElse("companyShareMomGrowth", "0.0").toDouble))
                )
            })
        }

        (Some(Map("proTableOverview" -> toJson(proTableOverview), "prodSalesValue" -> toJson(prodSalesValue))), None)
    }

    def getProvinceMarketPart(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxProvinceDashboard(company_id, ym, market)
        val provinceSalesLst = dashboard.getProvinceSalesLstMap(ym)

        val marketSharePart: Map[String, JsValue] = Map(
            "title" -> toJson(s"${market}市场 销售组成"),
            "subtitle" -> toJson(time)
        )

        val provLstMapWithColor = provinceSalesLst match {
            case Nil => List.empty
            case lst => lst.sortBy(x => x("companyShare").toDouble).reverse.zipWithIndex.map(m => {
                val color = getIndexColor(m._2, lst.length).toUpperCase()
                m._1 ++ Map("color" -> color)
            })
        }

        val pie = provLstMapWithColor.map(m => {
            Map(
                "prov" -> toJson(m.getOrElse("province", "无")),
                "mkt_scale" -> toJson(getFormatSales(m.getOrElse("provinceSales", "0.0").toDouble)),
                "mkt_sales" -> toJson(getFormatSales(m.getOrElse("companySales", "0.0").toDouble)),
                "share" -> toJson(getFormatShare(m.getOrElse("companyShare", "0.0").toDouble)),
                "color" -> toJson(m.getOrElse("color", "#FFFFFF"))
            )
        })

        (Some(Map("marketSharePart" -> toJson(marketSharePart), "pie" -> toJson(pie))), None)
    }

    def getProvLevelRank(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val tag = (jv \ "condition" \ "tag").asOpt[String].getOrElse(throw new Exception("Illegal tag"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxProvinceDashboard(company_id, ym, market)

        val unit = tag match {
            case t if t.toLowerCase().contains("share") => "%"
            case t if t.toLowerCase().contains("grow") => "%"
            case t if t.toLowerCase().contains("sale") => "mil"
            case _ => "undefined"
        }

        val ranking = dashboard.getProvinceSalesLstMap(ym) match {
            case Nil => List.empty
            case _ =>
                dashboard.getCurrMonthProvSortByKey(tag).map(m => {
                    val value = m.getOrElse(tag, "0.0").toDouble
                    val formatValue: Double = tag match {
                        case t if t.toLowerCase().contains("share") => getFormatShare(value)
                        case t if t.toLowerCase().contains("grow") => getFormatShare(value)
                        case t if t.toLowerCase().contains("sale") => getFormatSales(value)
                        case _ => 0.0
                    }
                    Map(
                        "no" -> toJson(m.getOrElse(s"${tag}Rank", "0").toInt),
                        "province" -> toJson(m.getOrElse("province", "无")),
                        "growth" -> toJson(m.getOrElse(s"${tag}RankChanges", "0").toInt),
                        "value" -> toJson(formatValue)
                    )
                })
        }

        (Some(Map("unit" -> toJson(unit), "ranking" -> toJson(ranking))), None)
    }

    def getProvMarketSale(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val province = (jv \ "condition" \ "province").asOpt[String].getOrElse(throw new Exception("Illegal province"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxProvinceDashboard(company_id, ym, market, province)

        val currProvSalesMap: Map[String, String] = dashboard.getCurrProvinceSalesMap

        val productMarketSale: List[JsValue] = List(
            toJson(Map(
                "title" -> toJson("市场总销售额"),
                "subtitle" -> toJson(time),
                "province" -> toJson(province),
                "tag" -> toJson("mil"),
                "num" -> toJson(getFormatSales(currProvSalesMap.getOrElse("provinceSales", "0.0").toDouble)),
                "yearOnYear" -> toJson(getFormatShare(currProvSalesMap.getOrElse("provYoyGrowth", "0.0").toDouble)),
                "ringRatio" -> toJson(getFormatShare(currProvSalesMap.getOrElse("provMomGrowth", "0.0").toDouble))
            )),
            toJson(Map(
                "title" -> toJson(s"${company_name}产品销售额"),
                "subtitle" -> toJson(time),
                "province" -> toJson(province),
                "tag" -> toJson("mil"),
                "num" -> toJson(getFormatSales(currProvSalesMap.getOrElse("companySales", "0.0").toDouble)),
                "yearOnYear" -> toJson(getFormatShare(currProvSalesMap.getOrElse("companySalesYoyGrowth", "0.0").toDouble)),
                "ringRatio" -> toJson(getFormatShare(currProvSalesMap.getOrElse("companySalesMomGrowth", "0.0").toDouble))
            )),
            toJson(Map(
                "title" -> toJson(s"${company_name}产品份额"),
                "subtitle" -> toJson(time),
                "province" -> toJson(province),
                "tag" -> toJson("%"),
                "num" -> toJson(getFormatShare(currProvSalesMap.getOrElse("companyShare", "0.0").toDouble)),
                "yearOnYear" -> toJson(getFormatShare(currProvSalesMap.getOrElse("companyShareYoyGrowth", "0.0").toDouble)),
                "ringRatio" -> toJson(getFormatShare(currProvSalesMap.getOrElse("companyShareMomGrowth", "0.0").toDouble))
            ))
        )

        (Some(Map("productMarketSale" -> toJson(productMarketSale))), None)
    }

    def getProvProdTrend(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val province = (jv \ "condition" \ "province").asOpt[String].getOrElse(throw new Exception("Illegal province"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxProvinceDashboard(company_id, ym, market, province)

        val tableSale: Map[String, JsValue] = Map(
            "prodSalesOverview" -> toJson(Map(
                "title" -> toJson(s"市场规模 & ${company_name}产品销售趋势"),
                "timeStart" -> toJson(getFormatYM(dashboard.dashboardStartYM)),
                "timeOver" -> toJson(getFormatYM(dashboard.dashboardEndYM)),
                "area" -> toJson(province)
            )),
            "multiData" -> toJson(dashboard.getSeveralMonthProvinceSalesMap.map(x => {
                toJson(Map(
                    "date" -> toJson(getFormatYM(x("ym"))),
                    "marketSales" -> toJson(getFormatSales(x("provinceSale").toDouble)),
                    "prodSales" -> toJson(getFormatSales(x("currProvinceCompanySale").toDouble)),
                    "share" -> toJson(getFormatShare(x("currProvinceCompanyShare").toDouble))
                ))
            }))
        )

        (Some(Map("tableSale" -> toJson(tableSale))), None)
    }

    def getProvProdCard(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val province = (jv \ "condition" \ "province").asOpt[String].getOrElse(throw new Exception("Illegal province"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxProvinceDashboard(company_id, ym, market, province)

        val provinceSalesLst = dashboard.getProvinceSalesLstMap(ym)

        val competingProdCount = provinceSalesLst match {
            case Nil => 0
            case _ => dashboard.getCurrProvinceCompetingProdCount
        }
        val currProvMaxShareMap: Map[String, String] = provinceSalesLst match {
            case Nil => Map.empty
            case _ => dashboard.getCurrProvinceMaxShareProdMap
        }
        val currProvFastGrowingSaleMap: Map[String, String] = provinceSalesLst match {
            case Nil => Map.empty
            case _ => dashboard.getCurrProvSalesFastGrowingProdMap
        }
        val currProvFastDeclineShareMap: Map[String, String] = provinceSalesLst match {
            case Nil => Map.empty
            case _ => dashboard.getCurrProvShareFastDeclineProdMap
        }

        val proProductCard: List[JsValue] = List(
            toJson(Map(
                "title" -> toJson("竞品数量"),
                "subtitle" -> toJson(time),
                "province" -> toJson(province),
                "tag" -> toJson("count"),
                "name" -> toJson(competingProdCount)
            )),
            toJson(Map(
                "title" -> toJson("份额最大"),
                "subtitle" -> toJson(time),
                "province" -> toJson(province),
                "tag" -> toJson("%"),
                "name" -> toJson(currProvMaxShareMap.getOrElse("PRODUCT_NAME", "无")),
                "subname" -> toJson(currProvMaxShareMap.getOrElse("CORP_NAME", "无")),
                "value" -> toJson(getFormatShare(currProvMaxShareMap.getOrElse("share", "0.0").toDouble)),
                "percent" -> toJson(getFormatShare(currProvMaxShareMap.getOrElse("shareGrowth", "0.0").toDouble))
            )),
            toJson(Map(
                "title" -> toJson("销售额增长最快"),
                "subtitle" -> toJson(time),
                "province" -> toJson(province),
                "tag" -> toJson("mil"),
                "name" -> toJson(currProvFastGrowingSaleMap.getOrElse("PRODUCT_NAME", "无")),
                "subname" -> toJson(currProvFastGrowingSaleMap.getOrElse("CORP_NAME", "无")),
                "value" -> toJson(getFormatSales(currProvFastGrowingSaleMap.getOrElse("sales", "0.0").toDouble)),
                "percent" -> toJson(getFormatShare(currProvFastGrowingSaleMap.getOrElse("salesGrowth", "0.0").toDouble))
            )),
            toJson(Map(
                "title" -> toJson("份额下降最多"),
                "subtitle" -> toJson(time),
                "province" -> toJson(province),
                "tag" -> toJson("%"),
                "name" -> toJson(currProvFastDeclineShareMap.getOrElse("PRODUCT_NAME", "无")),
                "subname" -> toJson(currProvFastDeclineShareMap.getOrElse("CORP_NAME", "无")),
                "value" -> toJson(getFormatShare(currProvFastDeclineShareMap.getOrElse("share", "0.0").toDouble)),
                "percent" -> toJson(getFormatShare(currProvFastDeclineShareMap.getOrElse("shareGrowth", "0.0").toDouble))
            ))
        )

        (Some(Map("proProductCard" -> toJson(proProductCard))), None)
    }

    def getProvProdShare(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val province = (jv \ "condition" \ "province").asOpt[String].getOrElse(throw new Exception("Illegal province"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxProvinceDashboard(company_id, ym, market, province)
        val provProdSalesLst = dashboard.getCurrProvinceAllProdLstMap

        val marketSharePart: Map[String, JsValue] = Map(
            "title" -> toJson(s"${market}各产品销售份额"),
            "subtitle" -> toJson(time),
            "province" -> toJson(province)
        )

        val provLstMapWithColor = provProdSalesLst match {
            case Nil => List.empty
            case lst => lst.sortBy(x => x("share").toDouble).reverse.zipWithIndex.map(m => {
                val color = getIndexColor(m._2, lst.length).toUpperCase()
                m._1 ++ Map("color" -> color)
            })
        }

        val pie = provLstMapWithColor.map(m => {
            Map(
                "prod" -> toJson(m.getOrElse("PRODUCT_NAME", "无")),
                "corp" -> toJson(m.getOrElse("CORP_NAME", "无")),
                "sales" -> toJson(getFormatSales(m.getOrElse("sales", "0.0").toDouble)),
                "share" -> toJson(getFormatShare(m.getOrElse("share", "0.0").toDouble)),
                "color" -> toJson(m.getOrElse("color", "#FFFFFF"))
            )
        })

        (Some(Map("marketSharePart" -> toJson(marketSharePart), "pie" -> toJson(pie))), None)
    }

    def getProvProdRankChange(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val province = (jv \ "condition" \ "province").asOpt[String].getOrElse(throw new Exception("Illegal province"))
        val tag = (jv \ "condition" \ "tag").asOpt[String].getOrElse(throw new Exception("Illegal tag"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxProvinceDashboard(company_id, ym, market, province)

        val unit = tag match {
            case t if t.toLowerCase().contains("share") => "%"
            case t if t.toLowerCase().contains("grow") => "%"
            case t if t.toLowerCase().contains("sale") => "mil"
            case _ => "undefined"
        }

        val ranking = dashboard.getProvinceProdLstMapByYM(ym) match {
            case Nil => List.empty
            case _ =>
                dashboard.getCurrMonthProvProdSortByKey(tag).map(m => {
                    val value = m.getOrElse(tag, "0.0").toDouble
                    val formatValue: Double = tag match {
                        case t if t.toLowerCase().contains("share") => getFormatShare(value)
                        case t if t.toLowerCase().contains("grow") => getFormatShare(value)
                        case t if t.toLowerCase().contains("sale") => getFormatSales(value)
                        case _ => 0.0
                    }
                    Map(
                        "no" -> toJson(m.getOrElse(s"${tag}Rank", "0").toInt),
                        "prod" -> toJson(m.getOrElse("PRODUCT_NAME", "无")),
                        "manu" -> toJson(m.getOrElse("CORP_NAME", "无")),
                        "growth" -> toJson(m.getOrElse(s"${tag}RankChanges", "0").toInt),
                        "value" -> toJson(formatValue)
                    )
                })
        }

        (Some(Map("unit" -> toJson(unit), "ranking" -> toJson(ranking))), None)
    }

    def getProvProdSaleOverview(jv: JsValue): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val company_id = (jv \ "user" \ "company" \ "company_id").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val company_name = (jv \ "user" \ "company" \ "company_name").asOpt[String].getOrElse(throw new Exception("Illegal company"))
        val time = (jv \ "condition" \ "time").asOpt[String].getOrElse(throw new Exception("Illegal time"))
        val market = (jv \ "condition" \ "market").asOpt[String].getOrElse(throw new Exception("Illegal market"))
        val province = (jv \ "condition" \ "province").asOpt[String].getOrElse(throw new Exception("Illegal province"))
        val ym = time.replaceAll("-", "")

        val dashboard = phMaxProvinceDashboard(company_id, ym, market, province)

        val prodSalesOverview = Map(
            "title" -> toJson(s"${market}各产品销售份额"),
            "subtitle" -> toJson(time),
            "province" -> toJson(province)
        )

        val competeSaleTable = dashboard.getCurrProvinceAllProdLstMap.map(m => {
            Map(
                "prod" -> toJson(m.getOrElse("PRODUCT_NAME", "无")),
                "manufacturer" -> toJson(m.getOrElse("CORP_NAME", "无")),
                "market_sale" -> toJson(getFormatSales(m.getOrElse("sales", "0.0").toDouble)),
                "sales_growth" -> toJson(getFormatShare(m.getOrElse("salesGrowth", "0.0").toDouble)),
                "ev_value" -> toJson(getFormatShare(m.getOrElse("EV", "0.0").toDouble)),
                "share" -> toJson(getFormatShare(m.getOrElse("share", "0.0").toDouble)),
                "share_growth" -> toJson(getFormatShare(m.getOrElse("shareGrowth", "0.0").toDouble))
            )
        })

        (Some(Map("prodSalesOverview" -> toJson(prodSalesOverview), "competeSaleTable" -> toJson(competeSaleTable))), None)
    }

}
