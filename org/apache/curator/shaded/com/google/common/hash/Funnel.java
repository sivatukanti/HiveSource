// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.shaded.com.google.common.hash;

import org.apache.curator.shaded.com.google.common.annotations.Beta;
import java.io.Serializable;

@Beta
public interface Funnel<T> extends Serializable
{
    void funnel(final T p0, final PrimitiveSink p1);
}
