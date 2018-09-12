//package com.pharbers
//
//import java.util.Scanner
//import com.pharbers.xmpp.xmppClient
//
//
///**
//  * @ ProjectName pharbers-xmppClient.com.pharbers.xmppClientTest
//  * @ author jeorch
//  * @ date 18-9-11
//  * @ Description: TODO
//  */
//object main extends App {
//    val host = "192.168.100.172"
//    val port = 5222
//    val username = "admin"
//    val password = "196125"
//    val userJID = "jeorch@localhost"
//    val xc = xmppClient(host, port)
//    xc.login(username, password)
//
//    val scanner = new Scanner(System.in)
//    while (scanner.hasNext()) {
//        xc.chat(userJID, scanner.next())
//    }
//    xc.disconnect()
//}
