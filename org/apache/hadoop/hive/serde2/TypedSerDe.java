// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import org.apache.hadoop.io.BytesWritable;
import java.util.Properties;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.io.Writable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypedSerDe extends AbstractSerDe
{
    protected Type objectType;
    protected Class<?> objectClass;
    protected Object deserializeCache;
    
    public TypedSerDe(final Type objectType) throws SerDeException {
        this.objectType = objectType;
        if (objectType instanceof Class) {
            this.objectClass = (Class<?>)objectType;
        }
        else {
            if (!(objectType instanceof ParameterizedType)) {
                throw new SerDeException("Cannot create TypedSerDe with type " + objectType);
            }
            this.objectClass = (Class<?>)((ParameterizedType)objectType).getRawType();
        }
    }
    
    @Override
    public Object deserialize(final Writable blob) throws SerDeException {
        if (this.deserializeCache == null) {
            return ReflectionUtils.newInstance(this.objectClass, null);
        }
        assert this.deserializeCache.getClass().equals(this.objectClass);
        return this.deserializeCache;
    }
    
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return ObjectInspectorFactory.getReflectionObjectInspector(this.objectType, this.getObjectInspectorOptions());
    }
    
    protected ObjectInspectorFactory.ObjectInspectorOptions getObjectInspectorOptions() {
        return ObjectInspectorFactory.ObjectInspectorOptions.JAVA;
    }
    
    @Override
    public void initialize(final Configuration job, final Properties tbl) throws SerDeException {
    }
    
    @Override
    public Class<? extends Writable> getSerializedClass() {
        return BytesWritable.class;
    }
    
    @Override
    public Writable serialize(final Object obj, final ObjectInspector objInspector) throws SerDeException {
        throw new RuntimeException("not supported");
    }
    
    @Override
    public SerDeStats getSerDeStats() {
        return null;
    }
}
