// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import java.math.BigInteger;

public class BigIntegerAggregateExpression extends NumericAggregateExpression
{
    public BigIntegerAggregateExpression(final BigInteger value) {
        super(value);
    }
    
    @Override
    public Object add(final Object obj) {
        if (obj instanceof BigInteger) {
            return BigInteger.valueOf(((BigInteger)obj).longValue() + ((BigInteger)this.value).longValue());
        }
        return super.add(obj);
    }
    
    @Override
    public Object sub(final Object obj) {
        return super.sub(obj);
    }
    
    @Override
    public Object div(final Object obj) {
        if (obj instanceof BigInteger) {
            return BigInteger.valueOf(((BigInteger)this.value).longValue() / ((BigInteger)obj).longValue());
        }
        return super.add(obj);
    }
    
    @Override
    public Boolean gt(final Object obj) {
        if (obj instanceof BigInteger) {
            return ((BigInteger)this.value).longValue() > ((BigInteger)obj).longValue();
        }
        return super.gt(obj);
    }
    
    @Override
    public Boolean lt(final Object obj) {
        if (obj instanceof BigInteger) {
            return ((BigInteger)this.value).longValue() < ((BigInteger)obj).longValue();
        }
        return super.lt(obj);
    }
    
    @Override
    public Boolean eq(final Object obj) {
        if (obj instanceof BigInteger) {
            return ((BigInteger)this.value).longValue() == ((BigInteger)obj).longValue();
        }
        return super.eq(obj);
    }
}
