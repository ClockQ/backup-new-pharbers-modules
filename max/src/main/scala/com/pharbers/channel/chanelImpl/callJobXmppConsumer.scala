package com.pharbers.channel.chanelImpl

import akka.actor.ActorSystem
import akka.util.Timeout
import com.pharbers.channel.callJobRequestModule2
import com.pharbers.pattern2.detail.{PhMaxJob, commonresult}
import com.pharbers.pattern2.entry.DispatchEntry
import com.pharbers.xmpp.xmppTrait
import pattern.manager.SequenceSteps
import io.circe.syntax._
import com.pharbers.macros._
import com.pharbers.jsonapi.model._
import com.pharbers.macros.convert.jsonapi.JsonapiMacro._
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport

import scala.concurrent.duration._

class callJobXmppConsumer(context : ActorSystem) extends xmppTrait with CirceJsonapiSupport {
    implicit val t: Timeout = 10 minutes
    val entry = DispatchEntry()(context)

    override val encodeHandler: commonresult => String = { obj =>
        toJsonapi(obj).asJson.noSpaces
    }

    override val decodeHandler: String => commonresult = { str =>
        val json_data = parseJson(str)
        val jsonapi = decodeJson[RootObject](json_data)
        formJsonapi[PhMaxJob](jsonapi)
    }

    override val consumeHandler: String => String = { input =>
        val obj = decodeHandler(input)
        val reVal = entry.commonExcution(
            SequenceSteps(callJobRequestModule2(obj) :: Nil, None))
        encodeHandler(reVal)
    }
}
