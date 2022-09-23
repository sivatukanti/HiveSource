// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.AlreadyExistsException;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.Partition;
import java.util.List;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.conf.Configurable;

public interface AlterHandler extends Configurable
{
    void alterTable(final RawStore p0, final Warehouse p1, final String p2, final String p3, final Table p4) throws InvalidOperationException, MetaException;
    
    void alterTable(final RawStore p0, final Warehouse p1, final String p2, final String p3, final Table p4, final boolean p5) throws InvalidOperationException, MetaException;
    
    Partition alterPartition(final RawStore p0, final Warehouse p1, final String p2, final String p3, final List<String> p4, final Partition p5) throws InvalidOperationException, InvalidObjectException, AlreadyExistsException, MetaException;
    
    List<Partition> alterPartitions(final RawStore p0, final Warehouse p1, final String p2, final String p3, final List<Partition> p4) throws InvalidOperationException, InvalidObjectException, AlreadyExistsException, MetaException;
}
