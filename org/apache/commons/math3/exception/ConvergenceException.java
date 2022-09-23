// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class ConvergenceException extends MathIllegalStateException
{
    private static final long serialVersionUID = 4330003017885151975L;
    
    public ConvergenceException() {
        this(LocalizedFormats.CONVERGENCE_FAILED, new Object[0]);
    }
    
    public ConvergenceException(final Localizable pattern, final Object... args) {
        this.getContext().addMessage(pattern, args);
    }
}
