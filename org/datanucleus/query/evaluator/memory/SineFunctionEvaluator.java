// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class SineFunctionEvaluator extends MathFunctionEvaluator
{
    @Override
    protected String getFunctionName() {
        return "sin";
    }
    
    @Override
    protected double evaluateMathFunction(final double num) {
        return Math.sin(num);
    }
}
