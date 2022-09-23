// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class MaxIter implements OptimizationData
{
    private final int maxIter;
    
    public MaxIter(final int max) {
        if (max <= 0) {
            throw new NotStrictlyPositiveException(max);
        }
        this.maxIter = max;
    }
    
    public int getMaxIter() {
        return this.maxIter;
    }
    
    public static MaxIter unlimited() {
        return new MaxIter(Integer.MAX_VALUE);
    }
}
