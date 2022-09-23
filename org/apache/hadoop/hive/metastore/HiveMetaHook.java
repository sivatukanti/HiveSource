// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;

public interface HiveMetaHook
{
    void preCreateTable(final Table p0) throws MetaException;
    
    void rollbackCreateTable(final Table p0) throws MetaException;
    
    void commitCreateTable(final Table p0) throws MetaException;
    
    void preDropTable(final Table p0) throws MetaException;
    
    void rollbackDropTable(final Table p0) throws MetaException;
    
    void commitDropTable(final Table p0, final boolean p1) throws MetaException;
}
