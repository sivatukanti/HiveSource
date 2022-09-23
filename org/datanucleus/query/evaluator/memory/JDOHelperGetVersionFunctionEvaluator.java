// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import javax.jdo.JDOHelper;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class JDOHelperGetVersionFunctionEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final Expression argExpr = expr.getArguments().get(0);
        if (argExpr instanceof PrimaryExpression) {
            final PrimaryExpression primExpr = (PrimaryExpression)argExpr;
            final Object value = eval.getValueForPrimaryExpression(primExpr);
            return JDOHelper.getVersion(value);
        }
        if (argExpr instanceof ParameterExpression) {
            final ParameterExpression paramExpr = (ParameterExpression)argExpr;
            final Object value = QueryUtils.getValueForParameterExpression(eval.getParameterValues(), paramExpr);
            return JDOHelper.getVersion(value);
        }
        throw new NucleusException("Dont currently support JDOHelper.getVersion with arg of type " + argExpr.getClass().getName());
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
