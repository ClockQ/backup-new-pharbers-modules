package com.pharbers.search.modules

trait phMaxDashboardNationModule extends phMaxDashboardCommon {

    val market : String

    def getCurrMonthNationSales: Double = getSalesByScopeYM("NATION_SALES", ym, market)
    def getNationSalesYearOnYear: Double = getSalesGrowthByScopeYM("NATION_SALES", lastYearYM, market)
    def getNationSalesMonthOnMonth: Double = getSalesGrowthByScopeYM("NATION_SALES", lastMonthYM, market)

    def getCurrMonthCompanySales: Double = getSalesByScopeYM("NATION_COMPANY_SALES", ym, market)
    def getCompanySalesYearOnYear: Double = getSalesGrowthByScopeYM("NATION_COMPANY_SALES", lastYearYM, market)
    def getCompanySalesMonthOnMonth: Double = getSalesGrowthByScopeYM("NATION_COMPANY_SALES", lastMonthYM, market)

    def getCurrMonthCompanyShare: Double = getShare(getCurrMonthCompanySales, getCurrMonthNationSales)
    def getCompanyShareYearOnYear: Double = getShare(getCompanySalesYearOnYear, getNationSalesYearOnYear)
    def getCompanyShareMonthOnMonth: Double = getShare(getCompanySalesMonthOnMonth, getNationSalesMonthOnMonth)

    def getListMonthTrend: List[Map[String, String]] = (dashboardEndYM :: getLastSeveralMonthYM(dashboardMonth.toInt, dashboardEndYM)).map(x => {
        val tempNationSales: Double = getSalesByScopeYM("NATION_SALES", x, market)
        val tempCompanySales: Double = getSalesByScopeYM("NATION_COMPANY_SALES", x, market)
        val tempCompanyShare: Double = getShare(tempCompanySales, tempNationSales)
        Map(
            "ym" -> x,
            "NationSales" -> tempNationSales.toString,
            "CompanySales" -> tempCompanySales.toString,
            "CompanyShare" -> tempCompanyShare.toString
        )
    })


    //TODO: 等有了需求后完成
    def getListSeasonTrend: List[Map[String, String]] = ???
    def getListYearTrend: List[Map[String, String]] = ???

    def getCompetingProductCount: Int = getProdSalesMapByScopeYM("PRODUCT_SALES", ym, market).length - getProdSalesMapByScopeYM("PRODUCT_COMPANY_SALES", ym, market).length

    def getCurrMktSalesGrowth: List[Map[String, String]] = getMktSalesLstMap(ym, market).map(x => {
        val lastMonthMktSales = getMktSalesLstMap(lastMonthYM, market).find(y => y("market")==x("market")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val growth = lastMonthMktSales match {
            case 0.0 => 0.0
            case _ => (x("sales").toString.toDouble - lastMonthMktSales)/lastMonthMktSales
        }
        Map("market" -> x("market").toString, "sales" -> x("sales").toString, "growth" -> growth.toString)
    })

    private val currMktSalesGrowthMap = getCurrMktSalesGrowth

    def getProdSalesGrowthByYM(temp_ym: String): List[Map[String, String]] = getProdSalesMapByScopeYM("PRODUCT_SALES", temp_ym, market).map(x => {
        val mktSales = currMktSalesGrowthMap.find(m => m("market") == x("market")).get("sales")
        val mktGrowth = currMktSalesGrowthMap.find(m => m("market") == x("market")).get("growth")
        val prodLastMonthSales = getProdSalesMapByScopeYM("PRODUCT_SALES", getLastMonthYM(temp_ym), market).find(y => y("market")==x("market") && y("product")==x("product")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val prodGrowth = prodLastMonthSales match {
            case 0.0 => "0.0"
            case lastSales => ((x("sales").toDouble - lastSales)/lastSales).toString
        }
        val EV = mktGrowth match {
            case "0.0" => "0.0"
            case mkt_growth => (prodGrowth.toDouble/mkt_growth.toDouble).toString
        }
        val prodShare = mktSales match {
            case "0.0" => "0.0"
            case mkt_sales => (x("sales").toDouble/mkt_sales.toDouble).toString
        }
        val prodShareGrowth = mktGrowth match {
            case "0.0" => "0.0"
            case mkt_growth => (prodGrowth.toDouble + 1)/(mkt_growth.toDouble + 1) - 1
        }
        Map(
            "product" -> getProdFromProdCorp(x("product")),
            "corp" -> getCorpFromProdCorp(x("product")),
            "market" -> x("market"),
            "marketSales" -> mktSales,
            "marketGrowth" -> mktGrowth,
            "sales" -> x("sales"),
            "salesGrowth" -> prodGrowth,
            "EV" -> EV,
            "prodShare" -> prodShare,
            "prodShareGrowth" -> prodShareGrowth.toString
        )
    })

    private lazy val currMonthProdLstMap = getProdSalesGrowthByYM(ym)

    def getFastestShareGrowingProd: Map[String, String] = currMonthProdLstMap.maxBy(x => x("prodShareGrowth").toDouble) match {
        case m if m("prodShareGrowth").toDouble <= 0 => Map.empty
        case m => m
    }

    def getFastestShareDeclineProd: Map[String, String] = currMonthProdLstMap.minBy(x => x("prodShareGrowth").toDouble) match {
        case m if m("prodShareGrowth").toDouble >= 0 => Map.empty
        case m => m
    }

    private lazy val lastMonthProdLstMap = getProdSalesGrowthByYM(lastMonthYM)

    def getLastMonthProdSortByKey(key: String) = lastMonthProdLstMap.sortBy(m => m(key).toDouble).reverse.zipWithIndex

    def getCurrMonthProdSortByKey(key: String) = currMonthProdLstMap.sortBy(m => m(key).toDouble).reverse.zipWithIndex.map(one => {
        val lastMonthProdRank = getLastMonthProdSortByKey(key).find(x => x._1("product") == one._1("product") && x._1("corp") == one._1("corp")).getOrElse((Map.empty, -1))._2
        val RankChanges = lastMonthProdRank match {
            case -1 => "0"
            case _ => (lastMonthProdRank - one._2).toString
        }
        one._1 ++ Map(s"${key}RankChanges" -> RankChanges, s"${key}Rank" -> (one._2 + 1).toString)
    })

}
