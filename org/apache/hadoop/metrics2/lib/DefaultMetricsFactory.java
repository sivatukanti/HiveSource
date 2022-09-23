// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.apache.hadoop.metrics2.MetricsException;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public enum DefaultMetricsFactory
{
    INSTANCE;
    
    private MutableMetricsFactory mmfImpl;
    
    public static MutableMetricsFactory getAnnotatedMetricsFactory() {
        return DefaultMetricsFactory.INSTANCE.getInstance(MutableMetricsFactory.class);
    }
    
    public synchronized <T> T getInstance(final Class<T> cls) {
        if (cls == MutableMetricsFactory.class) {
            if (this.mmfImpl == null) {
                this.mmfImpl = new MutableMetricsFactory();
            }
            return (T)this.mmfImpl;
        }
        throw new MetricsException("Unknown metrics factory type: " + cls.getName());
    }
    
    public synchronized void setInstance(final MutableMetricsFactory factory) {
        this.mmfImpl = factory;
    }
}
