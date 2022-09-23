// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import java.net.UnknownHostException;
import java.net.InetAddress;

public class IpV4SubnetFilterRule extends IpV4Subnet implements IpFilterRule
{
    private boolean isAllowRule;
    
    public IpV4SubnetFilterRule(final boolean allow) {
        this.isAllowRule = true;
        this.isAllowRule = allow;
    }
    
    public IpV4SubnetFilterRule(final boolean allow, final InetAddress inetAddress, final int cidrNetMask) {
        super(inetAddress, cidrNetMask);
        this.isAllowRule = true;
        this.isAllowRule = allow;
    }
    
    public IpV4SubnetFilterRule(final boolean allow, final InetAddress inetAddress, final String netMask) {
        super(inetAddress, netMask);
        this.isAllowRule = true;
        this.isAllowRule = allow;
    }
    
    public IpV4SubnetFilterRule(final boolean allow, final String netAddress) throws UnknownHostException {
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
