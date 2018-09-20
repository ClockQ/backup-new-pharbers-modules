//package dataCleanDemo
//
//import java.util.UUID
//import akka.actor.Actor
//import com.pharbers.common.algorithm.max_path_obj
//import com.pharbers.pactions.actionbase.{MapArgs, StringArgs, pActionTrait}
//import com.pharbers.pactions.excel.input.PhExcelXLSXCommonFormat
//import com.pharbers.pactions.generalactions._
//import com.pharbers.pactions.jobs.{sequenceJob, sequenceJobWithMap}
//import com.pharbers.panel.nhwa.format.phNhwaCpaFormat
//
//case class dataCleanJob(args: Map[String, String])(implicit _actor: Actor) extends sequenceJobWithMap {
//    override val name: String = "data_clean_job"
//    val df = MapArgs(args.map(x => x._1 -> StringArgs(x._2)))
//
//    val load_source_file: sequenceJob = new sequenceJob {
//        val temp_name: String = UUID.randomUUID().toString
//        val temp_dir: String = max_path_obj.p_cachePath
//        override val name = "source_file"
//        override val actions: List[pActionTrait] =
//            xlsxReadingAction[PhExcelXLSXCommonFormat]("/mnt/config/MatchFile/pfizer/pha_config_repository1804/180615辉瑞1804底层检索.xlsx", temp_name) ::
//                    saveCurrenResultAction(temp_dir + temp_name) ::
//                    csv2DFAction(temp_dir + temp_name) :: Nil
//    }
//
//    val load_standard_file: sequenceJob = new sequenceJob {
//        val temp_name: String = UUID.randomUUID().toString
//        val temp_dir: String = max_path_obj.p_cachePath + temp_name + "/"
//        override val actions: List[pActionTrait] = existenceRdd("full_hosp_file") ::
//                csv2DFAction(temp_dir + "full_hosp_file") ::
//                new sequenceJob {
//                    override val name: String = "read_full_hosp_file_job"
//                    override val actions: List[pActionTrait] =
//                        xlsxReadingAction[phNhwaCpaFormat]("", "full_hosp_file") ::
//                                saveCurrenResultAction(temp_dir + "full_hosp_file") ::
//                                csv2DFAction(temp_dir + "full_hosp_file") :: Nil
//                } :: Nil
//        override val name: String = "standard_file"
//    }
//
//    val actions: List[pActionTrait] = {
//        jarPreloadAction() ::
//                setLogLevelAction("ERROR") ::
//                load_source_file ::
//                load_standard_file ::
//                dataCleanAction(df) ::
//                Nil
//    }
//}
