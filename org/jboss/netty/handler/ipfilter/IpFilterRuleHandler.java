// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import java.util.Iterator;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jboss.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class IpFilterRuleHandler extends IpFilteringHandlerImpl
{
    private final CopyOnWriteArrayList<IpFilterRule> ipFilterRuleList;
    
    public IpFilterRuleHandler(final List<IpFilterRule> newList) {
        this.ipFilterRuleList = new CopyOnWriteArrayList<IpFilterRule>();
        if (newList != null) {
            this.ipFilterRuleList.addAll(newList);
        }
    }
    
    public IpFilterRuleHandler() {
        this.ipFilterRuleList = new CopyOnWriteArrayList<IpFilterRule>();
    }
    
    public void add(final IpFilterRule ipFilterRule) {
        if (ipFilterRule == null) {
            throw new NullPointerException("IpFilterRule can not be null");
        }
        this.ipFilterRuleList.add(ipFilterRule);
    }
    
    public void add(final int index, final IpFilterRule ipFilterRule) {
        if (ipFilterRule == null) {
            throw new NullPointerException("IpFilterRule can not be null");
        }
        this.ipFilterRuleList.add(index, ipFilterRule);
    }
    
    public void addAll(final Collection<IpFilterRule> c) {
        if (c == null) {
            throw new NullPointerException("Collection can not be null");
        }
        this.ipFilterRuleList.addAll(c);
    }
    
    public void addAll(final int index, final Collection<IpFilterRule> c) {
        if (c == null) {
            throw new NullPointerException("Collection can not be null");
        }
        this.ipFilterRuleList.addAll(index, c);
    }
    
    public int addAllAbsent(final Collection<IpFilterRule> c) {
        if (c == null) {
            throw new NullPointerException("Collection can not be null");
        }
        return this.ipFilterRuleList.addAllAbsent(c);
    }
    
    public boolean addIfAbsent(final IpFilterRule ipFilterRule) {
        if (ipFilterRule == null) {
            throw new NullPointerException("IpFilterRule can not be null");
        }
        return this.ipFilterRuleList.addIfAbsent(ipFilterRule);
    }
    
    public void clear() {
        this.ipFilterRuleList.clear();
    }
    
    public boolean contains(final IpFilterRule ipFilterRule) {
        if (ipFilterRule == null) {
            throw new NullPointerException("IpFilterRule can not be null");
        }
        return this.ipFilterRuleList.contains(ipFilterRule);
    }
    
    public boolean containsAll(final Collection<IpFilterRule> c) {
        if (c == null) {
            throw new NullPointerException("Collection can not be null");
        }
        return this.ipFilterRuleList.containsAll(c);
    }
    
    public IpFilterRule get(final int index) {
        return this.ipFilterRuleList.get(index);
    }
    
    public boolean isEmpty() {
        return this.ipFilterRuleList.isEmpty();
    }
    
    public void remove(final IpFilterRule ipFilterRule) {
        if (ipFilterRule == null) {
            throw new NullPointerException("IpFilterRule can not be null");
        }
        this.ipFilterRuleList.remove(ipFilterRule);
    }
    
    public IpFilterRule remove(final int index) {
        return this.ipFilterRuleList.remove(index);
    }
    
    public void removeAll(final Collection<IpFilterRule> c) {
        if (c == null) {
            throw new NullPointerException("Collection can not be null");
        }
        this.ipFilterRuleList.removeAll(c);
    }
    
    public void retainAll(final Collection<IpFilterRule> c) {
        if (c == null) {
            throw new NullPointerException("Collection can not be null");
        }
        this.ipFilterRuleList.retainAll(c);
    }
    
    public IpFilterRule set(final int index, final IpFilterRule ipFilterRule) {
        if (ipFilterRule == null) {
            throw new NullPointerException("IpFilterRule can not be null");
        }
        return this.ipFilterRuleList.set(index, ipFilterRule);
    }
    
    public int size() {
        return this.ipFilterRuleList.size();
    }
    
    @Override
    protected boolean accept(final ChannelHandlerContext ctx, final ChannelEvent e, final InetSocketAddress inetSocketAddress) throws Exception {
        if (this.ipFilterRuleList.isEmpty()) {
            return true;
        }
        final InetAddress inetAddress = inetSocketAddress.getAddress();
        for (final IpFilterRule ipFilterRule : this.ipFilterRuleList) {
            if (ipFilterRule.contains(inetAddress)) {
                return ipFilterRule.isAllowRule();
            }
        }
        return true;
    }
}
