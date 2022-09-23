// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class LocateFunctionEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        final Object param = expr.getArguments().get(0);
        Object paramValue = null;
        if (param instanceof PrimaryExpression) {
            final PrimaryExpression primExpr = (PrimaryExpression)param;
            paramValue = eval.getValueForPrimaryExpression(primExpr);
        }
        else if (param instanceof ParameterExpression) {
            final ParameterExpression paramExpr = (ParameterExpression)param;
            paramValue = QueryUtils.getValueForParameterExpression(eval.getParameterValues(), paramExpr);
        }
        else {
            if (!(param instanceof Literal)) {
                throw new NucleusException(method + "(str1, str2, pos) where str1 is instanceof " + param.getClass().getName() + " not supported");
            }
            paramValue = ((Literal)param).getLiteral();
        }
        if (paramValue == null) {
            return -1;
        }
        final Object locStr = expr.getArguments().get(1);
        String locStrValue = null;
        if (!(locStr instanceof Literal)) {
            throw new NucleusException(method + "(str, str2, pos) where str2 is instanceof " + locStr.getClass().getName() + " not supported");
        }
        locStrValue = (String)((Literal)locStr).getLiteral();
        if (expr.getArguments().size() != 3) {
            return ((String)paramValue).indexOf(locStrValue);
        }
        final Object pos = expr.getArguments().get(2);
        int num2Value = -1;
        if (pos instanceof Literal) {
            num2Value = eval.getIntegerForLiteral((Literal)pos);
            return ((String)paramValue).indexOf(locStrValue, num2Value);
        }
        throw new NucleusException(method + "(str, str2, pos) where pos is instanceof " + pos.getClass().getName() + " not supported");
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
