// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.SimpleFilter;
import org.tukaani.xz.simple.ARMThumb;

public class ARMThumbOptions extends BCJOptions
{
    private static final int ALIGNMENT = 2;
    
    public ARMThumbOptions() {
        super(2);
    }
    
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream) {
        return new SimpleOutputStream(finishableOutputStream, new ARMThumb(true, this.startOffset));
    }
    
    public InputStream getInputStream(final InputStream inputStream) {
        return new SimpleInputStream(inputStream, new ARMThumb(false, this.startOffset));
    }
    
    FilterEncoder getFilterEncoder() {
        return new BCJEncoder(this, 8L);
    }
}
