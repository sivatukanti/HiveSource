// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.util;

import java.util.Map;

public interface QuantileEstimator
{
    void insert(final long p0);
    
    Map<Quantile, Long> snapshot();
    
    long getCount();
    
    void clear();
}
