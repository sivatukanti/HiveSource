// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.Inet4Address;

public class CIDR4 extends CIDR
{
    private int addressInt;
    private final int addressEndInt;
    
    protected CIDR4(final Inet4Address newaddr, final int mask) {
        this.cidrMask = mask;
        this.addressInt = ipv4AddressToInt(newaddr);
        final int newmask = ipv4PrefixLengthToMask(mask);
        this.addressInt &= newmask;
        try {
            this.baseAddress = intToIPv4Address(this.addressInt);
        }
        catch (UnknownHostException ex) {}
        this.addressEndInt = this.addressInt + ipv4PrefixLengthToLength(this.cidrMask) - 1;
    }
    
    @Override
    public InetAddress getEndAddress() {
        try {
            return intToIPv4Address(this.addressEndInt);
        }
        catch (UnknownHostException e) {
            return null;
        }
    }
    
    public int compareTo(final CIDR arg) {
        if (arg instanceof CIDR6) {
            final byte[] address = CIDR.getIpV4FromIpV6((Inet6Address)arg.baseAddress);
            final int net = ipv4AddressToInt(address);
            if (net == this.addressInt && arg.cidrMask == this.cidrMask) {
                return 0;
            }
            if (net < this.addressInt) {
                return 1;
            }
            if (net > this.addressInt) {
                return -1;
            }
            if (arg.cidrMask < this.cidrMask) {
                return -1;
            }
            return 1;
        }
        else {
            final CIDR4 o = (CIDR4)arg;
            if (o.addressInt == this.addressInt && o.cidrMask == this.cidrMask) {
                return 0;
            }
            if (o.addressInt < this.addressInt) {
                return 1;
            }
            if (o.addressInt > this.addressInt) {
                return -1;
            }
            if (o.cidrMask < this.cidrMask) {
                return -1;
            }
            return 1;
        }
    }
    
    @Override
    public boolean contains(final InetAddress inetAddress) {
        if (inetAddress == null) {
            throw new NullPointerException("inetAddress");
        }
        if (this.cidrMask == 0) {
            return true;
        }
        final int search = ipv4AddressToInt(inetAddress);
        return search >= this.addressInt && search <= this.addressEndInt;
    }
    
    private static int ipv4PrefixLengthToLength(final int prefixLength) {
        return 1 << 32 - prefixLength;
    }
    
    private static int ipv4PrefixLengthToMask(final int prefixLength) {
        return ~((1 << 32 - prefixLength) - 1);
    }
    
    private static InetAddress intToIPv4Address(final int addr) throws UnknownHostException {
        final byte[] a = { (byte)(addr >> 24 & 0xFF), (byte)(addr >> 16 & 0xFF), (byte)(addr >> 8 & 0xFF), (byte)(addr & 0xFF) };
        return InetAddress.getByAddress(a);
    }
    
    private static int ipv4AddressToInt(final InetAddress addr) {
        byte[] address;
        if (addr instanceof Inet6Address) {
            address = CIDR.getIpV4FromIpV6((Inet6Address)addr);
        }
        else {
            address = addr.getAddress();
        }
        return ipv4AddressToInt(address);
    }
    
    private static int ipv4AddressToInt(final byte[] address) {
        int net = 0;
        for (final byte addres : address) {
            net <<= 8;
            net |= (addres & 0xFF);
        }
        return net;
    }
}
