package org.cboard.services;

import com.alibaba.fastjson.JSONObject;
import org.cboard.dao.CategoryDao;
import org.cboard.pojo.DashboardCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yfyuan on 2016/8/26.
 */
@Repository
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    public ServiceStatus save(String userId, String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        DashboardCategory category = new DashboardCategory();
        category.setUserId(userId);
        category.setName(jsonObject.getString("name"));

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("category_name", category.getName());
        if (categoryDao.countExistCategoryName(paramMap) <= 0) {
            categoryDao.save(category);
            return new ServiceStatus(ServiceStatus.Status.Success, "success");
        } else {
            return new ServiceStatus(ServiceStatus.Status.Fail, "名称已存在");
        }
    }

    public ServiceStatus update(String userId, String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        DashboardCategory category = new DashboardCategory();
        category.setUserId(userId);
        category.setId(jsonObject.getLong("id"));
        category.setName(jsonObject.getString("name"));

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("category_id", category.getId());
        paramMap.put("category_name", category.getName());
        if (categoryDao.countExistCategoryName(paramMap) <= 0) {
            categoryDao.update(category);
            return new ServiceStatus(ServiceStatus.Status.Success, "success");
        } else {
            return new ServiceStatus(ServiceStatus.Status.Fail, "名称已存在");
        }
    }

    public String delete(Long id) {
        categoryDao.delete(id);
        return "1";
    }

    public List<DashboardCategory> getCategoryListByType(String json) {
        JSONObject jsonObject=JSONObject.parseObject(json);
        DashboardCategory category=new DashboardCategory();
        category.setType(jsonObject.getInteger("type"));
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("category_type",category.getType());
        return categoryDao.getCategoryListByType(paramMap);
    }
}
