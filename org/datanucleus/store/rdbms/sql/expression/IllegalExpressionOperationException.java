// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.exceptions.NucleusUserException;

public class IllegalExpressionOperationException extends NucleusUserException
{
    public IllegalExpressionOperationException(final String operation, final SQLExpression operand) {
        super("Cannot perform operation \"" + operation + "\" on " + operand);
    }
    
    public IllegalExpressionOperationException(final SQLExpression operand1, final String operation, final SQLExpression operand2) {
        super("Cannot perform operation \"" + operation + "\" on " + operand1 + " and " + operand2);
    }
}
