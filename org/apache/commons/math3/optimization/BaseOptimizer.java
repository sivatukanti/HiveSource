// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optimization;

@Deprecated
public interface BaseOptimizer<PAIR>
{
    int getMaxEvaluations();
    
    int getEvaluations();
    
    ConvergenceChecker<PAIR> getConvergenceChecker();
}
