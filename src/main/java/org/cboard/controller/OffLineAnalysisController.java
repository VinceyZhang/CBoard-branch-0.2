package org.cboard.controller;

import com.google.common.collect.Lists;
import org.cboard.dao.DatasetDao;
import org.cboard.dto.DataProviderResult;
import org.cboard.dto.DataResult;
import org.cboard.dto.ViewDashboardDataManager;
import org.cboard.dto.ViewDashboardDataset;
import org.cboard.pojo.DashboardDataManager;
import org.cboard.pojo.DashboardDataset;
import org.cboard.services.*;
import org.cboard.util.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by yfyuan on 2016/8/9.
 */
@RestController
@RequestMapping("/offLineAnalysis")
public class OffLineAnalysisController {

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private OffLineAnalysisService offLineAnalysisService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private DataManagerService dataManagerService;


    @RequestMapping(value = "/getTablesByDBName")
    public DataProviderResult getTablesByDBName(@RequestParam(name = "datasourceId") Long datasourceId,@RequestParam(name = "dbName") String dbName) {

        return dataManagerService.getTablesBySource(datasourceId,dbName);
    }

    @RequestMapping(value = "/getDBByDatasource")
    public DataProviderResult getDBByDatasource(@RequestParam(name = "datasourceId") String datasourceId) {
        String userId = authenticationService.getCurrentUser().getUserId();
        return dataManagerService.getDBByDatasource(userId, Long.parseLong(datasourceId));
    }

    @RequestMapping(value = "/saveNewAnalysis")
    public ServiceStatus saveNewAnalysis(@RequestParam(name = "json") String json) {
        String userId = authenticationService.getCurrentUser().getUserId();
        return offLineAnalysisService.saveAnalysisParamInfo(userId, json);
    }


    @RequestMapping(value = "/updateAnalysis")
    public ServiceStatus updateAnalysis(@RequestParam(name = "json") String json) {
        String userId = authenticationService.getCurrentUser().getUserId();
        return offLineAnalysisService.updateAnalysisParamInfo(userId, json);
    }



}
