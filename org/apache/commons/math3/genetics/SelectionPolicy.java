// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface SelectionPolicy
{
    ChromosomePair select(final Population p0) throws MathIllegalArgumentException;
}
