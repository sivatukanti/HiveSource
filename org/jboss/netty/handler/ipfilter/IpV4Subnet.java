// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import java.util.StringTokenizer;
import org.jboss.netty.util.internal.StringUtil;
import java.net.UnknownHostException;
import java.net.InetAddress;

public class IpV4Subnet implements IpSet, Comparable<IpV4Subnet>
{
    private static final int SUBNET_MASK = Integer.MIN_VALUE;
    private InetAddress inetAddress;
    private int subnet;
    private int mask;
    private int cidrMask;
    
    public IpV4Subnet() {
        this.mask = -1;
        this.inetAddress = null;
        this.subnet = 0;
        this.cidrMask = 0;
    }
    
    public IpV4Subnet(final String netAddress) throws UnknownHostException {
        this.setNetAddress(netAddress);
    }
    
    public IpV4Subnet(final InetAddress inetAddress, final int cidrNetMask) {
        this.setNetAddress(inetAddress, cidrNetMask);
    }
    
    public IpV4Subnet(final InetAddress inetAddress, final String netMask) {
        this.setNetAddress(inetAddress, netMask);
    }
    
    private void setNetAddress(final String netAddress) throws UnknownHostException {
        final String[] tokens = StringUtil.split(netAddress, '/');
        if (tokens.length != 2) {
            throw new IllegalArgumentException("netAddress: " + netAddress + " (expected: CIDR or Decimal Notation)");
        }
        if (tokens[1].length() < 3) {
            this.setNetId(tokens[0]);
            this.setCidrNetMask(Integer.parseInt(tokens[1]));
        }
        else {
            this.setNetId(tokens[0]);
            this.setNetMask(tokens[1]);
        }
    }
    
    private void setNetAddress(final InetAddress inetAddress, final int cidrNetMask) {
        this.setNetId(inetAddress);
        this.setCidrNetMask(cidrNetMask);
    }
    
    private void setNetAddress(final InetAddress inetAddress, final String netMask) {
        this.setNetId(inetAddress);
        this.setNetMask(netMask);
    }
    
    private void setNetId(final String netId) throws UnknownHostException {
        final InetAddress inetAddress1 = InetAddress.getByName(netId);
        this.setNetId(inetAddress1);
    }
    
    private static int toInt(final InetAddress inetAddress1) {
        final byte[] octets = inetAddress1.getAddress();
        assert octets.length == 4;
        return (octets[0] & 0xFF) << 24 | (octets[1] & 0xFF) << 16 | (octets[2] & 0xFF) << 8 | (octets[3] & 0xFF);
    }
    
    private void setNetId(final InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        this.subnet = toInt(inetAddress);
    }
    
    private void setNetMask(final String netMask) {
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
        this.setCidrNetMask(mask1);
    }
    
    private void setCidrNetMask(final int cidrNetMask) {
        this.cidrMask = cidrNetMask;
        this.mask = Integer.MIN_VALUE >> this.cidrMask - 1;
    }
    
    public boolean contains(final String ipAddr) throws UnknownHostException {
        final InetAddress inetAddress1 = InetAddress.getByName(ipAddr);
        return this.contains(inetAddress1);
    }
    
    public boolean contains(final InetAddress inetAddress1) {
        return (toInt(inetAddress1) & this.mask) == this.subnet;
    }
    
    @Override
    public String toString() {
        return this.inetAddress.getHostAddress() + '/' + this.cidrMask;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IpV4Subnet)) {
            return false;
        }
        final IpV4Subnet ipV4Subnet = (IpV4Subnet)o;
        return ipV4Subnet.subnet == this.subnet && ipV4Subnet.cidrMask == this.cidrMask;
    }
    
    @Override
    public int hashCode() {
        return this.subnet;
    }
    
    public int compareTo(final IpV4Subnet o) {
        if (o.subnet == this.subnet && o.cidrMask == this.cidrMask) {
            return 0;
        }
        if (o.subnet < this.subnet) {
            return 1;
        }
        if (o.subnet > this.subnet) {
            return -1;
        }
        if (o.cidrMask < this.cidrMask) {
            return -1;
        }
        return 1;
    }
}
