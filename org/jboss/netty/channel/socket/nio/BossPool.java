// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

public interface BossPool<E extends Boss> extends NioSelectorPool
{
    E nextBoss();
}
