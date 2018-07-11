package com.pharbers.panel.format.input.writable.astellas;

import java.util.HashMap;

public class phAstellasUniverseWritable extends phAstellasCommonWritable {

    public phAstellasUniverseWritable() {
        titleMap = new HashMap<String, String>() {{
            put("公司", "COMPANY");
            put("PHA_HOSP_NAME", "PHA_NAME");
            put("PHA_HOSP_ID", "PHA_ID");
        }};
    }

    @Override
    protected String getCellKey(String[] lst, String flag) {
        if (flag.equals("PANEL_ID")) {
            return lst[7];
        } else if (flag.equals("PHA_ID")) {
            return lst[9];
        }

        return "not implements";
    }

}
