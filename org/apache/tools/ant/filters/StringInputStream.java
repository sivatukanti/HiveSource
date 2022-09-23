// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.filters;

import java.io.Reader;
import java.io.StringReader;
import org.apache.tools.ant.util.ReaderInputStream;

public class StringInputStream extends ReaderInputStream
{
    public StringInputStream(final String source) {
        super(new StringReader(source));
    }
    
    public StringInputStream(final String source, final String encoding) {
        super(new StringReader(source), encoding);
    }
}
