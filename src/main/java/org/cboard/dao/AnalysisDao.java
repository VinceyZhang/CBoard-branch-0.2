package org.cboard.dao;

import org.cboard.pojo.DashboardBoard;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by yfyuan on 2016/8/23.
 */
@Repository
public interface AnalysisDao {



    int getAnalysisList(String userId);


}
