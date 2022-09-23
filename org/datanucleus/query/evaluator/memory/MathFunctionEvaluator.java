// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import java.math.BigDecimal;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public abstract class MathFunctionEvaluator implements InvocationEvaluator
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
        else if (param instanceof InvokeExpression) {
            final InvokeExpression invokeExpr = (InvokeExpression)param;
            paramValue = eval.getValueForInvokeExpression(invokeExpr);
        }
        else {
            if (!(param instanceof Literal)) {
                throw new NucleusException(method + "(num) where num is instanceof " + param.getClass().getName() + " not supported");
            }
            paramValue = ((Literal)param).getLiteral();
        }
        Object result = null;
        if (paramValue instanceof Double) {
            result = new Double(this.evaluateMathFunction((double)paramValue));
        }
        else if (paramValue instanceof Float) {
            result = new Float(this.evaluateMathFunction((float)paramValue));
        }
        else if (paramValue instanceof BigDecimal) {
            result = new BigDecimal(this.evaluateMathFunction(((BigDecimal)paramValue).doubleValue()));
        }
        else if (paramValue instanceof Integer) {
            result = new Double(this.evaluateMathFunction((double)paramValue));
        }
        else {
            if (!(paramValue instanceof Long)) {
                throw new NucleusException("Not possible to use " + this.getFunctionName() + " on value of type " + paramValue.getClass().getName());
            }
            result = new Double(this.evaluateMathFunction((double)paramValue));
        }
        return result;
    }
    
    protected abstract String getFunctionName();
    
    protected abstract double evaluateMathFunction(final double p0);
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
