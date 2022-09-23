// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import org.datanucleus.exceptions.NucleusException;

public class PrimaryExpressionIsVariableException extends NucleusException
{
    VariableExpression varExpr;
    
    public PrimaryExpressionIsVariableException(final String varName) {
        super("PrimaryExpression should be a VariableExpression with name " + varName);
        this.varExpr = new VariableExpression(varName);
    }
    
    public VariableExpression getVariableExpression() {
        return this.varExpr;
    }
}
