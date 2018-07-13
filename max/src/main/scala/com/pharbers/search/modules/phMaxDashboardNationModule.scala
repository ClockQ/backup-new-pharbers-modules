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
        Map("market" -> x("market").toString, "sales" -> x("sales").toString, "growth" -> (x("sales").toString.toDouble - getMktSalesLstMap(lastMonthYM, market).find(y => y("market")==x("market")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble).toString)
    })

    private val currMktSalesGrowthMap = getCurrMktSalesGrowth

    def getProdSalesGrowth: List[Map[String, String]] = getProdSalesMapByScopeYM("PRODUCT_SALES", ym, market).map(x => {
        val mktSales = currMktSalesGrowthMap.find(m => m("market") == x("market")).get("sales")
        val mktGrowth = currMktSalesGrowthMap.find(m => m("market") == x("market")).get("growth")
        val prodLastMonthSales = getProdSalesMapByScopeYM("PRODUCT_SALES", lastMonthYM, market).find(y => y("market")==x("market") && y("product")==x("product")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val prodGrowth = prodLastMonthSales match {
            case 0.0 => "0.0"
            case lastSales => ((x("sales").toDouble - lastSales)/lastSales).toString
        }
        val EV = mktGrowth match {
            case "0.0" => "0.0"
            case mkt_growth => (prodGrowth.toDouble/mkt_growth.toDouble * 100).toString
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
            "productGrowth" -> prodGrowth,
            "EV" -> EV,
            "prodShare" -> prodShare,
            "prodShareGrowth" -> prodShareGrowth.toString
        )
    })

}
