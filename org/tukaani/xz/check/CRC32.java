// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.check;

public class CRC32 extends Check
{
    private java.util.zip.CRC32 state;
    
    public CRC32() {
        this.state = new java.util.zip.CRC32();
        this.size = 4;
        this.name = "CRC32";
    }
    
    public void update(final byte[] b, final int off, final int len) {
        this.state.update(b, off, len);
    }
    
    public byte[] finish() {
        final long value = this.state.getValue();
        final byte[] array = { (byte)value, (byte)(value >>> 8), (byte)(value >>> 16), (byte)(value >>> 24) };
        this.state.reset();
        return array;
    }
}
