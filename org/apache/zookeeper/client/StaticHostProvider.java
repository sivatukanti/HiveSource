// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.client;

import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Collections;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.net.InetSocketAddress;
import java.util.List;
import org.slf4j.Logger;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public final class StaticHostProvider implements HostProvider
{
    private static final Logger LOG;
    private final List<InetSocketAddress> serverAddresses;
    private int lastIndex;
    private int currentIndex;
    private Resolver resolver;
    
    public StaticHostProvider(final Collection<InetSocketAddress> serverAddresses) {
        this.serverAddresses = new ArrayList<InetSocketAddress>(5);
        this.lastIndex = -1;
        this.currentIndex = -1;
        this.resolver = new Resolver() {
            @Override
            public InetAddress[] getAllByName(final String name) throws UnknownHostException {
                return InetAddress.getAllByName(name);
            }
        };
        this.init(serverAddresses);
    }
    
    public StaticHostProvider(final Collection<InetSocketAddress> serverAddresses, final Resolver resolver) {
        this.serverAddresses = new ArrayList<InetSocketAddress>(5);
        this.lastIndex = -1;
        this.currentIndex = -1;
        this.resolver = resolver;
        this.init(serverAddresses);
    }
    
    private void init(final Collection<InetSocketAddress> serverAddresses) {
        if (serverAddresses.isEmpty()) {
            throw new IllegalArgumentException("A HostProvider may not be empty!");
        }
        this.serverAddresses.addAll(serverAddresses);
        Collections.shuffle(this.serverAddresses);
    }
    
    private String getHostString(final InetSocketAddress addr) {
        String hostString = "";
        if (addr == null) {
            return hostString;
        }
        if (!addr.isUnresolved()) {
            final InetAddress ia = addr.getAddress();
            if (ia.toString().startsWith("/")) {
                hostString = ia.getHostAddress();
            }
            else {
                hostString = addr.getHostName();
            }
        }
        else {
            final String addrString = addr.toString();
            hostString = addrString.substring(0, addrString.lastIndexOf(58));
        }
        return hostString;
    }
    
    @Override
    public int size() {
        return this.serverAddresses.size();
    }
    
    @Override
    public InetSocketAddress next(final long spinDelay) {
        this.currentIndex = ++this.currentIndex % this.serverAddresses.size();
        if (this.currentIndex == this.lastIndex && spinDelay > 0L) {
            try {
                Thread.sleep(spinDelay);
            }
            catch (InterruptedException e) {
                StaticHostProvider.LOG.warn("Unexpected exception", e);
            }
        }
        else if (this.lastIndex == -1) {
            this.lastIndex = 0;
        }
        final InetSocketAddress curAddr = this.serverAddresses.get(this.currentIndex);
        try {
            final String curHostString = this.getHostString(curAddr);
            final List<InetAddress> resolvedAddresses = new ArrayList<InetAddress>(Arrays.asList(this.resolver.getAllByName(curHostString)));
            if (resolvedAddresses.isEmpty()) {
                return curAddr;
            }
            Collections.shuffle(resolvedAddresses);
            return new InetSocketAddress(resolvedAddresses.get(0), curAddr.getPort());
        }
        catch (UnknownHostException e2) {
            return curAddr;
        }
    }
    
    @Override
    public void onConnected() {
        this.lastIndex = this.currentIndex;
    }
    
    static {
        LOG = LoggerFactory.getLogger(StaticHostProvider.class);
    }
    
    public interface Resolver
    {
        InetAddress[] getAllByName(final String p0) throws UnknownHostException;
    }
}
