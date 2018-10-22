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

//    var curr_conn_num: Int = 0
    var curr_conn_set: Set[String] = Set.empty

    def apply(applicationName: String = "test-dirver"): phSparkDriver = {

        if (curr_conn_set.contains(applicationName)) return new phSparkDriver(applicationName)

        var wait_count: Int = 0
        while (currConnNum >= sparkParallelNum) {
            println("Please waiting for spark instance")
            Thread.sleep(1000)
            println(s"Wait ${wait_count} seconds!")
            wait_count += 1
            if (wait_count >= waitSeconds) throw new Exception("Error! Wait for spark instance time out!")
        }
//        curr_conn_num += 1
        curr_conn_set += applicationName
        new phSparkDriver(applicationName)
    }

    //TODO：暂时指定以applicationName的Set的size做为当前"job数"的依据.
//    def currConnNum: Int = curr_conn_num
    def currConnNum: Int = curr_conn_set.size

}

class phSparkDriver(override val applicationName: String) extends spark_conn_trait with spark_managers {

    import phSparkDriver._

    //TODO:调用spark结束任务后需手动释放实例!
    def stopCurrConn: Unit ={
//        curr_conn_num -= 1
        curr_conn_set -= applicationName
    }

}
