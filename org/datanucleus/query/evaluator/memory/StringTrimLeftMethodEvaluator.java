// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import java.util.List;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.util.Localiser;

public class StringTrimLeftMethodEvaluator implements InvocationEvaluator
{
    protected static final Localiser LOCALISER;
    
    @Override
    public Object evaluate(final InvokeExpression expr, final Object invokedValue, final InMemoryExpressionEvaluator eval) {
        final String method = expr.getOperation();
        final List args = expr.getArguments();
        char trimChar = ' ';
        if (args != null && args.size() > 0) {
            trimChar = args.get(0);
        }
        if (invokedValue == null) {
            return null;
        }
        if (!(invokedValue instanceof String)) {
            throw new NucleusException(StringTrimLeftMethodEvaluator.LOCALISER.msg("021011", method, invokedValue.getClass().getName()));
        }
        final String strValue = (String)invokedValue;
        int substringPos = 0;
        for (int i = 0; i < strValue.length() && strValue.charAt(i) == trimChar; ++i) {
            ++substringPos;
        }
        return strValue.substring(substringPos);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
