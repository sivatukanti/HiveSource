// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class MetricsSystem implements MetricsSystemMXBean
{
    @InterfaceAudience.Private
    public abstract MetricsSystem init(final String p0);
    
    public abstract <T> T register(final String p0, final String p1, final T p2);
    
    public abstract void unregisterSource(final String p0);
    
    public <T> T register(final T source) {
        return this.register((String)null, (String)null, source);
    }
    
    @InterfaceAudience.Private
    public abstract MetricsSource getSource(final String p0);
    
    public abstract <T extends MetricsSink> T register(final String p0, final String p1, final T p2);
    
    public abstract void register(final Callback p0);
    
    public abstract void publishMetricsNow();
    
    public abstract boolean shutdown();
    
    public abstract static class AbstractCallback implements Callback
    {
        @Override
        public void preStart() {
        }
        
        @Override
        public void postStart() {
        }
        
        @Override
        public void preStop() {
        }
        
        @Override
        public void postStop() {
        }
    }
    
    public interface Callback
    {
        void preStart();
        
        void postStart();
        
        void preStop();
        
        void postStop();
    }
}
