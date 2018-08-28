package com.pharbers.panel.format.input.writable.pfizer;

public class phPfizerCpaWritable extends phPfizerCommonWritable {

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
            return lst[4].trim();
        } else if (flag.equals("PACK_DES")) {
            return lst[5].trim();
        }else if (flag.equals("PACK_NUMBER")) {
            return lst[6].trim();
        }else if (flag.equals("VALUE")) {
            return lst[7];
        }else if (flag.equals("STANDARD_UNIT")) {
            return lst[8];
        }else if (flag.equals("DOSAGE")) {
            return lst[9].trim();
        }else if (flag.equals("DELIVERY_WAY")) {
            return lst[10];
        }else if (flag.equals("CORP_NAME")) {
            return lst[11].trim();
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
        if (flag.equals("PRODUCT_NAME")) {
            lst[4] = value;
            return lst;
        } else if (flag.equals("PACK_DES")) {
            lst[5] = value;
            return lst;
        } else if (flag.equals("VALUE")) {
            lst[7] = value;
            return lst;
        } else if (flag.equals("STANDARD_UNIT")) {
            lst[8] = value;
            return lst;
        } else{
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

        lst = setCellKey(lst, "PACK_DES", getCellKey(lst, "PACK_DES"));

        if("".equals(getCellKey(lst, "PRODUCT_NAME")))
            lst = setCellKey(lst, "PRODUCT_NAME", getCellKey(lst, "MOLE_NAME"));
        if("".equals(getCellKey(lst, "VALUE")))
            lst = setCellKey(lst, "VALUE", "0");
        if("".equals(getCellKey(lst, "STANDARD_UNIT")))
            lst = setCellKey(lst, "STANDARD_UNIT", "0");

        String ym = getCellKey(lst, "YEAR") + getCellKey(lst, "MONTH");

        String min1 = getCellKey(lst, "PRODUCT_NAME") +
                getCellKey(lst, "APP2_COD") +
                getCellKey(lst, "PACK_DES") +
                getCellKey(lst, "PACK_NUMBER") +
                getCellKey(lst, "CORP_NAME");

        return mkString(lst, delimiter) + delimiter + ym + delimiter + min1;
    }

    @Override
    public String richWithInputRow(int index, String value) {
        if (index == 1) {
            return expendTitle(value);
        } else
            return prePanelFunction(value);
    }

}
