// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import java.lang.reflect.Field;
import org.datanucleus.exceptions.NucleusException;

public class PrimaryExpressionIsClassStaticFieldException extends NucleusException
{
    Field field;
    
    public PrimaryExpressionIsClassStaticFieldException(final Field fld) {
        super("PrimaryExpression should be a Literal representing field " + fld.getName());
        this.field = fld;
    }
    
    public Field getLiteralField() {
        return this.field;
    }
}
