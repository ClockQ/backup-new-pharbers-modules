package com.pharbers.panel.format.input.writable.astellas;

import java.util.HashMap;

public class phAstellasHospitalWritable extends phAstellasCommonWritable {

    public phAstellasHospitalWritable() {
        titleMap = new HashMap<String, String>() {{
            put("ACN_ID", "ACN_CODE");
            put("CPA_ID", "CPA_CODE");
            put("GYCX_ID", "GYC_CODE");
            put("GYCX_HOSP_NAME", "GYC_HOSP_NAME");
            put("GYCX_HOSP_LEVEL", "GYC_HOSP_LEVEL");
            put("STANDARD_ID", "STANDARD_CODE");
        }};
    }

    @Override
    public String richWithInputRow(int index, String value) {
        if (index == 1) {
            return expendTitle(transTitle2Eng(value));
        } else return expendValues(15, value);
    }

    @Override
    protected String getCellKey(String[] lst, String flag) {
        if (flag.equals("ACN_CODE")) {
            return lst[0];
        } else if (flag.equals("ACN_HOSP_NAME")) {
            return lst[1];
        } else if (flag.equals("ACN_HOSP_LEVEL")) {
            return lst[2];
        } else if (flag.equals("CPA_CODE")) {
            return lst[3];
        } else if (flag.equals("CPA_HOSP_NAME")) {
            return lst[4];
        } else if (flag.equals("CPA_HOSP_LEVEL")) {
            return lst[5];
        } else if (flag.equals("GYC_CODE")) {
            return lst[6];
        }else if (flag.equals("GYC_HOSP_NAME")) {
            return lst[7];
        }else if (flag.equals("GYC_HOSP_LEVEL")) {
            return lst[8];
        }else if (flag.equals("STANDARD_CODE")) {
            return lst[9];
        }else if (flag.equals("STANDARD_HOSP_NAME")) {
            return lst[10];
        }else if (flag.equals("STANDARD_HOSP_LEVEL")) {
            return lst[11];
        }else if (flag.equals("SOURCE")) {
            return lst[12];
        }else if (flag.equals("CPA_DIS")) {
            return lst[13];
        }else if (flag.equals("GYC_DIS")) {
            return lst[14];
        }

        return "not implements";
        // throw new Exception("not implements");
    }

    @Override
    protected String[] setCellKey(String[] lst, String flag, String value) {
        if (flag.equals("ACN_CODE")) {
            lst[0] = value;
            return lst;
        } else if (flag.equals("ACN_HOSP_NAME")) {
            lst[1] =value;
            return lst;
        } else if (flag.equals("ACN_HOSP_LEVEL")) {
            lst[2] = value;
            return lst;
        } else if (flag.equals("CPA_CODE")) {
            lst[3] = value;
            return lst;
        } else if (flag.equals("CPA_HOSP_NAME")) {
            lst[4] = value;
            return lst;
        } else if (flag.equals("CPA_HOSP_LEVEL")) {
            lst[5] = value;
            return lst;
        } else if (flag.equals("GYC_CODE")) {
            lst[6] = value;
            return lst;
        }else if (flag.equals("GYC_HOSP_NAME")) {
            lst[7] = value;
            return lst;
        }else if (flag.equals("GYC_HOSP_LEVEL")) {
            lst[8] = value;
            return lst;
        }else if (flag.equals("STANDARD_CODE")) {
            lst[9] = value;
            return lst;
        }else if (flag.equals("STANDARD_HOSP_NAME")) {
            lst[10] = value;
            return lst;
        }else if (flag.equals("STANDARD_HOSP_LEVEL")) {
            lst[11] = value;
            return lst;
        }else if (flag.equals("STANDARD_UNIT")) {
            lst[12] = value;
            return lst;
        }else if (flag.equals("SOURCE")) {
            lst[13] = value;
            return lst;
        }else if (flag.equals("CPA_DIS")) {
            lst[14] = value;
            return lst;
        }else if (flag.equals("GYC_DIS")) {
            lst[15] = value;
            return lst;
        }else{
            return lst;
        }
    }

    @Override
    protected String prePanelFunction(String value) {
        String[] lst = splitValues(value);

        if(!"".equals(getCellKey(lst, "ACN_CODE")) && !" ".equals(getCellKey(lst, "ACN_CODE")))
            lst = setCellKey(lst, "STANDARD_CODE", getCellKey(lst, "ACN_CODE"));
        else{
            if(!"".equals(getCellKey(lst, "CPA_CODE")) && !" ".equals(getCellKey(lst, "CPA_CODE")))
                lst = setCellKey(lst, "STANDARD_CODE", getCellKey(lst, "CPA_CODE"));
            else
                lst = setCellKey(lst, "STANDARD_CODE", getCellKey(lst, "GYC_CODE"));
        }

        if(!"".equals(getCellKey(lst, "ACN_HOSP_NAME")) && !" ".equals(getCellKey(lst, "ACN_HOSP_NAME")))
            lst = setCellKey(lst, "STANDARD_HOSP_NAME", getCellKey(lst, "ACN_HOSP_NAME"));
        else{
            if(!"".equals(getCellKey(lst, "CPA_HOSP_NAME")) && !" ".equals(getCellKey(lst, "CPA_HOSP_NAME")))
                lst = setCellKey(lst, "STANDARD_HOSP_NAME", getCellKey(lst, "CPA_HOSP_NAME"));
            else
                lst = setCellKey(lst, "STANDARD_HOSP_NAME", getCellKey(lst, "GYC_HOSP_NAME"));
        }

        if(!"".equals(getCellKey(lst, "ACN_HOSP_LEVEL")) && !" ".equals(getCellKey(lst, "ACN_HOSP_LEVEL")))
            lst = setCellKey(lst, "STANDARD_HOSP_LEVEL", getCellKey(lst, "ACN_HOSP_LEVEL"));
        else{
            if(!"".equals(getCellKey(lst, "CPA_HOSP_LEVEL")) && !" ".equals(getCellKey(lst, "CPA_HOSP_LEVEL")))
                lst = setCellKey(lst, "STANDARD_HOSP_LEVEL", getCellKey(lst, "CPA_HOSP_LEVEL"));
            else
                lst = setCellKey(lst, "STANDARD_HOSP_LEVEL", getCellKey(lst, "GYC_HOSP_LEVEL"));
        }

        if ("5".equals(getCellKey(lst, "STANDARD_CODE")))
            lst = setCellKey(lst, "SOURCE", "ACN");
        else if ("6".equals(getCellKey(lst, "STANDARD_CODE")))
            lst = setCellKey(lst, "SOURCE", "CPA");
        else if ("7".equals(getCellKey(lst, "STANDARD_CODE")))
            lst = setCellKey(lst, "SOURCE", "GYC");

        return mkString(lst, delimiter);
    }

}
