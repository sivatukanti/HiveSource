// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.metadata.JoinMetaData;

public interface SecondaryDatastoreClass extends DatastoreClass
{
    DatastoreClass getPrimaryDatastoreClass();
    
    JoinMetaData getJoinMetaData();
}
