// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.SimpleFilter;
import org.tukaani.xz.simple.PowerPC;

public class PowerPCOptions extends BCJOptions
{
    private static final int ALIGNMENT = 4;
    
    public PowerPCOptions() {
        super(4);
    }
    
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream) {
        return new SimpleOutputStream(finishableOutputStream, new PowerPC(true, this.startOffset));
    }
    
    public InputStream getInputStream(final InputStream inputStream) {
        return new SimpleInputStream(inputStream, new PowerPC(false, this.startOffset));
    }
    
    FilterEncoder getFilterEncoder() {
        return new BCJEncoder(this, 5L);
    }
}
