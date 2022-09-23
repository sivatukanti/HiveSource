// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import java.lang.reflect.Array;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class ArraySizeMethodEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        Integer result = null;
        if (invokedValue == null) {
            result = 0;
        }
        else {
            if (!invokedValue.getClass().isArray()) {
                throw new NucleusException(ArraySizeMethodEvaluator.LOCALISER.msg("021011", method, invokedValue.getClass().getName()));
            }
            result = Array.getLength(invokedValue);
        }
        return result;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
