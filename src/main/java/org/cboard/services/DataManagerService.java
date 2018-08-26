package org.cboard.services;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import org.cboard.dao.DataManagerDao;
import org.cboard.dao.DatasourceDao;
import org.cboard.dataprovider.DataProvider;
import org.cboard.dataprovider.DataProviderManager;
import org.cboard.dto.DataProviderResult;
import org.cboard.dto.DataResult;
import org.cboard.pojo.DashboardDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class DataManagerService extends DataProviderService {

    @Autowired
    DatasourceDao datasourceDao;

    public DataProviderResult getTablesBySource(Long datasourceId) {
        //查找该数据源的连接信息
        DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);
        //通过连接信息去查询该库下所有表
        Map<String, String> query = new HashMap<String, String>();

        query.put("sql", "select table_name from information_schema.tables where table_schema='" + datasource.getName()
                + "' and table_type='base table';");
        DataProviderResult result = getData(datasourceId, query, null);
        return result;
    }


    public DataProviderResult getData(DashboardDatasource datasource, Map<String, String> query, Long datasetId) {
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

    public DataProviderResult getDatasBySourceAndTable(Long datasourceId, String table, Object params) {
        //查找该数据源的连接信息
        DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);
        Map<String, String> query = new HashMap<String, String>();
        StringBuffer sbSql = new StringBuffer("select * from " + table);
        if (params != null) {

            Map<String, String> mapParam = (Map<String, String>) params;
            sbSql.append(" where ");
            for (String key : mapParam.keySet()) {
                mapParam.get(key);
                sbSql.append(key + " ='" + mapParam.get(key) + "' ");
                sbSql.append(" and ");
            }
        }
        sbSql.append(" 1=1 ");
        query.put("sql", sbSql.toString());
        DataProviderResult result = getData(datasource, query, null);
        return result;
    }

    public DataProviderResult getColumnsBySourceAndTable(Long datasourceId, String table, Object params) {
        //查找该数据源的连接信息
        DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);
        Map<String, String> query = new HashMap<String, String>();
        query.put("sql", "select column_name from information_schema.columns  where table_name='" + table + "'");
        DataProviderResult result = getData(datasource, query, null);
        return result;
    }

    public DataResult getDataSources(Long datasourceId) {
        DataResult result=new DataResult();
        if (datasourceId != null) {
            DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);
            result.setMsg("Get  datasource by id");
            result.setData(datasource);
            return result;
        }
        List<DashboardDatasource> datasources = datasourceDao.getDatasources();
        result.setMsg("Get  all datasources");
        result.setData(datasources);
        return result;
    }

}
