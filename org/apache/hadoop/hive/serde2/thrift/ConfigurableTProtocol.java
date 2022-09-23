// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift;

import org.apache.thrift.TException;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;

public interface ConfigurableTProtocol
{
    void initialize(final Configuration p0, final Properties p1) throws TException;
}
