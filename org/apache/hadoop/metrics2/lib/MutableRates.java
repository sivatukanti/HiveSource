// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import java.lang.reflect.Method;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.util.Set;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MutableRates extends MutableMetric
{
    static final Logger LOG;
    private final MetricsRegistry registry;
    private final Set<Class<?>> protocolCache;
    
    MutableRates(final MetricsRegistry registry) {
        this.protocolCache = (Set<Class<?>>)Sets.newHashSet();
        this.registry = Preconditions.checkNotNull(registry, (Object)"metrics registry");
    }
    
    public void init(final Class<?> protocol) {
        if (this.protocolCache.contains(protocol)) {
            return;
        }
        this.protocolCache.add(protocol);
        for (final Method method : protocol.getDeclaredMethods()) {
            final String name = method.getName();
            MutableRates.LOG.debug(name);
            try {
                this.registry.newRate(name, name, false, true);
            }
            catch (Exception e) {
                MutableRates.LOG.error("Error creating rate metrics for " + method.getName(), e);
            }
        }
    }
    
    public void add(final String name, final long elapsed) {
        this.registry.add(name, elapsed);
    }
    
    @Override
    public void snapshot(final MetricsRecordBuilder rb, final boolean all) {
        this.registry.snapshot(rb, all);
    }
    
    static {
        LOG = LoggerFactory.getLogger(MutableRates.class);
    }
}
