// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.template;

import java.io.IOException;
import java.io.OutputStream;

public interface TemplateProcessor
{
    @Deprecated
    String resolve(final String p0);
    
    @Deprecated
    void writeTo(final String p0, final Object p1, final OutputStream p2) throws IOException;
}
