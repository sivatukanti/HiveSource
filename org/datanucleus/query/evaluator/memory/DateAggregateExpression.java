// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import java.util.Date;

public class DateAggregateExpression extends AggregateExpression
{
    Date value;
    
    public DateAggregateExpression(final Date value) {
        this.value = value;
    }
    
    @Override
    public Object add(final Object obj) {
        if (obj instanceof Date) {
            final long currentVal = this.value.getTime();
            final long inputVal = ((Date)obj).getTime();
            return new Date(currentVal + inputVal);
        }
        return super.add(obj);
    }
    
    @Override
    public Object sub(final Object obj) {
        if (obj instanceof Date) {
            final long currentVal = this.value.getTime();
            final long inputVal = ((Date)obj).getTime();
            return new Date(currentVal - inputVal);
        }
        return super.sub(obj);
    }
    
    @Override
    public Boolean gt(final Object obj) {
        if (obj instanceof Date) {
            final long currentVal = this.value.getTime();
            final long inputVal = ((Date)obj).getTime();
            return (currentVal > inputVal) ? Boolean.TRUE : Boolean.FALSE;
        }
        return super.gt(obj);
    }
    
    @Override
    public Boolean lt(final Object obj) {
        if (obj instanceof Date) {
            final long currentVal = this.value.getTime();
            final long inputVal = ((Date)obj).getTime();
            return (currentVal < inputVal) ? Boolean.TRUE : Boolean.FALSE;
        }
        return super.lt(obj);
    }
    
    @Override
    public Boolean eq(final Object obj) {
        if (obj instanceof Date) {
            final long currentVal = this.value.getTime();
            final long inputVal = ((Date)obj).getTime();
            return (currentVal == inputVal) ? Boolean.TRUE : Boolean.FALSE;
        }
        return super.eq(obj);
    }
}
