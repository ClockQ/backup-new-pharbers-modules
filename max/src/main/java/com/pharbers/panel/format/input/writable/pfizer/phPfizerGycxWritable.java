package com.pharbers.panel.format.input.writable.pfizer;

import java.util.HashMap;

public class phPfizerGycxWritable extends phPfizerCommonWritable {
    @Override
    protected String getCellKey(String[] lst, String flag) {
        if (flag.equals("YEAR")) {
            return lst[0];
        } else if (flag.equals("MONTH")) {
            if(lst[1].length() == 1) return "0" + lst[1];
            return lst[1];
        } else if (flag.equals("HOSP_ID")) {
            return lst[2];
        } else if (flag.equals("MOLE_NAME")) {
            return lst[3];
        } else if (flag.equals("PRODUCT_NAME")) {
            return lst[4];
        } else if (flag.equals("PACK_DES")) {
            return lst[5];
        }else if (flag.equals("PACK_NUMBER")) {
            return lst[6];
        }else if (flag.equals("VALUE")) {
            return lst[7];
        }else if (flag.equals("STANDARD_UNIT")) {
            return lst[8];
        }else if (flag.equals("DOSAGE")) {
            return lst[9];
        }else if (flag.equals("DELIVERY_WAY")) {
            return lst[10];
        }else if (flag.equals("CORP_NAME")) {
            return lst[11];
        }else if (flag.equals("YM")) {
            return lst[12];
        }else if (flag.equals("min1")) {
            return lst[13];
        }

        return "not implements";
        // throw new Exception("not implements");
    }

    @Override
    protected String[] setCellKey(String[] lst, String flag, String value) {
        if (flag.equals("YEAR")) {
            lst[0] = value;
            return lst;
        } else if (flag.equals("MONTH")) {
            lst[1] = value;
            return lst;
        } else if (flag.equals("HOSP_ID")) {
            lst[2] = value;
            return lst;
        } else if (flag.equals("MOLE_NAME")) {
            lst[3] = value;
            return lst;
        } else if (flag.equals("PRODUCT_NAME")) {
            lst[4] = value;
            return lst;
        } else if (flag.equals("PACK_DES")) {
            lst[5] = value;
            return lst;
        }else if (flag.equals("PACK_NUMBER")) {
            lst[6] = value;
            return lst;
        }else if (flag.equals("VALUE")) {
            lst[7] = value;
            return lst;
        }else if (flag.equals("STANDARD_UNIT")) {
            lst[8] = value;
            return lst;
        }else if (flag.equals("DOSAGE")) {
            lst[9] = value;
            return lst;
        }else if (flag.equals("DELIVERY_WAY")) {
            lst[10] = value;
            return lst;
        }else if (flag.equals("CORP_NAME")) {
            lst[11] = value;
            return lst;
        }else if (flag.equals("YM")) {
            lst[12] = value;
            return lst;
        }else if (flag.equals("min1")) {
            lst[13] = value;
            return lst;
        }else{
            return lst;
        }
    }

    @Override
    protected String expendTitle(String value) {
        return value + delimiter + "YM" + delimiter + "min1";
    }

    @Override
    protected String prePanelFunction(String value) {
        String[] lst = splitValues(value);

        if("".equals(getCellKey(lst, "PRODUCT_NAME")))
            lst = setCellKey(lst, "PRODUCT_NAME", getCellKey(lst, "MOLE_NAME"));
        if("".equals(getCellKey(lst, "VALUE")))
            lst = setCellKey(lst, "VALUE", "0");
        if("".equals(getCellKey(lst, "STANDARD_UNIT")))
            lst = setCellKey(lst, "STANDARD_UNIT", "0");

        String ym = getCellKey(lst, "YEAR") + getCellKey(lst, "MONTH");

        String min1 = getCellKey(lst, "PRODUCT_NAME") +
                getCellKey(lst, "DOSAGE") +
                getCellKey(lst, "PACK_DES") +
                getCellKey(lst, "PACK_NUMBER") +
                getCellKey(lst, "CORP_NAME");

        return mkString(lst, delimiter) + delimiter + ym + delimiter + min1;
    }

    @Override
    public String richWithInputRow(int index, String value) {
        if (index == 1) {
            return expendTitle(transTitle2Eng(value));
        } else
            return expendValues(titleMap.size(), value);
    }

}
