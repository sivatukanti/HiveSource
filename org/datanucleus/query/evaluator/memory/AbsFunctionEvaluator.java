// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class AbsFunctionEvaluator extends MathFunctionEvaluator
{
    @Override
    protected String getFunctionName() {
        return "abs";
    }
    
    @Override
    protected double evaluateMathFunction(final double num) {
        return Math.abs(num);
    }
}
