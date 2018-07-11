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

    def getListMonthTrend: List[Map[String, String]] = (dashboardYM :: getLastSeveralMonthYM(dashboardMonth.toInt, dashboardYM)).map(x => {
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

}
