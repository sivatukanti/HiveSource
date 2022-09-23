// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class ArcCosineFunctionEvaluator extends MathFunctionEvaluator
{
    @Override
    protected String getFunctionName() {
        return "acos";
    }
    
    @Override
    protected double evaluateMathFunction(final double num) {
        return Math.acos(num);
    }
}
