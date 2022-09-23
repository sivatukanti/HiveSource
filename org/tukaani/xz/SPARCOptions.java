// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.SimpleFilter;
import org.tukaani.xz.simple.SPARC;

public class SPARCOptions extends BCJOptions
{
    private static final int ALIGNMENT = 4;
    
    public SPARCOptions() {
        super(4);
    }
    
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream) {
        return new SimpleOutputStream(finishableOutputStream, new SPARC(true, this.startOffset));
    }
    
    public InputStream getInputStream(final InputStream inputStream) {
        return new SimpleInputStream(inputStream, new SPARC(false, this.startOffset));
    }
    
    FilterEncoder getFilterEncoder() {
        return new BCJEncoder(this, 9L);
    }
}
