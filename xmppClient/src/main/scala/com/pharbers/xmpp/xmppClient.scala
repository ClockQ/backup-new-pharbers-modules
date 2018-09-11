package com.pharbers.xmpp

import org.jivesoftware.smack._
import org.jivesoftware.smack.packet.Message

case class xmppClient(host: String, port: Int) extends xmppTrait {

    val config: ConnectionConfiguration = new ConnectionConfiguration(host, port)
    val conn = new XMPPConnection(config)
    try {
        conn.connect()
    } catch {
        case ex: XMPPException => ex.printStackTrace()
    }

    def login(username: String, password: String): Unit = {
        conn.login(username, password)
    }

    def disconnect(): Unit = {
        conn.disconnect()
    }

    def listen(userJID: String)(replyFunc: (Chat, Message) => Unit): Unit = {
        val chatMamager = conn.getChatManager
        chatMamager.createChat(userJID, new MessageListener(){
            override def processMessage(chat: Chat, message: Message): Unit = replyFunc
        })
    }

    def chat(userJID: String, msg: String): Unit = {
        val chatMamager = conn.getChatManager
        val newChat = chatMamager.createChat(userJID, new MessageListener(){
            override def processMessage(chat: Chat, message: Message): Unit ={
                println("Receivedmessage:" + message.getBody)
            }
        })

        try {
            newChat.sendMessage(msg)
        } catch {
            case ex: XMPPException => ex.printStackTrace()
        }
    }
}

