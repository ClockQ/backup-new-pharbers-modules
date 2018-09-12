package com.pharbers.channel.chanelImpl

import akka.actor.{ActorContext, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.pharbers.bmmessages.{CommonModules, MessageRoutes, excute}
import com.pharbers.bmpattern.LogMessage.msg_log
import com.pharbers.bmpattern.ResultMessage.msg_CommonResultMessage
import com.pharbers.bmpattern.RoutesActor
import com.pharbers.channel.callJobRequestMessage.{msg_executeJob, msg_responseJob}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.concurrent.Await
import scala.concurrent.duration._

class callJobXmppConsumer {
//    implicit val t: Timeout = 10 minutes
//
//    def commonExcution(msr: MessageRoutes): Unit = {
//        val act = context.actorOf(Props[RoutesActor])
//        val r = act ? excute(msr)
//        val result = Await.result(r.mapTo[JsValue], t.duration)
//    }
//
//    val consumeHandler : AnyRef => MessageRoutes = { jv =>
////        import com.pharbers.bmpattern.LogMessage.common_log
////        import com.pharbers.bmpattern.ResultMessage.common_result
////        MessageRoutes(msg_log(toJson(Map("method" -> toJson("call job request"))), jv)
////            :: msg_executeJob(jv)
////            :: msg_responseJob(jv)
////            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("as" -> dispatch))))
//    }

    def test(tt : String) : AnyRef = {
        println(tt)
        null
    }
}
