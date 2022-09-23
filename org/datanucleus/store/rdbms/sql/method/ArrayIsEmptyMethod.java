// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.BooleanLiteral;
import java.lang.reflect.Array;
import org.datanucleus.store.rdbms.sql.expression.ArrayLiteral;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class ArrayIsEmptyMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args != null && args.size() > 0) {
            throw new NucleusException(ArrayIsEmptyMethod.LOCALISER.msg("060015", "isEmpty", "ArrayExpression"));
        }
        if (expr instanceof ArrayLiteral) {
            final Object arr = ((ArrayLiteral)expr).getValue();
            final boolean isEmpty = arr == null || Array.getLength(arr) == 0;
            final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, false);
            return new BooleanLiteral(this.stmt, m, isEmpty ? Boolean.TRUE : Boolean.FALSE);
        }
        final SQLExpression sizeExpr = this.exprFactory.invokeMethod(this.stmt, "ARRAY", "size", expr, args);
        final JavaTypeMapping mapping = this.exprFactory.getMappingForType(Integer.class, true);
        final SQLExpression zeroExpr = this.exprFactory.newLiteral(this.stmt, mapping, 0);
        return sizeExpr.eq(zeroExpr);
    }
}
