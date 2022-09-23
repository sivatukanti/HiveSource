// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import org.datanucleus.query.expression.ExpressionEvaluator;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class StringSubstringMethodEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        if (invokedValue == null) {
            return null;
        }
        if (!(invokedValue instanceof String)) {
            throw new NucleusException(StringSubstringMethodEvaluator.LOCALISER.msg("021011", method, invokedValue.getClass().getName()));
        }
        int arg0 = -1;
        final Expression arg0Expr = expr.getArguments().get(0);
        Object arg0Val = null;
        if (arg0Expr instanceof PrimaryExpression) {
            arg0Val = eval.getValueForPrimaryExpression((PrimaryExpression)arg0Expr);
        }
        else if (arg0Expr instanceof ParameterExpression) {
            arg0Val = QueryUtils.getValueForParameterExpression(eval.getParameterValues(), (ParameterExpression)arg0Expr);
        }
        else if (arg0Expr instanceof Literal) {
            arg0Val = ((Literal)arg0Expr).getLiteral();
        }
        else if (arg0Expr instanceof DyadicExpression) {
            arg0Val = ((DyadicExpression)arg0Expr).evaluate(eval);
        }
        if (!(arg0Val instanceof Number)) {
            throw new NucleusException(method + "(param1[,param2]) : param1 must be numeric");
        }
        arg0 = ((Number)arg0Val).intValue();
        String result = null;
        if (expr.getArguments().size() == 2) {
            int arg2 = -1;
            final Expression arg1Expr = expr.getArguments().get(1);
            Object arg1Val = null;
            if (arg1Expr instanceof PrimaryExpression) {
                arg1Val = eval.getValueForPrimaryExpression((PrimaryExpression)arg1Expr);
            }
            else if (arg1Expr instanceof ParameterExpression) {
                arg1Val = QueryUtils.getValueForParameterExpression(eval.getParameterValues(), (ParameterExpression)arg1Expr);
            }
            else if (arg1Expr instanceof Literal) {
                arg1Val = ((Literal)arg1Expr).getLiteral();
            }
            else if (arg0Expr instanceof DyadicExpression) {
                arg1Val = ((DyadicExpression)arg1Expr).evaluate(eval);
            }
            if (!(arg1Val instanceof Number)) {
                throw new NucleusException(method + "(param1,param2) : param2 must be numeric");
            }
            arg2 = ((Number)arg1Val).intValue();
            if (((String)invokedValue).length() < arg2) {
                if (((String)invokedValue).length() < arg0) {
                    return null;
                }
                return ((String)invokedValue).substring(arg0);
            }
            else {
                result = ((String)invokedValue).substring(arg0, arg2);
            }
        }
        else {
            if (((String)invokedValue).length() < arg0) {
                return null;
            }
            result = ((String)invokedValue).substring(arg0);
        }
        return result;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
