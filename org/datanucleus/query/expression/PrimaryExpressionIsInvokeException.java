// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import org.datanucleus.exceptions.NucleusException;

public class PrimaryExpressionIsInvokeException extends NucleusException
{
    InvokeExpression invokeExpr;
    
    public PrimaryExpressionIsInvokeException(final InvokeExpression expr) {
        super("PrimaryExpression should be a InvokeExpression " + expr);
        this.invokeExpr = expr;
    }
    
    public InvokeExpression getInvokeExpression() {
        return this.invokeExpr;
    }
}
