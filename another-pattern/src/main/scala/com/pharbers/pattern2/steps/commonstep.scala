package com.pharbers.pattern2.steps

import com.pharbers.pattern2.detail.commonresult
import com.pharbers.pattern2.error.commonerror

trait commonstep {
    val module : String
    val methed : String
    val args : commonresult

    def processes(pr : Option[commonresult]) : (Option[commonresult], Option[commonerror])
}
