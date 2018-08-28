/**
 * 查询
 */
function searchByConditions() {
    var params = {};
    $(".condition-info").each(
        function () {
            var key = $(this).find("select").val();
            var val = $(this).find("input").val();
            params[key] = val;
        }
    );
    var data = {
        "datasourceId": $("#datasources").val(),
        "table": $("#tables").val(),
        "pageSize": 10,
        "curPage": 1,
        "params": params
    };

    getDataByTable(data);
}

/**
 * 根据数据源获取表名
 */
function getTablesBySource(obj) {

    var id = $(obj).find("option:selected").val();
    $.ajax({
        url: "/datamanager/getTablesBySource.do",
        data: {datasourceId: id},
        success: function (data) {
            if (data) {

                var tables = data.data;
                if (tables != null) {
                    $("#tables option").remove();
                    for (var i = 1; i < tables.length; i++) {
                        $("#tables").append("<option value='" + tables[i] + "' >"
                            + tables[i] + "</option>");
                    }
                } else {
                    $("#tables").find("option").remove();
                }
            }
        }
    })
}

/**
 * a标签点击查询数据
 * @param obj
 */
function getDataFromA(obj) {
    var data = {
        "datasourceId": $("#datasources").val(),
        "table": $("#tables").val(),
        "pageSize": 10,
        "curPage": $(obj).text()
    };

    getDataByTable(data);
    $(obj).parents("li").addClass("active");
    $(obj).parents("li").siblings(".active").removeClass("active");
}

/**
 * select点击查询数据
 * @param obj
 */
function getDataFromSelect(obj) {
    var data = {
        "datasourceId": $("#datasources").val(),
        "table": $(obj).val(),
        "pageSize": 10,
        "curPage": 1
    };

    getDataByTable(data);
}

/**
 *
 * 根据表名获取数据
 */
function getDataByTable(data) {

    //查询列
    $.ajax({
        contentType: "application/json",
        type: "post",
        url: "/datamanager/getColumnsByTable.do",
        data: JSON.stringify(data),
        success: function (data) {

            var columns = data.data;
            if (columns) {
                $("#columns option").remove();
                for (var i = 1; i < columns.length; i++) {
                    $("#columns").append("<option>" + columns[i] + "</option>");
                }
            }

        }
    })


    $.ajax({
            contentType: "application/json",
            url: "/datamanager/getDataByTable.do",
            type: "post",
            data: JSON.stringify(data),
            success: function (data) {
                var datas = data.data;
                if (datas != null) {

                    $("#dataList").html("");
                    //生成抬头
                    var columns = datas[0];

                    //生成表格数据
                    $("#dataList").append("<thead><tr class=''>");
                    for (var i in columns) {
                        $("#dataList").append("<th>" + columns[i] + "</th>");
                    }
                    $("#dataList").append("</tr></thead>");
                    for (var i = 1; i < datas.length; i++) {
                        var html = "<tr class='active'>";
                        for (var j in datas[i]) {
                            html += "<td class='info'>" + datas[i][j] + "</td>";
                        }
                        html += "</tr>"
                        $("#dataList").append(html);
                    }

                    //生成分页
                    var totalPage = data.tatalPage
                    $(".pagination li").remove();
                    for (var i = 1; i < 10; i++) {
                        $(".pagination").append("<li><a href='javascript:void(0);' onclick='javascript:getDataFromA(this)'>" + i + "</a></li>");
                    }
                }
            }

        }
    )
}
;

$(function () {

    /**
     * 获取各种数据源
     * */
    $.ajax({
        url: "/datamanager/getDataSources.do",

        success: function (data) {

            if (data) {
                var sources = data.data;
                for (var i in sources) {
                    $("#datasources").append("<option value='" + sources[i].id + "' >"
                        + sources[i].name + "</option>");
                }
            }
        }
    })


})