// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

public class DoubleAggregateExpression extends NumericAggregateExpression
{
    public DoubleAggregateExpression(final Double value) {
        super(value);
    }
    
    @Override
    public Object add(final Object obj) {
        if (obj instanceof Double) {
            return new Double((double)obj + (double)this.value);
        }
        return super.add(obj);
    }
    
    @Override
    public Object sub(final Object obj) {
        return super.sub(obj);
    }
    
    @Override
    public Object div(final Object obj) {
        if (obj instanceof Double) {
            return new Double((double)this.value / (double)obj);
        }
        return super.add(obj);
    }
    
    @Override
    public Boolean gt(final Object obj) {
        if (!(obj instanceof Double)) {
            return super.gt(obj);
        }
        if ((double)this.value > (double)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean lt(final Object obj) {
        if (!(obj instanceof Double)) {
            return super.lt(obj);
        }
        if ((double)this.value < (double)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean eq(final Object obj) {
        if (!(obj instanceof Double)) {
            return super.eq(obj);
        }
        if ((double)this.value == (double)obj) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
