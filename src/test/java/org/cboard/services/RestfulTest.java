package org.cboard.services;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RestfulTest {


    public static void main(String[] rags) {

        try {

            HttpHost targetHost = new HttpHost("192.168.188.59", 8087, "http");
            DefaultHttpClient httpclient = new DefaultHttpClient();
            //此用户名和密码上生产前需要修改为自己的账户
            httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                    new UsernamePasswordCredentials("vincey", "123456"));

            //调用离线分析保存接口
            // HttpClient client = HttpClientBuilder.create().build();


            // paramsMap.put("pagesParams","123456");

            AuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);

            BasicHttpContext localcontext = new BasicHttpContext();
            localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);


            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("datasetId", "79");

            Map<String, String> pageParams = new HashMap<String, String>();
            pageParams.put("curPage", "1");
            pageParams.put("pageSize", "10");
            paramsMap.put("pagesParams", pageParams);

            String json = JSONUtils.toJSONString(paramsMap);

            System.out.print("paramsMap:" + json);

            HttpPost request = new HttpPost("http://192.168.188.59:8087/dashboard/searchDataByParams.do");
            request.setHeader("Content-type", "application/json; charset=utf-8");
            request.setHeader("Accept", "application/json");

            StringEntity entityForm = new StringEntity(JSONUtils.toJSONString(paramsMap), "UTF-8");
            request.setHeader("Content-Type", "application/json");
            request.setHeader("accept", "application/json");

            request.setEntity(entityForm);


            HttpResponse response = httpclient.execute(request, localcontext);
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
