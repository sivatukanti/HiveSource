// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.io;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public interface EscapingWriterFactory
{
    Writer createEscapingWriterFor(final Writer p0, final String p1) throws UnsupportedEncodingException;
    
    Writer createEscapingWriterFor(final OutputStream p0, final String p1) throws UnsupportedEncodingException;
}
