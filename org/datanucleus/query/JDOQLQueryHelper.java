// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query;

import java.util.Iterator;
import java.util.List;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.Expression;

public class JDOQLQueryHelper
{
    static final String[] SINGLE_STRING_KEYWORDS;
    static final String[] SINGLE_STRING_KEYWORDS_LOWERCASE;
    
    public static boolean isKeyword(final String name) {
        for (int i = 0; i < JDOQLQueryHelper.SINGLE_STRING_KEYWORDS.length; ++i) {
            if (name.equals(JDOQLQueryHelper.SINGLE_STRING_KEYWORDS[i]) || name.equals(JDOQLQueryHelper.SINGLE_STRING_KEYWORDS_LOWERCASE[i])) {
                return true;
            }
        }
        return name.equals("IMPORT") || name.equals("import");
    }
    
    public static boolean isKeywordExtended(final String name) {
        for (int i = 0; i < JDOQLQueryHelper.SINGLE_STRING_KEYWORDS.length; ++i) {
            if (name.equals(JDOQLQueryHelper.SINGLE_STRING_KEYWORDS[i]) || name.equals(JDOQLQueryHelper.SINGLE_STRING_KEYWORDS_LOWERCASE[i])) {
                return true;
            }
        }
        return name.equals("DELETE") || name.equals("delete") || (name.equals("UPDATE") || name.equals("update")) || (name.equals("SET") || name.equals("set")) || (name.equals("IMPORT") || name.equals("import"));
    }
    
    public static boolean isValidJavaIdentifierForJDOQL(final String s) {
        final int len = s.length();
        if (len < 1) {
            return false;
        }
        if (s.equals("this")) {
            return false;
        }
        final char[] c = new char[len];
        s.getChars(0, len, c, 0);
        if (!Character.isJavaIdentifierStart(c[0])) {
            return false;
        }
        for (int i = 1; i < len; ++i) {
            if (!Character.isJavaIdentifierPart(c[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static String getJDOQLForExpression(final Expression expr) {
        if (expr instanceof DyadicExpression) {
            final DyadicExpression dyExpr = (DyadicExpression)expr;
            final Expression left = dyExpr.getLeft();
            final Expression right = dyExpr.getRight();
            final StringBuffer str = new StringBuffer("(");
            if (dyExpr.getOperator() == Expression.OP_DISTINCT) {
                str.append("DISTINCT ");
            }
            if (left != null) {
                str.append(getJDOQLForExpression(left));
            }
            if (dyExpr.getOperator() == Expression.OP_AND) {
                str.append(" && ");
            }
            else if (dyExpr.getOperator() == Expression.OP_OR) {
                str.append(" || ");
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
                str.append(" == ");
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
            else if (dyExpr.getOperator() == Expression.OP_NOTEQ) {
                str.append(" != ");
            }
            else if (dyExpr.getOperator() != Expression.OP_DISTINCT) {
                throw new UnsupportedOperationException("Dont currently support operator " + dyExpr.getOperator() + " in JDOQL conversion");
            }
            if (right != null) {
                str.append(getJDOQLForExpression(right));
            }
            str.append(")");
            return str.toString();
        }
        if (expr instanceof PrimaryExpression) {
            final PrimaryExpression primExpr = (PrimaryExpression)expr;
            if (primExpr.getLeft() != null) {
                return getJDOQLForExpression(primExpr.getLeft()) + "." + primExpr.getId();
            }
            return primExpr.getId();
        }
        else if (expr instanceof ParameterExpression) {
            final ParameterExpression paramExpr = (ParameterExpression)expr;
            if (paramExpr.getId() != null) {
                return ":" + paramExpr.getId();
            }
            return "?" + paramExpr.getPosition();
        }
        else {
            if (expr instanceof VariableExpression) {
                final VariableExpression varExpr = (VariableExpression)expr;
                return varExpr.getId();
            }
            if (expr instanceof InvokeExpression) {
                final InvokeExpression invExpr = (InvokeExpression)expr;
                final StringBuffer str2 = new StringBuffer();
                if (invExpr.getLeft() != null) {
                    str2.append(getJDOQLForExpression(invExpr.getLeft())).append(".");
                }
                str2.append(invExpr.getOperation());
                str2.append("(");
                final List<Expression> args = invExpr.getArguments();
                if (args != null) {
                    final Iterator<Expression> iter = args.iterator();
                    while (iter.hasNext()) {
                        str2.append(getJDOQLForExpression(iter.next()));
                        if (iter.hasNext()) {
                            str2.append(",");
                        }
                    }
                }
                str2.append(")");
                return str2.toString();
            }
            if (expr instanceof Literal) {
                final Literal litExpr = (Literal)expr;
                final Object value = litExpr.getLiteral();
                if (value instanceof String || value instanceof Character) {
                    return "'" + value.toString() + "'";
                }
                if (value instanceof Boolean) {
                    return value ? "TRUE" : "FALSE";
                }
                if (litExpr.getLiteral() == null) {
                    return "null";
                }
                return litExpr.getLiteral().toString();
            }
            else {
                if (expr instanceof VariableExpression) {
                    final VariableExpression varExpr = (VariableExpression)expr;
                    return varExpr.getId();
                }
                throw new UnsupportedOperationException("Dont currently support " + expr.getClass().getName() + " in JDOQLHelper");
            }
        }
    }
    
    static {
        SINGLE_STRING_KEYWORDS = new String[] { "SELECT", "UNIQUE", "INTO", "FROM", "EXCLUDE", "SUBCLASSES", "WHERE", "VARIABLES", "PARAMETERS", "GROUP", "ORDER", "BY", "RANGE" };
        SINGLE_STRING_KEYWORDS_LOWERCASE = new String[] { "select", "unique", "into", "from", "exclude", "subclasses", "where", "variables", "parameters", "group", "order", "by", "range" };
    }
}
