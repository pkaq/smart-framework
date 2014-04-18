package smart.plugin.search.bean;

import java.util.ArrayList;
import java.util.List;
import smart.framework.base.BaseBean;

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
