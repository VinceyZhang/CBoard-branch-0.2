package org.cboard.dao;

import org.cboard.pojo.DashboardWidget;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by yfyuan on 2016/8/122.
 */
@Repository
public interface WidgetDao {

    List<String> getCategoryList();

    List<DashboardWidget> getWidgetList(String userId);

    int save(DashboardWidget dashboardWidget);

    long countExistWidgetName(Map<String, Object> map);

    int update(DashboardWidget dashboardWidget);

    int delete(Long id, String userId);

    DashboardWidget getWidget(Long id);

    long checkWidgetRole(String userId, Long widgetId);

    List<DashboardWidget> getWidgetListByType(Map<String, Object> map);

    DashboardWidget getWidgetById(Map<String, Object> params);
}
