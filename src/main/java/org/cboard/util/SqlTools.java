package org.cboard.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import org.cboard.dataprovider.DataProvider;
import org.cboard.dataprovider.DataProviderManager;
import org.cboard.dto.DataProviderResult;
import org.cboard.pojo.DashboardDatasource;
import org.cboard.services.DataProviderService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SqlTools {
    public static Map<String, String> appendParams(String query, String params) {

        JSONObject jqObject = JSONObject.parseObject(query);
        JSONObject paObject = null;
        if (params != null) {
            paObject = JSONObject.parseObject(params);
        }


        Map<String, String> queryMap = null;
        Map<String, String> paramsMap = null;
        if (query != null) {
            queryMap = Maps.transformValues(jqObject, Functions.toStringFunction());
        }
        if (params != null) {
            paramsMap = Maps.transformValues(paObject, Functions.toStringFunction());
        }
        StringBuffer sql = new StringBuffer(queryMap.get("sql"));
        if (paramsMap != null && paramsMap.size() > 0) {
            sql.append(" where 1=1 ");
            Iterator<Map.Entry<String, String>> iterator = paramsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                sql.append(" and ");
                sql.append(entry.getKey() + " = \'" + entry.getValue() + "\' ");

            }
        }
        JSONObject reObj = JSONObject.parseObject("{sql:\"" + sql.toString() + "\"}");

        return Maps.transformValues(reObj, Functions.toStringFunction());
    }


}
