// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.io.IOException;
import org.apache.kerby.asn1.EnumType;

public class Asn1Flags extends Asn1BitString
{
    private static final int MAX_SIZE = 32;
    private static final int MASK;
    private int flags;
    
    public Asn1Flags() {
        this(0);
    }
    
    public Asn1Flags(final int value) {
        this.setFlags(value);
    }
    
    public void setFlags(final int flags) {
        this.flags = flags;
        this.flags2Value();
    }
    
    @Override
    public void setValue(final byte[] value) {
        super.setValue(value);
        this.value2Flags();
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    public boolean isFlagSet(final int flag) {
        return (this.flags & flag) != 0x0;
    }
    
    public void setFlag(final int flag) {
        this.setFlags(this.flags | flag);
    }
    
    public void clearFlag(final int flag) {
        this.setFlags(this.flags & (Asn1Flags.MASK ^ flag));
    }
    
    public void clear() {
        this.setFlags(0);
    }
    
    public boolean isFlagSet(final EnumType flag) {
        return this.isFlagSet(flag.getValue());
    }
    
    public void setFlag(final EnumType flag) {
        this.setFlag(flag.getValue());
    }
    
    public void setFlag(final EnumType flag, final boolean isSet) {
        if (isSet) {
            this.setFlag(flag.getValue());
        }
        else {
            this.clearFlag(flag.getValue());
        }
    }
    
    public void clearFlag(final EnumType flag) {
        this.clearFlag(flag.getValue());
    }
    
    private void flags2Value() {
        final byte[] bytes = { (byte)(this.flags >> 24), (byte)(this.flags >> 16 & 0xFF), (byte)(this.flags >> 8 & 0xFF), (byte)(this.flags & 0xFF) };
        this.setValue(bytes);
    }
    
    private void value2Flags() {
        final byte[] valueBytes = this.getValue();
        this.flags = ((valueBytes[0] & 0xFF) << 24 | (valueBytes[1] & 0xFF) << 16 | (valueBytes[2] & 0xFF) << 8 | (0xFF & valueBytes[3]));
    }
    
    @Override
    protected void toValue() throws IOException {
        super.toValue();
        if (this.getPadding() != 0 || this.getValue().length != 4) {
            throw new IOException("Bad bitstring decoded as invalid krb flags");
        }
        this.value2Flags();
    }
    
    static {
        int maskBuilder = 0;
        for (int i = 0; i < 32; ++i) {
            maskBuilder <<= 1;
            maskBuilder |= 0x1;
        }
        MASK = maskBuilder;
    }
}
