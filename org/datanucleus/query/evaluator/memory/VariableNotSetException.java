// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.query.expression.VariableExpression;

public class VariableNotSetException extends RuntimeException
{
    protected VariableExpression varExpr;
    protected Object[] variableValues;
    
    public VariableNotSetException(final VariableExpression varExpr) {
        this.varExpr = null;
        this.variableValues = null;
        this.varExpr = varExpr;
    }
    
    public VariableNotSetException(final VariableExpression varExpr, final Object[] values) {
        this.varExpr = null;
        this.variableValues = null;
        this.varExpr = varExpr;
        this.variableValues = values;
    }
    
    public VariableExpression getVariableExpression() {
        return this.varExpr;
    }
    
    public Object[] getValues() {
        return this.variableValues;
    }
}
