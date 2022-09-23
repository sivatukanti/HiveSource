// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.InputStream;

public class DeltaOptions extends FilterOptions
{
    public static final int DISTANCE_MIN = 1;
    public static final int DISTANCE_MAX = 256;
    private int distance;
    
    public DeltaOptions() {
        this.distance = 1;
    }
    
    public DeltaOptions(final int distance) throws UnsupportedOptionsException {
        this.distance = 1;
        this.setDistance(distance);
    }
    
    public void setDistance(final int n) throws UnsupportedOptionsException {
        if (n < 1 || n > 256) {
            throw new UnsupportedOptionsException("Delta distance must be in the range [1, 256]: " + n);
        }
        this.distance = n;
    }
    
    public int getDistance() {
        return this.distance;
    }
    
    public int getEncoderMemoryUsage() {
        return DeltaOutputStream.getMemoryUsage();
    }
    
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream) {
        return new DeltaOutputStream(finishableOutputStream, this);
    }
    
    public int getDecoderMemoryUsage() {
        return 1;
    }
    
    public InputStream getInputStream(final InputStream inputStream) {
        return new DeltaInputStream(inputStream, this.distance);
    }
    
    FilterEncoder getFilterEncoder() {
        return new DeltaEncoder(this);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException ex) {
            assert false;
            throw new RuntimeException();
        }
    }
}
