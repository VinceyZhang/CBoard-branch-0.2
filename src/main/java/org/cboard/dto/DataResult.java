package org.cboard.dto;

import java.io.Serializable;

/**
 * Created by yfyuan on 2016/8/26.
 */
public class DataResult implements Serializable{

    private Object data;
    private String msg;
    private int resultCount = 0;
    public DataResult() {
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
}
