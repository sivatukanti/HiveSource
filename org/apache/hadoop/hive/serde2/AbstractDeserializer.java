// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Writable;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;

public abstract class AbstractDeserializer implements Deserializer
{
    @Override
    public abstract void initialize(final Configuration p0, final Properties p1) throws SerDeException;
    
    @Override
    public abstract Object deserialize(final Writable p0) throws SerDeException;
    
    @Override
    public abstract ObjectInspector getObjectInspector() throws SerDeException;
    
    @Override
    public abstract SerDeStats getSerDeStats();
}
