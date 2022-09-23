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
import java.util.Map;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class MapContainsKeyMethodEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        if (invokedValue == null) {
            return Boolean.FALSE;
        }
        if (!(invokedValue instanceof Map)) {
            throw new NucleusException(MapContainsKeyMethodEvaluator.LOCALISER.msg("021011", method, invokedValue.getClass().getName()));
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
                throw new NucleusException("Dont currently support use of containsKey(" + param.getClass().getName() + ")");
            }
            final VariableExpression varExpr = (VariableExpression)param;
            try {
                paramValue = eval.getValueForVariableExpression(varExpr);
            }
            catch (VariableNotSetException vnse) {
                throw new VariableNotSetException(varExpr, ((Map)invokedValue).keySet().toArray());
            }
        }
        return ((Map)invokedValue).containsKey(paramValue) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
