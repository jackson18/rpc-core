package com.qijiabin.core.common;

import java.io.Serializable;

/**
 * ========================================================
 * 日 期：2016年6月15日 上午10:34:41
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：封装 RPC 请求
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
@SuppressWarnings("serial")
public class RpcRequest implements Serializable {

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
