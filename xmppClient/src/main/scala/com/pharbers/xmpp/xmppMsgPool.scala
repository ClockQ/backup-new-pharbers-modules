package com.pharbers.xmpp

import akka.actor.{Actor, ActorLogging, Props}
import com.pharbers.pattern2.detail.commonresult

object xmppMsgPool {
    def props(handler : xmppTrait) = Props(new xmppMsgPool(handler))
}

class xmppMsgPool(handler : xmppTrait) extends Actor with ActorLogging {
    override def receive: Receive = {
        case (who : String, body : AnyRef) => handler.consumeHandler(body.toString)
        case (jr : commonresult, cli : xmppClient) => cli.broadcastXmppMsg(handler.encodeHandler(jr))
        case _ => ???
    }
}
