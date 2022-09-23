// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import java.lang.reflect.Array;
import java.util.Collection;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class ArrayContainsMethodEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        if (invokedValue == null) {
            return Boolean.FALSE;
        }
        if (!invokedValue.getClass().isArray()) {
            throw new NucleusException(ArrayContainsMethodEvaluator.LOCALISER.msg("021011", method, invokedValue.getClass().getName()));
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
                throw new NucleusException("Dont currently support use of Array.contains(" + param.getClass().getName() + ")");
            }
            final VariableExpression varExpr = (VariableExpression)param;
            try {
                paramValue = eval.getValueForVariableExpression(varExpr);
            }
            catch (VariableNotSetException vnse) {
                throw new VariableNotSetException(varExpr, ((Collection)invokedValue).toArray());
            }
        }
        for (int i = 0; i < Array.getLength(invokedValue); ++i) {
            final Object elem = Array.get(invokedValue, i);
            if (elem == null && paramValue == null) {
                return Boolean.TRUE;
            }
            if (elem != null && paramValue != null) {
                if (elem.equals(paramValue)) {
                    return Boolean.TRUE;
                }
                if (!paramValue.getClass().isAssignableFrom(elem.getClass()) && !elem.getClass().isAssignableFrom(paramValue.getClass()) && (paramValue.getClass() == Long.class || paramValue.getClass() == Integer.class || paramValue.getClass() == Short.class) && (elem.getClass() == Long.class || elem.getClass() == Integer.class || elem.getClass() == Short.class)) {
                    final long paramLong = ((Number)paramValue).longValue();
                    final long elemLong = ((Number)elem).longValue();
                    if (paramLong == elemLong) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
