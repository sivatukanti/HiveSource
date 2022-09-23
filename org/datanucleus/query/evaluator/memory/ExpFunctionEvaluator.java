// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class ExpFunctionEvaluator extends MathFunctionEvaluator
{
    @Override
    protected String getFunctionName() {
        return "exp";
    }
    
    @Override
    protected double evaluateMathFunction(final double num) {
        return Math.exp(num);
    }
}
