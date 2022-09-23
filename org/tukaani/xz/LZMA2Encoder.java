// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import org.tukaani.xz.lzma.LZMAEncoder;

class LZMA2Encoder extends LZMA2Coder implements FilterEncoder
{
    private final LZMA2Options options;
    private final byte[] props;
    
    LZMA2Encoder(final LZMA2Options lzma2Options) {
        this.props = new byte[1];
        if (lzma2Options.getPresetDict() != null) {
            throw new IllegalArgumentException("XZ doesn't support a preset dictionary for now");
        }
        if (lzma2Options.getMode() == 0) {
            this.props[0] = 0;
        }
        else {
            this.props[0] = (byte)(LZMAEncoder.getDistSlot(Math.max(lzma2Options.getDictSize(), 4096) - 1) - 23);
        }
        this.options = (LZMA2Options)lzma2Options.clone();
    }
    
    public long getFilterID() {
        return 33L;
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
