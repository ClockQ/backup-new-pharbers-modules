package com.pharbers.panel.nhwa

import java.util.UUID

import akka.actor.Actor
import com.pharbers.channel.util.sendEmTrait
import com.pharbers.common.algorithm.max_path_obj
import com.pharbers.pactions.excel.input.{PhExcelXLSXCommonFormat, PhXlsxThirdSheetFormat}
import com.pharbers.pactions.actionbase._
import com.pharbers.pactions.generalactions._
import com.pharbers.pactions.generalactions.memory.phMemoryArgs
import com.pharbers.pactions.jobs._
import com.pharbers.panel.common.{phPanelInfo2Redis, phSavePanelJob}
import com.pharbers.panel.nhwa.format._
import org.apache.spark.listener
import org.apache.spark.listener.addListenerAction
import org.apache.spark.listener.progress.sendMultiProgress

/**
  * 1. read 2017年未出版医院名单.xlsx
  * 2. read universe_麻醉市场_online.xlsx
  * 3. read 匹配表
  * 4. read 补充医院
  * 5. read 通用名市场定义, 读第三页
  * 6. read CPA文件第一页
  * 7. read CPA文件第二页
  **/
case class phNhwaPanelJob(args: Map[String, String])(implicit _actor: Actor) extends sequenceJobWithMap {
    override val name: String = "phNhwaPanelJob"
    
    val temp_name: String = UUID.randomUUID().toString
    val temp_dir: String = max_path_obj.p_cachePath + temp_name + "/"
    val match_dir: String = max_path_obj.p_matchFilePath
    val source_dir: String = max_path_obj.p_clientPath
    
    val not_published_hosp_file: String = match_dir + args("not_published_hosp_file")
    val universe_file: String = match_dir + args("universe_file")
    val product_match_file: String = match_dir + args("product_match_file")
    val fill_hos_data_file: String = match_dir + args("fill_hos_data_file")
    val markets_match_file: String = match_dir + args("markets_match_file")
    val cpa_file: String = source_dir + args("cpa")
    val hosp_ID_file: String = match_dir + args("hosp_ID")
    val not_arrival_hosp_file: String = match_dir + args("not_arrival_hosp_file")
    
    lazy val ym: String = args("ym")
    lazy val mkt: String = args("mkt")
    lazy val user: String = args("user_id")
    lazy val job_id: String = args("job_id")
    lazy val company: String = args("company_id")
    lazy val p_total: Double = args("p_total").toDouble
    lazy val p_current: Double = args("p_current").toDouble
    
    implicit val companyArgs: phMemoryArgs = phMemoryArgs(company)
    implicit val mp: (sendEmTrait, Double, String) => Unit = sendMultiProgress(company, user, "panel")(p_current, p_total).multiProgress
    
    /**
      * 1. read 未出版医院文件
      */
    val loadNotPublishedHosp: sequenceJob = new sequenceJob {
        override val name = "not_published_hosp_file"
        override val actions: List[pActionTrait] = readCsvAction(not_published_hosp_file) :: Nil
    }
    
    /**
      * 2. read hosp_ID file
      */
    val load_hosp_ID_file: sequenceJob = new sequenceJob {
        override val name: String = "hosp_ID_file"
        override val actions: List[pActionTrait] = readCsvAction(hosp_ID_file) :: Nil
    }
    
    /**
      * 3. read product match file
      */
    val loadProductMatchFile: sequenceJob = new sequenceJob {
        override val name = "product_match_file"
        val actions: List[pActionTrait] = readCsvAction(product_match_file) :: Nil
    }
    
    /**
      * 4. read full hosp file
      */
    val loadFullHospFile: sequenceJob = new sequenceJob {
        override val name = "full_hosp_file"
        val actions: List[pActionTrait] = readCsvAction(fill_hos_data_file) ::Nil
    }
    
    /**
      * 5. read market match file
      */
    val loadMarketMatchFile: sequenceJob = new sequenceJob {
        override val name = "markets_match_file"
        val actions: List[pActionTrait] = readCsvAction(markets_match_file) :: Nil
    }
    
    /**
      * 6. read CPA文件第一页
      */
    val readCpa: sequenceJob = new sequenceJob {
        override val name = "cpa"
        override val actions: List[pActionTrait] = readCsvAction(cpa_file) :: Nil
    }
    
    /**
      * 7. read CPA文件第二页
      */
    val readNotArrivalHosp: sequenceJob = new sequenceJob {
        override val name = "not_arrival_hosp_file"
        override val actions: List[pActionTrait] = readCsvAction(not_arrival_hosp_file) :: Nil
    }
    
    val df = MapArgs(
        Map(
            "ym" -> StringArgs(ym),
            "mkt" -> StringArgs(mkt),
            "user" -> StringArgs(user),
            "name" -> StringArgs(temp_name),
            "company" -> StringArgs(company),
            "job_id" -> StringArgs(job_id)
        )
    )
    
    override val actions: List[pActionTrait] = {
        jarPreloadAction() ::
                setLogLevelAction("ERROR") ::
                addListenerAction(listener.MaxSparkListener(0, 10)) ::
                loadNotPublishedHosp ::
                addListenerAction(listener.MaxSparkListener(11, 20)) ::
                load_hosp_ID_file ::
                addListenerAction(listener.MaxSparkListener(21, 30)) ::
                loadProductMatchFile ::
                addListenerAction(listener.MaxSparkListener(31, 40)) ::
                loadFullHospFile ::
                addListenerAction(listener.MaxSparkListener(41, 50)) ::
                loadMarketMatchFile ::
                addListenerAction(listener.MaxSparkListener(51, 60)) ::
                readCpa ::
                readNotArrivalHosp ::
                addListenerAction(listener.MaxSparkListener(61, 90)) ::
                phNhwaPanelConcretJob(df) ::
                phSavePanelJob(df) ::
                addListenerAction(listener.MaxSparkListener(91, 99)) ::
                phPanelInfo2Redis(df) ::
                Nil
    }
    
}