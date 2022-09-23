// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp.proxy;

import java.util.HashMap;
import java.util.Map;

public class TransactionRecoveryResult
{
    private Object result;
    private Map<Object, Object> replaceTarget;
    
    public TransactionRecoveryResult() {
        this.replaceTarget = new HashMap<Object, Object>();
    }
    
    public Object getResult() {
        return this.result;
    }
    
    public void setResult(final Object result) {
        this.result = result;
    }
    
    public Map<Object, Object> getReplaceTarget() {
        return this.replaceTarget;
    }
}
