package org.cboard.controller.rest;

import org.cboard.dto.DataProviderResult;
import org.cboard.services.DataManagerService;
import org.cboard.util.TransferTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/open")
public class OpenController {

    @Autowired
    DataManagerService dataManagerService;

    @RequestMapping(value = "/searchDataByParams/{datasetId}", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public DataProviderResult searchDataByParams(@PathVariable Long datasetId, @RequestBody Map<String, Object> map) {
        DataProviderResult result = new DataProviderResult();

        if (datasetId == null) {
            result.setMsg("数据集id不能为空！");
            return result;
        }
        String paramsStr = null;
        if (map.get("params") != null) {
            Map<String, Object> paramsMap = (Map<String, Object>) map.get("params");
            paramsStr = TransferTool.mapToString(paramsMap);
        }
        String pagesParamsStr = null;
        if (map.get("pagesParams") != null) {
            Map<String, Object> pagesParamsMap = (Map<String, Object>) map.get("pagesParams");
            pagesParamsStr = TransferTool.mapToString(pagesParamsMap);
        }
        result = dataManagerService.getData(paramsStr, pagesParamsStr, datasetId);
        return result;
    }
}
