package org.cboard.util;

public class PageHelper {


    private Integer pageSize;

    private Integer totalCount;

    private Integer curPage;

    private Integer totalPage;


    public PageHelper() {
    }

    public PageHelper(Integer pageSize, Integer curPage) {
        this.pageSize = pageSize;
        this.curPage = curPage;
    }


    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getCurPage() {
        return curPage;
    }

    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        if(totalCount<=pageSize){
            this.totalPage=1;
            return;
        }
        this.totalPage = totalCount / pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
    }
}
