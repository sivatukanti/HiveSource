// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.ParameterLiteral;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import org.datanucleus.store.rdbms.sql.expression.IntegerLiteral;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringLength2Method extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (expr instanceof StringLiteral) {
            final JavaTypeMapping m = this.exprFactory.getMappingForType(Integer.TYPE, false);
            final String val = (String)((StringLiteral)expr).getValue();
            return new IntegerLiteral(this.stmt, m, val.length(), null);
        }
        if (expr instanceof StringExpression || expr instanceof ParameterLiteral) {
            final ArrayList funcArgs = new ArrayList();
            funcArgs.add(expr);
            return new NumericExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "STRLEN", funcArgs);
        }
        throw new NucleusException(StringLength2Method.LOCALISER.msg("060001", "length", expr));
    }
}
