package com.pharbers.xmpp

import akka.actor.{Actor, ActorLogging, Props}

object xmppReceiver {
    def apply = Props[xmppReceiver]
}

class xmppReceiver extends Actor with ActorLogging {
    override def receive: Receive = ???
}
