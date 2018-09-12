package com.pharbers.xmpp

import akka.actor.{Actor, ActorLogging, Props}

object xmppReceiver {
    def props(handler : String => Unit) = Props(xmppReceiver.getClass, handler)
}

class xmppReceiver(handler : String => Unit) extends Actor with ActorLogging {
    override def receive: Receive = {
        case (who : String, body : AnyRef) => {
            println(s"$who post a message")
            println(s"message content is $body")
            handler(body.toString)
        }
        case _ => ???
    }
}
