// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class ArcSineFunctionEvaluator extends MathFunctionEvaluator
{
    @Override
    protected String getFunctionName() {
        return "asin";
    }
    
    @Override
    protected double evaluateMathFunction(final double num) {
        return Math.asin(num);
    }
}
