// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

class DeltaEncoder extends DeltaCoder implements FilterEncoder
{
    private final DeltaOptions options;
    private final byte[] props;
    
    DeltaEncoder(final DeltaOptions deltaOptions) {
        (this.props = new byte[1])[0] = (byte)(deltaOptions.getDistance() - 1);
        this.options = (DeltaOptions)deltaOptions.clone();
    }
    
    public long getFilterID() {
        return 3L;
    }
    
    public byte[] getFilterProps() {
        return this.props;
    }
    
    public boolean supportsFlushing() {
        return true;
    }
    
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream) {
        return this.options.getOutputStream(finishableOutputStream);
    }
}
