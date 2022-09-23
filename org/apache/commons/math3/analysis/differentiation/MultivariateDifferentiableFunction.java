// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.MultivariateFunction;

public interface MultivariateDifferentiableFunction extends MultivariateFunction
{
    DerivativeStructure value(final DerivativeStructure[] p0) throws MathIllegalArgumentException;
}
