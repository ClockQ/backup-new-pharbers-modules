package org.apache.spark.listener.progress

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.util.Timeout
import com.pharbers.channel.util.sendTrait
import play.api.libs.json.Json.toJson
import com.pharbers.common.algorithm.alTempLog
import com.pharbers.pattern2.detail.PhMaxJob
import scala.concurrent.duration._

import scala.concurrent.Await

sealed trait sendProgressTrait {

    implicit val act: Actor

    def sendProcess(company: String, user_id: String, call: String, job_id: String, process: Double): Unit = {

        implicit val resolveTimeout = Timeout(5 seconds)
        val a = act.context.actorSelection("akka://maxActor/user/xmpp")

        /**
          * make an instance of PhMaxJobResult
          */
        val result = new PhMaxJob
        result.user_id = user_id
        result.company_id = company
        result.call = call
        result.job_id = job_id
        result.percentage = process.toInt

        a ! result
    }
}

case class sendSingleProgress(company: String, user: String) {

    implicit val singleProgress: (sendTrait, Double, String) => Unit = { (st, progress, tag) =>
        st.sendMessage(company, user, "ymCalc", "ing", toJson(Map("progress" -> toJson(progress))))
        alTempLog(s"$company $user current $tag progress = " + progress.toInt)
    }
}


case class sendMultiProgress(company: String, user: String, stage: String)
                            (p_current: Double, p_total: Double) {

    var previousProgress = 0
    implicit val multiProgress: (sendTrait, Double, String) => Unit = { (st, progress, tag) =>
        val currentprogress = p_total match {
            case d: Double if d < 1 => 0
            case _ => ((p_current - 1) / p_total * 100 + progress / p_total).toInt
        }

        if(currentprogress > previousProgress){
            st.sendMessage(company, user, stage, "ing", toJson(Map("progress" -> toJson(currentprogress))))
            alTempLog(s"$company $user current $tag progress = " + currentprogress)
            previousProgress = currentprogress
        }
    }
}

case class sendXmppSingleProgress(company: String, user: String, stage: String, job_id: String)(implicit _acc: Actor) extends sendProgressTrait {

    override implicit val act: Actor = _acc

    implicit val singleProgress: (sendTrait, Double, String) => Unit = { (st, progress, tag) =>
        sendProcess(company, user, stage, job_id, progress)
        alTempLog(s"$company $user current $tag progress = " + progress.toInt)
    }
}

case class sendXmppMultiProgress(company: String, user: String, stage: String, job_id: String)
                                (p_current: Double, p_total: Double)
                                (implicit _acc: Actor) extends sendProgressTrait {

    override implicit val act: Actor = _acc

    var previousProgress = 0
    implicit val multiProgress: (sendTrait, Double, String) => Unit = { (st, progress, tag) =>
        val currentprogress = p_total match {
            case d: Double if d < 1 => 0
            case _ => ((p_current - 1) / p_total * 100 + progress / p_total).toInt
        }

        if(currentprogress > previousProgress){
            sendProcess(company, user, stage, job_id, currentprogress)
            alTempLog(s"xmpp msg => $company $user current $tag progress = " + currentprogress)
            previousProgress = currentprogress
        }
    }
}
