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
import org.cboard.util.HandleExcel;
import org.cboard.util.PathTool;
import org.cboard.util.SqlTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yfyuan on 2016/8/9.
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private DatasourceDao datasourceDao;

    @Autowired
    private DataProviderService dataProviderService;

    @Autowired
    private CachedDataProviderService cachedDataProviderService;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private WidgetDao widgetDao;

    @Autowired
    private BoardService boardService;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private DatasetDao datasetDao;

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private DataManagerService dataManagerService;

    @RequestMapping(value = "/test")
    public ServiceStatus test(@RequestParam(name = "datasource", required = false) String datasource, @RequestParam(name = "query", required = false) String query) {
        JSONObject queryO = JSONObject.parseObject(query);
        JSONObject datasourceO = JSONObject.parseObject(datasource);
        return dataProviderService.test(datasourceO, Maps.transformValues(queryO, Functions.toStringFunction()));
    }

    @RequestMapping(value = "/getData")
    public DataProviderResult getData(@RequestParam(name = "datasourceId", required = false) Long datasourceId, @RequestParam(name = "query", required = false) String query, @RequestParam(name = "datasetId", required = false) Long datasetId) {
        Map<String, String> strParams = null;
        if (query != null) {
            JSONObject queryO = JSONObject.parseObject(query);
            strParams = Maps.transformValues(queryO, Functions.toStringFunction());
        }
        DataProviderResult result = dataProviderService.getData(datasourceId, strParams, datasetId);
        return result;
    }

    @RequestMapping(value = "/getDataForTest")
    public DataProviderResult getDataForTest(@RequestParam(name = "datasourceId", required = false) Long datasourceId,
                                             @RequestParam(name = "query", required = false) String query){
        Map<String, String> strParams = null;
        if (query != null) {
            JSONObject queryO = JSONObject.parseObject(query);
            strParams = Maps.transformValues(queryO, Functions.toStringFunction());
        }
        DataProviderResult result = cachedDataProviderService.getDataForTest(datasourceId, strParams);
        return result;
    }

    @RequestMapping(value = "/getCachedData")
    public DataProviderResult getCachedData(@RequestParam(name = "datasourceId", required = false) Long datasourceId, @RequestParam(name = "query", required = false) String query, @RequestParam(name = "datasetId", required = false) Long datasetId, @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload) {
        Map<String, String> strParams = null;
        if (query != null) {
            JSONObject queryO = JSONObject.parseObject(query);
            strParams = Maps.transformValues(queryO, Functions.toStringFunction());
        }
        DataProviderResult result = cachedDataProviderService.getData(datasourceId, strParams, datasetId, reload);
        return result;
    }

    @RequestMapping(value = "/getCachedDataByParams")
    public DataProviderResult getCachedDataByParams(@RequestParam(name = "datasourceId", required = false) Long datasourceId,
                                                    @RequestParam(name = "query", required = false) String query,
                                                    @RequestParam(name = "datasetId", required = false) Long datasetId,
                                                    @RequestParam(name = "params", required = false) String params,
                                                    @RequestParam(name = "pagesParams", required = false) String pagesParams) {
        Map<String, String> strParams = null;
        if (query != null) {


            JSONObject queryO = JSONObject.parseObject(query);
            strParams = Maps.transformValues(queryO, Functions.toStringFunction());
        }
        DataProviderResult result = dataManagerService.getData(datasourceId, strParams, params, pagesParams, datasetId);

        return result;
    }

    @RequestMapping(value = "/getDatasourceList")
    public List<ViewDashboardDatasource> getDatasourceList() {

        String userid = authenticationService.getCurrentUser().getUserId();

        List<DashboardDatasource> list = datasourceDao.getDatasourceList(userid);
        return Lists.transform(list, ViewDashboardDatasource.TO);
    }

    @RequestMapping(value = "/getProviderList")
    public Set<String> getProviderList() {
        return DataProviderManager.getProviderList();
    }

    @RequestMapping(value = "/getConfigView")
    public String getConfigView(@RequestParam(name = "type") String type) {
        return DataProviderViewManager.getQueryView(type);
    }

    @RequestMapping(value = "/getDatasourceView")
    public String getDatasourceView(@RequestParam(name = "type") String type) {
        return DataProviderViewManager.getDatasourceView(type);
    }

    @RequestMapping(value = "/saveNewDatasource")
    public ServiceStatus saveNewDatasource(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return datasourceService.save(userid, json);
    }

    @RequestMapping(value = "/updateDatasource")
    public ServiceStatus updateDatasource(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return datasourceService.update(userid, json);
    }

    @RequestMapping(value = "/deleteDatasource")
    public ServiceStatus deleteDatasource(@RequestParam(name = "id") Long id) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return datasourceService.delete(userid, id);
    }

    @RequestMapping(value = "/saveNewWidget")
    public ServiceStatus saveNewWidget(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return widgetService.save(userid, json);
    }

    @RequestMapping(value = "/getWidgetList")
    public List<ViewDashboardWidget> getWidgetList() {

        String userid = authenticationService.getCurrentUser().getUserId();
        List<DashboardWidget> list = widgetDao.getWidgetList(userid);
        return Lists.transform(list, ViewDashboardWidget.TO);
    }

    @RequestMapping(value = "/getWidgetListByType")
    public List<ViewDashboardWidget> getWidgetListByType(@RequestParam String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        List<DashboardWidget> list = widgetService.getWidgetList(userid, json);
        return Lists.transform(list, ViewDashboardWidget.TO);
    }

    @RequestMapping(value = "/getWidgetById")
    public DashboardWidget getWidgetById(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        DashboardWidget view = widgetService.getWidgetById(json);

        return view;
    }

    @RequestMapping(value = "/updateWidget")
    public ServiceStatus updateWidget(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return widgetService.update(userid, json);
    }

    @RequestMapping(value = "/deleteWidget")
    public ServiceStatus deleteWidget(@RequestParam(name = "id") Long id) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return widgetService.delete(userid, id);
    }

    @RequestMapping(value = "/getBoardList")
    public List<ViewDashboardBoard> getBoardList() {

        String userid = authenticationService.getCurrentUser().getUserId();
        List<DashboardBoard> list = boardService.getBoardList(userid);
        return Lists.transform(list, ViewDashboardBoard.TO);
    }

    @RequestMapping(value = "/getBoardListByType")
    public List<ViewDashboardBoard> getBoardListByType(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        List<DashboardBoard> list = boardService.getBoardListByType(userid, json);
        return Lists.transform(list, ViewDashboardBoard.TO);
    }

    @RequestMapping(value = "/saveNewBoard")
    public ServiceStatus saveNewBoard(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return boardService.save(userid, json);
    }

    @RequestMapping(value = "/updateBoard")
    public ServiceStatus updateBoard(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return boardService.update(userid, json);
    }

    @RequestMapping(value = "/deleteBoard")
    public String deleteBoard(@RequestParam(name = "id") Long id) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return boardService.delete(userid, id);
    }

    @RequestMapping(value = "/getBoardData")
    public ViewDashboardBoard getBoardData(@RequestParam(name = "id") String id) {
        if (id.matches("\\d+")) {
            return boardService.getBoardData(Long.parseLong(id));
        } else {

            id = "{" + id + "}";
            JSONObject jb = JSON.parseObject(id);
            String sql = jb.getString("sql");
            String categoryName = jb.getString("category_name");
            String userId = jb.getString("user_id");

            //保存Dateset的data字段
            JSONObject jsData = new JSONObject();
            jsData.put("datasource", 3);
            jsData.put("expressions", new JSONObject[0]);
            jsData.put("interval", "10");
            JSONObject jbQuery = new JSONObject();
            jbQuery.put("sql", sql);
            jsData.put("query", jbQuery);
            //保存dataset里面data字段的query
            JSONObject jsDataset = new JSONObject();
            jsDataset.put("userId", userId);
            String stData = jsData.toJSONString();
            JSONObject dataset = new JSONObject();

            dataset.put("categoryName", categoryName);
            dataset.put("data", stData);
            dataset.put("name", "new");

            String stDataset = dataset.toJSONString();
            datasetService.save(userId, stDataset);

            JSONObject jbWidget = new JSONObject();
            jbWidget.put("name", "new ");
            jbWidget.put("data", "{\"config\":{\"chart_type\":\"pie\",\"filters\":[],\"groups\":[],\"keys\":[{\"col\":\"c\",\"type\":\"eq\",\"values\":[]}],\"selects\":[],\"values\":[{\"cols\":[{\"aggregate_type\":\"sum\",\"col\":\"d\"}],\"name\":\"\"}]},\"datasetId\":2}");
            jbWidget.put("categoryName", "new");
            widgetService.save(userId, jbWidget.toJSONString());

            JSONObject jbBoard = new JSONObject();
            jbBoard.put("name", "new");
            jbBoard.put("categoryId", 2);
            jbBoard.put("layout", "{\"rows\":[{\"height\":\"500\",\"type\":\"widget\",\"widgets\":[{\"name\":\"new\",\"widgetId\":9,\"width\":\"3\"},{\"name\":\"new\",\"widgetId\":9,\"width\":\"3\"},{\"name\":\"new\",\"widgetId\":9,\"width\":\"3\"},{\"name\":\"new\",\"widgetId\":9,\"width\":\"3\"}]}]}");
            boardService.save(userId, jbBoard.toJSONString());


            return boardService.getBoardData(Long.parseLong("5"));
        }

    }


    @RequestMapping(value = "/getBoardDataByType")
    public ViewDashboardBoard getBoardDataByType(@RequestParam(name = "id") String id) {
        if (id.matches("\\d+")) {
            return boardService.getBoardDataByType(Long.parseLong(id));
        } else {

            id = "{" + id + "}";
            JSONObject jb = JSON.parseObject(id);
            String sql = jb.getString("sql");
            String categoryName = jb.getString("category_name");
            String userId = jb.getString("user_id");

            //保存Dateset的data字段
            JSONObject jsData = new JSONObject();
            jsData.put("datasource", 3);
            jsData.put("expressions", new JSONObject[0]);
            jsData.put("interval", "10");
            JSONObject jbQuery = new JSONObject();
            jbQuery.put("sql", sql);
            jsData.put("query", jbQuery);
            //保存dataset里面data字段的query
            JSONObject jsDataset = new JSONObject();
            jsDataset.put("userId", userId);
            String stData = jsData.toJSONString();
            JSONObject dataset = new JSONObject();

            dataset.put("categoryName", categoryName);
            dataset.put("data", stData);
            dataset.put("name", "new");

            String stDataset = dataset.toJSONString();
            datasetService.save(userId, stDataset);

            JSONObject jbWidget = new JSONObject();
            jbWidget.put("name", "new ");
            jbWidget.put("data", "{\"config\":{\"chart_type\":\"pie\",\"filters\":[],\"groups\":[],\"keys\":[{\"col\":\"c\",\"type\":\"eq\",\"values\":[]}],\"selects\":[],\"values\":[{\"cols\":[{\"aggregate_type\":\"sum\",\"col\":\"d\"}],\"name\":\"\"}]},\"datasetId\":2}");
            jbWidget.put("categoryName", "new");
            widgetService.save(userId, jbWidget.toJSONString());

            JSONObject jbBoard = new JSONObject();
            jbBoard.put("name", "new");
            jbBoard.put("categoryId", 2);
            jbBoard.put("layout", "{\"rows\":[{\"height\":\"500\",\"type\":\"widget\",\"widgets\":[{\"name\":\"new\",\"widgetId\":9,\"width\":\"3\"},{\"name\":\"new\",\"widgetId\":9,\"width\":\"3\"},{\"name\":\"new\",\"widgetId\":9,\"width\":\"3\"},{\"name\":\"new\",\"widgetId\":9,\"width\":\"3\"}]}]}");
            boardService.save(userId, jbBoard.toJSONString());


            return boardService.getBoardData(Long.parseLong("5"));
        }

    }

    @RequestMapping(value = "/saveNewCategory")
    public ServiceStatus saveNewCategory(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return categoryService.save(userid, json);
    }

    @RequestMapping(value = "/importData")
    public ServiceStatus importData(@RequestParam(name = "datasourceId", required = false) Long datasourceId,
                                    @RequestParam(name = "datasetId", required = false) Long datasetId,
                                    @RequestParam(value = "file", required = false) MultipartFile file) {
        return dataManagerService.importData(datasourceId,datasetId,file);

    }

    @RequestMapping(value = "/getCategoryList")
    public List<DashboardCategory> getCategoryList() {
        List<DashboardCategory> list = categoryDao.getCategoryList();
        return list;
    }

    @RequestMapping(value = "/getCategoryListByType")
    public List<DashboardCategory> getCategoryListByType(@RequestParam(name = "json") String json) {
        List<DashboardCategory> list = categoryService.getCategoryListByType(json);
        return list;
    }


    @RequestMapping(value = "/updateCategory")
    public ServiceStatus updateCategory(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return categoryService.update(userid, json);
    }

    @RequestMapping(value = "/deleteCategory")
    public String deleteCategory(@RequestParam(name = "id") Long id) {
        return categoryService.delete(id);
    }

    @RequestMapping(value = "/getWidgetCategoryList")
    public List<String> getWidgetCategoryList() {
        return widgetDao.getCategoryList();
    }

    @RequestMapping(value = "/saveNewDataset")
    public ServiceStatus saveNewDataset(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return datasetService.save(userid, json);
    }

    @RequestMapping(value = "/getDatasetList")
    public List<ViewDashboardDataset> getDatasetList() {

        String userid = authenticationService.getCurrentUser().getUserId();
        List<DashboardDataset> list = datasetDao.getDatasetList(userid);
        return Lists.transform(list, ViewDashboardDataset.TO);
    }

    @RequestMapping(value = "/getDatasetListByType")
    public List<ViewDashboardDataset> getDatasetListByType(@RequestParam(name = "type") Integer type) {

        String userId = authenticationService.getCurrentUser().getUserId();
        List<DashboardDataset> list = datasetService.getDatasetListByType(userId, type);
        return Lists.transform(list, ViewDashboardDataset.TO);
    }

    @RequestMapping(value = "/updateDataset")
    public ServiceStatus updateDataset(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return datasetService.update(userid, json);
    }

    @RequestMapping(value = "/deleteDataset")
    public ServiceStatus deleteDataset(@RequestParam(name = "id") Long id) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return datasetService.delete(userid, id);
    }

    @RequestMapping(value = "/getDatasetCategoryList")
    public List<String> getDatasetCategoryList() {
        return datasetDao.getCategoryList();
    }

    @RequestMapping(value = "/checkWidget")
    public ServiceStatus checkWidget(@RequestParam(name = "id") Long id) {
        return widgetService.checkRule(authenticationService.getCurrentUser().getUserId(), id);
    }

    @RequestMapping(value = "/checkDatasource")
    public ServiceStatus checkDatasource(@RequestParam(name = "id") Long id) {
        return datasourceService.checkDatasource(authenticationService.getCurrentUser().getUserId(), id);
    }

}
