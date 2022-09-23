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
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class StringCharAtMethodEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        if (invokedValue == null) {
            return -1;
        }
        if (!(invokedValue instanceof String)) {
            throw new NucleusException(StringCharAtMethodEvaluator.LOCALISER.msg("021011", method, invokedValue.getClass().getName()));
        }
        Object arg1Obj = null;
        final Object param = expr.getArguments().get(0);
        if (param instanceof PrimaryExpression) {
            final PrimaryExpression primExpr = (PrimaryExpression)param;
            arg1Obj = eval.getValueForPrimaryExpression(primExpr);
        }
        else if (param instanceof ParameterExpression) {
            final ParameterExpression paramExpr = (ParameterExpression)param;
            arg1Obj = QueryUtils.getValueForParameterExpression(eval.getParameterValues(), paramExpr);
        }
        else if (param instanceof Literal) {
            arg1Obj = ((Literal)param).getLiteral();
        }
        else if (param instanceof InvokeExpression) {
            arg1Obj = eval.getValueForInvokeExpression((InvokeExpression)param);
        }
        else {
            if (!(param instanceof DyadicExpression)) {
                throw new NucleusException(method + "(param1) where param is instanceof " + param.getClass().getName() + " not supported");
            }
            arg1Obj = ((DyadicExpression)param).evaluate(eval);
        }
        if (!(arg1Obj instanceof Number)) {
            throw new NucleusException(method + "(param1]) : param1 must be numeric");
        }
        final int arg1 = ((Number)arg1Obj).intValue();
        return ((String)invokedValue).indexOf(arg1);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
