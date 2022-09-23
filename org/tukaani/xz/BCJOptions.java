// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

abstract class BCJOptions extends FilterOptions
{
    private final int alignment;
    int startOffset;
    
    BCJOptions(final int alignment) {
        this.startOffset = 0;
        this.alignment = alignment;
    }
    
    public void setStartOffset(final int startOffset) throws UnsupportedOptionsException {
        if ((startOffset & this.alignment - 1) != 0x0) {
            throw new UnsupportedOptionsException("Start offset must be a multiple of " + this.alignment);
        }
        this.startOffset = startOffset;
    }
    
    public int getStartOffset() {
        return this.startOffset;
    }
    
    public int getEncoderMemoryUsage() {
        return SimpleOutputStream.getMemoryUsage();
    }
    
    public int getDecoderMemoryUsage() {
        return SimpleInputStream.getMemoryUsage();
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
