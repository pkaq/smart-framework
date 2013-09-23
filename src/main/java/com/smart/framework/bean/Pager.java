package com.smart.framework.bean;

import com.smart.framework.base.BaseBean;
import java.util.List;

public class Pager<T> extends BaseBean {

    private int pageNumber;     // 页面编号
    private int pageSize;       // 每页条数
    private int totalRecord;    // 总记录数
    private List<T> recordList; // 数据列表

    public Pager(int pageNumber, int pageSize, int totalRecord, List<T> recordList) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalRecord = totalRecord;
        this.recordList = recordList;
    }

    public int getTotalPage() {
        return totalRecord % pageSize == 0 ? totalRecord / pageSize : totalRecord / pageSize + 1;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public List<T> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<T> recordList) {
        this.recordList = recordList;
    }
}
