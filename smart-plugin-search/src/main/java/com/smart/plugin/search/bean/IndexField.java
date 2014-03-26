package com.smart.plugin.search.bean;

import com.smart.framework.base.BaseBean;
import com.smart.plugin.search.IndexFieldName;

public class IndexField extends BaseBean {

    private IndexFieldName name;
    private String value;

    public IndexField(IndexFieldName name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name.name();
    }

    public String getValue() {
        return value;
    }
}
