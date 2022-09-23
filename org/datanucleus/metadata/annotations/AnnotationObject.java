// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata.annotations;

import java.util.HashMap;

public class AnnotationObject
{
    String name;
    HashMap<String, Object> nameValueMap;
    
    public AnnotationObject(final String name, final HashMap<String, Object> map) {
        this.name = name;
        this.nameValueMap = map;
    }
    
    public String getName() {
        return this.name;
    }
    
    public HashMap<String, Object> getNameValueMap() {
        return this.nameValueMap;
    }
}
