// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.template;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.jersey.api.view.Viewable;

public interface ViewProcessor<T>
{
    T resolve(final String p0);
    
    void writeTo(final T p0, final Viewable p1, final OutputStream p2) throws IOException;
}
