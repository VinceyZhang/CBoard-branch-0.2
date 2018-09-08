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

    @RequestMapping(value = "/saveNewAnalysis")
    public ServiceStatus saveNewAnalysis(@RequestParam(name = "json") String json) {
        String userId = authenticationService.getCurrentUser().getUserId();
        return offLineAnalysisService.saveAnalysisParamInfo(userId,json);
    }

    @RequestMapping(value = "/getDatasetListByType")
    public List<ViewDashboardDataset> getDatasetListByType(@RequestParam(name = "type")Integer type) {

        String userId = authenticationService.getCurrentUser().getUserId();
        List<DashboardDataset> list = datasetService.getDatasetListByType(userId,type);
        return Lists.transform(list, ViewDashboardDataset.TO);
    }
}
