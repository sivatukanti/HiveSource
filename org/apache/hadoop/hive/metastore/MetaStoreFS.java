// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;

public interface MetaStoreFS
{
    boolean deleteDir(final FileSystem p0, final Path p1, final boolean p2, final boolean p3, final Configuration p4) throws MetaException;
}
