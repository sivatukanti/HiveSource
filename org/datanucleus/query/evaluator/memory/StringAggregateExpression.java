// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class StringAggregateExpression extends AggregateExpression
{
    String value;
    
    public StringAggregateExpression(final String value) {
        this.value = value;
    }
    
    @Override
    public Boolean gt(final Object obj) {
        if (!(obj instanceof String)) {
            return super.gt(obj);
        }
        if (this.value.compareTo((String)obj) > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean lt(final Object obj) {
        if (!(obj instanceof String)) {
            return super.lt(obj);
        }
        if (this.value.compareTo((String)obj) < 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean eq(final Object obj) {
        if (!(obj instanceof String)) {
            return super.eq(obj);
        }
        if (this.value.equals(obj)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
