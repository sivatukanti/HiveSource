// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import java.util.StringTokenizer;
import java.net.Inet6Address;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.net.InetAddress;

public abstract class CIDR implements Comparable<CIDR>
{
    protected InetAddress baseAddress;
    protected int cidrMask;
    
    public static CIDR newCIDR(final InetAddress baseAddress, final int cidrMask) throws UnknownHostException {
        if (cidrMask < 0) {
            throw new UnknownHostException("Invalid mask length used: " + cidrMask);
        }
        if (baseAddress instanceof Inet4Address) {
            if (cidrMask > 32) {
                throw new UnknownHostException("Invalid mask length used: " + cidrMask);
            }
            return new CIDR4((Inet4Address)baseAddress, cidrMask);
        }
        else {
            if (cidrMask > 128) {
                throw new UnknownHostException("Invalid mask length used: " + cidrMask);
            }
            return new CIDR6((Inet6Address)baseAddress, cidrMask);
        }
    }
    
    public static CIDR newCIDR(final InetAddress baseAddress, final String scidrMask) throws UnknownHostException {
        int cidrMask = getNetMask(scidrMask);
        if (cidrMask < 0) {
            throw new UnknownHostException("Invalid mask length used: " + cidrMask);
        }
        if (baseAddress instanceof Inet4Address) {
            if (cidrMask > 32) {
                throw new UnknownHostException("Invalid mask length used: " + cidrMask);
            }
            return new CIDR4((Inet4Address)baseAddress, cidrMask);
        }
        else {
            cidrMask += 96;
            if (cidrMask > 128) {
                throw new UnknownHostException("Invalid mask length used: " + cidrMask);
            }
            return new CIDR6((Inet6Address)baseAddress, cidrMask);
        }
    }
    
    public static CIDR newCIDR(final String cidr) throws UnknownHostException {
        final int p = cidr.indexOf(47);
        if (p < 0) {
            throw new UnknownHostException("Invalid CIDR notation used: " + cidr);
        }
        final String addrString = cidr.substring(0, p);
        final String maskString = cidr.substring(p + 1);
        final InetAddress addr = addressStringToInet(addrString);
        int mask;
        if (maskString.indexOf(46) < 0) {
            mask = parseInt(maskString, -1);
        }
        else {
            mask = getNetMask(maskString);
            if (addr instanceof Inet6Address) {
                mask += 96;
            }
        }
        if (mask < 0) {
            throw new UnknownHostException("Invalid mask length used: " + maskString);
        }
        return newCIDR(addr, mask);
    }
    
    public InetAddress getBaseAddress() {
        return this.baseAddress;
    }
    
    public int getMask() {
        return this.cidrMask;
    }
    
    @Override
    public String toString() {
        return this.baseAddress.getHostAddress() + '/' + this.cidrMask;
    }
    
    public abstract InetAddress getEndAddress();
    
    public abstract boolean contains(final InetAddress p0);
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof CIDR && this.compareTo((CIDR)o) == 0;
    }
    
    @Override
    public int hashCode() {
        return this.baseAddress.hashCode();
    }
    
    private static InetAddress addressStringToInet(final String addr) throws UnknownHostException {
        return InetAddress.getByName(addr);
    }
    
    private static int getNetMask(final String netMask) {
        final StringTokenizer nm = new StringTokenizer(netMask, ".");
        int i = 0;
        final int[] netmask = new int[4];
        while (nm.hasMoreTokens()) {
            netmask[i] = Integer.parseInt(nm.nextToken());
            ++i;
        }
        int mask1 = 0;
        for (i = 0; i < 4; ++i) {
            mask1 += Integer.bitCount(netmask[i]);
        }
        return mask1;
    }
    
    private static int parseInt(final String intstr, final int def) {
        if (intstr == null) {
            return def;
        }
        Integer res;
        try {
            res = Integer.decode(intstr);
        }
        catch (Exception e) {
            res = def;
        }
        return res;
    }
    
    public static byte[] getIpV4FromIpV6(final Inet6Address address) {
        final byte[] baddr = address.getAddress();
        for (int i = 0; i < 9; ++i) {
            if (baddr[i] != 0) {
                throw new IllegalArgumentException("This IPv6 address cannot be used in IPv4 context");
            }
        }
        if ((baddr[10] != 0 && baddr[10] != 255) || (baddr[11] != 0 && baddr[11] != 255)) {
            throw new IllegalArgumentException("This IPv6 address cannot be used in IPv4 context");
        }
        return new byte[] { baddr[12], baddr[13], baddr[14], baddr[15] };
    }
    
    public static byte[] getIpV6FromIpV4(final Inet4Address address) {
        final byte[] baddr = address.getAddress();
        return new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, baddr[0], baddr[1], baddr[2], baddr[3] };
    }
}
