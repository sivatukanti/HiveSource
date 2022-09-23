// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

public interface IpFilterRule extends IpSet
{
    boolean isAllowRule();
    
    boolean isDenyRule();
}
