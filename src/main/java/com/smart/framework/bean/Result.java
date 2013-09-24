package com.smart.framework.bean;

import com.smart.framework.base.BaseBean;

public class Result extends BaseBean {

    private boolean success = true;
    private int error = 0;
    private Object data = null;

    public Result(boolean success) {
        this.success = success;
    }

    public Result data(Object data) {
        this.data = data;
        return this;
    }

    public Result error(int error) {
        this.error = error;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
