// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.random;

import java.util.Random;

public class JDKRandomGenerator extends Random implements RandomGenerator
{
    private static final long serialVersionUID = -7745277476784028798L;
    
    public void setSeed(final int seed) {
        this.setSeed(seed);
    }
    
    public void setSeed(final int[] seed) {
        final long prime = 4294967291L;
        long combined = 0L;
        for (final int s : seed) {
            combined = combined * 4294967291L + s;
        }
        this.setSeed(combined);
    }
}
