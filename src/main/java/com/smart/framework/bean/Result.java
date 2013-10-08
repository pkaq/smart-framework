package com.smart.framework.bean;

import com.smart.framework.base.BaseBean;

public class Result extends BaseBean {

    private boolean success = true; // 成功标志
    private int error = 0;          // 错误代码
    private Object data = null;     // 相关数据

    // 构造结果对象（设置成功标志）
    public Result(boolean success) {
        this.success = success;
    }

    // 构造结果对象（添加相关数据）
    public Result data(Object data) {
        this.data = data;
        return this;
    }

    // 构造结果对象（添加错误代码）
    public Result error(int error) {
        this.error = error;
        return this;
    }

    // 判断是否成功
    public boolean isSuccess() {
        return success;
    }

    // 设置成功标志
    public void setSuccess(boolean success) {
        this.success = success;
    }

    // 获取错误代码
    public int getError() {
        return error;
    }

    // 设置错误代码
    public void setError(int error) {
        this.error = error;
    }

    // 获取相关数据
    public Object getData() {
        return data;
    }

    // 设置相关数据
    public void setData(Object data) {
        this.data = data;
    }
}
