// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class IntegerAggregateExpression extends NumericAggregateExpression
{
    public IntegerAggregateExpression(final Integer value) {
        super(value);
    }
    
    @Override
    public Object add(final Object obj) {
        if (obj instanceof Integer) {
            return (int)obj + (int)this.value;
        }
        return super.add(obj);
    }
    
    @Override
    public Object sub(final Object obj) {
        return super.sub(obj);
    }
    
    @Override
    public Object div(final Object obj) {
        if (obj instanceof Integer) {
            return (int)this.value / (int)obj;
        }
        return super.add(obj);
    }
    
    @Override
    public Boolean gt(final Object obj) {
        if (!(obj instanceof Integer)) {
            return super.gt(obj);
        }
        if ((int)this.value > (int)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean lt(final Object obj) {
        if (!(obj instanceof Integer)) {
            return super.lt(obj);
        }
        if ((int)this.value < (int)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean eq(final Object obj) {
        if (!(obj instanceof Integer)) {
            return super.eq(obj);
        }
        if ((int)this.value == (int)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
