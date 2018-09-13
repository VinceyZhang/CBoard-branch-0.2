package org.cboard.services;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.cboard.dao.AnalysisDao;
import org.cboard.dao.DatasetDao;
import org.cboard.dao.DatasourceDao;
import org.cboard.dataprovider.DataProvider;
import org.cboard.dataprovider.DataProviderManager;
import org.cboard.datasource.DataSourceEnum;
import org.cboard.datasource.DataSourceHolder;
import org.cboard.dto.DataProviderResult;
import org.cboard.pojo.DashboardDataset;
import org.cboard.pojo.DashboardDatasource;
import org.cboard.pojo.DashboardOffLineAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;


@Repository
public class OffLineAnalysisService {


    @Autowired
    private DatasetDao datasetDao;

    @Autowired
    private CachedDataProviderService cachedDataProviderService;

    @Autowired
    private AnalysisDao analysisDao;

    @Autowired
    private DatasourceDao datasourceDao;

    @Autowired
    private DatasetService datasetService;

    public ServiceStatus saveAnalysisParamInfo(String userId, String json) {

        JSONObject jsonObject = JSONObject.parseObject(json);
        DashboardDataset dataset = new DashboardDataset();

        //保存离线分析数据到本系统数据库
        dataset.setUserId(userId);
        dataset.setName(jsonObject.getString("name"));
        dataset.setData(jsonObject.getString("data"));
        dataset.setCategoryName(jsonObject.getString("categoryName"));
        dataset.setConfig(jsonObject.getString("config"));
        dataset.setType(1);
        if (StringUtils.isEmpty(dataset.getCategoryName())) {
            dataset.setCategoryName("默认分类");
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("dataset_name", dataset.getName());
        paramMap.put("user_id", dataset.getUserId());
        paramMap.put("category_name", dataset.getCategoryName());

        JSONObject dataObject = JSONObject.parseObject(jsonObject.getString("data"));
        Map<String, String> sqlMap = Maps.transformValues(JSONObject.parseObject(dataObject.getString("query")), Functions.toStringFunction());
        Map<String, String> map = new HashMap<String, String>();
        map.put("sql", sqlMap.get("sql") + " limit 0,1");

        DataProviderResult result = cachedDataProviderService.getData(dataObject.getLong("datasource"), map, null);


        if (datasetDao.countExistDatasetName(paramMap) <= 0) {
            ServiceStatus status = transferInterface(jsonObject);
            if (status.getStatus().equals("1")) {
                datasetDao.save(dataset);
            }
            return status;
        } else {
            return new ServiceStatus(ServiceStatus.Status.Fail, "名称已存在");
        }

    }


    public ServiceStatus updateAnalysisParamInfo(String userId, String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        DashboardDataset dataset = new DashboardDataset();

        //保存离线分析数据到本系统数据库
        dataset.setUserId(userId);
        dataset.setId(jsonObject.getLong("id"));
        dataset.setName(jsonObject.getString("name"));
        dataset.setData(jsonObject.getString("data"));
        dataset.setCategoryName(jsonObject.getString("categoryName"));
        dataset.setConfig(jsonObject.getString("config"));
        dataset.setType(1);
        if (StringUtils.isEmpty(dataset.getCategoryName())) {
            dataset.setCategoryName("默认分类");
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("dataset_name", dataset.getName());
        paramMap.put("user_id", dataset.getUserId());
        paramMap.put("dataset_id", dataset.getId());
        paramMap.put("category_name", dataset.getCategoryName());
        if (datasetDao.countExistDatasetName(paramMap) <= 0) {
            ServiceStatus status = transferInterface(jsonObject);
            if (status.getStatus().equals("1")) {
                datasetDao.update(dataset);
            }
            return status;
        } else {
            return new ServiceStatus(ServiceStatus.Status.Fail, "名称已存在");
        }

    }

    /**
     * 调用远程接口保存离线分析配置信息 todo 验证sql查询表是否存在
     *
     * @param jsonObject
     * @return
     */
    private ServiceStatus transferInterface(JSONObject jsonObject) {

        JSONObject config = JSONObject.parseObject(jsonObject.getString("config"));
        Map<String, Object> dataMap = config;

        // cachedDataProviderService.getData();
        //获取前端数据源配置信息
        JSONObject datasourceFrom = JSONObject.parseObject(config.getString("databaseSource"));
        JSONObject datasourceTo = JSONObject.parseObject(config.getString("databaseResult"));
        JSONObject datasourceFromConfig = JSONObject.parseObject(datasourceFrom.getString("config"));
        JSONObject datasourceToConfig = JSONObject.parseObject(datasourceTo.getString("config"));


        DataSourceHolder.setDataSources(DataSourceEnum.dataSourceAnalysis.getKey());
        //通过数据源名称查询是否已存在
        int countFrom = analysisDao.getAnalysisList(datasourceFrom.getString("name"));
        int countTo = analysisDao.getAnalysisList(datasourceTo.getString("name"));
        DataSourceHolder.setDataSources(DataSourceEnum.dataSourceSys.getKey());
        Map<String, Object> dbSourceLinkMap = new HashMap<String, Object>();
        if (countFrom == 0) {

            dbSourceLinkMap.put("url", datasourceFromConfig.get("jdbcurl"));
            dbSourceLinkMap.put("username", datasourceFromConfig.get("username"));
            dbSourceLinkMap.put("password", datasourceFromConfig.get("password"));
            dbSourceLinkMap.put("driverClassName", datasourceFromConfig.get("driver"));

        }
        dataMap.put("dbSourceLink", dbSourceLinkMap);
        dataMap.put("databaseSource", datasourceFrom.get("name"));

        Map<String, Object> dbResultLinkMap = new HashMap<String, Object>();
        if (countTo == 0) {

            dbResultLinkMap.put("url", datasourceToConfig.get("jdbcurl"));
            dbResultLinkMap.put("username", datasourceToConfig.get("username"));
            dbResultLinkMap.put("password", datasourceToConfig.get("password"));
            dbResultLinkMap.put("driverClassName", datasourceToConfig.get("driver"));

        }
        dataMap.put("dbResultLink", dbResultLinkMap);
        dataMap.put("databaseResult", datasourceTo.get("name"));
        //设置count语句
        dataMap.put("sqlCount", "select count(*) from " + config.getString("tableSource"));

        try {
            //调用离线分析保存接口
            HttpClient client = HttpClients.createDefault();
            HttpPost request = new HttpPost("http://192.168.188.58:9016/start/excute");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            String paramsMap = JSONUtils.toJSONString(dataMap);
            System.out.print("paramsMap" + paramsMap);
            StringEntity entityForm = new StringEntity(paramsMap);
            entityForm.setContentType("application/json");
            entityForm.setContentEncoding("UTF-8");

            request.setEntity(entityForm);
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
            String buffer = "";

            StringBuffer sb = new StringBuffer();
            while ((buffer = reader.readLine()) != null) {
                sb.append(buffer);
            }
            reader.close();
            request.releaseConnection();
            JSONObject resObject = JSONObject.parseObject(sb.toString());
            if (resObject.getInteger("code") != 0) {
                return new ServiceStatus(ServiceStatus.Status.Fail, "离线分析配置信息有误：" + sb);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceStatus(ServiceStatus.Status.Fail, "离线分析远程接口调用失败！");
        }
        return new ServiceStatus(ServiceStatus.Status.Success, "离线分析数据信息保存成功！");

    }


}