// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.special;

import org.apache.commons.math3.util.FastMath;

public class Erf
{
    private static final double X_CRIT = 0.4769362762044697;
    
    private Erf() {
    }
    
    public static double erf(final double x) {
        if (FastMath.abs(x) > 40.0) {
            return (x > 0.0) ? 1.0 : -1.0;
        }
        final double ret = Gamma.regularizedGammaP(0.5, x * x, 1.0E-15, 10000);
        return (x < 0.0) ? (-ret) : ret;
    }
    
    public static double erfc(final double x) {
        if (FastMath.abs(x) > 40.0) {
            return (x > 0.0) ? 0.0 : 2.0;
        }
        final double ret = Gamma.regularizedGammaQ(0.5, x * x, 1.0E-15, 10000);
        return (x < 0.0) ? (2.0 - ret) : ret;
    }
    
    public static double erf(final double x1, final double x2) {
        if (x1 > x2) {
            return -erf(x2, x1);
        }
        return (x1 < -0.4769362762044697) ? ((x2 < 0.0) ? (erfc(-x2) - erfc(-x1)) : (erf(x2) - erf(x1))) : ((x2 > 0.4769362762044697 && x1 > 0.0) ? (erfc(x1) - erfc(x2)) : (erf(x2) - erf(x1)));
    }
}
