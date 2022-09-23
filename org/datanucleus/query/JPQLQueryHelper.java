// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query;

import java.util.List;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.Expression;

public class JPQLQueryHelper
{
    static final String[] SINGLE_STRING_KEYWORDS;
    static final String[] RESERVED_IDENTIFIERS;
    
    public static boolean isKeyword(final String name) {
        for (int i = 0; i < JPQLQueryHelper.SINGLE_STRING_KEYWORDS.length; ++i) {
            if (name.equalsIgnoreCase(JPQLQueryHelper.SINGLE_STRING_KEYWORDS[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isReservedIdentifier(final String name) {
        for (int i = 0; i < JPQLQueryHelper.RESERVED_IDENTIFIERS.length; ++i) {
            if (name.equalsIgnoreCase(JPQLQueryHelper.RESERVED_IDENTIFIERS[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static String getJPQLForExpression(final Expression expr) {
        if (expr instanceof DyadicExpression) {
            final DyadicExpression dyExpr = (DyadicExpression)expr;
            final Expression left = dyExpr.getLeft();
            final Expression right = dyExpr.getRight();
            final StringBuffer str = new StringBuffer("(");
            if (left != null) {
                str.append(getJPQLForExpression(left));
            }
            if (right != null && right instanceof Literal && ((Literal)right).getLiteral() == null && (dyExpr.getOperator() == Expression.OP_EQ || dyExpr.getOperator() == Expression.OP_NOTEQ)) {
                str.append((dyExpr.getOperator() == Expression.OP_EQ) ? " IS NULL" : " IS NOT NULL");
            }
            else {
                if (dyExpr.getOperator() == Expression.OP_AND) {
                    str.append(" AND ");
                }
                else if (dyExpr.getOperator() == Expression.OP_OR) {
                    str.append(" OR ");
                }
                else if (dyExpr.getOperator() == Expression.OP_ADD) {
                    str.append(" + ");
                }
                else if (dyExpr.getOperator() == Expression.OP_SUB) {
                    str.append(" - ");
                }
                else if (dyExpr.getOperator() == Expression.OP_MUL) {
                    str.append(" * ");
                }
                else if (dyExpr.getOperator() == Expression.OP_DIV) {
                    str.append(" / ");
                }
                else if (dyExpr.getOperator() == Expression.OP_EQ) {
                    str.append(" = ");
                }
                else if (dyExpr.getOperator() == Expression.OP_GT) {
                    str.append(" > ");
                }
                else if (dyExpr.getOperator() == Expression.OP_LT) {
                    str.append(" < ");
                }
                else if (dyExpr.getOperator() == Expression.OP_GTEQ) {
                    str.append(" >= ");
                }
                else if (dyExpr.getOperator() == Expression.OP_LTEQ) {
                    str.append(" <= ");
                }
                else {
                    if (dyExpr.getOperator() != Expression.OP_NOTEQ) {
                        throw new UnsupportedOperationException("Dont currently support operator " + dyExpr.getOperator() + " in JPQL conversion");
                    }
                    str.append(" <> ");
                }
                if (right != null) {
                    str.append(getJPQLForExpression(right));
                }
            }
            str.append(")");
            return str.toString();
        }
        if (expr instanceof PrimaryExpression) {
            final PrimaryExpression primExpr = (PrimaryExpression)expr;
            return primExpr.getId();
        }
        if (expr instanceof ParameterExpression) {
            final ParameterExpression paramExpr = (ParameterExpression)expr;
            if (paramExpr.getId() != null) {
                return ":" + paramExpr.getId();
            }
            return "?" + paramExpr.getPosition();
        }
        else if (expr instanceof InvokeExpression) {
            final InvokeExpression invExpr = (InvokeExpression)expr;
            final Expression invoked = invExpr.getLeft();
            final List<Expression> args = invExpr.getArguments();
            final String method = invExpr.getOperation();
            if (method.equalsIgnoreCase("CURRENT_DATE")) {
                return "CURRENT_DATE";
            }
            if (method.equalsIgnoreCase("CURRENT_TIME")) {
                return "CURRENT_TIME";
            }
            if (method.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                return "CURRENT_TIMESTAMP";
            }
            if (method.equalsIgnoreCase("length")) {
                final StringBuffer str2 = new StringBuffer("LENGTH(");
                str2.append(getJPQLForExpression(invoked));
                if (args != null && !args.isEmpty()) {
                    final Expression firstExpr = args.get(0);
                    str2.append(",").append(getJPQLForExpression(firstExpr));
                    if (args.size() == 2) {
                        final Expression secondExpr = args.get(1);
                        str2.append(",").append(getJPQLForExpression(secondExpr));
                    }
                }
                str2.append(")");
                return str2.toString();
            }
            if (method.equals("toLowerCase")) {
                return "LOWER(" + getJPQLForExpression(invoked) + ")";
            }
            if (method.equals("toUpperCase")) {
                return "UPPER(" + getJPQLForExpression(invoked) + ")";
            }
            if (method.equalsIgnoreCase("isEmpty")) {
                final StringBuffer str2 = new StringBuffer();
                str2.append(getJPQLForExpression(invoked));
                str2.append(" IS EMPTY");
                return str2.toString();
            }
            if (method.equalsIgnoreCase("indexOf")) {
                final StringBuffer str2 = new StringBuffer("LOCATE(");
                str2.append(getJPQLForExpression(invoked));
                final Expression firstExpr = args.get(0);
                str2.append(",").append(getJPQLForExpression(firstExpr));
                if (args.size() > 1) {
                    final Expression secondExpr = args.get(1);
                    str2.append(",").append(getJPQLForExpression(secondExpr));
                }
                str2.append(")");
                return str2.toString();
            }
            if (method.equalsIgnoreCase("substring")) {
                final StringBuffer str2 = new StringBuffer("SUBSTRING(");
                str2.append(getJPQLForExpression(invoked));
                final Expression firstExpr = args.get(0);
                str2.append(",").append(getJPQLForExpression(firstExpr));
                if (args.size() > 1) {
                    final Expression secondExpr = args.get(1);
                    str2.append(",").append(getJPQLForExpression(secondExpr));
                }
                str2.append(")");
                return str2.toString();
            }
            if (method.equalsIgnoreCase("trim")) {
                final StringBuffer str2 = new StringBuffer("TRIM(BOTH ");
                str2.append(getJPQLForExpression(invoked));
                if (args.size() > 0) {
                    final Expression trimChrExpr = args.get(0);
                    str2.append(getJPQLForExpression(trimChrExpr));
                }
                str2.append(" FROM ");
                str2.append(getJPQLForExpression(invoked));
                str2.append(")");
                return str2.toString();
            }
            if (method.equalsIgnoreCase("trimLeft")) {
                final StringBuffer str2 = new StringBuffer("TRIM(LEADING ");
                str2.append(getJPQLForExpression(invoked));
                if (args.size() > 0) {
                    final Expression trimChrExpr = args.get(0);
                    str2.append(getJPQLForExpression(trimChrExpr));
                }
                str2.append(" FROM ");
                str2.append(getJPQLForExpression(invoked));
                str2.append(")");
                return str2.toString();
            }
            if (method.equalsIgnoreCase("trimLeft")) {
                final StringBuffer str2 = new StringBuffer("TRIM(TRAILING ");
                str2.append(getJPQLForExpression(invoked));
                if (args.size() > 0) {
                    final Expression trimChrExpr = args.get(0);
                    str2.append(getJPQLForExpression(trimChrExpr));
                }
                str2.append(" FROM ");
                str2.append(getJPQLForExpression(invoked));
                str2.append(")");
                return str2.toString();
            }
            if (method.equalsIgnoreCase("matches")) {
                final StringBuffer str2 = new StringBuffer();
                str2.append(getJPQLForExpression(invoked));
                str2.append(" LIKE ");
                final Expression firstExpr = args.get(0);
                str2.append(getJPQLForExpression(firstExpr));
                if (args.size() > 1) {
                    final Expression secondExpr = args.get(1);
                    str2.append(" ESCAPE ").append(getJPQLForExpression(secondExpr));
                }
                return str2.toString();
            }
            if (method.equalsIgnoreCase("contains")) {
                final StringBuffer str2 = new StringBuffer();
                final Expression firstExpr = args.get(0);
                str2.append(getJPQLForExpression(firstExpr));
                str2.append(" MEMBER OF ");
                str2.append(getJPQLForExpression(invoked));
                return str2.toString();
            }
            if (method.equalsIgnoreCase("COUNT")) {
                final Expression argExpr = args.get(0);
                if (argExpr instanceof DyadicExpression && ((DyadicExpression)argExpr).getOperator() == Expression.OP_DISTINCT) {
                    final DyadicExpression dyExpr2 = (DyadicExpression)argExpr;
                    return "COUNT(DISTINCT " + getJPQLForExpression(dyExpr2.getLeft()) + ")";
                }
                return "COUNT(" + getJPQLForExpression(argExpr) + ")";
            }
            else {
                if (method.equalsIgnoreCase("COALESCE")) {
                    final StringBuffer str2 = new StringBuffer("COALESCE(");
                    for (int i = 0; i < args.size(); ++i) {
                        final Expression argExpr2 = args.get(i);
                        str2.append(getJPQLForExpression(argExpr2));
                        if (i < args.size() - 1) {
                            str2.append(",");
                        }
                    }
                    str2.append(")");
                    return str2.toString();
                }
                if (method.equalsIgnoreCase("NULLIF")) {
                    final StringBuffer str2 = new StringBuffer("NULLIF(");
                    for (int i = 0; i < args.size(); ++i) {
                        final Expression argExpr2 = args.get(i);
                        str2.append(getJPQLForExpression(argExpr2));
                        if (i < args.size() - 1) {
                            str2.append(",");
                        }
                    }
                    str2.append(")");
                    return str2.toString();
                }
                if (method.equalsIgnoreCase("ABS")) {
                    final Expression argExpr = args.get(0);
                    return "ABS(" + getJPQLForExpression(argExpr) + ")";
                }
                if (method.equalsIgnoreCase("AVG")) {
                    final Expression argExpr = args.get(0);
                    return "AVG(" + getJPQLForExpression(argExpr) + ")";
                }
                if (method.equalsIgnoreCase("MAX")) {
                    final Expression argExpr = args.get(0);
                    return "MAX(" + getJPQLForExpression(argExpr) + ")";
                }
                if (method.equalsIgnoreCase("MIN")) {
                    final Expression argExpr = args.get(0);
                    return "MIN(" + getJPQLForExpression(argExpr) + ")";
                }
                if (method.equalsIgnoreCase("SQRT")) {
                    final Expression argExpr = args.get(0);
                    return "SQRT(" + getJPQLForExpression(argExpr) + ")";
                }
                if (method.equalsIgnoreCase("SUM")) {
                    final Expression argExpr = args.get(0);
                    return "SUM(" + getJPQLForExpression(argExpr) + ")";
                }
                throw new UnsupportedOperationException("Dont currently support InvokeExpression (" + invExpr + ") conversion into JPQL");
            }
        }
        else if (expr instanceof Literal) {
            final Literal litExpr = (Literal)expr;
            final Object value = litExpr.getLiteral();
            if (value instanceof String || value instanceof Character) {
                return "'" + value.toString() + "'";
            }
            if (value instanceof Boolean) {
                return value ? "TRUE" : "FALSE";
            }
            return litExpr.getLiteral().toString();
        }
        else {
            if (expr instanceof VariableExpression) {
                final VariableExpression varExpr = (VariableExpression)expr;
                return varExpr.getId();
            }
            throw new UnsupportedOperationException("Dont currently support " + expr.getClass().getName() + " in JPQLQueryHelper");
        }
    }
    
    static {
        SINGLE_STRING_KEYWORDS = new String[] { "SELECT", "UPDATE", "DELETE", "FROM", "WHERE", "GROUP BY", "HAVING", "ORDER BY" };
        RESERVED_IDENTIFIERS = new String[] { "SELECT", "FROM", "WHERE", "UPDATE", "DELETE", "JOIN", "OUTER", "INNER", "LEFT", "GROUP", "BY", "HAVING", "FETCH", "DISTINCT", "OBJECT", "NULL", "TRUE", "FALSE", "NOT", "AND", "OR", "BETWEEN", "LIKE", "IN", "AS", "UNKNOWN", "EMPTY", "MEMBER", "OF", "IS", "AVG", "MAX", "MIN", "SUM", "COUNT", "ORDER", "ASC", "DESC", "MOD", "UPPER", "LOWER", "TRIM", "POSITION", "CHARACTER_LENGTH", "CHAR_LENGTH", "BIT_LENGTH", "CURRENT_TIME", "CURRENT_DATE", "CURRENT_TIMESTAMP", "NEW", "EXISTS", "ALL", "ANY", "SOME" };
    }
}
