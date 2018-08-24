package org.cboard.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import org.cboard.dao.DataManagerDao;
import org.cboard.dao.DatasourceDao;
import org.cboard.dataprovider.DataProvider;
import org.cboard.dataprovider.DataProviderManager;
import org.cboard.dto.DataProviderResult;
import org.cboard.jdbc.JdbcDataProvider;
import org.cboard.pojo.DashboardDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class DataManagerService extends DataProviderService {
    @Autowired
    DataManagerDao dataManagerDao;

    @Autowired
    DatasourceDao datasourceDao;

    public DataProviderResult getTablesBySource(Long datasourceId) {
        //查找该数据源的连接信息
        DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);
        //通过连接信息去查询该库下所有表
        Map<String, String> query = new HashMap<String, String>();

        JSONObject jsonObject = JSONObject.parseObject(datasource.getConfig());
        String jdbcurl = jsonObject.get("jdbcurl").toString();
        String dbName = jdbcurl.substring(jdbcurl.lastIndexOf("/") + 1, jdbcurl.length());
        query.put("sql", "select table_name from information_schema.tables where table_schema='" + dbName + "' and table_type='base table';");
        DataProviderResult result = getData(datasourceId, query, null);
        return result;
    }


    public DataProviderResult getData(DashboardDatasource datasource ,Map<String, String> query, Long datasetId) {
        String[][] dataArray = null;
        int resultCount = 0;
        String msg = "1";

        if (datasetId != null) {
            Dataset dataset = getDataset(datasetId);
            query = dataset.getQuery();
        }

        try {
            JSONObject config = JSONObject.parseObject(datasource.getConfig());
            DataProvider dataProvider = DataProviderManager.getDataProvider(datasource.getType());
            Map<String, String> parameterMap = Maps.transformValues(config, Functions.toStringFunction());

            dataArray = dataProvider.getData(parameterMap, query);

        } catch (Exception e) {
            msg = e.getMessage();
        }
        return new DataProviderResult(dataArray, msg);
    }

    public DataProviderResult getDatasBySourceAndTable(Long datasourceId, String table) {
        //查找该数据源的连接信息
        DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);
        Map<String, String> query = new HashMap<String, String>();
        JSONObject jsonObject = JSONObject.parseObject(datasource.getConfig());
        String jdbcurl = jsonObject.get("jdbcurl").toString();
        String dbName = jdbcurl.substring(jdbcurl.lastIndexOf("/") + 1, jdbcurl.length());
        query.put("sql", "select * from " + table);
        DataProviderResult result = getData(datasource, query, null);
        return result;
    }
}
