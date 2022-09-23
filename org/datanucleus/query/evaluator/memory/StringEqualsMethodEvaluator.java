// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class StringEqualsMethodEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        if (invokedValue == null) {
            return Boolean.FALSE;
        }
        if (!(invokedValue instanceof String)) {
            throw new NucleusException(StringEqualsMethodEvaluator.LOCALISER.msg("021011", method, invokedValue.getClass().getName()));
        }
        String arg1 = null;
        Object argObj = null;
        final Object param = expr.getArguments().get(0);
        if (param instanceof PrimaryExpression) {
            final PrimaryExpression primExpr = (PrimaryExpression)param;
            argObj = eval.getValueForPrimaryExpression(primExpr);
        }
        else if (param instanceof ParameterExpression) {
            final ParameterExpression paramExpr = (ParameterExpression)param;
            argObj = QueryUtils.getValueForParameterExpression(eval.getParameterValues(), paramExpr);
        }
        else if (param instanceof Literal) {
            argObj = ((Literal)param).getLiteral();
        }
        else {
            if (!(param instanceof InvokeExpression)) {
                throw new NucleusException(method + "(param) where param is instanceof " + param.getClass().getName() + " not supported");
            }
            argObj = eval.getValueForInvokeExpression((InvokeExpression)param);
        }
        arg1 = QueryUtils.getStringValue(argObj);
        return ((String)invokedValue).equals(arg1) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
