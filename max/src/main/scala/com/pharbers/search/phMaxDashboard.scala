package com.pharbers.search

import com.pharbers.search.modules._

case class phMaxCompanyDashboard(company: String, ym: String) extends phMaxDashboardCompanyModule
case class phMaxNativeDashboard(company: String, ym: String, market: String) extends  phMaxDashboardNationModule
case class phMaxProvinceDashboard(company: String, ym: String, market: String, province : String = "all") extends  phMaxDashboardProvinceModule
case class phMaxCityDashboard(company: String, ym: String, market: String, city : String = "all") extends  phMaxDashboardCityModule