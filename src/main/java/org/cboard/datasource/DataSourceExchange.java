package org.cboard.datasource;

import org.aspectj.lang.JoinPoint;

public class DataSourceExchange {

    public void before(JoinPoint point) {

        //获取目标对象的类类型
        Class<?> aClass = point.getTarget().getClass();
        String c = aClass.getName();
        String[] ss = c.split("\\.");
        //获取包名用于区分不同数据源
        String packageName = ss[2];

        if ("product".equals(packageName)) {
            DataSourceHolder.setDataSources(DataSourceEnum.dataSourceSys.getKey());
            System.out.println("数据源："+DataSourceEnum.dataSourceSys.getKey());
        } else {
            DataSourceHolder.setDataSources(DataSourceEnum.dataSourceAnalysis.getKey());
            System.out.println("数据源："+DataSourceEnum.dataSourceAnalysis.getKey());
        }
    }

    /**
     * 执行后将数据源置为空
     */
    public void after() {
        DataSourceHolder.setDataSources(null);
    }

}
