// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class CosineFunctionEvaluator extends MathFunctionEvaluator
{
    @Override
    protected String getFunctionName() {
        return "cos";
    }
    
    @Override
    protected double evaluateMathFunction(final double num) {
        return Math.cos(num);
    }
}
