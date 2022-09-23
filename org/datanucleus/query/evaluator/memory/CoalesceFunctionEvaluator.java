// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import java.util.Iterator;
import java.util.List;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;

public class CoalesceFunctionEvaluator implements InvocationEvaluator
{
    @Override
    public Object evaluate(final InvokeExpression expr, final Object ignored, final InMemoryExpressionEvaluator eval) {
        final List<Expression> args = expr.getArguments();
        if (args == null || args.isEmpty()) {
            return null;
        }
        final Iterator<Expression> iter = args.iterator();
        Object argValue = null;
        while (iter.hasNext()) {
            final Expression argExpr = iter.next();
            argValue = this.getValueForArgExpression(argExpr, eval);
            if (argValue != null) {
                return argValue;
            }
        }
        return null;
    }
    
    protected Object getValueForArgExpression(final Expression argExpr, final InMemoryExpressionEvaluator eval) {
        Object argValue = null;
        if (argExpr instanceof PrimaryExpression) {
            final PrimaryExpression primExpr = (PrimaryExpression)argExpr;
            argValue = eval.getValueForPrimaryExpression(primExpr);
        }
        else if (argExpr instanceof ParameterExpression) {
            final ParameterExpression paramExpr = (ParameterExpression)argExpr;
            argValue = QueryUtils.getValueForParameterExpression(eval.getParameterValues(), paramExpr);
        }
        else {
            if (!(argExpr instanceof Literal)) {
                throw new NucleusException("Don't support COALESCE with argument of type " + argExpr.getClass().getName());
            }
            argValue = ((Literal)argExpr).getLiteral();
        }
        return argValue;
    }
}
