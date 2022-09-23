// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class ListGetMethodEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        if (invokedValue == null) {
            return null;
        }
        if (!(invokedValue instanceof List)) {
            throw new NucleusException(ListGetMethodEvaluator.LOCALISER.msg("021011", method, invokedValue.getClass().getName()));
        }
        final Object param = expr.getArguments().get(0);
        Object paramValue = null;
        if (param instanceof Literal) {
            paramValue = ((Literal)param).getLiteral();
        }
        else if (param instanceof PrimaryExpression) {
            final PrimaryExpression primExpr = (PrimaryExpression)param;
            paramValue = eval.getValueForPrimaryExpression(primExpr);
        }
        else if (param instanceof ParameterExpression) {
            final ParameterExpression paramExpr = (ParameterExpression)param;
            paramValue = QueryUtils.getValueForParameterExpression(eval.getParameterValues(), paramExpr);
        }
        else {
            if (!(param instanceof VariableExpression)) {
                throw new NucleusException("Dont currently support use of get(" + param.getClass().getName() + ")");
            }
            final VariableExpression varExpr = (VariableExpression)param;
            try {
                paramValue = eval.getValueForVariableExpression(varExpr);
            }
            catch (VariableNotSetException vnse) {
                throw new VariableNotSetException(varExpr, ((List)invokedValue).toArray());
            }
        }
        if (paramValue instanceof Number) {
            final int paramInt = ((Number)paramValue).intValue();
            return ((List)invokedValue).get(paramInt);
        }
        throw new NucleusException("List.get() should take in an integer but is " + paramValue);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
