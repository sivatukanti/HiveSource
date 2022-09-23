// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class ArcTangentFunctionEvaluator extends MathFunctionEvaluator
{
    @Override
    protected String getFunctionName() {
        return "atan";
    }
    
    @Override
    protected double evaluateMathFunction(final double num) {
        return Math.atan(num);
    }
}
