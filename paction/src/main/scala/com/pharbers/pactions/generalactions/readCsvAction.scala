package com.pharbers.pactions.generalactions

import com.pharbers.pactions.actionbase.{DFArgs, StringArgs, pActionArgs, pActionTrait}
import com.pharbers.spark.phSparkDriver

object readCsvAction{
    def apply(arg_path: String, delimiter: String = ",",
              arg_name: String = "readCsvJob", applicationName: String = "test-dirver"): pActionTrait =
        new readCsvAction(StringArgs(arg_path), delimiter, arg_name, applicationName)
}

class readCsvAction(override val defaultArgs: pActionArgs,
                   delimiter: String,
                   override val name: String, applicationName: String) extends pActionTrait {
    override def perform(args: pActionArgs): pActionArgs =
        DFArgs(phSparkDriver(applicationName).readCsv(defaultArgs.asInstanceOf[StringArgs].get, delimiter))
}
