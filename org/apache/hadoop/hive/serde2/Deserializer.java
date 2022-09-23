// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Writable;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;

@Deprecated
public interface Deserializer
{
    void initialize(final Configuration p0, final Properties p1) throws SerDeException;
    
    Object deserialize(final Writable p0) throws SerDeException;
    
    ObjectInspector getObjectInspector() throws SerDeException;
    
    SerDeStats getSerDeStats();
}
