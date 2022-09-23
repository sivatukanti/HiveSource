// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import java.net.UnknownHostException;
import java.net.InetAddress;

public class IpSubnetFilterRule extends IpSubnet implements IpFilterRule
{
    private boolean isAllowRule;
    
    public IpSubnetFilterRule(final boolean allow) {
        this.isAllowRule = true;
        this.isAllowRule = allow;
    }
    
    public IpSubnetFilterRule(final boolean allow, final InetAddress inetAddress, final int cidrNetMask) throws UnknownHostException {
        super(inetAddress, cidrNetMask);
        this.isAllowRule = true;
        this.isAllowRule = allow;
    }
    
    public IpSubnetFilterRule(final boolean allow, final InetAddress inetAddress, final String netMask) throws UnknownHostException {
        super(inetAddress, netMask);
        this.isAllowRule = true;
        this.isAllowRule = allow;
    }
    
    public IpSubnetFilterRule(final boolean allow, final String netAddress) throws UnknownHostException {
        super(netAddress);
        this.isAllowRule = true;
        this.isAllowRule = allow;
    }
    
    public boolean isAllowRule() {
        return this.isAllowRule;
    }
    
    public boolean isDenyRule() {
        return !this.isAllowRule;
    }
}
