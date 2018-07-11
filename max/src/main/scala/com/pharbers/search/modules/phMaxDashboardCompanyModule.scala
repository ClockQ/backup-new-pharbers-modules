package com.pharbers.search.modules

trait phMaxDashboardCompanyModule extends phMaxDashboardCommon {

    def getCurrMonthCompanySales: Double = getSalesByScopeYM("NATION_COMPANY_SALES", ym)

    def getListMonthCompanySales: List[Map[String, String]] = (dashboardYM :: getLastSeveralMonthYM(dashboardMonth.toInt, dashboardYM)).map(x => Map("ym" -> x, "sales" -> getSalesByScopeYM("NATION_COMPANY_SALES", x).toString))

    def getCurrFullYearCompanySales: Double = getSalesByScopeYM("NATION_COMPANY_SALES", dashboardYear)

    def getCurrYearCompanySalesAvg: Double = getSalesByScopeYM("NATION_COMPANY_SALES", dashboardYear)/dashboardMonth.toInt

    def getCompanyYearOnYear: Double = getSalesGrowthByScopeYM("NATION_COMPANY_SALES", lastYearYM)

    def getCompanyMonthOnMonth: Double = getSalesGrowthByScopeYM("NATION_COMPANY_SALES", lastMonthYM)

    def getMktCurrSalesGrowth: List[Map[String, String]] = getMktSalesMapByYM(ym).map(x => {
        Map("market" -> x("market").toString, "sales" -> x("sales").toString, "growth" -> (x("sales").toString.toDouble - getMktSalesMapByYM(lastMonthYM).find(y => y("market")==x("market")).get("sales").toString.toDouble).toString)
    })

    private val mktCurrSalesGrowthMap = getMktCurrSalesGrowth

    def getCompanyProdCurrSalesGrowth: List[Map[String, String]] = getProdSalesMapByScopeYM("PRODUCT_COMPANY_SALES", ym).map(x => {
        val mktSales = mktCurrSalesGrowthMap.find(m => m("market") == x("market")).get("sales")
        val mktGrowth = mktCurrSalesGrowthMap.find(m => m("market") == x("market")).get("growth")
        val prodLastMonthSales = getProdSalesMapByScopeYM("PRODUCT_COMPANY_SALES", lastMonthYM).find(y => y("market")==x("market") && y("product")==x("product")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val prodLastSeasonSales = getProdSalesMapByScopeYM("PRODUCT_COMPANY_SALES", lastSeasonYM).find(y => y("market")==x("market") && y("product")==x("product")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val prodLastYearSales = getProdSalesMapByScopeYM("PRODUCT_COMPANY_SALES", lastYearYM).find(y => y("market")==x("market") && y("product")==x("product")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val prodGrowth = ((x("sales").toDouble - prodLastMonthSales)/prodLastMonthSales).toString
        val EV = prodGrowth.toDouble/mktGrowth.toDouble * 100
        val companyProdShare = x("sales").toDouble/mktSales.toDouble
        val companyProdShareGrowth = (prodGrowth.toDouble + 1)/(mktGrowth.toDouble + 1) - 1
        val contribution = x("sales").toDouble/getCurrMonthCompanySales
        val lastMonthContribution = prodLastMonthSales/getSalesByScopeYM("NATION_COMPANY_SALES", lastMonthYM)
        val lastSeasonContribution = prodLastSeasonSales/getSalesByScopeYM("NATION_COMPANY_SALES", lastSeasonYM)
        val lastYearContribution = prodLastYearSales/getSalesByScopeYM("NATION_COMPANY_SALES", lastYearYM)
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

    def getCompanyFastestSaleGrowingProd: String = companyProdCurrSalesGrowthMap.maxBy(x => x("productGrowth").toString.toDouble) match {
        case m if m("productGrowth").toString.toDouble < 0 => "无"
        case m => m("product").toString
    }

    def getCompanyFastestSaleDeclineProd: String = companyProdCurrSalesGrowthMap.minBy(x => x("productGrowth").toString.toDouble) match {
        case m if m("productGrowth").toString.toDouble > 0 => "无"
        case m => m("product").toString
    }

    def getCompanyFastestShareGrowingProd: String = companyProdCurrSalesGrowthMap.maxBy(x => x("companyProdShareGrowth").toString.toDouble) match {
        case m if m("companyProdShareGrowth").toString.toDouble < 0 => "无"
        case m => m("product").toString
    }

    def getCompanyFastestShareDeclineProd: String = companyProdCurrSalesGrowthMap.minBy(x => x("companyProdShareGrowth").toString.toDouble) match {
        case m if m("companyProdShareGrowth").toString.toDouble > 0 => "无"
        case m => m("product").toString
    }

}
