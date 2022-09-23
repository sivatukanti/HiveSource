// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class FloorFunctionEvaluator extends MathFunctionEvaluator
{
    @Override
    protected String getFunctionName() {
        return "floor";
    }
    
    @Override
    protected double evaluateMathFunction(final double num) {
        return Math.floor(num);
    }
}
