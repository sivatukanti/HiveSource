// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class Constant implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction
{
    private final double c;
    
    public Constant(final double c) {
        this.c = c;
    }
    
    public double value(final double x) {
        return this.c;
    }
    
    @Deprecated
    public DifferentiableUnivariateFunction derivative() {
        return new Constant(0.0);
    }
    
    public DerivativeStructure value(final DerivativeStructure t) {
        return new DerivativeStructure(t.getFreeParameters(), t.getOrder(), this.c);
    }
}
