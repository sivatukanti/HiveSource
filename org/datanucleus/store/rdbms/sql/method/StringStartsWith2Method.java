// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.store.rdbms.sql.expression.ParameterLiteral;
import org.datanucleus.store.rdbms.sql.expression.CharacterExpression;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.ExpressionUtils;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringStartsWith2Method extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() == 0 || args.size() > 2) {
            throw new NucleusException(StringStartsWith2Method.LOCALISER.msg("060003", "startsWith", "StringExpression", 0, "StringExpression/CharacterExpression/Parameter"));
        }
        final SQLExpression one = ExpressionUtils.getLiteralForOne(this.stmt);
        final ArrayList funcArgs = new ArrayList();
        final SQLExpression substrExpr = args.get(0);
        if (!(substrExpr instanceof StringExpression) && !(substrExpr instanceof CharacterExpression) && !(substrExpr instanceof ParameterLiteral)) {
            throw new NucleusException(StringStartsWith2Method.LOCALISER.msg("060003", "startsWith", "StringExpression", 0, "StringExpression/CharacterExpression/Parameter"));
        }
        if (args.size() == 2) {
            final NumericExpression numExpr = args.get(1);
            funcArgs.add(substrExpr);
            funcArgs.add(expr);
            return new BooleanExpression(new StringExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "CHARINDEX", funcArgs), Expression.OP_EQ, one.add(numExpr));
        }
        funcArgs.add(substrExpr);
        funcArgs.add(expr);
        return new BooleanExpression(new StringExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "CHARINDEX", funcArgs), Expression.OP_EQ, one);
    }
}
