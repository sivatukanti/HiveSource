// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import java.util.Calendar;
import org.datanucleus.exceptions.NucleusException;
import java.util.Date;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class DateGetMonthMethodEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        if (invokedValue == null) {
            return Boolean.FALSE;
        }
        if (!(invokedValue instanceof Date)) {
            throw new NucleusException(DateGetMonthMethodEvaluator.LOCALISER.msg("021011", method, invokedValue.getClass().getName()));
        }
        final Calendar cal = Calendar.getInstance();
        cal.setTime((Date)invokedValue);
        return cal.get(2);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
