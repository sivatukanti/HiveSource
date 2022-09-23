// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service;

import java.util.Map;
import java.util.List;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface Service extends Closeable
{
    void init(final Configuration p0);
    
    void start();
    
    void stop();
    
    void close() throws IOException;
    
    void registerServiceListener(final ServiceStateChangeListener p0);
    
    void unregisterServiceListener(final ServiceStateChangeListener p0);
    
    String getName();
    
    Configuration getConfig();
    
    STATE getServiceState();
    
    long getStartTime();
    
    boolean isInState(final STATE p0);
    
    Throwable getFailureCause();
    
    STATE getFailureState();
    
    boolean waitForServiceToStop(final long p0);
    
    List<LifecycleEvent> getLifecycleHistory();
    
    Map<String, String> getBlockers();
    
    public enum STATE
    {
        NOTINITED(0, "NOTINITED"), 
        INITED(1, "INITED"), 
        STARTED(2, "STARTED"), 
        STOPPED(3, "STOPPED");
        
        private final int value;
        private final String statename;
        
        private STATE(final int value, final String name) {
            this.value = value;
            this.statename = name;
        }
        
        public int getValue() {
            return this.value;
        }
        
        @Override
        public String toString() {
            return this.statename;
        }
    }
}
