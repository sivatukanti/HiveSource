// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;

public interface HiveMetaHookLoader
{
    HiveMetaHook getHook(final Table p0) throws MetaException;
}
