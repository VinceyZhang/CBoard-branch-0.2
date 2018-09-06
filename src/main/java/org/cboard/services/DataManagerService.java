package org.cboard.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.cboard.dao.DataManagerDao;
import org.cboard.dao.DatasetDao;
import org.cboard.dao.DatasourceDao;
import org.cboard.dataprovider.DataProvider;
import org.cboard.dataprovider.DataProviderManager;
import org.cboard.dto.DataProviderResult;
import org.cboard.dto.DataResult;
import org.cboard.pojo.DashboardDataManager;
import org.cboard.pojo.DashboardDatasource;
import org.cboard.util.HandleExcel;
import org.cboard.util.PageHelper;
import org.cboard.util.PathTool;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;


@Repository
public class DataManagerService extends DataProviderService {

    @Autowired
    DatasourceDao datasourceDao;

    @Autowired
    DatasetDao datasetDao;

    @Autowired
    DataManagerDao dataManagerDao;

    @Autowired
    CachedDataProviderService cachedDataProviderService;

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

    public ServiceStatus importData(Long datasourceId, Long datasetId, MultipartFile file) {

        if(file==null){
            return new ServiceStatus(ServiceStatus.Status.Fail,"请先选择一份文件！");
        }

        // 先判断上传的文件是不是excel
        if (!file.getOriginalFilename().matches("^.+\\.(?i)(xls|xlsx)$")) {
            return new ServiceStatus(ServiceStatus.Status.Fail, "上传的文件格式不正确，请选择excel文件！");
        }
        ServiceStatus serviceStatus = null;
        String filePath = PathTool.getRealPath();
        File uploadPath = new File(filePath);
        if (!uploadPath.isDirectory()) {
            uploadPath.mkdirs();
        }

        try {
            String savePath = filePath + file.getOriginalFilename();
            File saveFile = new File(savePath);
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
            file.transferTo(saveFile);
            String absolutePath = saveFile.getAbsolutePath();
            // 解析excel
            List<Map<String, String>> readExcel = HandleExcel.readExcel(absolutePath);

            Dataset dataset = getDataset(datasetId);
            datasourceId = dataset.getDatasourceId();
            Map<String, String> query = dataset.getQuery();

            DataProviderResult result = cachedDataProviderService.getData(datasourceId, query, null);
            String[] columns = result.getData()[0];
            Set<String> columnsExcel = readExcel.get(0).keySet();

            //判断excel的列是否为需要的列

            for (String colExc : columnsExcel) {
                int isEqual = 0;
                for (String col : columns) {
                    if (col.toLowerCase().equals(colExc.toLowerCase())) {
                        continue;
                    } else {
                        isEqual++;
                    }
                }
                if (isEqual == columns.length) {
                    return new ServiceStatus(ServiceStatus.Status.Fail, "\"" + colExc + "\"列与数据库的列名不匹配，导入失败！");
                }

            }
            // excel解析数据入库
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            for (Map<String, String> map : readExcel) {
                list.add(map);
            }

            List<String> colList = new ArrayList<String>(Arrays.asList(columns));
            // 分批插入数据库
            serviceStatus = insertData(datasourceId, datasetId, list, colList);


        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceStatus(ServiceStatus.Status.Fail, e.getMessage());
        }

        return serviceStatus;
    }

    public ServiceStatus insertData(Long datasourceId, Long datasetId, List<Map<String, String>> mapList, List<String> colList) {

        String msg = "";

        Dataset dataset = getDataset(datasetId);
        datasourceId = dataset.getDatasourceId();

        Map<String, String> queryMap = dataset.getQuery();
        StringBuffer tempTable = new StringBuffer(queryMap.get("sql").trim());

        String table = tempTable.substring(tempTable.lastIndexOf(" ") + 1, tempTable.length());
        System.out.println(table);
        DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);
        try {
            JSONObject config = JSONObject.parseObject(datasource.getConfig());
            Map<String, String> parameterMap = Maps.transformValues(config, Functions.toStringFunction());
            DataProvider dataProvider = DataProviderManager.getDataProvider(datasource.getType());
            List<String> query = new ArrayList<String>();

            StringBuffer sbCol = new StringBuffer("insert into " + table + "(");
            for (String col : colList) {
                sbCol.append(col + ",");
            }
            sbCol.append(")");
            sbCol.deleteCharAt(sbCol.lastIndexOf(","));


            for (Map<String, String> map : mapList) {

                StringBuffer sbVal = new StringBuffer(" values (");
                for (String col : colList) {
                    if (map.get(col) != null) {
                        sbVal.append("'" + map.get(col) + "',");
                    } else {
                        sbVal.append("null,");
                    }
                }

                sbVal.append(")");
                StringBuffer sql = new StringBuffer();
                sql.append(sbCol);
                sql.append(sbVal.deleteCharAt(sbVal.lastIndexOf(",")));
                query.add(sql.toString());


            }


            dataProvider.insertData(parameterMap, query);
        } catch (Exception e) {
            msg = e.getMessage();
            return new ServiceStatus(ServiceStatus.Status.Fail, msg);
        }

        return new ServiceStatus(ServiceStatus.Status.Success, "数据导入成功！");
    }


    /**
     * 保存数据集，即保存sql
     *
     * @param userId
     * @param json
     * @return
     */
    public ServiceStatus save(String userId, String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        DashboardDataManager dataManager = new DashboardDataManager();
        dataManager.setUserId(userId);
        dataManager.setName(jsonObject.getString("name"));
        dataManager.setData(jsonObject.getString("data"));
        dataManager.setCategoryName(jsonObject.getString("categoryName"));
        if (StringUtils.isEmpty(dataManager.getCategoryName())) {
            dataManager.setCategoryName("默认分类");
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("datamanager_name", dataManager.getName());
        paramMap.put("user_id", dataManager.getUserId());
        paramMap.put("category_name", dataManager.getCategoryName());
        if (dataManagerDao.countExistDataManagerName(paramMap) <= 0) {
            dataManagerDao.save(dataManager);
            return new ServiceStatus(ServiceStatus.Status.Success, "success");
        } else {
            return new ServiceStatus(ServiceStatus.Status.Fail, "Duplicated name");
        }
    }

    public ServiceStatus update(String userId, String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        DashboardDataManager dataManager = new DashboardDataManager();
        dataManager.setUserId(userId);
        dataManager.setId(jsonObject.getLong("id"));
        dataManager.setName(jsonObject.getString("name"));
        dataManager.setCategoryName(jsonObject.getString("categoryName"));
        dataManager.setData(jsonObject.getString("data"));
        if (StringUtils.isEmpty(dataManager.getCategoryName())) {
            dataManager.setCategoryName("默认分类");
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("datamanager_name", dataManager.getName());
        paramMap.put("user_id", dataManager.getUserId());
        paramMap.put("datamanager_id", dataManager.getId());
        paramMap.put("category_name", dataManager.getCategoryName());
        if (dataManagerDao.countExistDataManagerName(paramMap) <= 0) {
            dataManagerDao.update(dataManager);
            return new ServiceStatus(ServiceStatus.Status.Success, "success");
        } else {
            return new ServiceStatus(ServiceStatus.Status.Fail, "Duplicated name");
        }
    }

    public List<DashboardDataManager> getDataManagerList(String userId) {
        return dataManagerDao.getDataManagerList(userId);
    }

    public DashboardDataManager getDataManagerById(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        DashboardDataManager dataManager = new DashboardDataManager();
        dataManager.setId(jsonObject.getLong("id"));

        return dataManagerDao.getDataManager(dataManager.getId());
    }


    /**
     * 分页获取数据
     *
     * @param datasourceId
     * @param query
     * @param params
     * @param pagesParams
     * @param datasetId
     * @return
     */
    public DataProviderResult getData(Long datasourceId, Map<String, String> query, String params, String pagesParams, Long datasetId) {

        PageHelper pageHelper = new PageHelper();
        pageHelper.setCurPage(1);
        pageHelper.setPageSize(10);
        if (pagesParams != null) {
            JSONObject pgObject = JSONObject.parseObject(pagesParams);
            pageHelper.setCurPage(pgObject.getInteger("curPage") == null ? 1 : pgObject.getInteger("curPage"));
            pageHelper.setPageSize(pgObject.getInteger("pageSize") == null ? 2 : pgObject.getInteger("pageSize"));
        }

        if (datasetId != null) {
            Dataset dataset = getDataset(datasetId);
            datasourceId = dataset.getDatasourceId();
            query = dataset.getQuery();
        }
        Map<String, String> paramsMap = null;
        if (params != null) {
            JSONObject paObject = JSONObject.parseObject(params);
            paramsMap = Maps.transformValues(paObject, Functions.toStringFunction());

            StringBuffer sql = new StringBuffer(query.get("sql"));
            if (paramsMap != null && paramsMap.size() > 0) {
                sql.append(" where 1=1 ");
                Iterator<Map.Entry<String, String>> iterator = paramsMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    sql.append(" and ");
                    sql.append(entry.getKey() + " = \'" + entry.getValue() + "\' ");

                }
            }
            query = new HashMap<String, String>();
            query.put("sql", sql.toString());
        }

        StringBuffer sbSql = new StringBuffer(query.get("sql"));
        sbSql.append(" limit " + (pageHelper.getCurPage() - 1) * pageHelper.getPageSize() + "," + pageHelper.getPageSize());

        DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);

        JSONObject config = JSONObject.parseObject(datasource.getConfig());

        Map<String, String> parameterMap = Maps.transformValues(config, Functions.toStringFunction());
        Integer resultCount = null;
        try {
            DataProvider dataProvider = DataProviderManager.getDataProvider(datasource.getType());
            resultCount = dataProvider.resultCount(parameterMap, query);
            pageHelper.setTotalCount(resultCount);
        } catch (Exception e) {
        }
        Map<String, String> q = new HashMap<String, String>();
        q.put("sql", sbSql.toString());
        pageHelper.setTotalPage(resultCount);
        DataProviderResult result = cachedDataProviderService.getData(datasourceId, q, null);
        result.setTotalPage(pageHelper.getTotalPage());
        result.setPageSize(pageHelper.getPageSize());
        result.setCurPage(pageHelper.getCurPage());
        return result;
    }

    public ServiceStatus delete(String userId, Long id) {
        dataManagerDao.delete(id, userId);
        return new ServiceStatus(ServiceStatus.Status.Success, "success");
    }

    public DataResult getData(DashboardDatasource datasource, Map<String, String> query, Long datasetId) {
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
        return new DataResult(dataArray, msg);
    }

    public DataResult getDatasBySourceAndTable(Long datasourceId, String table, PageHelper pageHelper, Object params) {
        //查找该数据源的连接信息
        DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);
        Map<String, String> query = new HashMap<String, String>();
        JSONObject jsonObject = JSONObject.parseObject(datasource.getConfig());
        String jdbcurl = jsonObject.get("jdbcurl").toString();
        String dbName = jdbcurl.substring(jdbcurl.lastIndexOf("/") + 1, jdbcurl.length());
        StringBuffer sbSql = new StringBuffer("select * from " + table);


        if (params != null) {

            Map<String, String> mapParam = (Map<String, String>) params;
            sbSql.append(" where ");
            int i = 0;
            for (String key : mapParam.keySet()) {
                i++;
                mapParam.get(key);
                sbSql.append(key + " ='" + mapParam.get(key) + "' ");
                if (i < mapParam.size()) {
                    sbSql.append(" and ");
                }

            }
        }


        int curPage = pageHelper.getCurPage() == null ? 1 : pageHelper.getCurPage();
        int pageSize = pageHelper.getPageSize() == null ? 10 : pageHelper.getPageSize();
        pageHelper.setCurPage(curPage);
        pageHelper.setPageSize(pageSize);
        StringBuffer countSql = new StringBuffer("select count(*) from " + table);
        query.put("sql", countSql.toString());
        DataResult countResult = getData(datasource, query, null);
        String[][] strArr = (String[][]) countResult.getData();
        pageHelper.setTotalCount(Integer.parseInt(strArr[1][0]));
        sbSql.append(" limit " + pageHelper.getPageSize() * (pageHelper.getCurPage() - 1) + "," + pageHelper.getPageSize());
        query.put("sql", sbSql.toString());
        DataResult result = getData(datasource, query, null);
        result.setTotalPage(pageHelper.getTotalPage());
        if (params == null) {
            result.setSql(sbSql.toString());
            result.setDatasourceId(datasourceId);
        }
        return result;
    }

    public DataResult getColumnsBySourceAndTable(Long datasourceId, String table) {
        //查找该数据源的连接信息
        DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);
        Map<String, String> query = new HashMap<String, String>();
        query.put("sql", "select column_name from information_schema.columns  where table_name='" + table + "'");
        DataResult result = getData(datasource, query, null);
        return result;
    }

    public DataResult getDataSources(Long datasourceId) {
        DataResult result = new DataResult();
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
