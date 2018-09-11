package com.pharbers.xmpp

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.packet.Message

/**
  * @ ProjectName pharbers-xmppClient.com.pharbers.xmpp.xmppTrait
  * @ author jeorch
  * @ date 18-9-11
  * @ Description: TODO
  */
trait xmppTrait {
    val host: String
    val port: Int

    def login(username: String, password: String): Unit
    def listen(userJID: String)(replyFunc: (Chat, Message) => Unit): Unit
    def disconnect(): Unit

}
