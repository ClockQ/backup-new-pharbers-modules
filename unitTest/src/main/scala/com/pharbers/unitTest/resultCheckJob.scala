package com.pharbers.unitTest

import java.util.UUID
import akka.actor.Actor
import com.pharbers.unitTest.action._
import com.pharbers.pactions.generalactions._
import com.pharbers.common.algorithm.max_path_obj
import com.pharbers.pactions.jobs.{sequenceJob, sequenceJobWithMap}
import com.pharbers.pactions.actionbase.{MapArgs, StringArgs, pActionTrait}

case class resultCheckJob(args: Map[String, String])
                         (implicit _actor: Actor) extends sequenceJobWithMap {
    override val name: String = "result_check_job"
    
    val df = MapArgs(args.map(x => x._1 -> StringArgs(x._2)))
    
    // 加载线下结果文件
    val loadOfflineResult: sequenceJob = new sequenceJob {
        override val name = "offline_result"
        override val actions: List[pActionTrait] = readCsvAction(args("offlineResult")) :: Nil
    }
    
    override val actions: List[pActionTrait] = {
        setLogLevelAction("ERROR") ::
                executeMaxAction(df) ::
//                loadUnitTestJarAction() ::
                loadOfflineResult ::
                resultCheckAction(df) ::
                writeCheckResultAction(df) ::
                Nil
    }
}
