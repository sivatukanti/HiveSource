// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import java.util.List;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.InvokeExpression;

public class NullIfFunctionEvaluator implements InvocationEvaluator
{
    @Override
    public Object evaluate(final InvokeExpression expr, final Object ignored, final InMemoryExpressionEvaluator eval) {
        final List<Expression> args = expr.getArguments();
        if (args == null || args.isEmpty()) {
            throw new NucleusException("NULLIF requires two arguments");
        }
        if (args.size() == 1) {
            return this.getValueForArgExpression(args.get(0), eval);
        }
        final Expression argExpr1 = args.get(0);
        final Expression argExpr2 = args.get(1);
        final Object argValue1 = this.getValueForArgExpression(argExpr1, eval);
        final Object argValue2 = this.getValueForArgExpression(argExpr2, eval);
        if (argValue1 == argValue2) {
            return null;
        }
        return argValue1;
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
                throw new NucleusException("Don't support NULLIF with argument of type " + argExpr.getClass().getName());
            }
            argValue = ((Literal)argExpr).getLiteral();
        }
        return argValue;
    }
}
