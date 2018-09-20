package com.pharbers

import akka.actor.{Actor, Props}
import com.pharbers.pactions.actionbase.{MapArgs, StringArgs}
import org.scalatest.FunSuite
import com.pharbers.sparkContexttest.xlsxToCsv

object xlsxToCsvHeader {
    def props(): Props = Props[xlsxToCsvHeader]
    case class testJob(args: Map[String, String])
}

class xlsxToCsvHeader extends Actor {
    override def receive: Receive = {
        case xlsxToCsvHeader.testJob(args) =>
            sender() ! xlsxToCsv(args)(this)
                    .perform(MapArgs(Map().empty))
                    .asInstanceOf[MapArgs]
                    .get("checkResult")
                    .asInstanceOf[StringArgs].get
    }
}