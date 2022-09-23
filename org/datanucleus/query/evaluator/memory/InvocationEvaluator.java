// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.query.expression.InvokeExpression;

public interface InvocationEvaluator
{
    Object evaluate(final InvokeExpression p0, final Object p1, final InMemoryExpressionEvaluator p2);
}
