// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;

public class NonMonotonicSequenceException extends MathIllegalNumberException
{
    private static final long serialVersionUID = 3596849179428944575L;
    private final MathArrays.OrderDirection direction;
    private final boolean strict;
    private final int index;
    private final Number previous;
    
    public NonMonotonicSequenceException(final Number wrong, final Number previous, final int index) {
        this(wrong, previous, index, MathArrays.OrderDirection.INCREASING, true);
    }
    
    public NonMonotonicSequenceException(final Number wrong, final Number previous, final int index, final MathArrays.OrderDirection direction, final boolean strict) {
        super((direction == MathArrays.OrderDirection.INCREASING) ? (strict ? LocalizedFormats.NOT_STRICTLY_INCREASING_SEQUENCE : LocalizedFormats.NOT_INCREASING_SEQUENCE) : (strict ? LocalizedFormats.NOT_STRICTLY_DECREASING_SEQUENCE : LocalizedFormats.NOT_DECREASING_SEQUENCE), wrong, new Object[] { previous, index, index - 1 });
        this.direction = direction;
        this.strict = strict;
        this.index = index;
        this.previous = previous;
    }
    
    public MathArrays.OrderDirection getDirection() {
        return this.direction;
    }
    
    public boolean getStrict() {
        return this.strict;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public Number getPrevious() {
        return this.previous;
    }
}
