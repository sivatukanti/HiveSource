// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class SqrtFunctionEvaluator extends MathFunctionEvaluator
{
    @Override
    protected String getFunctionName() {
        return "sqrt";
    }
    
    @Override
    protected double evaluateMathFunction(final double num) {
        return Math.sqrt(num);
    }
}
