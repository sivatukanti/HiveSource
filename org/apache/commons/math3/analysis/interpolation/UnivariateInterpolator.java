// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface UnivariateInterpolator
{
    UnivariateFunction interpolate(final double[] p0, final double[] p1);
}
