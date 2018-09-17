package org.cboard.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value={"classpath:config.properties"})
public class AnalysisProperties {


    @Value(value="${off_line_analysis_api}")
    private String analysisAPI;


    public String getAnalysisAPI() {
        return analysisAPI;
    }

    public void setAnalysisAPI(String analysisAPI) {
        this.analysisAPI = analysisAPI;
    }
}
