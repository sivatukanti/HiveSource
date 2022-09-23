// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;

public class NotPositiveException extends NumberIsTooSmallException
{
    private static final long serialVersionUID = -2250556892093726375L;
    
    public NotPositiveException(final Number value) {
        super(value, 0, true);
    }
    
    public NotPositiveException(final Localizable specific, final Number value) {
        super(specific, value, 0, true);
    }
}
