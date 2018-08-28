package org.cboard.dto;

import java.io.Serializable;

/**
 * Created by yfyuan on 2016/8/26.
 */
public class DataResult implements Serializable {

    private Object data;
    private String msg;
    private Integer resultCount = 0;
    private Integer curPage;
    private Integer pageSize;
    private Integer totalPage;
    private String sql;
    private Long datasourceId;

    public DataResult() {
    }

    public DataResult(String[][] dataArray, String msg) {
        this.data = dataArray;
        this.msg = msg;
    }


    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Long getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Long datasourceId) {
        this.datasourceId = datasourceId;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getResultCount() {
        return resultCount;
    }


    public Integer getCurPage() {
        return curPage;
    }

    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
