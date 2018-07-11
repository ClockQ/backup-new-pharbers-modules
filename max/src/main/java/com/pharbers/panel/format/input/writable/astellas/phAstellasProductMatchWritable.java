package com.pharbers.panel.format.input.writable.astellas;

import java.util.HashMap;

public class phAstellasProductMatchWritable extends phAstellasCommonWritable {

    public phAstellasProductMatchWritable() {
        titleMap = new HashMap<String, String>() {{
            put("MOLE_NAME", "MOLE_NAME1");
            put("PRODUCT_NAME", "PRODUCT_NAME1");
            put("DOSAGE", "APP2_COD1");
            put("PACK_DES", "PACK_DES1");
            put("PACK_COUNT", "PACK_NUMBER1");
            put("CORP_NAME", "CORP_NAME1");
            put("MIN_PRODUCT_UNIT", "min1");
            put("STANDARD_DOSAGE", "STANDARD_APP2_COD");
            put("STANDARD_PACK_NUMBER", "PACK_NUMBER2");
            put("MIN_PRODUCT_UNIT_STANDARD", "min2");
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
        if (flag.equals("MOLE_NAME1")) {
            return lst[0];
        } else if (flag.equals("PRODUCT_NAME1")) {
            return lst[1];
        } else if (flag.equals("APP2_COD1")) {
            return lst[2];
        } else if (flag.equals("PACK_DES1")) {
            return lst[3];
        } else if (flag.equals("PACK_NUMBER1")) {
            return lst[4];
        } else if (flag.equals("CORP_NAME1")) {
            return lst[5];
        } else if (flag.equals("min1")) {
            return lst[6];
        } else if (flag.equals("STANDARD_MOLE_NAME")) {
            return lst[7];
        } else if (flag.equals("STANDARD_PRODUCT_NAME")) {
            return lst[8];
        } else if (flag.equals("STANDARD_APP2_COD")) {
            return lst[9];
        } else if (flag.equals("STANDARD_PACK_DES")) {
            return lst[10];
        } else if (flag.equals("PACK_NUMBER2")) {
            return lst[11];
        } else if (flag.equals("STANDARD_CORP_NAME")) {
            return lst[12];
        } else if (flag.equals("min2")) {
            return lst[13];
        } else if (flag.equals("PACK_ID")) {
            return lst[14];
        }

        return "not implements";
        // throw new Exception("not implements");
    }

    @Override
    protected String[] setCellKey(String[] lst, String flag, String value) {
        if (flag.equals("MOLE_NAME1")) {
            lst[0] = value;
            return lst;
        } else if (flag.equals("PRODUCT_NAME1")) {
            lst[1] = value;
            return lst;
        } else if (flag.equals("APP2_COD1")) {
            lst[2] = value;
            return lst;
        } else if (flag.equals("PACK_DES1")) {
            lst[3] = value;
            return lst;
        } else if (flag.equals("PACK_NUMBER1")) {
            lst[4] = value;
            return lst;
        } else if (flag.equals("CORP_NAME1")) {
            lst[5] = value;
            return lst;
        } else if (flag.equals("min1")) {
            lst[6] = value;
            return lst;
        } else if (flag.equals("STANDARD_MOLE_NAME")) {
            lst[7] = value;
            return lst;
        } else if (flag.equals("STANDARD_PRODUCT_NAME")) {
            lst[8] = value;
            return lst;
        } else if (flag.equals("STANDARD_APP2_COD")) {
            lst[9] = value;
            return lst;
        } else if (flag.equals("STANDARD_PACK_DES")) {
            lst[10] = value;
            return lst;
        } else if (flag.equals("PACK_NUMBER2")) {
            lst[11] = value;
            return lst;
        } else if (flag.equals("STANDARD_CORP_NAME")) {
            lst[12] = value;
            return lst;
        } else if (flag.equals("min2")) {
            lst[13] = value;
            return lst;
        } else if (flag.equals("PACK_ID")) {
            lst[14] = value;
            return lst;
        } else {
            return lst;
        }
    }

    @Override
    protected String expendTitle(String value) {
        return value;
    }

    @Override
    protected String prePanelFunction(String value) {
        String[] lst = splitValues(value);

        if("".equals(getCellKey(lst, "PACK_NUMBER2")))
            lst = setCellKey(lst, "PACK_NUMBER2", getCellKey(lst, "PACK_NUMBER1"));
        if("抗人胸腺细胞兔免疫球蛋白".equals(getCellKey(lst, "STANDARD_MOLE_NAME")))
            lst = setCellKey(lst, "STANDARD_MOLE_NAME", "抗人胸腺细胞免疫球蛋白");
        if("米芙".equals(getCellKey(lst, "STANDARD_PRODUCT_NAME")))
            lst = setCellKey(lst, "STANDARD_MOLE_NAME", "麦考芬酸钠");
        if("哈乐".equals(getCellKey(lst, "STANDARD_PRODUCT_NAME")) &&
                "片剂".equals(getCellKey(lst, "STANDARD_APP2_COD")) &&
                    "14".equals(getCellKey(lst, "PACK_NUMBER2")))
            lst = setCellKey(lst, "STANDARD_PRODUCT_NAME", "新哈乐");
        if("新哈乐".equals(getCellKey(lst, "STANDARD_PRODUCT_NAME")) &&
                "片剂".equals(getCellKey(lst, "STANDARD_APP2_COD")) &&
                    "10".equals(getCellKey(lst, "PACK_NUMBER2")))
            lst = setCellKey(lst, "STANDARD_PRODUCT_NAME", "哈乐");

        String min1 = getCellKey(lst, "PRODUCT_NAME1") + "|" +
                getCellKey(lst, "APP2_COD1") + "|" +
                getCellKey(lst, "PACK_DES1") + "|" +
                getCellKey(lst, "PACK_NUMBER1") + "|" +
                getCellKey(lst, "CORP_NAME1");
        String min2 = getCellKey(lst, "STANDARD_PRODUCT_NAME") + "|" +
                getCellKey(lst, "STANDARD_APP2_COD") + "|" +
                getCellKey(lst, "STANDARD_PACK_DES") + "|" +
                getCellKey(lst, "PACK_NUMBER2") + "|" +
                getCellKey(lst, "STANDARD_CORP_NAME");

        lst = setCellKey(lst, "min1", min1);
        lst = setCellKey(lst, "min2", min2);

        return mkString(lst, delimiter);
    }

}
