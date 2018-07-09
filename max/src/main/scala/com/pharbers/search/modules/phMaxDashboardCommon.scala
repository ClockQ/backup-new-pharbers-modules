package com.pharbers.search.modules

import java.util.Base64

import com.pharbers.search.phMaxSearchTrait

trait phMaxDashboardCommon extends phMaxSearchTrait {

    def getLstKeySales(list: List[String], scope: String): List[Double] = list match {
        case Nil => 0.0 :: Nil
        case lst => lst.map(x => getHistorySalesByRange(scope, x))
    }

    def getLstKeySalesMap(list: List[String], scope: String): List[Map[String, String]] = list match {
        case Nil => List.empty
        case lst => lst.map(x => {
            val mkt = new String(Base64.getDecoder.decode(x)).split("#")(2)
            Map("market" -> mkt, "sales" -> getHistorySalesByRange(scope, x).toString)
        })
    }

    def getLstProductSalesMap(list: List[String], scope: String): List[Map[String, String]] = list match {
        case Nil => List.empty
        case lst => lst.flatMap(x => {
            val mkt = new String(Base64.getDecoder.decode(x)).split("#")(2)
            getAreaSalesByRange(scope, x).map(m => Map("market" -> mkt, "product" -> m("Area"), "sales" -> m("Sales")))
        })
    }

    def filterJobKeySet(jobKeySet: Set[String], ym: String, company: String): List[(String, String, String, String)] = {
        jobKeySet
            .map(singleJobKey => {
                val singleJobInfoArr = new String(Base64.getDecoder.decode(singleJobKey)).split("#")
                (
                    singleJobInfoArr(0),
                    singleJobInfoArr(1),
                    singleJobInfoArr(2),
                    singleJobKey
                )
            })
            .filter(x => x._1 == company)
            .filter(x => x._2.contains(ym))
            .toList
    }

}
