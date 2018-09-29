package org.cboard.services;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RestfulTest {


    public static void main(String[] rags) {

        try {
            //调用离线分析保存接口
            HttpClient client = HttpClients.createDefault();
            HttpPost request = new HttpPost("http://192.168.188.59:8087/dashboard/getCachedDataByParams.do");
            request.addHeader("Content-type", "application/json; charset=utf-8");
            request.setHeader("Accept", "application/json");
            Map<String, String> dataMap = new HashMap<String, String>();
            String paramsMap = JSONUtils.toJSONString(dataMap);
            System.out.print("paramsMap" + paramsMap);
            StringEntity entityForm = new StringEntity(paramsMap, "UTF-8");
            entityForm.setContentType("application/json");

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

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
