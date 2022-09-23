// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class FloatAggregateExpression extends NumericAggregateExpression
{
    public FloatAggregateExpression(final Float value) {
        super(value);
    }
    
    @Override
    public Object add(final Object obj) {
        if (obj instanceof Float) {
            return new Float((float)obj + (float)this.value);
        }
        return super.add(obj);
    }
    
    @Override
    public Object sub(final Object obj) {
        return super.sub(obj);
    }
    
    @Override
    public Object div(final Object obj) {
        if (obj instanceof Float) {
            return new Float((float)this.value / (float)obj);
        }
        return super.add(obj);
    }
    
    @Override
    public Boolean gt(final Object obj) {
        if (!(obj instanceof Float)) {
            return super.gt(obj);
        }
        if ((float)this.value > (float)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean lt(final Object obj) {
        if (!(obj instanceof Float)) {
            return super.lt(obj);
        }
        if ((float)this.value < (float)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean eq(final Object obj) {
        if (!(obj instanceof Float)) {
            return super.eq(obj);
        }
        if ((float)this.value == (float)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
