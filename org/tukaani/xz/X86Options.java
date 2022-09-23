// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.SimpleFilter;
import org.tukaani.xz.simple.X86;

public class X86Options extends BCJOptions
{
    private static final int ALIGNMENT = 1;
    
    public X86Options() {
        super(1);
    }
    
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream) {
        return new SimpleOutputStream(finishableOutputStream, new X86(true, this.startOffset));
    }
    
    public InputStream getInputStream(final InputStream inputStream) {
        return new SimpleInputStream(inputStream, new X86(false, this.startOffset));
    }
    
    FilterEncoder getFilterEncoder() {
        return new BCJEncoder(this, 4L);
    }
}
