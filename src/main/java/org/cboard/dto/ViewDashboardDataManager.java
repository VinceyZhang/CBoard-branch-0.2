package org.cboard.dto;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.cboard.pojo.DashboardDataManager;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by yfyuan on 2016/10/11.
 */
public class ViewDashboardDataManager {
    private Long id;
    private String userId;
    private String name;
    private String categoryName;
    private Map<String, Object> data;


    public static final Function TO = new Function<DashboardDataManager, ViewDashboardDataManager>() {
        @Nullable
        @Override
        public ViewDashboardDataManager apply(@Nullable DashboardDataManager input) {
            return new ViewDashboardDataManager(input);
        }
    };

    public ViewDashboardDataManager(DashboardDataManager dataset) {
        this.id = dataset.getId();
        this.userId = dataset.getUserId();
        this.name = dataset.getName();
        this.categoryName = dataset.getCategoryName();
        this.data = JSONObject.parseObject(dataset.getData());
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
