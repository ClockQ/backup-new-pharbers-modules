package com.pharbers.common.algorithm

trait phDealRGB {

    def getIndexColor(index: Int, step: Int, startRGB: String = "#2C82BE", endRGD: String = "#DBECF8"): String = index match {

        case 0 => startRGB
        case i if i == step-1 => endRGD
        case i => {
            val sr: Int = startRGB.substring(1, 3)
            val sg: Int = startRGB.substring(3, 5)
            val sb: Int = startRGB.substring(5, 7)
            val er: Int = endRGD.substring(1, 3)
            val eg: Int = endRGD.substring(3, 5)
            val eb: Int = endRGD.substring(5, 7)

            val r: Int = (er - sr)/step
            val g: Int = (eg - sg)/step
            val b: Int = (eb - sb)/step

            val rr: Int = sr + (i * r)
            val rg: Int = sg + (i * g)
            val rb: Int = sb + (i * b)

            val hexR: String = getHexString(rr)
            val hexG: String = getHexString(rg)
            val hexB: String = getHexString(rb)

            "#" + hexR + hexG + hexB
        }
    }

    def getHexString(singleColorDecim: Int): String = singleColorDecim.toHexString match {
        case str if str.length < 2 => "0" + str
        case str => str
    }

    private implicit def getHex2Decim(hexStr: String): Int = Integer.parseInt(hexStr, 16)

}
