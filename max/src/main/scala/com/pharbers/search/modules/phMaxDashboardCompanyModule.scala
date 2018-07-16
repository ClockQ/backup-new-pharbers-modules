package com.pharbers.search.modules

trait phMaxDashboardCompanyModule extends phMaxDashboardCommon {

    def getCurrMonthCompanySales: Double = getSalesByScopeYM("NATION_COMPANY_SALES", ym)

    def getListMonthCompanySales: List[Map[String, String]] = (dashboardEndYM :: getLastSeveralMonthYM(dashboardMonth.toInt, dashboardEndYM)).map(x => Map("ym" -> x, "sales" -> getSalesByScopeYM("NATION_COMPANY_SALES", x).toString)).reverse

    def getCurrFullYearCompanySales: Double = getSalesByScopeYM("NATION_COMPANY_SALES", dashboardYear)

    def getCurrYearCompanySalesAvg: Double = getSalesByScopeYM("NATION_COMPANY_SALES", dashboardYear)/dashboardMonth.toInt

    def getCompanyYearOnYear: Double = getSalesGrowthByScopeYM("NATION_COMPANY_SALES", lastYearYM)

    def getCompanyMonthOnMonth: Double = getSalesGrowthByScopeYM("NATION_COMPANY_SALES", lastMonthYM)

    def getMktCurrSalesGrowth: List[Map[String, String]] = getMktSalesLstMap(ym).map(x => {
        Map("market" -> x("market").toString, "sales" -> x("sales").toString, "growth" -> (x("sales").toString.toDouble - getMktSalesLstMap(lastMonthYM).find(y => y("market")==x("market")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble).toString)
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

        val lastMonthContribution = getSalesByScopeYM("NATION_COMPANY_SALES", lastMonthYM) match {
            case sale if sale == 0.0 => 0.0
            case sale => prodLastMonthSales/sale
        }
        val lastSeasonContribution = getSalesByScopeYM("NATION_COMPANY_SALES", lastSeasonYM) match {
            case sale if sale == 0.0 => 0.0
            case sale => prodLastSeasonSales/sale
        }
        val lastYearContribution = getSalesByScopeYM("NATION_COMPANY_SALES", lastYearYM) match {
            case sale if sale == 0.0 => 0.0
            case sale => prodLastYearSales/sale
        }
        Map(
            "product" -> getProdFromProdCorp(x("product")),
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

    def getFastestGrowingMkt: Map[String, String] = mktCurrSalesGrowthMap.maxBy(x => x("growth").toDouble) match {
        case m if m("growth").toDouble <= 0 => Map.empty
        case m => m
    }

    def getCompanyFastestSaleGrowingProd: Map[String, String] = companyProdCurrSalesGrowthMap.maxBy(x => x("productGrowth").toDouble) match {
        case m if m("productGrowth").toDouble <= 0 => Map.empty
        case m => m
    }

    def getCompanyFastestSaleDeclineProd: Map[String, String] = companyProdCurrSalesGrowthMap.minBy(x => x("productGrowth").toDouble) match {
        case m if m("productGrowth").toDouble >= 0 => Map.empty
        case m => m
    }

    def getCompanyMaxShareProd: Map[String, String] = companyProdCurrSalesGrowthMap.maxBy(x => x("companyProdShare").toDouble)

    def getCompanyFastestShareGrowingProd: Map[String, String] = companyProdCurrSalesGrowthMap.maxBy(x => x("companyProdShareGrowth").toDouble) match {
        case m if m("companyProdShareGrowth").toDouble <= 0 => Map.empty
        case m => m
    }

    def getCompanyFastestShareDeclineProd: Map[String, String] = companyProdCurrSalesGrowthMap.minBy(x => x("companyProdShareGrowth").toDouble) match {
        case m if m("companyProdShareGrowth").toDouble >= 0 => Map.empty
        case m => m
    }

}
