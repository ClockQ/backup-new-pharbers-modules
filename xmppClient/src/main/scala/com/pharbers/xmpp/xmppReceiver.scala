package com.pharbers.xmpp

import akka.actor.{Actor, ActorLogging, Props}

object xmppReceiver {
    def props(handler : xmppTrait) = Props(new xmppReceiver(handler))
}

class xmppReceiver(handler : xmppTrait) extends Actor with ActorLogging {
    override def receive: Receive = {
        case (who : String, body : AnyRef) => {
            println(s"$who post a message")
            println(s"message content is $body")
            handler.consumeHandler(body.toString)
//            parent.broadcastXmppMsg(result)
        }
        case _ => ???
    }
}
