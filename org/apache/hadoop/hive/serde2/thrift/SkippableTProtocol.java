// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift;

import org.apache.thrift.TException;

public interface SkippableTProtocol
{
    void skip(final byte p0) throws TException;
}
