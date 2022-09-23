// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Writable;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;

public abstract class AbstractSerializer implements Serializer
{
    @Override
    public abstract void initialize(final Configuration p0, final Properties p1) throws SerDeException;
    
    @Override
    public abstract Class<? extends Writable> getSerializedClass();
    
    @Override
    public abstract Writable serialize(final Object p0, final ObjectInspector p1) throws SerDeException;
    
    @Override
    public abstract SerDeStats getSerDeStats();
}
