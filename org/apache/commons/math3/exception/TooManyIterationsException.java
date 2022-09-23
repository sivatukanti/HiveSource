// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class TooManyIterationsException extends MaxCountExceededException
{
    private static final long serialVersionUID = 20121211L;
    
    public TooManyIterationsException(final Number max) {
        super(max);
        this.getContext().addMessage(LocalizedFormats.ITERATIONS, new Object[0]);
    }
}
