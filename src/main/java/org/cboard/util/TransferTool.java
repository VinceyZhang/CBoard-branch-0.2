package org.cboard.util;

import java.util.Map;

public class TransferTool {
    public static String mapToString(Map<String, Object> map) {
        StringBuffer sb = new StringBuffer("{");
        for (String key : map.keySet()) {
            sb.append("\"" + key + "\":" + "\"" + map.get(key) + "\",");
        }
        return sb.substring(0, sb.lastIndexOf(",")) + "}";
    }
}
