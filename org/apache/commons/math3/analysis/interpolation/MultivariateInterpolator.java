// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.MultivariateFunction;

public interface MultivariateInterpolator
{
    MultivariateFunction interpolate(final double[][] p0, final double[] p1);
}
