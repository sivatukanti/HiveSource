// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema;

import java.util.Map;

public interface MapStoreSchemaData extends StoreSchemaData
{
    void setParent(final StoreSchemaData p0);
    
    StoreSchemaData getParent();
    
    void addChild(final StoreSchemaData p0);
    
    void clearChildren();
    
    Map getChildren();
    
    StoreSchemaData getChild(final String p0);
    
    int getNumberOfChildren();
}
