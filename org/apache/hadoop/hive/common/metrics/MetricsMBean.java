// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.metrics;

import java.io.IOException;
import javax.management.DynamicMBean;

public interface MetricsMBean extends DynamicMBean
{
    boolean hasKey(final String p0);
    
    void put(final String p0, final Object p1) throws IOException;
    
    Object get(final String p0) throws IOException;
    
    void clear();
}
