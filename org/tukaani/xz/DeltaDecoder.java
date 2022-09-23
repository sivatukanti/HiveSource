// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.InputStream;

class DeltaDecoder extends DeltaCoder implements FilterDecoder
{
    private int distance;
    
    DeltaDecoder(final byte[] array) throws UnsupportedOptionsException {
        if (array.length != 1) {
            throw new UnsupportedOptionsException("Unsupported Delta filter properties");
        }
        this.distance = (array[0] & 0xFF) + 1;
    }
    
    public int getMemoryUsage() {
        return 1;
    }
    
    public InputStream getInputStream(final InputStream inputStream) {
        return new DeltaInputStream(inputStream, this.distance);
    }
}
