// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.stat.descriptive.rank;

import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;

public class Median extends Percentile implements Serializable
{
    private static final long serialVersionUID = -3961477041290915687L;
    
    public Median() {
        super(50.0);
    }
    
    public Median(final Median original) throws NullArgumentException {
        super(original);
    }
}
