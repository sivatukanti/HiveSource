// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.HashMap;
import java.util.Map;

public class EventListenerMetaData extends MetaData
{
    String className;
    Map<String, String> methodNamesByCallbackName;
    
    public EventListenerMetaData(final String className) {
        this.methodNamesByCallbackName = new HashMap<String, String>();
        this.className = className;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public void addCallback(final String callbackClassName, final String methodName) {
        this.addCallback(callbackClassName, this.className, methodName);
    }
    
    public void addCallback(final String callbackClassName, final String className, final String methodName) {
        if (this.methodNamesByCallbackName == null) {
            this.methodNamesByCallbackName = new HashMap<String, String>();
        }
        if (this.methodNamesByCallbackName.get(callbackClassName) != null) {
            return;
        }
        this.methodNamesByCallbackName.put(callbackClassName, className + '.' + methodName);
    }
    
    public String getMethodNameForCallbackClass(final String callbackClassName) {
        return this.methodNamesByCallbackName.get(callbackClassName);
    }
}
