// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import java.util.concurrent.Executor;
import org.jboss.netty.util.ThreadNameDeterminer;

public class NioServerBossPool extends AbstractNioBossPool<NioServerBoss>
{
    private final ThreadNameDeterminer determiner;
    
    public NioServerBossPool(final Executor bossExecutor, final int bossCount, final ThreadNameDeterminer determiner) {
        super(bossExecutor, bossCount, false);
        this.determiner = determiner;
        this.init();
    }
    
    public NioServerBossPool(final Executor bossExecutor, final int bossCount) {
        this(bossExecutor, bossCount, null);
    }
    
    @Override
    protected NioServerBoss newBoss(final Executor executor) {
        return new NioServerBoss(executor, this.determiner);
    }
}
