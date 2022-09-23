// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.Reader;

public class Stax2StringSource extends Stax2BlockSource
{
    final String mText;
    
    public Stax2StringSource(final String mText) {
        this.mText = mText;
    }
    
    @Override
    public Reader constructReader() throws IOException {
        return new StringReader(this.mText);
    }
    
    @Override
    public InputStream constructInputStream() throws IOException {
        return null;
    }
    
    public String getText() {
        return this.mText;
    }
}
