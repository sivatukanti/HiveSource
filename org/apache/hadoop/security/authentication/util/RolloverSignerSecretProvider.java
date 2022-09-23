// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.util;

import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import javax.servlet.ServletContext;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Unstable
@InterfaceAudience.Private
public abstract class RolloverSignerSecretProvider extends SignerSecretProvider
{
    @VisibleForTesting
    static Logger LOG;
    private volatile byte[][] secrets;
    private ScheduledExecutorService scheduler;
    private boolean schedulerRunning;
    private boolean isDestroyed;
    
    public RolloverSignerSecretProvider() {
        this.schedulerRunning = false;
        this.isDestroyed = false;
    }
    
    @Override
    public void init(final Properties config, final ServletContext servletContext, final long tokenValidity) throws Exception {
        this.initSecrets(this.generateNewSecret(), null);
        this.startScheduler(tokenValidity, tokenValidity);
    }
    
    protected void initSecrets(final byte[] currentSecret, final byte[] previousSecret) {
        this.secrets = new byte[][] { currentSecret, previousSecret };
    }
    
    protected synchronized void startScheduler(final long initialDelay, final long period) {
        if (!this.schedulerRunning) {
            this.schedulerRunning = true;
            (this.scheduler = Executors.newSingleThreadScheduledExecutor()).scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    RolloverSignerSecretProvider.this.rollSecret();
                }
            }, initialDelay, period, TimeUnit.MILLISECONDS);
        }
    }
    
    @Override
    public synchronized void destroy() {
        if (!this.isDestroyed) {
            this.isDestroyed = true;
            if (this.scheduler != null) {
                this.scheduler.shutdown();
            }
            this.schedulerRunning = false;
            super.destroy();
        }
    }
    
    protected synchronized void rollSecret() {
        if (!this.isDestroyed) {
            RolloverSignerSecretProvider.LOG.debug("rolling secret");
            final byte[] newSecret = this.generateNewSecret();
            this.secrets = new byte[][] { newSecret, this.secrets[0] };
        }
    }
    
    protected abstract byte[] generateNewSecret();
    
    @Override
    public byte[] getCurrentSecret() {
        return this.secrets[0];
    }
    
    @Override
    public byte[][] getAllSecrets() {
        return this.secrets;
    }
    
    static {
        RolloverSignerSecretProvider.LOG = LoggerFactory.getLogger(RolloverSignerSecretProvider.class);
    }
}
