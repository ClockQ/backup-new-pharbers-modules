package com.pharbers.spark

import com.pharbers.baseModules.PharbersInjectModule
import com.pharbers.spark.session.spark_conn_trait

/**
  * Created by clock on 18-2-26.
  */
object phSparkDriver  extends PharbersInjectModule {

    override val id: String = "spark-config"
    override val configPath: String = "pharbers_config/spark-config.xml"
    override val md = "parallel-number" :: "wait-seconds" ::  Nil

    protected val sparkParallelNum: Int = config.mc.find(p => p._1 == "parallel-number").get._2.toString.toInt
    protected val waitSeconds: Int = config.mc.find(p => p._1 == "wait-seconds").get._2.toString.toInt

    var curr_conn_num: Int = 0

    def apply(_applicationName: String = "test-dirver"): phSparkDriver = {
        var wait_count: Int = 0
        while (currConnNum >= sparkParallelNum) {
            println("Please waiting for spark instance")
            Thread.sleep(1000)
            wait_count += 1
            if (wait_count >= waitSeconds) throw new Exception("Error! Wait for spark instance time out!")
        }
        curr_conn_num += 1
        new phSparkDriver(_applicationName)
    }

    def currConnNum: Int = curr_conn_num

}

class phSparkDriver(override val applicationName: String) extends spark_conn_trait with spark_managers {

    import phSparkDriver._

    //TODO:调用spark后需手动释放实例!
    def stopCurrConn: Unit ={
        curr_conn_num -= 1
    }

}
