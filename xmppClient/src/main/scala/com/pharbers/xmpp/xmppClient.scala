package com.pharbers.xmpp

import org.jivesoftware.smack._
import org.jivesoftware.smack.packet.Message

class xmppClient {
    val config: ConnectionConfiguration = new ConnectionConfiguration("192.168.100.172", 5222)
    val conn = new XMPPConnection(config)
    try {
        conn.connect()
    } catch {
        case ex: XMPPException => ex.printStackTrace()
    }
    //登录
    conn.login("cui", "cui")
    val chatMamager = conn.getChatManager
    val newChat = chatMamager.createChat("jeorch@localhost", new MessageListener(){
        override def processMessage(chat: Chat, message: Message): Unit ={
            println("Receivedmessage:" + message.getBody)
        }
    })
    
    try {
        newChat.sendMessage("Hello world!")
    } catch {
        case ex: XMPPException => ex.printStackTrace()
    }
    //获取用户
//    val roster: Roster = conn.getRoster
//    val entries: util.Collection[RosterEntry] = roster.getEntries
//    println(entries)
//    val x = entries.iterator()
//    val i = entries.size()
//    var n = 1
//    while(x.hasNext){
//        val entry: RosterEntry = x.next()
//        println(entry)
//    }
    //关闭连接
    Thread.sleep(20000)
    conn.disconnect()
}
