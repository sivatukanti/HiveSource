// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS;

import org.xbill.DNS.utils.base16;
import java.io.IOException;

public class GenericEDNSOption extends EDNSOption
{
    private byte[] data;
    
    GenericEDNSOption(final int code) {
        super(code);
    }
    
    public GenericEDNSOption(final int code, final byte[] data) {
        super(code);
        this.data = Record.checkByteArrayLength("option data", data, 65535);
    }
    
    void optionFromWire(final DNSInput in) throws IOException {
        this.data = in.readByteArray();
    }
    
    void optionToWire(final DNSOutput out) {
        out.writeByteArray(this.data);
    }
    
    String optionToString() {
        return "<" + base16.toString(this.data) + ">";
    }
}
