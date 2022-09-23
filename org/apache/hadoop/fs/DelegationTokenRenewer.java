// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.security.token.DelegationTokenIssuer;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.util.Time;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.security.token.Token;
import java.lang.ref.WeakReference;
import java.util.concurrent.Delayed;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.concurrent.DelayQueue;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class DelegationTokenRenewer extends Thread
{
    private static final Logger LOG;
    private static final long RENEW_CYCLE = 86400000L;
    @InterfaceAudience.Private
    @VisibleForTesting
    public static long renewCycle;
    private volatile DelayQueue<RenewAction<?>> queue;
    private static DelegationTokenRenewer INSTANCE;
    
    @VisibleForTesting
    protected int getRenewQueueLength() {
        return this.queue.size();
    }
    
    private DelegationTokenRenewer(final Class<? extends FileSystem> clazz) {
        super(clazz.getSimpleName() + "-" + DelegationTokenRenewer.class.getSimpleName());
        this.queue = new DelayQueue<RenewAction<?>>();
        this.setDaemon(true);
    }
    
    public static synchronized DelegationTokenRenewer getInstance() {
        if (DelegationTokenRenewer.INSTANCE == null) {
            DelegationTokenRenewer.INSTANCE = new DelegationTokenRenewer(FileSystem.class);
        }
        return DelegationTokenRenewer.INSTANCE;
    }
    
    @VisibleForTesting
    static synchronized void reset() {
        if (DelegationTokenRenewer.INSTANCE != null) {
            DelegationTokenRenewer.INSTANCE.queue.clear();
            DelegationTokenRenewer.INSTANCE.interrupt();
            try {
                DelegationTokenRenewer.INSTANCE.join();
            }
            catch (InterruptedException e) {
                DelegationTokenRenewer.LOG.warn("Failed to reset renewer");
            }
            finally {
                DelegationTokenRenewer.INSTANCE = null;
            }
        }
    }
    
    public <T extends org.apache.hadoop.fs.FileSystem> RenewAction<T> addRenewAction(final T fs) {
        synchronized (this) {
            if (!this.isAlive()) {
                this.start();
            }
        }
        final RenewAction<T> action = new RenewAction<T>((FileSystem)fs);
        if (((RenewAction<FileSystem>)action).token != null) {
            this.queue.add((RenewAction<?>)action);
        }
        else {
            FileSystem.LOG.error("does not have a token for renewal");
        }
        return action;
    }
    
    public <T extends org.apache.hadoop.fs.FileSystem> void removeRenewAction(final T fs) throws IOException {
        final RenewAction<T> action = new RenewAction<T>((FileSystem)fs);
        if (this.queue.remove(action)) {
            try {
                ((RenewAction<FileSystem>)action).cancel();
            }
            catch (InterruptedException ie) {
                DelegationTokenRenewer.LOG.error("Interrupted while canceling token for " + ((FileSystem)fs).getUri() + "filesystem");
                if (DelegationTokenRenewer.LOG.isDebugEnabled()) {
                    DelegationTokenRenewer.LOG.debug("Exception in removeRenewAction: ", ie);
                }
            }
        }
    }
    
    @Override
    public void run() {
        while (true) {
            RenewAction<?> action = null;
            try {
                action = this.queue.take();
                if (!((RenewAction<FileSystem>)action).renew()) {
                    continue;
                }
                this.queue.add(action);
            }
            catch (InterruptedException ie2) {}
            catch (Exception ie) {
                final FileSystem fileSystem = (FileSystem)((RenewAction<FileSystem>)action).weakFs.get();
                FileSystem.LOG.warn("Failed to renew token, action=" + action, ie);
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(DelegationTokenRenewer.class);
        DelegationTokenRenewer.renewCycle = 86400000L;
        DelegationTokenRenewer.INSTANCE = null;
    }
    
    public static class RenewAction<T extends org.apache.hadoop.fs.FileSystem> implements Delayed
    {
        private long renewalTime;
        private final WeakReference<T> weakFs;
        private Token<?> token;
        boolean isValid;
        
        private RenewAction(final T fs) {
            this.isValid = true;
            this.weakFs = new WeakReference<T>(fs);
            this.token = ((Renewable)fs).getRenewToken();
            this.updateRenewalTime(DelegationTokenRenewer.renewCycle);
        }
        
        public boolean isValid() {
            return this.isValid;
        }
        
        @Override
        public long getDelay(final TimeUnit unit) {
            final long millisLeft = this.renewalTime - Time.now();
            return unit.convert(millisLeft, TimeUnit.MILLISECONDS);
        }
        
        @Override
        public int compareTo(final Delayed delayed) {
            final RenewAction<?> that = (RenewAction<?>)delayed;
            return (this.renewalTime < that.renewalTime) ? -1 : ((this.renewalTime == that.renewalTime) ? 0 : 1);
        }
        
        @Override
        public int hashCode() {
            return this.token.hashCode();
        }
        
        @Override
        public boolean equals(final Object that) {
            return this == that || (that != null && that instanceof RenewAction && this.token.equals(((RenewAction)that).token));
        }
        
        private void updateRenewalTime(final long delay) {
            this.renewalTime = Time.now() + delay - delay / 10L;
        }
        
        private boolean renew() throws IOException, InterruptedException {
            final T fs = this.weakFs.get();
            final boolean b = fs != null;
            if (b) {
                synchronized (fs) {
                    try {
                        final long expires = this.token.renew(((Configured)fs).getConf());
                        this.updateRenewalTime(expires - Time.now());
                    }
                    catch (IOException ie) {
                        try {
                            final Token<?>[] tokens = ((DelegationTokenIssuer)fs).addDelegationTokens(null, null);
                            if (tokens.length == 0) {
                                throw new IOException("addDelegationTokens returned no tokens");
                            }
                            this.token = tokens[0];
                            this.updateRenewalTime(DelegationTokenRenewer.renewCycle);
                            ((Renewable)fs).setDelegationToken(this.token);
                            return b;
                        }
                        catch (IOException ie2) {
                            this.isValid = false;
                            throw new IOException("Can't renew or get new delegation token ", ie);
                        }
                    }
                }
            }
            return b;
        }
        
        private void cancel() throws IOException, InterruptedException {
            final T fs = this.weakFs.get();
            if (fs != null) {
                this.token.cancel(((Configured)fs).getConf());
            }
        }
        
        @Override
        public String toString() {
            final Renewable fs = this.weakFs.get();
            return (fs == null) ? "evaporated token renew" : ("The token will be renewed in " + this.getDelay(TimeUnit.SECONDS) + " secs, renewToken=" + this.token);
        }
    }
    
    public interface Renewable
    {
        Token<?> getRenewToken();
        
         <T extends TokenIdentifier> void setDelegationToken(final Token<T> p0);
    }
}
