// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.ExpressionUtils;
import org.datanucleus.store.rdbms.sql.expression.ParameterLiteral;
import org.datanucleus.store.rdbms.sql.expression.CharacterExpression;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringIndexOf2Method extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() == 0 || args.size() > 2) {
            throw new NucleusException(StringIndexOf2Method.LOCALISER.msg("060003", "indexOf", "StringExpression", 0, "StringExpression/CharacterExpression/ParameterLiteral"));
        }
        final SQLExpression substrExpr = args.get(0);
        if (!(substrExpr instanceof StringExpression) && !(substrExpr instanceof CharacterExpression) && !(substrExpr instanceof ParameterLiteral)) {
            throw new NucleusException(StringIndexOf2Method.LOCALISER.msg("060003", "indexOf", "StringExpression", 0, "StringExpression/CharacterExpression/ParameterLiteral"));
        }
        final SQLExpression one = ExpressionUtils.getLiteralForOne(this.stmt);
        final ArrayList funcArgs = new ArrayList();
        funcArgs.add(expr);
        funcArgs.add(substrExpr);
        if (args.size() == 2) {
            final SQLExpression fromExpr = args.get(1);
            if (!(fromExpr instanceof NumericExpression)) {
                throw new NucleusException(StringIndexOf2Method.LOCALISER.msg("060003", "indexOf", "StringExpression", 1, "NumericExpression"));
            }
            funcArgs.add(fromExpr.add(one));
        }
        final NumericExpression locateExpr = new NumericExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "INSTR", funcArgs);
        return new NumericExpression(locateExpr, Expression.OP_SUB, one).encloseInParentheses();
    }
}
