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
import org.cboard.util.PageHelper;
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
@RequestMapping("/dataManager")
public class DataManagerController {


    @Autowired
    private DataManagerService dataManagerService;

    @Autowired
    private AuthenticationService authenticationService;


    @RequestMapping(value = "/getDataManagerList")
    public List<ViewDashboardDataManager> getDataManagerList() {

        String userid = authenticationService.getCurrentUser().getUserId();
        List<DashboardDataManager> list = dataManagerService.getDataManagerList(userid);
        return Lists.transform(list, ViewDashboardDataManager.TO);
    }


    @RequestMapping(value = "/saveNewDataManager")
    public ServiceStatus saveNewDataManager(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return dataManagerService.save(userid, json);
    }

    @RequestMapping(value = "/saveDataManager")
    public ServiceStatus saveDataManger(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return dataManagerService.save(userid, json);
    }


    @RequestMapping(value = "/updateDataManager")
    public ServiceStatus updateDataset(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return dataManagerService.update(userid, json);
    }

//    @RequestMapping(value = "/saveDataManger")
//    public ServiceStatus saveDataManger(@RequestParam(name = "datasourceId", required = false) String json) {
//        String userid = authenticationService.getCurrentUser().getUserId();
//        return dataManagerService.saveDataManger(userid,json);
//    }



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
    public DataResult getDatasByTable(@RequestBody Object data) {

        Map<String, Object> objectMap = (Map<String, Object>) (data);
        Long datasourceId = Long.parseLong(objectMap.get("datasourceId").toString());
        String table = objectMap.get("table").toString();
        Integer pageSize = Integer.parseInt(objectMap.get("pageSize").toString());
        Integer curPage = Integer.parseInt(objectMap.get("curPage").toString());
        PageHelper pageHelper = new PageHelper(pageSize, curPage);
        Map<String, String> params = (Map<String, String>) objectMap.get("params");

        return dataManagerService.getDatasBySourceAndTable(datasourceId, table, pageHelper, params);
    }

    /**
     * 根据表名查询列名
     *
     * @param data
     * @return
     */
    @RequestMapping(value = "/getColumnsByTable")
    @ResponseBody
    public DataResult getColumnsByTable(@RequestBody Object data) {

        Map<String, Object> objectMap = (Map<String, Object>) (data);
        Long datasourceId = Long.parseLong(objectMap.get("datasourceId").toString());
        String table = objectMap.get("table").toString();
        return dataManagerService.getColumnsBySourceAndTable(datasourceId, table);
    }
}
