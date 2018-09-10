package org.cboard.datasource;

public enum DataSourceEnum {
    dataSourceSys("dataSourceSys"), dataSourceAnalysis("dataSourceAnalysis");

    private String key;

    DataSourceEnum(String key) { this.key = key; }

    public String getKey() { return key; }

    public void setKey(String key) {  this.key = key; }

}

