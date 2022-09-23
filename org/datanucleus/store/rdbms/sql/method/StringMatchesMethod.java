// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.SQLText;
import org.datanucleus.store.rdbms.sql.expression.CharacterLiteral;
import org.datanucleus.store.rdbms.adapter.BaseDatastoreAdapter;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.util.RegularExpressionConverter;
import org.datanucleus.store.rdbms.sql.expression.BooleanLiteral;
import org.datanucleus.store.rdbms.sql.expression.SQLLiteral;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.store.rdbms.sql.expression.CharacterExpression;
import org.datanucleus.store.rdbms.sql.expression.ParameterLiteral;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringMatchesMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() > 2) {
            throw new NucleusException("Incorrect arguments for String.matches(StringExpression)");
        }
        if (!(args.get(0) instanceof StringExpression) && !(args.get(0) instanceof ParameterLiteral)) {
            throw new NucleusException("Incorrect arguments for String.matches(StringExpression)");
        }
        final SQLExpression likeExpr = args.get(0);
        if (!(likeExpr instanceof StringExpression) && !(likeExpr instanceof CharacterExpression) && !(likeExpr instanceof ParameterLiteral)) {
            throw new NucleusException(StringMatchesMethod.LOCALISER.msg("060003", "like/matches", "StringExpression", 0, "StringExpression/CharacterExpression/ParameterLiteral"));
        }
        SQLExpression escapeExpr = null;
        if (args.size() > 1) {
            escapeExpr = args.get(1);
        }
        if ((likeExpr instanceof StringLiteral || likeExpr instanceof ParameterLiteral) && likeExpr.isParameter()) {
            this.stmt.getQueryGenerator().useParameterExpressionAsLiteral((SQLLiteral)likeExpr);
        }
        if (expr instanceof StringLiteral && likeExpr instanceof StringLiteral) {
            final String primary = (String)((StringLiteral)expr).getValue();
            final String pattern = (String)((StringLiteral)likeExpr).getValue();
            return new BooleanLiteral(this.stmt, this.exprFactory.getMappingForType(Boolean.TYPE, false), primary.matches(pattern));
        }
        if (expr instanceof StringLiteral) {
            return this.getBooleanLikeExpression(expr, likeExpr, escapeExpr);
        }
        if (expr instanceof StringExpression && likeExpr instanceof StringLiteral) {
            String pattern2 = (String)((StringLiteral)likeExpr).getValue();
            if (!this.stmt.getQueryGenerator().getQueryLanguage().equalsIgnoreCase("JDOQL")) {
                final SQLExpression patternExpr = this.exprFactory.newLiteral(this.stmt, likeExpr.getJavaTypeMapping(), pattern2);
                return this.getBooleanLikeExpression(expr, patternExpr, escapeExpr);
            }
            boolean caseSensitive = false;
            if (pattern2.startsWith("(?i)")) {
                caseSensitive = true;
                pattern2 = pattern2.substring(4);
            }
            final DatastoreAdapter dba = this.stmt.getDatastoreAdapter();
            final RegularExpressionConverter converter = new RegularExpressionConverter(dba.getPatternExpressionZeroMoreCharacters().charAt(0), dba.getPatternExpressionAnyCharacter().charAt(0), dba.getEscapeCharacter().charAt(0));
            if (caseSensitive) {
                final SQLExpression patternExpr2 = this.exprFactory.newLiteral(this.stmt, likeExpr.getJavaTypeMapping(), converter.convert(pattern2).toLowerCase());
                return this.getBooleanLikeExpression(expr.invoke("toLowerCase", null), patternExpr2, escapeExpr);
            }
            final SQLExpression patternExpr2 = this.exprFactory.newLiteral(this.stmt, likeExpr.getJavaTypeMapping(), converter.convert(pattern2));
            return this.getBooleanLikeExpression(expr, patternExpr2, escapeExpr);
        }
        else {
            if (expr instanceof StringExpression) {
                return this.getExpressionForStringExpressionInput(expr, likeExpr, escapeExpr);
            }
            throw new NucleusException(StringMatchesMethod.LOCALISER.msg("060001", "matches", expr));
        }
    }
    
    protected BooleanExpression getExpressionForStringExpressionInput(final SQLExpression expr, final SQLExpression regExpr, final SQLExpression escapeExpr) {
        final BooleanExpression likeExpr = this.getBooleanLikeExpression(expr, regExpr, escapeExpr);
        return likeExpr;
    }
    
    protected BooleanExpression getBooleanLikeExpression(final SQLExpression expr, final SQLExpression regExpr, final SQLExpression escapeExpr) {
        final BooleanExpression likeExpr = new BooleanExpression(this.stmt, this.exprFactory.getMappingForType(Boolean.TYPE, false));
        final SQLText sql = likeExpr.toSQLText();
        sql.clearStatement();
        if (Expression.OP_LIKE.isHigherThanLeftSide(expr.getLowestOperator())) {
            sql.append("(").append(expr).append(")");
        }
        else {
            sql.append(expr);
        }
        sql.append(" LIKE ");
        if (Expression.OP_LIKE.isHigherThanRightSide(regExpr.getLowestOperator())) {
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
        return likeExpr;
    }
}
