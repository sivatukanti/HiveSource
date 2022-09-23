// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class ShortAggregateExpression extends NumericAggregateExpression
{
    public ShortAggregateExpression(final Short value) {
        super(value);
    }
    
    @Override
    public Object add(final Object obj) {
        if (obj instanceof Short) {
            return (short)((short)obj + (short)this.value);
        }
        return super.add(obj);
    }
    
    @Override
    public Object sub(final Object obj) {
        return super.sub(obj);
    }
    
    @Override
    public Object div(final Object obj) {
        if (obj instanceof Short) {
            return (short)((short)this.value / (short)obj);
        }
        return super.add(obj);
    }
    
    @Override
    public Boolean gt(final Object obj) {
        if (!(obj instanceof Short)) {
            return super.gt(obj);
        }
        if ((short)this.value > (short)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean lt(final Object obj) {
        if (!(obj instanceof Short)) {
            return super.lt(obj);
        }
        if ((short)this.value < (short)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean eq(final Object obj) {
        if (!(obj instanceof Short)) {
            return super.eq(obj);
        }
        if ((short)this.value == (short)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
