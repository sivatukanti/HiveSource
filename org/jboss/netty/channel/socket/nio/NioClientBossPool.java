// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.util.HashedWheelTimer;
import java.util.concurrent.Executor;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.ThreadNameDeterminer;

public class NioClientBossPool extends AbstractNioBossPool<NioClientBoss>
{
    private final ThreadNameDeterminer determiner;
    private final Timer timer;
    private boolean stopTimer;
    
    public NioClientBossPool(final Executor bossExecutor, final int bossCount, final Timer timer, final ThreadNameDeterminer determiner) {
        super(bossExecutor, bossCount, false);
        this.determiner = determiner;
        this.timer = timer;
        this.init();
    }
    
    public NioClientBossPool(final Executor bossExecutor, final int bossCount) {
        this(bossExecutor, bossCount, new HashedWheelTimer(), null);
        this.stopTimer = true;
    }
    
    @Override
    protected NioClientBoss newBoss(final Executor executor) {
        return new NioClientBoss(executor, this.timer, this.determiner);
    }
    
    @Override
    public void shutdown() {
        super.shutdown();
        if (this.stopTimer) {
            this.timer.stop();
        }
    }
    
    @Override
    public void releaseExternalResources() {
        super.releaseExternalResources();
        this.timer.stop();
    }
}
