// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class EnumMatchesMethodEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        if (invokedValue == null) {
            return Boolean.FALSE;
        }
        if (!(invokedValue instanceof Enum)) {
            throw new NucleusException(EnumMatchesMethodEvaluator.LOCALISER.msg("021011", method, invokedValue.getClass().getName()));
        }
        String arg = null;
        Object argObj = null;
        final Object param = expr.getArguments().get(0);
        if (expr.getArguments().size() > 1) {
            NucleusLogger.QUERY.info("Please note that any escape character is currently ignored");
        }
        if (param instanceof PrimaryExpression) {
            final PrimaryExpression primExpr = (PrimaryExpression)param;
            argObj = eval.getValueForPrimaryExpression(primExpr);
        }
        else if (param instanceof ParameterExpression) {
            final ParameterExpression paramExpr = (ParameterExpression)param;
            argObj = QueryUtils.getValueForParameterExpression(eval.getParameterValues(), paramExpr);
        }
        else {
            if (!(param instanceof Literal)) {
                throw new NucleusException(method + "(param, num1, num2) where param is instanceof " + param.getClass().getName() + " not supported");
            }
            argObj = ((Literal)param).getLiteral();
        }
        arg = QueryUtils.getStringValue(argObj);
        return ((Enum)invokedValue).toString().matches(arg) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
