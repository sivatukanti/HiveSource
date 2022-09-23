// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.random;

import org.apache.commons.math3.util.FastMath;

public class UnitSphereRandomVectorGenerator implements RandomVectorGenerator
{
    private final RandomGenerator rand;
    private final int dimension;
    
    public UnitSphereRandomVectorGenerator(final int dimension, final RandomGenerator rand) {
        this.dimension = dimension;
        this.rand = rand;
    }
    
    public UnitSphereRandomVectorGenerator(final int dimension) {
        this(dimension, new MersenneTwister());
    }
    
    public double[] nextVector() {
        final double[] v = new double[this.dimension];
        double normSq;
        do {
            normSq = 0.0;
            for (int i = 0; i < this.dimension; ++i) {
                final double comp = 2.0 * this.rand.nextDouble() - 1.0;
                v[i] = comp;
                normSq += comp * comp;
            }
        } while (normSq > 1.0);
        final double f = 1.0 / FastMath.sqrt(normSq);
        for (int j = 0; j < this.dimension; ++j) {
            final double[] array = v;
            final int n = j;
            array[n] *= f;
        }
        return v;
    }
}
