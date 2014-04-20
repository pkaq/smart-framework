package org.smart4j.plugin.search.bean;

import org.smart4j.framework.core.bean.BaseBean;
import org.smart4j.plugin.search.IndexFieldName;

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
