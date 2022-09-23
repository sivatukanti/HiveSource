// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.hive.metastore.api.ThriftHiveMetastore;

public interface IHMSHandler extends ThriftHiveMetastore.Iface, Configurable
{
    void init() throws MetaException;
}
