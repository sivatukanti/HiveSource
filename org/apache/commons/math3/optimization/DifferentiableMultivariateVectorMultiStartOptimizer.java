// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optimization;

import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.analysis.DifferentiableMultivariateVectorFunction;

@Deprecated
public class DifferentiableMultivariateVectorMultiStartOptimizer extends BaseMultivariateVectorMultiStartOptimizer<DifferentiableMultivariateVectorFunction> implements DifferentiableMultivariateVectorOptimizer
{
    public DifferentiableMultivariateVectorMultiStartOptimizer(final DifferentiableMultivariateVectorOptimizer optimizer, final int starts, final RandomVectorGenerator generator) {
        super(optimizer, starts, generator);
    }
}
