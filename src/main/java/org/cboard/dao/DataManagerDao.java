package org.cboard.dao;

import org.cboard.pojo.DashboardDataManager;

import java.util.List;
import java.util.Map;

public interface DataManagerDao {
    List<String> getCategoryList();

    List<DashboardDataManager> getDataManagerList(String userId);

    int save(DashboardDataManager dataManager);

    long countExistDataManagerName(Map<String, Object> map);

    int update(DashboardDataManager dataManager);

    int delete(Long id, String userId);

    DashboardDataManager getDataManager(Long id);

    long checkDataManagerRole(String userId, Long widgetId);
}
