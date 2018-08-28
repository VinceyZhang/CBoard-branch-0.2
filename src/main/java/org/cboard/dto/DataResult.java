package org.cboard.dto;

import java.io.Serializable;

/**
 * Created by yfyuan on 2016/8/26.
 */
public class DataResult implements Serializable{

    private Object data;
    private String msg;
    private int resultCount = 0;
    private Integer curPage;
    private Integer pageSize;
    private Integer totalPage;

    public DataResult() {
    }

    public DataResult(String[][] dataArray, String msg) {
        this.data=dataArray;
        this.msg=msg;
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

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
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
