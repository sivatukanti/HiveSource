// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

public class NonPositiveDefiniteMatrixException extends NumberIsTooSmallException
{
    private static final long serialVersionUID = 1641613838113738061L;
    private final int index;
    private final double threshold;
    
    public NonPositiveDefiniteMatrixException(final double wrong, final int index, final double threshold) {
        super(wrong, threshold, false);
        this.index = index;
        this.threshold = threshold;
        final ExceptionContext context = this.getContext();
        context.addMessage(LocalizedFormats.NOT_POSITIVE_DEFINITE_MATRIX, new Object[0]);
        context.addMessage(LocalizedFormats.ARRAY_ELEMENT, wrong, index);
    }
    
    public int getRow() {
        return this.index;
    }
    
    public int getColumn() {
        return this.index;
    }
    
    public double getThreshold() {
        return this.threshold;
    }
}
