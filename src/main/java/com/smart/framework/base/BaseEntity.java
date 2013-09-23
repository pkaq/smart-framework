package com.smart.framework.base;

public abstract class BaseEntity extends BaseBean {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
