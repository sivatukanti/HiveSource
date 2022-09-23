// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NucleusException;
import java.util.Map;
import java.util.Collection;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class ContainerIsEmptyMethodEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        Boolean result = null;
        if (invokedValue == null) {
            result = Boolean.TRUE;
        }
        else if (invokedValue instanceof Collection) {
            result = (((Collection)invokedValue).isEmpty() ? Boolean.TRUE : Boolean.FALSE);
        }
        else {
            if (!(invokedValue instanceof Map)) {
                throw new NucleusException(ContainerIsEmptyMethodEvaluator.LOCALISER.msg("021011", method, invokedValue.getClass().getName()));
            }
            result = (((Map)invokedValue).isEmpty() ? Boolean.TRUE : Boolean.FALSE);
        }
        return result;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
