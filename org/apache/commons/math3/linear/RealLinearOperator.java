// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;

public abstract class RealLinearOperator
{
    public abstract int getRowDimension();
    
    public abstract int getColumnDimension();
    
    public abstract RealVector operate(final RealVector p0) throws DimensionMismatchException;
    
    public RealVector operateTranspose(final RealVector x) throws DimensionMismatchException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    public boolean isTransposable() {
        return false;
    }
}
