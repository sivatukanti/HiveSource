// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.Space;

public interface Embedding<S extends Space, T extends Space>
{
    Vector<T> toSubSpace(final Vector<S> p0);
    
    Vector<S> toSpace(final Vector<T> p0);
}
