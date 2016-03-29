package com.yabadun.util;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * ClassMethod
 *
 * @author panjn
 * @date 2016/3/29
 */
public class ClassMethod implements Serializable {
    private static final long serialVersionUID = 1L;

    private Object obj;
    private Method method;

    public ClassMethod(Object obj, Method method) {
        this.obj = obj;
        this.method = method;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
