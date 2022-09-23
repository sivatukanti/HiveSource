// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.sql.expression.ExpressionUtils;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.sql.expression.SQLLiteral;
import org.datanucleus.store.rdbms.sql.expression.ParameterLiteral;
import org.datanucleus.store.rdbms.sql.expression.CharacterExpression;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringEndsWithMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() == 0 || args.size() > 2) {
            throw new NucleusException(StringEndsWithMethod.LOCALISER.msg("060003", "endsWith", "StringExpression", 0, "StringExpression/CharacterExpression/ParameterLiteral"));
        }
        final SQLExpression substrExpr = args.get(0);
        if (!(substrExpr instanceof StringExpression) && !(substrExpr instanceof CharacterExpression) && !(substrExpr instanceof ParameterLiteral)) {
            throw new NucleusException(StringEndsWithMethod.LOCALISER.msg("060003", "endsWith", "StringExpression", 0, "StringExpression/CharacterExpression/ParameterLiteral"));
        }
        if (args.size() > 1) {
            if (substrExpr.isParameter()) {
                final SQLLiteral substrLit = (SQLLiteral)substrExpr;
                this.stmt.getQueryGenerator().useParameterExpressionAsLiteral(substrLit);
                if (substrLit.getValue() == null) {
                    return new BooleanExpression(expr, Expression.OP_LIKE, ExpressionUtils.getEscapedPatternExpression(substrExpr));
                }
            }
            final SQLExpression likeSubstrExpr = new StringLiteral(this.stmt, expr.getJavaTypeMapping(), '%', null);
            return new BooleanExpression(expr, Expression.OP_LIKE, likeSubstrExpr.add(ExpressionUtils.getEscapedPatternExpression(substrExpr)));
        }
        if (substrExpr.isParameter()) {
            final SQLLiteral substrLit = (SQLLiteral)substrExpr;
            this.stmt.getQueryGenerator().useParameterExpressionAsLiteral(substrLit);
            if (substrLit.getValue() == null) {
                return new BooleanExpression(expr, Expression.OP_LIKE, ExpressionUtils.getEscapedPatternExpression(substrExpr));
            }
        }
        final SQLExpression likeSubstrExpr = new StringLiteral(this.stmt, expr.getJavaTypeMapping(), '%', null);
        return new BooleanExpression(expr, Expression.OP_LIKE, likeSubstrExpr.add(ExpressionUtils.getEscapedPatternExpression(substrExpr)));
    }
}
