package org.cboard.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.cboard.dao.*;
import org.cboard.dataprovider.DataProviderManager;
import org.cboard.dataprovider.DataProviderViewManager;
import org.cboard.dto.*;
import org.cboard.pojo.*;
import org.cboard.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yfyuan on 2016/8/9.
 */
@RestController
@RequestMapping("/datamanager")
public class DataManagerController {


    @Autowired
    private DataManagerService dataManagerService;
    @Autowired
    private CachedDataProviderService cachedDataProviderService;

    @Autowired
    private DatasourceService datasourceService;


    /**
     * 获取所有数据源名
     *
     * @param datasourceId
     * @return
     */
    @RequestMapping(value = "/getDataSources")
    public DataResult getDataSource(@RequestParam(name = "datasourceId", required = false) Long datasourceId) {
        return dataManagerService.getDataSources(datasourceId);
    }

    /**
     * 获取所有表名
     *
     * @param datasourceId
     * @return
     */
    @RequestMapping(value = "/getTablesBySource")
    public DataProviderResult getTablesBySource(@RequestParam(name = "datasourceId", required = false) Long datasourceId) {
        return dataManagerService.getTablesBySource(datasourceId);
    }

    /**
     * 根据表名查询数据
     *
     * @param data
     * @return
     */
    @RequestMapping(value = "/getDataByTable")
    @ResponseBody
    public DataProviderResult getDatasByTable(@RequestBody Object data) {

        Map<String, Object> jsonObject = (Map<String, Object>) (data);
        Long datasourceId = Long.parseLong(jsonObject.get("datasourceId").toString());
        String table = jsonObject.get("table").toString();
        Map<String, String> params = (Map<String, String>) jsonObject.get("params");
        return dataManagerService.getDatasBySourceAndTable(datasourceId, table, params);
    }

    /**
     * 根据表名查询列名
     *
     * @param datasourceId
     * @param table
     * @return
     */
    @RequestMapping(value = "/getColumnsByTable")
    public DataProviderResult getColumnsByTable(@RequestParam(name = "datasourceId", required = false) Long datasourceId,
                                                @RequestParam(name = "table", required = false) String table,
                                                @RequestParam(name = "params", required = false) Object params) {
        Map<String, String> strParams = new HashMap<String, String>();

        return dataManagerService.getColumnsBySourceAndTable(datasourceId, table, params);
    }
}
