package com.pharbers.channel

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Props}
import com.pharbers.ErrorCode.getErrorCodeByName
import com.pharbers.builder.phBuilder
import com.pharbers.channel.chanelImpl.responsePusher
import com.pharbers.channel.doJobActor2.{msg_doCalc2, msg_doKill2, msg_doPanel2, msg_doYmCalc2}
import com.pharbers.common.algorithm.alTempLog
import com.pharbers.pattern2.detail.PhMaxJob
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

object doJobActor2 {
    def name = "doJob2"
    def props: Props = Props[doJobActor2]

    case class msg_doYmCalc2(job : PhMaxJob)
    case class msg_doPanel2(job : PhMaxJob)
    case class msg_doCalc2(job : PhMaxJob)
    case class msg_doKill2(job : PhMaxJob)
}

class doJobActor2 extends Actor with ActorLogging {
    implicit val acc: Actor = this

    override def receive: Receive = {
        case msg_doYmCalc2(jv) => doYmCalc(jv)
        case msg_doPanel2(jv) => // doPanel(jv)
        case msg_doCalc2(jv) => // doCalc(jv)
        case msg_doKill2(jv) => //doKill(jv)
        case _ => ???
    }

    def sendMessage(str: String, str1: String, str2: String, str3: String, value: JsValue): Unit = {
        // TODO :
    }

    def sendError(str: String, str1: String, str2: String, value: JsValue): Unit = {
        // TODO :
    }

    def doYmCalc(jv: PhMaxJob): Unit = {

        val company = jv.company_id //(jv \ "company_id").asOpt[String].get
        val user = jv.user_id //(jv \ "user_id").asOpt[String].get
        val args = jv.args //getArgs2Map(jv)

        try{
            alTempLog(s"doYmCalc, company is = $company, user is = $user")
            sendMessage(company, user, "ymCalc", "start", toJson(Map("progress" -> toJson("0"))))

            val ymLst = phBuilder(company, user, args("job_id")).set(args).doCalcYM()

            alTempLog("计算月份完成, result = " + ymLst)
            sendMessage(company, user, "ymCalc", "done", toJson(Map("progress" -> toJson("100"), "content" -> toJson(Map("ymList" -> ymLst)))))
//            responsePusher().callJobResponse(Map("job_id" -> args("job_id")), "done")(jv)// send Kafka message
        } catch {
            case ex: Exception => sendError(company, user, "ymCalc", toJson(Map("code" -> toJson(getErrorCodeByName(ex.getMessage)), "message" -> toJson(ex.getMessage))))
        }
    }
//
//    def doPanel(jv: PhMaxJob): Unit = {
//        val company = jv.company_id // (jv \ "company_id").asOpt[String].get
//        val user = jv.user_id //(jv \ "user_id").asOpt[String].get
//        val args = jv.args // getArgs2Map(jv)
//
//        try{
//            alTempLog(s"doPanel, company is = $company, user is = $user")
//            sendMessage(company, user, "panel", "start", toJson(Map("progress" -> toJson("0"))))
//
//            phBuilder(company, user, args("job_id")).set(args).doPanel()
//
//            alTempLog("生成panel完成")
//            sendMessage(company, user, "panel", "done", toJson(Map("progress" -> toJson("100"), "content" -> toJson(Map("panel" -> toJson(args("job_id")))))))
//            responsePusher().callJobResponse(Map("job_id" -> args("job_id")), "done")(jv)// send Kafka message
//        } catch {
//            case ex: Exception => sendError(company, user, "panel", toJson(Map("code" -> toJson(getErrorCodeByName(ex.getMessage)), "message" -> toJson(ex.getMessage))))
//        }
//    }
//
//    def doCalc(jv: PhMaxJob): Unit = {
//        val company = jv.company_id //(jv \ "company_id").asOpt[String].get
//        val user = jv.user_id //(jv \ "user_id").asOpt[String].get
//        val job_id = jv.args("job_id").toString //getArgs2Map(jv)("job_id")
//
//        try {
//            alTempLog(s"doCalc, company is = $company, user is = $user")
//            sendMessage(company, user, "calc", "start", toJson(Map("progress" -> toJson("0"))))
//
//            phBuilder(company, user, job_id).doMax()
//
//            alTempLog("计算完成")
//            sendMessage(company, user, "calc", "done", toJson(Map("progress" -> toJson("100"), "content" -> toJson(Map("calc" -> toJson(job_id))))))
//            responsePusher().callJobResponse(Map("job_id" -> job_id), "done")(jv)// send Kafka message
//        } catch {
//            case ex: Exception => sendError(company, user, "calc", toJson(Map("code" -> toJson(getErrorCodeByName(ex.getMessage)), "message" -> toJson(ex.getMessage))))
//        }
//    }
}
