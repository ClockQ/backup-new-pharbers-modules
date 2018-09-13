package com.pharbers.xmpp

import java.util.function.Consumer

import com.pharbers.baseModules.PharbersInjectModule
import org.jivesoftware.smack._
import org.jivesoftware.smack.packet.Message
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinPool

object xmppClient {
    def props(handler : xmppTrait) = Props(new xmppClient(handler))
    var a : ActorRef = null

    def startLocalClient(as : ActorSystem, handler : xmppTrait) = {
        if (a == null) {
            a = as.actorOf(props(handler), name = "xmpp")
            println("=====> actor address : " + a.path.toString)
            a ! "start"
        }

        a.path.address.toString
    }
}

class xmppClient(val handler : xmppTrait) extends PharbersInjectModule with Actor with ActorLogging {

    override val id: String = "xmpp-module"
    override val configPath: String = "pharbers_config/xmpp_manager.xml"
    override val md = "xmpp-host" :: "xmpp-port" :: "xmpp-user" ::
        "xmpp-pwd" :: "xmpp-listens" :: "xmpp-report" :: "xmpp-pool-num" :: Nil

    val xmpp_host = config.mc.find(p => p._1 == "xmpp-host").get._2.toString
    val xmpp_port = config.mc.find(p => p._1 == "xmpp-port").get._2.toString.toInt
    val xmpp_user = config.mc.find(p => p._1 == "xmpp-user").get._2.toString
    val xmpp_pwd = config.mc.find(p => p._1 == "xmpp-pwd").get._2.toString

    val xmpp_listens = config.mc.find(p => p._1 == "xmpp-listens").get._2.toString.split("#")
    val xmpp_report = config.mc.find(p => p._1 == "xmpp-report").get._2.toString.split("#")

    val xmpp_pool_num = config.mc.find(p => p._1 == "xmpp-pool-num").get._2.toString.toInt

    val xmpp_receiver = context.actorOf(RoundRobinPool(xmpp_pool_num ).props(xmppReceiver.props(handler)), name = "xmpp-receiver")
    lazy val xmpp_config : ConnectionConfiguration = new ConnectionConfiguration(xmpp_host, xmpp_port)
    lazy val (conn, cm) : (XMPPConnection, ChatManager) = {
        try {
            val conn = new XMPPConnection(xmpp_config)
            conn.connect()
            conn.login(xmpp_user, xmpp_pwd)
            (conn, conn.getChatManager)
        } catch {
            case ex: XMPPException => ex.printStackTrace(); (null, null)
        }
    }

    def startXmpp = {
        cm.addChatListener(new ChatManagerListener {
            override def chatCreated(chat: Chat, b: Boolean): Unit = {
                println("==========> chat created !!!")
                if (!chat.getListeners.isEmpty) return

                chat.addMessageListener(new MessageListener {
                    override def processMessage(chat: Chat, message: Message): Unit = {
                        println("=======> receiving message:" + message.getBody)
                        println("=======> message from :" + chat.getParticipant)
                        xmpp_receiver ! (chat.getParticipant.substring(0, chat.getParticipant.indexOf("/")), message.getBody)
                    }
                })
            }
        })
    }

    def broadcastXmppMsg(reJson : String) = {
        xmpp_report.foreach { userJID =>
            cm.createChat(userJID, null).sendMessage(reJson)
        }
    }

    def stopXmpp = {
        conn.disconnect()
    }

    override def receive: Receive = {
        case "start" => startXmpp
        case _ => ???
    }
}
