// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema;

import org.datanucleus.store.StoreManager;

public interface StoreSchemaHandler
{
    void clear();
    
    void createSchema(final Object p0, final String p1);
    
    void deleteSchema(final Object p0, final String p1);
    
    StoreSchemaData getSchemaData(final Object p0, final String p1, final Object[] p2);
    
    StoreManager getStoreManager();
}
