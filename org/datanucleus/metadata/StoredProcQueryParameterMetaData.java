// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public class StoredProcQueryParameterMetaData extends MetaData
{
    String name;
    String type;
    StoredProcQueryParameterMode mode;
    
    public String getName() {
        return this.name;
    }
    
    public StoredProcQueryParameterMetaData setName(final String name) {
        this.name = name;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public StoredProcQueryParameterMetaData setType(final String type) {
        this.type = type;
        return this;
    }
    
    public StoredProcQueryParameterMode getMode() {
        return this.mode;
    }
    
    public StoredProcQueryParameterMetaData setMode(final StoredProcQueryParameterMode mode) {
        this.mode = mode;
        return this;
    }
}
