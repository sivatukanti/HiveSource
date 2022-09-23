// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optimization.general;

@Deprecated
public interface Preconditioner
{
    double[] precondition(final double[] p0, final double[] p1);
}
