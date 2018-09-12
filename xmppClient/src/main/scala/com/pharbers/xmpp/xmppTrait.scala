package com.pharbers.xmpp

import com.pharbers.pattern2.detail.commonresult

/**
  * @ ProjectName pharbers-xmppClient.com.pharbers.xmpp.xmppTrait
  * @ author jeorch
  * @ date 18-9-11
  * @ Description: TODO
  */
trait xmppTrait {
    val encodeHandler: commonresult => String
    val decodeHandler: String => commonresult
    val consumeHandler: String => String
}
