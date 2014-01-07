package com.smart.framework.bean;

import com.smart.framework.base.BaseBean;
import java.util.List;

public class Pager<T> extends BaseBean {

    private int pageNumber;     // 页面编号
    private int pageSize;       // 每页条数
    private long totalRecord;   // 总记录数
    private List<T> recordList; // 数据列表

    public Pager(int pageNumber, int pageSize, long totalRecord, List<T> recordList) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalRecord = totalRecord;
        this.recordList = recordList;
    }

    public long getTotalPage() {
        return totalRecord % pageSize == 0 ? totalRecord / pageSize : totalRecord / pageSize + 1;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalRecord() {
        return totalRecord;
    }

    public List<T> getRecordList() {
        return recordList;
    }
}
