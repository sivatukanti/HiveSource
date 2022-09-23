// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Writable;
import javax.annotation.Nullable;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;

public abstract class AbstractSerDe implements SerDe
{
    protected String configErrors;
    
    public void initialize(final Configuration configuration, final Properties tableProperties, final Properties partitionProperties) throws SerDeException {
        this.initialize(configuration, SerDeUtils.createOverlayedProperties(tableProperties, partitionProperties));
    }
    
    @Deprecated
    @Override
    public abstract void initialize(@Nullable final Configuration p0, final Properties p1) throws SerDeException;
    
    @Override
    public abstract Class<? extends Writable> getSerializedClass();
    
    @Override
    public abstract Writable serialize(final Object p0, final ObjectInspector p1) throws SerDeException;
    
    @Override
    public abstract SerDeStats getSerDeStats();
    
    @Override
    public abstract Object deserialize(final Writable p0) throws SerDeException;
    
    @Override
    public abstract ObjectInspector getObjectInspector() throws SerDeException;
    
    public String getConfigurationErrors() {
        return (this.configErrors == null) ? "" : this.configErrors;
    }
}
