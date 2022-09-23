// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class TangentFunctionEvaluator extends MathFunctionEvaluator
{
    @Override
    protected String getFunctionName() {
        return "tan";
    }
    
    @Override
    protected double evaluateMathFunction(final double num) {
        return Math.tan(num);
    }
}
