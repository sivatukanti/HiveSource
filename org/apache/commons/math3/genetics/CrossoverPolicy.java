// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface CrossoverPolicy
{
    ChromosomePair crossover(final Chromosome p0, final Chromosome p1) throws MathIllegalArgumentException;
}
