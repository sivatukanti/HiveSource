// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import org.datanucleus.exceptions.NucleusException;

public class PrimaryExpressionIsClassLiteralException extends NucleusException
{
    Literal literal;
    
    public PrimaryExpressionIsClassLiteralException(final Class cls) {
        super("PrimaryExpression should be a Literal representing class " + cls.getName());
        this.literal = new Literal(cls);
    }
    
    public Literal getLiteral() {
        return this.literal;
    }
}
