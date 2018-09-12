package pattern.manager

import com.pharbers.pattern2.detail.commonresult
import com.pharbers.pattern2.steps.commonstep

case class SequenceSteps(steps : List[commonstep], cr : Option[commonresult])
