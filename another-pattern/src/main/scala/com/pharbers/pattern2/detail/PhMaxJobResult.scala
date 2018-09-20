package com.pharbers.pattern2.detail

class PhMaxJobResult extends commonresult {
    var user_id : String = ""
    var company_id : String = ""
    var job_id : String = ""
    var call : String = ""
    
    /**
      * message stand for result hdfs path
      * percentage [0.0, 1.0]
      */
    var message : String = ""
    var percentage : Double = 0.0F
}
