// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema;

import java.util.List;

public interface ListStoreSchemaData extends StoreSchemaData
{
    void setParent(final StoreSchemaData p0);
    
    StoreSchemaData getParent();
    
    void addChild(final StoreSchemaData p0);
    
    void clearChildren();
    
    List getChildren();
    
    StoreSchemaData getChild(final int p0);
    
    int getNumberOfChildren();
}
