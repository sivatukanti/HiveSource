// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class LongAggregateExpression extends NumericAggregateExpression
{
    public LongAggregateExpression(final Long value) {
        super(value);
    }
    
    @Override
    public Object add(final Object obj) {
        if (obj instanceof Long) {
            return (long)obj + (long)this.value;
        }
        return super.add(obj);
    }
    
    @Override
    public Object sub(final Object obj) {
        return super.sub(obj);
    }
    
    @Override
    public Object div(final Object obj) {
        if (obj instanceof Long) {
            return (long)this.value / (long)obj;
        }
        return super.add(obj);
    }
    
    @Override
    public Boolean gt(final Object obj) {
        if (!(obj instanceof Long)) {
            return super.gt(obj);
        }
        if ((long)this.value > (long)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean lt(final Object obj) {
        if (!(obj instanceof Long)) {
            return super.lt(obj);
        }
        if ((long)this.value < (long)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean eq(final Object obj) {
        if (!(obj instanceof Long)) {
            return super.eq(obj);
        }
        if ((long)this.value == (long)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
