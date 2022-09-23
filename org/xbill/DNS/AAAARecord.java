// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS;

import java.net.UnknownHostException;
import java.io.IOException;
import java.net.InetAddress;

public class AAAARecord extends Record
{
    private static final long serialVersionUID = -4588601512069748050L;
    private byte[] address;
    
    AAAARecord() {
    }
    
    Record getObject() {
        return new AAAARecord();
    }
    
    public AAAARecord(final Name name, final int dclass, final long ttl, final InetAddress address) {
        super(name, 28, dclass, ttl);
        if (Address.familyOf(address) != 2) {
            throw new IllegalArgumentException("invalid IPv6 address");
        }
        this.address = address.getAddress();
    }
    
    void rrFromWire(final DNSInput in) throws IOException {
        this.address = in.readByteArray(16);
    }
    
    void rdataFromString(final Tokenizer st, final Name origin) throws IOException {
        this.address = st.getAddressBytes(2);
    }
    
    String rrToString() {
        InetAddress addr;
        try {
            addr = InetAddress.getByAddress(null, this.address);
        }
        catch (UnknownHostException e) {
            return null;
        }
        if (addr.getAddress().length == 4) {
            final StringBuffer sb = new StringBuffer("0:0:0:0:0:ffff:");
            final int high = ((this.address[12] & 0xFF) << 8) + (this.address[13] & 0xFF);
            final int low = ((this.address[14] & 0xFF) << 8) + (this.address[15] & 0xFF);
            sb.append(Integer.toHexString(high));
            sb.append(':');
            sb.append(Integer.toHexString(low));
            return sb.toString();
        }
        return addr.getHostAddress();
    }
    
    public InetAddress getAddress() {
        try {
            if (this.name == null) {
                return InetAddress.getByAddress(this.address);
            }
            return InetAddress.getByAddress(this.name.toString(), this.address);
        }
        catch (UnknownHostException e) {
            return null;
        }
    }
    
    void rrToWire(final DNSOutput out, final Compression c, final boolean canonical) {
        out.writeByteArray(this.address);
    }
}
