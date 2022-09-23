// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class NumericAggregateExpression extends AggregateExpression
{
    Number value;
    
    public NumericAggregateExpression(final Number value) {
        this.value = value;
    }
    
    @Override
    public Object add(final Object obj) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object sub(final Object obj) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object div(final Object obj) {
        throw new UnsupportedOperationException();
    }
}
