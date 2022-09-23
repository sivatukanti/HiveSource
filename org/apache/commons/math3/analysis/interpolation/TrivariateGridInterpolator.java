// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.analysis.TrivariateFunction;

public interface TrivariateGridInterpolator
{
    TrivariateFunction interpolate(final double[] p0, final double[] p1, final double[] p2, final double[][][] p3) throws NoDataException, DimensionMismatchException;
}
