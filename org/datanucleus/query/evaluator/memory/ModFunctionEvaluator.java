// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class ModFunctionEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        final Object param1 = expr.getArguments().get(0);
        int param1Value = -1;
        if (param1 instanceof PrimaryExpression) {
            final PrimaryExpression primExpr = (PrimaryExpression)param1;
            final Object val = eval.getValueForPrimaryExpression(primExpr);
            if (!(val instanceof Number)) {
                throw new NucleusException(method + "(num1, num2) where num1 is instanceof " + param1.getClass().getName() + " but should be integer");
            }
            param1Value = ((Number)val).intValue();
        }
        else if (param1 instanceof ParameterExpression) {
            final ParameterExpression paramExpr = (ParameterExpression)param1;
            final Object val = QueryUtils.getValueForParameterExpression(eval.getParameterValues(), paramExpr);
            if (!(val instanceof Number)) {
                throw new NucleusException(method + "(num1, num2) where num1 is instanceof " + param1.getClass().getName() + " but should be integer");
            }
            param1Value = ((Number)val).intValue();
        }
        else {
            if (!(param1 instanceof Literal)) {
                throw new NucleusException(method + "(num1, num2) where num1 is instanceof " + param1.getClass().getName() + " not supported");
            }
            final Object val2 = ((Literal)param1).getLiteral();
            if (!(val2 instanceof Number)) {
                throw new NucleusException(method + "(num1, num2) where num1 is instanceof " + param1.getClass().getName() + " but should be integer");
            }
            param1Value = ((Number)val2).intValue();
        }
        final Object param2 = expr.getArguments().get(1);
        int param2Value = -1;
        if (param2 instanceof PrimaryExpression) {
            final PrimaryExpression primExpr2 = (PrimaryExpression)param2;
            final Object val3 = eval.getValueForPrimaryExpression(primExpr2);
            if (!(val3 instanceof Number)) {
                throw new NucleusException(method + "(num1, num2) where num2 is instanceof " + param2.getClass().getName() + " but should be integer");
            }
            param2Value = ((Number)val3).intValue();
        }
        else if (param2 instanceof ParameterExpression) {
            final ParameterExpression paramExpr2 = (ParameterExpression)param2;
            final Object val3 = QueryUtils.getValueForParameterExpression(eval.getParameterValues(), paramExpr2);
            if (!(val3 instanceof Number)) {
                throw new NucleusException(method + "(num1, num2) where num1 is instanceof " + param2.getClass().getName() + " but should be integer");
            }
            param2Value = ((Number)val3).intValue();
        }
        else {
            if (!(param2 instanceof Literal)) {
                throw new NucleusException(method + "(num1, num2) where num2 is instanceof " + param2.getClass().getName() + " not supported");
            }
            final Object val4 = ((Literal)param2).getLiteral();
            if (!(val4 instanceof Number)) {
                throw new NucleusException(method + "(num1, num2) where num2 is instanceof " + param2.getClass().getName() + " but should be integer");
            }
            param2Value = ((Number)val4).intValue();
        }
        return param1Value % param2Value;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
