// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.request;

public enum RequestType
{
    INSERT("insert"), 
    UPDATE("update"), 
    DELETE("delete"), 
    FETCH("fetch"), 
    LOCATE("locate");
    
    private String name;
    
    private RequestType(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
