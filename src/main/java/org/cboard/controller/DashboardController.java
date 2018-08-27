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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/saveNewCategory")
    public ServiceStatus saveNewCategory(@RequestParam(name = "json") String json) {

        String userid = authenticationService.getCurrentUser().getUserId();
        return categoryService.save(userid, json);
    }

    @RequestMapping(value = "/getCategoryList")
    public List<DashboardCategory> getCategoryList() {
        List<DashboardCategory> list = categoryDao.getCategoryList();
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