// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import java.math.BigDecimal;

public class BigDecimalAggregateExpression extends NumericAggregateExpression
{
    public BigDecimalAggregateExpression(final BigDecimal value) {
        super(value);
    }
    
    @Override
    public Object add(final Object obj) {
        if (obj instanceof BigDecimal) {
            return new BigDecimal(((BigDecimal)obj).doubleValue() + ((BigDecimal)this.value).doubleValue());
        }
        return super.add(obj);
    }
    
    @Override
    public Object sub(final Object obj) {
        return super.sub(obj);
    }
    
    @Override
    public Object div(final Object obj) {
        if (obj instanceof BigDecimal) {
            return new BigDecimal(((BigDecimal)this.value).doubleValue() / ((BigDecimal)obj).doubleValue());
        }
        return super.add(obj);
    }
    
    @Override
    public Boolean gt(final Object obj) {
        if (!(obj instanceof BigDecimal)) {
            return super.gt(obj);
        }
        if (((BigDecimal)this.value).doubleValue() > ((BigDecimal)obj).doubleValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean lt(final Object obj) {
        if (!(obj instanceof BigDecimal)) {
            return super.lt(obj);
        }
        if (((BigDecimal)this.value).doubleValue() < ((BigDecimal)obj).doubleValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean eq(final Object obj) {
        if (!(obj instanceof BigDecimal)) {
            return super.eq(obj);
        }
        if (((BigDecimal)this.value).doubleValue() == ((BigDecimal)obj).doubleValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
