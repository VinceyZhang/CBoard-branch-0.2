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
import org.cboard.dao.DatasetDao;
import org.cboard.pojo.DashboardDataset;
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
    private DatasetService datasetService;

    public ServiceStatus saveAnalysisParamInfo(String userId, String json) {

        HttpClient client = HttpClients.createDefault();
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            DashboardDataset dataset = new DashboardDataset();

            JSONObject config=JSONObject.parseObject(jsonObject.getString("config"));

            HttpPost request = new HttpPost("http://192.168.188.158:9016/start/excute");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");


            Map<String, String> map = Maps.transformValues(config, Functions.toStringFunction());
//            map.put("taskId", "test_test");
//            map.put("sqlCount", "select count(*) from company_info");
//            map.put("sqlSelect", "select * from company_info");
//            map.put("databaseSource", "123");
//            map.put("databaseResult", "66");
//            map.put("dbResultName", "test");
//            map.put("tableResult", "company_info");

//            String paramsMap = JSONUtils.toJSONString(map);
//            StringEntity entityForm = new StringEntity(paramsMap);
//            entityForm.setContentType("application/json");
//            entityForm.setContentEncoding("UTF-8");
//
//            request.setEntity(entityForm);
//            HttpResponse response = client.execute(request);
//            HttpEntity entity = response.getEntity();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
//            String buffer = "";
//            StringBuffer sb = new StringBuffer();
//            while ((buffer = reader.readLine()) != null) {
//                sb.append(buffer);
//            }
//            reader.close();
//            request.releaseConnection();
//            System.out.println("entity:" + sb.toString());

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
            if (datasetDao.countExistDatasetName(paramMap) <= 0) {
                datasetDao.save(dataset);
            } else {
                return new ServiceStatus(ServiceStatus.Status.Fail, "Duplicated name");
            }
            return new ServiceStatus(ServiceStatus.Status.Success, "离线分析数据信息保存成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
