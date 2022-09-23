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

public class ConcatFunctionEvaluator implements InvocationEvaluator
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
                throw new NucleusException(method + "(param) where param is instanceof " + param.getClass().getName() + " not supported");
            }
            paramValue = ((Literal)param).getLiteral();
        }
        final Object param2 = expr.getArguments().get(1);
        Object param2Value = null;
        if (param2 instanceof PrimaryExpression) {
            final PrimaryExpression primExpr2 = (PrimaryExpression)param;
            param2Value = eval.getValueForPrimaryExpression(primExpr2);
        }
        else if (param2 instanceof ParameterExpression) {
            final ParameterExpression param2Expr = (ParameterExpression)param2;
            param2Value = QueryUtils.getValueForParameterExpression(eval.getParameterValues(), param2Expr);
        }
        else {
            if (!(param2 instanceof Literal)) {
                throw new NucleusException(method + "(param, param2) where param2 is instanceof " + param2.getClass().getName() + " not supported");
            }
            param2Value = ((Literal)param).getLiteral();
        }
        if (paramValue == null) {
            return null;
        }
        return ((String)paramValue).concat((String)param2Value);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
