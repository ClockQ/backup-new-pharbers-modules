package com.pharbers.search.modules

trait phMaxDashboardProvinceModule extends phMaxDashboardCommon {

    val market : String
    val province : String

    def getProvinceSalesLstMap(temp_ym: String): List[Map[String, String]] = getProvinceSalesMapByScopeYM("PROVINCE_SALES", temp_ym, market).map(x => {

        val lastMonthProvSales = getProvinceSalesMapByScopeYM("PROVINCE_SALES", lastMonthYM, market).find(y => y("market")==x("market") && y("province")==x("province")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val lastYearProvSales = getProvinceSalesMapByScopeYM("PROVINCE_SALES", lastYearYM, market).find(y => y("market")==x("market") && y("province")==x("province")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val provMomGrowth = lastMonthProvSales match {
            case 0.0 => "0.0"
            case lastSales => ((x("sales").toDouble - lastSales)/lastSales).toString
        }
        val provYoyGrowth = lastYearProvSales match {
            case 0.0 => "0.0"
            case lastSales => ((x("sales").toDouble - lastSales)/lastSales).toString
        }

        val companySales = getProvinceSalesMapByScopeYM("PROVINCE_COMPANY_SALES", temp_ym, market).find(y => y("market")==x("market") && y("province")==x("province")).getOrElse(Map("sales" -> "0.0"))("sales")
        val lastMonthCompanySales = getProvinceSalesMapByScopeYM("PROVINCE_COMPANY_SALES", lastMonthYM, market).find(y => y("market")==x("market") && y("province")==x("province")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val lastYearCompanySales = getProvinceSalesMapByScopeYM("PROVINCE_COMPANY_SALES", lastYearYM, market).find(y => y("market")==x("market") && y("province")==x("province")).getOrElse(Map("sales" -> "0.0"))("sales").toDouble
        val companySalesMomGrowth = lastMonthCompanySales match {
            case 0.0 => "0.0"
            case lastSales => ((companySales.toDouble - lastSales)/lastSales).toString
        }
        val companySalesYoyGrowth = lastYearCompanySales match {
            case 0.0 => "0.0"
            case lastSales => ((companySales.toDouble - lastSales)/lastSales).toString
        }

        val companyShare = x("sales") match {
            case sale if sale.toDouble == 0.0 => "0.0"
            case sale => (companySales.toDouble/sale.toDouble).toString
        }
        val lastMonthShare = lastMonthProvSales match {
            case sale if sale.toDouble == 0.0 => "0.0"
            case sale => (lastMonthCompanySales.toDouble/sale.toDouble).toString
        }
        val lastYearShare = lastYearProvSales match {
            case sale if sale.toDouble == 0.0 => "0.0"
            case sale => (lastYearCompanySales.toDouble/sale.toDouble).toString
        }
        val companyShareMomGrowth = lastMonthShare match {
            case last_share if last_share.toDouble == 0.0 => "0.0"
            case last_share => ((companyShare.toDouble - last_share.toDouble)/last_share.toDouble).toString
        }
        val companyShareYoyGrowth = lastYearShare match {
            case last_share if last_share.toDouble == 0.0 => "0.0"
            case last_share => ((companyShare.toDouble - last_share.toDouble)/last_share.toDouble).toString
        }

        val EV = (companySalesMomGrowth.toDouble/provMomGrowth.toDouble).toString

        Map(
            "province" -> x("province"),
            "market" -> x("market"),
            "provinceSales" -> x("sales"),
            "provMomGrowth" -> provMomGrowth,
            "provYoyGrowth" -> provYoyGrowth,
            "companySales" -> companySales,
            "companySalesMomGrowth" -> companySalesMomGrowth,
            "companySalesYoyGrowth" -> companySalesYoyGrowth,
            "companyShare" -> companyShare,
            "companyShareMomGrowth" -> companyShareMomGrowth,
            "companyShareYoyGrowth" -> companyShareYoyGrowth,
            "EV" -> EV
        )
    })

    //TODO:整合成一个排序接口
    private val currMonthProvinceSalesLstMap = getProvinceSalesLstMap(ym)
    private val lastMonthProvinceSalesLstMap = getProvinceSalesLstMap(lastMonthYM)

    def getLastMonthProvSortByKey(key: String) = lastMonthProvinceSalesLstMap.sortBy(m => m(key).toDouble).reverse.zipWithIndex

    def getCurrMonthProvSortByKey(key: String) = currMonthProvinceSalesLstMap.sortBy(m => m(key).toDouble).reverse.zipWithIndex.map(one => {
        val lastMonthProvRank = getLastMonthProvSortByKey(key).find(x => x._1("province") == one._1("province") && x._1("market") == one._1("market")).getOrElse((Map.empty, -1))._2
        val RankChanges = lastMonthProvRank match {
            case -1 => "0"
            case _ => (lastMonthProvRank - one._2).toString
        }
        one._1 ++ Map(s"${key}RankChanges" -> RankChanges, s"${key}Rank" -> (one._2 + 1).toString)
    })

    def getMaxProvinceSalesMap: Map[String, String] = currMonthProvinceSalesLstMap.maxBy(x => x("provinceSales").toString.toDouble)

    def getFastProvinceGrowingMap: Map[String, String] = currMonthProvinceSalesLstMap.maxBy(x => x("provMomGrowth").toString.toDouble) match {
        case m if m("provMomGrowth").toString.toDouble < 0 => Map.empty
        case m => m
    }

    def getMaxCompanySalesMap: Map[String, String] = currMonthProvinceSalesLstMap.maxBy(x => x("companySales").toString.toDouble)

    def getFastCompanySalesGrowingMap: Map[String, String] = currMonthProvinceSalesLstMap.maxBy(x => x("companySalesMomGrowth").toString.toDouble) match {
        case m if m("companySalesMomGrowth").toString.toDouble < 0 => Map.empty
        case m => m
    }

    def getMaxCompanyShareMap: Map[String, String] = currMonthProvinceSalesLstMap.maxBy(x => x("companyShare").toString.toDouble)

    def getFastCompanyShareGrowingMap: Map[String, String] = currMonthProvinceSalesLstMap.maxBy(x => x("companyShareMomGrowth").toString.toDouble) match {
        case m if m("companyShareMomGrowth").toString.toDouble < 0 => Map.empty
        case m => m
    }

    def getCurrProvinceSalesMap: Map[String, String] = currMonthProvinceSalesLstMap.find(m => m("province") == province).getOrElse(Map.empty)

    def getSeveralMonthProvinceSalesMap: List[Map[String, String]] = (dashboardEndYM :: getLastSeveralMonthYM(dashboardMonth.toInt, dashboardEndYM)).map(x => {
        val currProvinceSale = getProvinceSalesMapByScopeYM("PROVINCE_SALES", x, market).find(m => m("province") == province).getOrElse(Map.empty).getOrElse("sales", "0.0")
        val currProvinceCompanySale = getProvinceSalesMapByScopeYM("PROVINCE_COMPANY_SALES", x, market).find(m => m("province") == province).getOrElse(Map.empty).getOrElse("sales", "0.0")
        val currProvinceCompanyShare = currProvinceSale match {
            case sale if sale.toDouble == 0.0 => "0.0"
            case sale => (currProvinceCompanySale.toDouble/sale.toDouble).toString
        }
        Map(
            "ym" -> x,
            "provinceSale" -> currProvinceSale,
            "currProvinceCompanySale" -> currProvinceCompanySale,
            "currProvinceCompanyShare" -> currProvinceCompanyShare
        )
    })

//    val dataKey = filterJobKeySet(allSingleJobKeySet, ym, company, market) match {
//        case Nil => ""
//        case lst => lst.head._4
//    }

    //TODO: 不能满足新的一次计算后的数据进行可视化展示，只能展示历史数据，原因是最新一次计算没有立即存到数据库中。

    def getProvinceTotalSalesByYM(temp_ym: String): String = getSeveralMonthProvinceSalesMap.find(x => x("ym") == temp_ym).get.getOrElse("provinceSale", "0.0")

    def getCurrProvinceProdSaleMapByYM(temp_ym: String): List[Map[String, String]] = filterJobKeySet(allSingleJobKeySet, temp_ym, company, market) match {
        case Nil => List.empty
        case lst => {
            val lastMonthProvinceTotalSales: String = getProvinceTotalSalesByYM(getLastMonthYM(temp_ym))
            getProdSalesByArea("Province", province, lst.head._4).groupBy(x => x("Province")+"##"+x("PRODUCT_NAME")+"##"+x("CORP_NAME")+"##"+x("b2c")).map(x => {
                val ppcb = x._1.split("##")
                Map(
                    "Province" -> ppcb(0),
                    "PRODUCT_NAME" -> ppcb(1),
                    "CORP_NAME" -> ppcb(2),
                    "b2c" -> ppcb(3),
                    "sales" -> x._2.map(m => m("Sales").toDouble).sum.toString,
                    "share" -> (x._2.map(m => m("Sales").toDouble).sum/lastMonthProvinceTotalSales.toDouble).toString
                )
            }).toList
        }
    }

    def getProvinceProdLstMapByYM(temp_ym: String): List[Map[String, String]] = filterJobKeySet(allSingleJobKeySet, temp_ym, company, market) match {
        case Nil => List.empty
        case lst => {
            val temp_last_ym = getLastMonthYM(temp_ym)
            val currMonthProvinceTotalSales: String = getProvinceTotalSalesByYM(temp_ym)
            val lastMonthProvinceTotalSales: String = getProvinceTotalSalesByYM(temp_last_ym)
            val currMonthProvinceSalesGrowth: String = lastMonthProvinceTotalSales match {
                case last_sales if last_sales.toDouble == 0.0 => "0.0"
                case last_sales => ((currMonthProvinceTotalSales.toDouble - last_sales.toDouble)/last_sales.toDouble).toString
            }
            getProdSalesByArea("Province", province, lst.head._4).groupBy(x => x("Province")+"##"+x("PRODUCT_NAME")+"##"+x("CORP_NAME")+"##"+x("b2c")).map(x => {
                val ppcb = x._1.split("##")
                val sales = x._2.map(m => m("Sales").toDouble).sum.toString
                val share = (x._2.map(m => m("Sales").toDouble).sum/currMonthProvinceTotalSales.toDouble).toString
                val lastMonthMap = getCurrProvinceProdSaleMapByYM(temp_last_ym).find(x => x("PRODUCT_NAME") == ppcb(1) && x("CORP_NAME") == ppcb(2)).getOrElse(Map.empty)
                val lastMonthSales = lastMonthMap.getOrElse("sales", "0.0")
                val lastMonthShare = lastMonthMap.getOrElse("share", "0.0")
                val salesGrowth = lastMonthSales match {
                    case last_sales if last_sales.toDouble == 0.0 => "0.0"
                    case last_sales => ((sales.toDouble - last_sales.toDouble)/last_sales.toDouble).toString
                }
                val shareGrowth = lastMonthShare match {
                    case last_share if last_share.toDouble == 0.0 => "0.0"
                    case last_share => ((share.toDouble - last_share.toDouble)/last_share.toDouble).toString
                }
                val EV = currMonthProvinceSalesGrowth match {
                    case prov_growth if prov_growth.toDouble == 0.0 => "0.0"
                    case prov_growth => (salesGrowth.toDouble/prov_growth.toDouble).toString
                }
                Map(
                    "Province" -> ppcb(0),
                    "PRODUCT_NAME" -> ppcb(1),
                    "CORP_NAME" -> ppcb(2),
                    "b2c" -> ppcb(3),
                    "sales" -> sales,
                    "salesGrowth" -> salesGrowth,
                    "share" -> share,
                    "shareGrowth" -> shareGrowth,
                    "EV" -> EV
                )
            }).toList
        }
    }

    //TODO:整合成一个排序接口
    private lazy val currProvinceProdLstMap = getProvinceProdLstMapByYM(ym)
    private lazy val lastProvinceSalesLstMap = getProvinceProdLstMapByYM(lastMonthYM)

    def getLastMonthProvProdSortByKey(key: String) = lastProvinceSalesLstMap.sortBy(m => m(key).toDouble).reverse.zipWithIndex

    def getCurrMonthProvProdSortByKey(key: String) = currProvinceProdLstMap.sortBy(m => m(key).toDouble).reverse.zipWithIndex.map(one => {
        val lastMonthProvRank = getLastMonthProvProdSortByKey(key).find(x => x._1("PRODUCT_NAME") == one._1("PRODUCT_NAME") && x._1("CORP_NAME") == one._1("CORP_NAME")).getOrElse((Map.empty, -1))._2
        val RankChanges = lastMonthProvRank match {
            case -1 => "0"
            case _ => (lastMonthProvRank - one._2).toString
        }
        one._1 ++ Map(s"${key}RankChanges" -> RankChanges, s"${key}Rank" -> (one._2 + 1).toString)
    })


    private lazy val currProvinceCompetingProdLstMap = currProvinceProdLstMap.filter(x => x("b2c").toInt == 0)

    def getCurrProvinceCompetingProdCount = currProvinceCompetingProdLstMap.length

    def getCurrProvinceMaxShareProdMap: Map[String, String] = currProvinceProdLstMap.maxBy(x => x("sales").toDouble)

    def getCurrProvSalesFastGrowingProdMap: Map[String, String] = currProvinceProdLstMap.maxBy(x => x("salesGrowth").toDouble) match {
        case m if m("salesGrowth").toDouble == 0.0 => Map.empty
        case m => m
    }

    def getCurrProvShareFastDeclineProdMap: Map[String, String] = currProvinceProdLstMap.minBy(x => x("shareGrowth").toDouble) match {
        case m if m("shareGrowth").toDouble == 0.0 => Map.empty
        case m => m
    }

    def getCurrProvinceCompetingProdLstMap = currProvinceCompetingProdLstMap

    def getCurrProvinceAllProdLstMap = currProvinceProdLstMap

    def getCurrProvinceSeveralMonthProdeSalesMap: List[Map[String, String]] = (dashboardEndYM :: getLastSeveralMonthYM(dashboardMonth.toInt, dashboardEndYM)).flatMap(x => {
        getCurrProvinceProdSaleMapByYM(x).map(pm => {
            Map(
                "ym" -> x,
                "PRODUCT_NAME" -> pm("PRODUCT_NAME"),
                "sales" -> pm("sales"),
                "share" -> pm("share")
            )
        })
    })

}
