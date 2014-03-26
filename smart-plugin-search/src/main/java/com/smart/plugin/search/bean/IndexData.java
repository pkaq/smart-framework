package com.smart.plugin.search.bean;

import com.smart.framework.base.BaseBean;
import java.util.ArrayList;
import java.util.List;

public class IndexData extends BaseBean {

    private List<IndexDocument> indexDocumentList = new ArrayList<IndexDocument>();

    public void addIndexDocument(IndexDocument indexDocument) {
        if (indexDocument != null) {
            indexDocumentList.add(indexDocument);
        }
    }

    public List<IndexDocument> getIndexDocumentList() {
        return indexDocumentList;
    }
}
