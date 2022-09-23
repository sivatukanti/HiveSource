// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.SimpleFilter;
import org.tukaani.xz.simple.IA64;

public class IA64Options extends BCJOptions
{
    private static final int ALIGNMENT = 16;
    
    public IA64Options() {
        super(16);
    }
    
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream) {
        return new SimpleOutputStream(finishableOutputStream, new IA64(true, this.startOffset));
    }
    
    public InputStream getInputStream(final InputStream inputStream) {
        return new SimpleInputStream(inputStream, new IA64(false, this.startOffset));
    }
    
    FilterEncoder getFilterEncoder() {
        return new BCJEncoder(this, 6L);
    }
}
