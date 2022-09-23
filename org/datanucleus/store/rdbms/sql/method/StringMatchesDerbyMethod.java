// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.store.rdbms.sql.expression.ExpressionUtils;
import java.math.BigInteger;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringMatchesDerbyMethod extends StringMatchesMethod
{
    @Override
    protected BooleanExpression getExpressionForStringExpressionInput(final SQLExpression expr, final SQLExpression argExpr, final SQLExpression escapeExpr) {
        final List funcArgs = new ArrayList();
        funcArgs.add(expr);
        funcArgs.add(argExpr);
        final JavaTypeMapping m = this.exprFactory.getMappingForType(BigInteger.class, false);
        final SQLExpression one = ExpressionUtils.getLiteralForOne(this.stmt);
        return new NumericExpression(this.stmt, m, "NUCLEUS_MATCHES", funcArgs).eq(one);
    }
}
