// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class CeilFunctionEvaluator extends MathFunctionEvaluator
{
    @Override
    protected String getFunctionName() {
        return "ceil";
    }
    
    @Override
    protected double evaluateMathFunction(final double num) {
        return Math.ceil(num);
    }
}
