// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.SQLText;
import org.datanucleus.store.rdbms.sql.expression.CharacterLiteral;
import org.datanucleus.store.rdbms.adapter.BaseDatastoreAdapter;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.query.expression.Expression;

public class StringSimilarPostgresqlMethod extends StringMatchesMethod
{
    public static final Expression.DyadicOperator OP_SIMILAR_TO;
    
    @Override
    protected BooleanExpression getExpressionForStringExpressionInput(final SQLExpression expr, final SQLExpression regExpr, final SQLExpression escapeExpr) {
        return this.getBooleanLikeExpression(expr, regExpr, escapeExpr);
    }
    
    @Override
    protected BooleanExpression getBooleanLikeExpression(final SQLExpression expr, final SQLExpression regExpr, final SQLExpression escapeExpr) {
        final BooleanExpression similarToExpr = new BooleanExpression(this.stmt, this.exprFactory.getMappingForType(Boolean.TYPE, false));
        final SQLText sql = similarToExpr.toSQLText();
        sql.clearStatement();
        if (StringSimilarPostgresqlMethod.OP_SIMILAR_TO.isHigherThanLeftSide(expr.getLowestOperator())) {
            sql.append("(").append(expr).append(")");
        }
        else {
            sql.append(expr);
        }
        sql.append(" SIMILAR TO ");
        if (StringSimilarPostgresqlMethod.OP_SIMILAR_TO.isHigherThanRightSide(regExpr.getLowestOperator())) {
            sql.append("(").append(regExpr).append(")");
        }
        else {
            sql.append(regExpr);
        }
        final BaseDatastoreAdapter dba = (BaseDatastoreAdapter)this.stmt.getRDBMSManager().getDatastoreAdapter();
        if (escapeExpr != null) {
            if (escapeExpr instanceof CharacterLiteral) {
                final String chr = "" + ((CharacterLiteral)escapeExpr).getValue();
                if (chr.equals(dba.getEscapeCharacter())) {
                    sql.append(dba.getEscapePatternExpression());
                }
                else {
                    sql.append(" ESCAPE " + escapeExpr);
                }
            }
            else {
                sql.append(" ESCAPE " + escapeExpr);
            }
        }
        else {
            sql.append(" " + dba.getEscapePatternExpression());
        }
        return similarToExpr;
    }
    
    static {
        OP_SIMILAR_TO = new Expression.DyadicOperator("SIMILAR TO", 3, false);
    }
}
