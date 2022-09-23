// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.shaded.com.google.common.io;

import java.io.IOException;

@Deprecated
public interface OutputSupplier<T>
{
    T getOutput() throws IOException;
}
