// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface BaseUnivariateSolver<FUNC extends UnivariateFunction>
{
    int getMaxEvaluations();
    
    int getEvaluations();
    
    double getAbsoluteAccuracy();
    
    double getRelativeAccuracy();
    
    double getFunctionValueAccuracy();
    
    double solve(final int p0, final FUNC p1, final double p2, final double p3);
    
    double solve(final int p0, final FUNC p1, final double p2, final double p3, final double p4);
    
    double solve(final int p0, final FUNC p1, final double p2);
}
