// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim.linear;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalStateException;

public class UnboundedSolutionException extends MathIllegalStateException
{
    private static final long serialVersionUID = 940539497277290619L;
    
    public UnboundedSolutionException() {
        super(LocalizedFormats.UNBOUNDED_SOLUTION, new Object[0]);
    }
}
