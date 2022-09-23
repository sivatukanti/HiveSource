// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class WritableFactories
{
    private static final Map<Class, WritableFactory> CLASS_TO_FACTORY;
    
    private WritableFactories() {
    }
    
    public static void setFactory(final Class c, final WritableFactory factory) {
        WritableFactories.CLASS_TO_FACTORY.put(c, factory);
    }
    
    public static WritableFactory getFactory(final Class c) {
        return WritableFactories.CLASS_TO_FACTORY.get(c);
    }
    
    public static Writable newInstance(final Class<? extends Writable> c, final Configuration conf) {
        final WritableFactory factory = getFactory(c);
        if (factory != null) {
            final Writable result = factory.newInstance();
            if (result instanceof Configurable) {
                ((Configurable)result).setConf(conf);
            }
            return result;
        }
        return ReflectionUtils.newInstance(c, conf);
    }
    
    public static Writable newInstance(final Class<? extends Writable> c) {
        return newInstance(c, null);
    }
    
    static {
        CLASS_TO_FACTORY = new ConcurrentHashMap<Class, WritableFactory>();
    }
}
