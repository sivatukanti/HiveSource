// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.random;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.util.FastMath;
import java.io.Serializable;

public abstract class BitsStreamGenerator implements RandomGenerator, Serializable
{
    private static final long serialVersionUID = 20130104L;
    private double nextGaussian;
    
    public BitsStreamGenerator() {
        this.nextGaussian = Double.NaN;
    }
    
    public abstract void setSeed(final int p0);
    
    public abstract void setSeed(final int[] p0);
    
    public abstract void setSeed(final long p0);
    
    protected abstract int next(final int p0);
    
    public boolean nextBoolean() {
        return this.next(1) != 0;
    }
    
    public void nextBytes(final byte[] bytes) {
        int i = 0;
        for (int iEnd = bytes.length - 3; i < iEnd; i += 4) {
            final int random = this.next(32);
            bytes[i] = (byte)(random & 0xFF);
            bytes[i + 1] = (byte)(random >> 8 & 0xFF);
            bytes[i + 2] = (byte)(random >> 16 & 0xFF);
            bytes[i + 3] = (byte)(random >> 24 & 0xFF);
        }
        for (int random = this.next(32); i < bytes.length; bytes[i++] = (byte)(random & 0xFF), random >>= 8) {}
    }
    
    public double nextDouble() {
        final long high = (long)this.next(26) << 26;
        final int low = this.next(26);
        return (high | (long)low) * 2.220446049250313E-16;
    }
    
    public float nextFloat() {
        return this.next(23) * 1.1920929E-7f;
    }
    
    public double nextGaussian() {
        double random;
        if (Double.isNaN(this.nextGaussian)) {
            final double x = this.nextDouble();
            final double y = this.nextDouble();
            final double alpha = 6.283185307179586 * x;
            final double r = FastMath.sqrt(-2.0 * FastMath.log(y));
            random = r * FastMath.cos(alpha);
            this.nextGaussian = r * FastMath.sin(alpha);
        }
        else {
            random = this.nextGaussian;
            this.nextGaussian = Double.NaN;
        }
        return random;
    }
    
    public int nextInt() {
        return this.next(32);
    }
    
    public int nextInt(final int n) throws IllegalArgumentException {
        if (n <= 0) {
            throw new NotStrictlyPositiveException(n);
        }
        if ((n & -n) == n) {
            return (int)(n * (long)this.next(31) >> 31);
        }
        int bits;
        int val;
        do {
            bits = this.next(31);
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }
    
    public long nextLong() {
        final long high = (long)this.next(32) << 32;
        final long low = (long)this.next(32) & 0xFFFFFFFFL;
        return high | low;
    }
    
    public void clear() {
        this.nextGaussian = Double.NaN;
    }
}
