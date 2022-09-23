// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.linear;

public interface DecompositionSolver
{
    RealVector solve(final RealVector p0);
    
    RealMatrix solve(final RealMatrix p0);
    
    boolean isNonSingular();
    
    RealMatrix getInverse();
}
