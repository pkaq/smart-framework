package org.smart4j.framework.mvc.pojo;

import java.util.ArrayList;
import java.util.List;
import org.smart4j.framework.core.bean.BaseBean;

public class Multiparts extends BaseBean {

    private List<Multipart> multipartList = new ArrayList<Multipart>();

    public Multiparts(List<Multipart> multipartList) {
        this.multipartList = multipartList;
    }

    public int size() {
        return multipartList.size();
    }

    public List<Multipart> getAll() {
        return multipartList;
    }

    public Multipart getOne() {
        return size() == 1 ? multipartList.get(0) : null;
    }
}
