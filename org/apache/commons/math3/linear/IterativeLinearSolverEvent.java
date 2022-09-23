// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.util.IterationEvent;

public abstract class IterativeLinearSolverEvent extends IterationEvent
{
    private static final long serialVersionUID = 20120129L;
    
    public IterativeLinearSolverEvent(final Object source, final int iterations) {
        super(source, iterations);
    }
    
    public abstract RealVector getRightHandSideVector();
    
    public abstract double getNormOfResidual();
    
    public RealVector getResidual() {
        throw new MathUnsupportedOperationException();
    }
    
    public abstract RealVector getSolution();
    
    public boolean providesResidual() {
        return false;
    }
}
