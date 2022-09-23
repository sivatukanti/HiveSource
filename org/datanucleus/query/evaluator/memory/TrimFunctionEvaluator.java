// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class TrimFunctionEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        final Object param = expr.getArguments().get(0);
        char trimChar = ' ';
        if (expr.getArguments().size() == 2) {
            trimChar = (char)expr.getArguments().get(1).getLiteral();
        }
        String paramValue = null;
        if (param instanceof PrimaryExpression) {
            final PrimaryExpression primExpr = (PrimaryExpression)param;
            paramValue = (String)eval.getValueForPrimaryExpression(primExpr);
        }
        else if (param instanceof ParameterExpression) {
            final ParameterExpression paramExpr = (ParameterExpression)param;
            paramValue = (String)QueryUtils.getValueForParameterExpression(eval.getParameterValues(), paramExpr);
        }
        else {
            if (!(param instanceof Literal)) {
                throw new NucleusException(method + "(str1) where str1 is instanceof " + param.getClass().getName() + " not supported");
            }
            paramValue = (String)((Literal)param).getLiteral();
        }
        if (paramValue == null) {
            return null;
        }
        if (method.equals("TRIM")) {
            int substringStart = 0;
            for (int i = 0; i < paramValue.length() && paramValue.charAt(i) == trimChar; ++i) {
                ++substringStart;
            }
            int substringEnd = paramValue.length();
            for (int j = paramValue.length() - 1; j >= 0 && paramValue.charAt(j) == trimChar; --j) {
                --substringEnd;
            }
            return paramValue.substring(substringStart, substringEnd);
        }
        if (method.equals("TRIM_LEADING")) {
            int substringPos = 0;
            for (int i = 0; i < paramValue.length() && paramValue.charAt(i) == trimChar; ++i) {
                ++substringPos;
            }
            return paramValue.substring(substringPos);
        }
        if (method.equals("TRIM_TRAILING")) {
            int substringPos = paramValue.length();
            for (int i = paramValue.length() - 1; i >= 0 && paramValue.charAt(i) == trimChar; --i) {
                --substringPos;
            }
            return paramValue.substring(0, substringPos);
        }
        return null;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
