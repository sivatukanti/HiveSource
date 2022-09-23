// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.factories.impl.pb;

import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.factories.RecordFactory;

@InterfaceAudience.Private
public class RecordFactoryPBImpl implements RecordFactory
{
    private static final String PB_IMPL_PACKAGE_SUFFIX = "impl.pb";
    private static final String PB_IMPL_CLASS_SUFFIX = "PBImpl";
    private static final RecordFactoryPBImpl self;
    private Configuration localConf;
    private ConcurrentMap<Class<?>, Constructor<?>> cache;
    
    private RecordFactoryPBImpl() {
        this.localConf = new Configuration();
        this.cache = new ConcurrentHashMap<Class<?>, Constructor<?>>();
    }
    
    public static RecordFactory get() {
        return RecordFactoryPBImpl.self;
    }
    
    @Override
    public <T> T newRecordInstance(final Class<T> clazz) {
        Constructor<?> constructor = this.cache.get(clazz);
        if (constructor == null) {
            Class<?> pbClazz = null;
            try {
                pbClazz = this.localConf.getClassByName(this.getPBImplClassName(clazz));
            }
            catch (ClassNotFoundException e) {
                throw new YarnRuntimeException("Failed to load class: [" + this.getPBImplClassName(clazz) + "]", e);
            }
            try {
                constructor = pbClazz.getConstructor((Class<?>[])new Class[0]);
                constructor.setAccessible(true);
                this.cache.putIfAbsent(clazz, constructor);
            }
            catch (NoSuchMethodException e2) {
                throw new YarnRuntimeException("Could not find 0 argument constructor", e2);
            }
        }
        try {
            final Object retObject = constructor.newInstance(new Object[0]);
            return (T)retObject;
        }
        catch (InvocationTargetException e3) {
            throw new YarnRuntimeException(e3);
        }
        catch (IllegalAccessException e4) {
            throw new YarnRuntimeException(e4);
        }
        catch (InstantiationException e5) {
            throw new YarnRuntimeException(e5);
        }
    }
    
    private String getPBImplClassName(final Class<?> clazz) {
        final String srcPackagePart = this.getPackageName(clazz);
        final String srcClassName = this.getClassName(clazz);
        final String destPackagePart = srcPackagePart + "." + "impl.pb";
        final String destClassPart = srcClassName + "PBImpl";
        return destPackagePart + "." + destClassPart;
    }
    
    private String getClassName(final Class<?> clazz) {
        final String fqName = clazz.getName();
        return fqName.substring(fqName.lastIndexOf(".") + 1, fqName.length());
    }
    
    private String getPackageName(final Class<?> clazz) {
        return clazz.getPackage().getName();
    }
    
    static {
        self = new RecordFactoryPBImpl();
    }
}
