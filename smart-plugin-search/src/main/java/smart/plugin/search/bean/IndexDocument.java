package smart.plugin.search.bean;

import java.util.ArrayList;
import java.util.List;
import smart.framework.base.BaseBean;

public class IndexDocument extends BaseBean {

    private List<IndexField> indexFieldList = new ArrayList<IndexField>();

    public void addIndexField(IndexField indexField) {
        if (indexField != null) {
            indexFieldList.add(indexField);
        }
    }

    public List<IndexField> getIndexFieldList() {
        return indexFieldList;
    }
}
