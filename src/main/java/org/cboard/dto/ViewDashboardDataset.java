package org.cboard.dto;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.cboard.pojo.DashboardDataset;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by yfyuan on 2016/10/11.
 */
public class ViewDashboardDataset {
    private Long id;
    private String userId;
    private String name;
    private String categoryName;
    private Integer type;
    private Map<String, Object> data;


    public static final Function TO = new Function<DashboardDataset, ViewDashboardDataset>() {
        @Nullable
        @Override
        public ViewDashboardDataset apply(@Nullable DashboardDataset input) {
            return new ViewDashboardDataset(input);
        }
    };

    public ViewDashboardDataset(DashboardDataset dataManager) {
        this.id = dataManager.getId();
        this.userId = dataManager.getUserId();
        this.name = dataManager.getName();
        this.categoryName = dataManager.getCategoryName();
        this.data = JSONObject.parseObject(dataManager.getData());
        this.type = dataManager.getType();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
