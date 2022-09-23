// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS;

import java.util.Arrays;
import java.io.IOException;

public abstract class EDNSOption
{
    private final int code;
    
    public EDNSOption(final int code) {
        this.code = Record.checkU16("code", code);
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append(Code.string(this.code));
        sb.append(": ");
        sb.append(this.optionToString());
        sb.append("}");
        return sb.toString();
    }
    
    public int getCode() {
        return this.code;
    }
    
    byte[] getData() {
        final DNSOutput out = new DNSOutput();
        this.optionToWire(out);
        return out.toByteArray();
    }
    
    abstract void optionFromWire(final DNSInput p0) throws IOException;
    
    static EDNSOption fromWire(final DNSInput in) throws IOException {
        final int code = in.readU16();
        final int length = in.readU16();
        if (in.remaining() < length) {
            throw new WireParseException("truncated option");
        }
        final int save = in.saveActive();
        in.setActive(length);
        EDNSOption option = null;
        switch (code) {
            case 3: {
                option = new NSIDOption();
                break;
            }
            case 8: {
                option = new ClientSubnetOption();
                break;
            }
            default: {
                option = new GenericEDNSOption(code);
                break;
            }
        }
        option.optionFromWire(in);
        in.restoreActive(save);
        return option;
    }
    
    public static EDNSOption fromWire(final byte[] b) throws IOException {
        return fromWire(new DNSInput(b));
    }
    
    abstract void optionToWire(final DNSOutput p0);
    
    void toWire(final DNSOutput out) {
        out.writeU16(this.code);
        final int lengthPosition = out.current();
        out.writeU16(0);
        this.optionToWire(out);
        final int length = out.current() - lengthPosition - 2;
        out.writeU16At(length, lengthPosition);
    }
    
    public byte[] toWire() throws IOException {
        final DNSOutput out = new DNSOutput();
        this.toWire(out);
        return out.toByteArray();
    }
    
    public boolean equals(final Object arg) {
        if (arg == null || !(arg instanceof EDNSOption)) {
            return false;
        }
        final EDNSOption opt = (EDNSOption)arg;
        return this.code == opt.code && Arrays.equals(this.getData(), opt.getData());
    }
    
    public int hashCode() {
        final byte[] array = this.getData();
        int hashval = 0;
        for (int i = 0; i < array.length; ++i) {
            hashval += (hashval << 3) + (array[i] & 0xFF);
        }
        return hashval;
    }
    
    abstract String optionToString();
    
    public static class Code
    {
        public static final int NSID = 3;
        public static final int CLIENT_SUBNET = 8;
        private static Mnemonic codes;
        
        private Code() {
        }
        
        public static String string(final int code) {
            return Code.codes.getText(code);
        }
        
        public static int value(final String s) {
            return Code.codes.getValue(s);
        }
        
        static {
            (Code.codes = new Mnemonic("EDNS Option Codes", 2)).setMaximum(65535);
            Code.codes.setPrefix("CODE");
            Code.codes.setNumericAllowed(true);
            Code.codes.add(3, "NSID");
            Code.codes.add(8, "CLIENT_SUBNET");
        }
    }
}
