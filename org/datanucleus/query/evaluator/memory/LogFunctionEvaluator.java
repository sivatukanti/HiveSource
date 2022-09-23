// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class LogFunctionEvaluator extends MathFunctionEvaluator
{
    @Override
    protected String getFunctionName() {
        return "log";
    }
    
    @Override
    protected double evaluateMathFunction(final double num) {
        return Math.log(num);
    }
}
