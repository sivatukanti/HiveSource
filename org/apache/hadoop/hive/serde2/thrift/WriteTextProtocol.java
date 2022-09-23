// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift;

import org.apache.thrift.TException;
import org.apache.hadoop.io.Text;

public interface WriteTextProtocol
{
    void writeText(final Text p0) throws TException;
}
