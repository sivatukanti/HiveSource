// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.connection;

public enum ConnectionResourceType
{
    JTA("JTA"), 
    RESOURCE_LOCAL("RESOURCE_LOCAL");
    
    String name;
    
    private ConnectionResourceType(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
