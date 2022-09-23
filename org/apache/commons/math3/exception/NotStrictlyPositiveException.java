// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;

public class NotStrictlyPositiveException extends NumberIsTooSmallException
{
    private static final long serialVersionUID = -7824848630829852237L;
    
    public NotStrictlyPositiveException(final Number value) {
        super(value, 0, false);
    }
    
    public NotStrictlyPositiveException(final Localizable specific, final Number value) {
        super(specific, value, 0, false);
    }
}
