package com.qijiabin.core.common;

import java.io.Serializable;

/**
 * ========================================================
 * 日 期：2016年6月15日 上午10:35:10
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：封装 RPC 响应
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
@SuppressWarnings("serial")
public class RpcResponse implements Serializable {

    private String requestId;
    private Throwable error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}

