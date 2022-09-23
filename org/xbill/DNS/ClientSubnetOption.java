// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS;

import java.net.UnknownHostException;
import java.net.InetAddress;

public class ClientSubnetOption extends EDNSOption
{
    private static final long serialVersionUID = -3868158449890266347L;
    private int family;
    private int sourceNetmask;
    private int scopeNetmask;
    private InetAddress address;
    
    ClientSubnetOption() {
        super(8);
    }
    
    private static int checkMaskLength(final String field, final int family, final int val) {
        final int max = Address.addressLength(family) * 8;
        if (val < 0 || val > max) {
            throw new IllegalArgumentException("\"" + field + "\" " + val + " must be in the range " + "[0.." + max + "]");
        }
        return val;
    }
    
    public ClientSubnetOption(final int sourceNetmask, final int scopeNetmask, final InetAddress address) {
        super(8);
        this.family = Address.familyOf(address);
        this.sourceNetmask = checkMaskLength("source netmask", this.family, sourceNetmask);
        this.scopeNetmask = checkMaskLength("scope netmask", this.family, scopeNetmask);
        this.address = Address.truncate(address, sourceNetmask);
        if (!address.equals(this.address)) {
            throw new IllegalArgumentException("source netmask is not valid for address");
        }
    }
    
    public ClientSubnetOption(final int sourceNetmask, final InetAddress address) {
        this(sourceNetmask, 0, address);
    }
    
    public int getFamily() {
        return this.family;
    }
    
    public int getSourceNetmask() {
        return this.sourceNetmask;
    }
    
    public int getScopeNetmask() {
        return this.scopeNetmask;
    }
    
    public InetAddress getAddress() {
        return this.address;
    }
    
    void optionFromWire(final DNSInput in) throws WireParseException {
        this.family = in.readU16();
        if (this.family != 1 && this.family != 2) {
            throw new WireParseException("unknown address family");
        }
        this.sourceNetmask = in.readU8();
        if (this.sourceNetmask > Address.addressLength(this.family) * 8) {
            throw new WireParseException("invalid source netmask");
        }
        this.scopeNetmask = in.readU8();
        if (this.scopeNetmask > Address.addressLength(this.family) * 8) {
            throw new WireParseException("invalid scope netmask");
        }
        final byte[] addr = in.readByteArray();
        if (addr.length != (this.sourceNetmask + 7) / 8) {
            throw new WireParseException("invalid address");
        }
        final byte[] fulladdr = new byte[Address.addressLength(this.family)];
        System.arraycopy(addr, 0, fulladdr, 0, addr.length);
        try {
            this.address = InetAddress.getByAddress(fulladdr);
        }
        catch (UnknownHostException e) {
            throw new WireParseException("invalid address", e);
        }
        final InetAddress tmp = Address.truncate(this.address, this.sourceNetmask);
        if (!tmp.equals(this.address)) {
            throw new WireParseException("invalid padding");
        }
    }
    
    void optionToWire(final DNSOutput out) {
        out.writeU16(this.family);
        out.writeU8(this.sourceNetmask);
        out.writeU8(this.scopeNetmask);
        out.writeByteArray(this.address.getAddress(), 0, (this.sourceNetmask + 7) / 8);
    }
    
    String optionToString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.address.getHostAddress());
        sb.append("/");
        sb.append(this.sourceNetmask);
        sb.append(", scope netmask ");
        sb.append(this.scopeNetmask);
        return sb.toString();
    }
}
