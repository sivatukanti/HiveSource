// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import java.lang.reflect.Method;

public class ReplayLog
{
    private Object target;
    private Method method;
    private Object[] args;
    
    public ReplayLog(final Object target, final Method method, final Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public void setMethod(final Method method) {
        this.method = method;
    }
    
    public Object[] getArgs() {
        return this.args;
    }
    
    public void setArgs(final Object[] args) {
        this.args = args;
    }
    
    public Object getTarget() {
        return this.target;
    }
    
    public void setTarget(final Object target) {
        this.target = target;
    }
    
    @Override
    public String toString() {
        return ((this.target == null) ? "" : this.target.getClass().getName()) + "." + ((this.method == null) ? "" : this.method.getName()) + " with args " + ((this.args == null) ? "null" : this.args);
    }
}
