// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.lang3.concurrent;

public interface Computable<I, O>
{
    O compute(final I p0) throws InterruptedException;
}
