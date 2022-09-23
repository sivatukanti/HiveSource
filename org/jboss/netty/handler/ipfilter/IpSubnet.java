// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.jboss.netty.logging.InternalLogger;

public class IpSubnet implements IpSet, Comparable<IpSubnet>
{
    private static final InternalLogger logger;
    private final CIDR cidr;
    
    public IpSubnet() {
        this.cidr = null;
    }
    
    public IpSubnet(final String netAddress) throws UnknownHostException {
        this.cidr = CIDR.newCIDR(netAddress);
    }
    
    public IpSubnet(final InetAddress inetAddress, final int cidrNetMask) throws UnknownHostException {
        this.cidr = CIDR.newCIDR(inetAddress, cidrNetMask);
    }
    
    public IpSubnet(final InetAddress inetAddress, final String netMask) throws UnknownHostException {
        this.cidr = CIDR.newCIDR(inetAddress, netMask);
    }
    
    public boolean contains(final String ipAddr) throws UnknownHostException {
        final InetAddress inetAddress1 = InetAddress.getByName(ipAddr);
        return this.contains(inetAddress1);
    }
    
    public boolean contains(final InetAddress inetAddress) {
        return this.cidr == null || this.cidr.contains(inetAddress);
    }
    
    @Override
    public String toString() {
        return this.cidr.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IpSubnet)) {
            return false;
        }
        final IpSubnet ipSubnet = (IpSubnet)o;
        return ipSubnet.cidr.equals(this.cidr);
    }
    
    @Override
    public int hashCode() {
        return this.cidr.hashCode();
    }
    
    public int compareTo(final IpSubnet o) {
        return this.cidr.toString().compareTo(o.cidr.toString());
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(IpSubnet.class);
    }
}
