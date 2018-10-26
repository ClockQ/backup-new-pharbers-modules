package com.pharbers.pattern2.entry

import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await

import scala.concurrent.duration._
import akka.actor.{ActorSystem, Props}
import com.pharbers.pattern2.Gateway
import com.pharbers.pattern2.common.excute
import com.pharbers.pattern2.detail.commonresult
import pattern.manager.SequenceSteps

object DispatchEntry {
    def apply()(implicit akkasys : ActorSystem) = new DispatchEntry
}

class DispatchEntry (implicit akkasys : ActorSystem) {
    implicit val t = Timeout(5 hours)

    def commonExcution(msr : SequenceSteps) : commonresult = {
        val act = akkasys.actorOf(Props[Gateway])
        val r = act ? excute(msr)
        Await.result(r.mapTo[commonresult], t.duration)
    }
}
