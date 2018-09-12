package com.pharbers.channel

import com.pharbers.pattern2.detail.commonresult
import com.pharbers.pattern2.error.commonerror
import com.pharbers.pattern2.steps.commonstep

case class callJobRequestModule2(override val args : commonresult) extends commonstep {

    override val module: String = "max calc"
    override val methed: String = "call max job"

    override def processes(pr: Option[commonresult]): (Option[commonresult], Option[commonerror]) = {
        println(s"module $module")
        println(s"method $methed")
        println(s"args $args")
        val a = new commonresult {}
        (Some(a), None)
    }
}