// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.security;

import org.apache.hadoop.yarn.event.AbstractEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppRejectedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import java.util.Map;
import java.util.ArrayList;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import org.apache.commons.logging.LogFactory;
import java.util.List;
import org.apache.hadoop.fs.FileSystem;
import java.nio.ByteBuffer;
import java.io.DataOutputStream;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.util.Time;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.TimerTask;
import java.util.Date;
import java.util.Collection;
import org.apache.hadoop.io.Text;
import java.util.Collections;
import java.io.IOException;
import org.apache.hadoop.security.Credentials;
import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.HashSet;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import java.util.Timer;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class DelegationTokenRenewer extends AbstractService
{
    private static final Log LOG;
    public static final String SCHEME = "hdfs";
    private Timer renewalTimer;
    private RMContext rmContext;
    private DelegationTokenCancelThread dtCancelThread;
    private ThreadPoolExecutor renewerService;
    private ConcurrentMap<ApplicationId, Set<DelegationTokenToRenew>> appTokens;
    private final ConcurrentMap<ApplicationId, Long> delayedRemovalMap;
    private long tokenRemovalDelayMs;
    private Thread delayedRemovalThread;
    private ReadWriteLock serviceStateLock;
    private volatile boolean isServiceStarted;
    private LinkedBlockingQueue<DelegationTokenRenewerEvent> pendingEventQueue;
    private boolean tokenKeepAliveEnabled;
    private boolean hasProxyUserPrivileges;
    private long credentialsValidTimeRemaining;
    public static final String RM_SYSTEM_CREDENTIALS_VALID_TIME_REMAINING = "yarn.resourcemanager.system-credentials.valid-time-remaining";
    public static final long DEFAULT_RM_SYSTEM_CREDENTIALS_VALID_TIME_REMAINING = 10800000L;
    
    public DelegationTokenRenewer() {
        super(DelegationTokenRenewer.class.getName());
        this.dtCancelThread = new DelegationTokenCancelThread();
        this.appTokens = new ConcurrentHashMap<ApplicationId, Set<DelegationTokenToRenew>>();
        this.delayedRemovalMap = new ConcurrentHashMap<ApplicationId, Long>();
        this.serviceStateLock = new ReentrantReadWriteLock();
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        this.hasProxyUserPrivileges = conf.getBoolean("yarn.resourcemanager.proxy-user-privileges.enabled", YarnConfiguration.DEFAULT_RM_PROXY_USER_PRIVILEGES_ENABLED);
        this.tokenKeepAliveEnabled = conf.getBoolean("yarn.log-aggregation-enable", false);
        this.tokenRemovalDelayMs = conf.getInt("yarn.nm.liveness-monitor.expiry-interval-ms", 600000);
        this.credentialsValidTimeRemaining = conf.getLong("yarn.resourcemanager.system-credentials.valid-time-remaining", 10800000L);
        this.setLocalSecretManagerAndServiceAddr();
        this.renewerService = this.createNewThreadPoolService(conf);
        this.pendingEventQueue = new LinkedBlockingQueue<DelegationTokenRenewerEvent>();
        this.renewalTimer = new Timer(true);
        super.serviceInit(conf);
    }
    
    protected ThreadPoolExecutor createNewThreadPoolService(final Configuration conf) {
        final int nThreads = conf.getInt("yarn.resourcemanager.delegation-token-renewer.thread-count", 50);
        final ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat("DelegationTokenRenewer #%d").build();
        final ThreadPoolExecutor pool = new ThreadPoolExecutor((5 < nThreads) ? 5 : nThreads, nThreads, 3L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        pool.setThreadFactory(tf);
        pool.allowCoreThreadTimeOut(true);
        return pool;
    }
    
    private void setLocalSecretManagerAndServiceAddr() {
        RMDelegationTokenIdentifier.Renewer.setSecretManager(this.rmContext.getRMDelegationTokenSecretManager(), this.rmContext.getClientRMService().getBindAddress());
    }
    
    @Override
    protected void serviceStart() throws Exception {
        this.dtCancelThread.start();
        if (this.tokenKeepAliveEnabled) {
            (this.delayedRemovalThread = new Thread(new DelayedTokenRemovalRunnable(this.getConfig()), "DelayedTokenCanceller")).start();
        }
        this.setLocalSecretManagerAndServiceAddr();
        this.serviceStateLock.writeLock().lock();
        this.isServiceStarted = true;
        this.serviceStateLock.writeLock().unlock();
        while (!this.pendingEventQueue.isEmpty()) {
            this.processDelegationTokenRenewerEvent(this.pendingEventQueue.take());
        }
        super.serviceStart();
    }
    
    private void processDelegationTokenRenewerEvent(final DelegationTokenRenewerEvent evt) {
        this.serviceStateLock.readLock().lock();
        try {
            if (this.isServiceStarted) {
                this.renewerService.execute(new DelegationTokenRenewerRunnable(evt));
            }
            else {
                this.pendingEventQueue.add(evt);
            }
        }
        finally {
            this.serviceStateLock.readLock().unlock();
        }
    }
    
    @Override
    protected void serviceStop() {
        if (this.renewalTimer != null) {
            this.renewalTimer.cancel();
        }
        this.appTokens.clear();
        this.renewerService.shutdown();
        this.dtCancelThread.interrupt();
        try {
            this.dtCancelThread.join(1000L);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.tokenKeepAliveEnabled && this.delayedRemovalThread != null) {
            this.delayedRemovalThread.interrupt();
            try {
                this.delayedRemovalThread.join(1000L);
            }
            catch (InterruptedException e) {
                DelegationTokenRenewer.LOG.info("Interrupted while joining on delayed removal thread.", e);
            }
        }
    }
    
    @VisibleForTesting
    public Set<Token<?>> getDelegationTokens() {
        final Set<Token<?>> tokens = new HashSet<Token<?>>();
        for (final Set<DelegationTokenToRenew> tokenList : this.appTokens.values()) {
            for (final DelegationTokenToRenew token : tokenList) {
                tokens.add(token.token);
            }
        }
        return tokens;
    }
    
    public void addApplicationAsync(final ApplicationId applicationId, final Credentials ts, final boolean shouldCancelAtEnd, final String user) {
        this.processDelegationTokenRenewerEvent(new DelegationTokenRenewerAppSubmitEvent(applicationId, ts, shouldCancelAtEnd, user));
    }
    
    public void addApplicationSync(final ApplicationId applicationId, final Credentials ts, final boolean shouldCancelAtEnd, final String user) throws IOException, InterruptedException {
        this.handleAppSubmitEvent(new DelegationTokenRenewerAppSubmitEvent(applicationId, ts, shouldCancelAtEnd, user));
    }
    
    private void handleAppSubmitEvent(final DelegationTokenRenewerAppSubmitEvent evt) throws IOException, InterruptedException {
        final ApplicationId applicationId = evt.getApplicationId();
        final Credentials ts = evt.getCredentials();
        final boolean shouldCancelAtEnd = evt.shouldCancelAtEnd();
        if (ts == null) {
            return;
        }
        if (DelegationTokenRenewer.LOG.isDebugEnabled()) {
            DelegationTokenRenewer.LOG.debug("Registering tokens for renewal for: appId = " + applicationId);
        }
        final Collection<Token<?>> tokens = (Collection<Token<?>>)ts.getAllTokens();
        final long now = System.currentTimeMillis();
        this.appTokens.put(applicationId, (Set<Object>)Collections.synchronizedSet(new HashSet<DelegationTokenToRenew>()));
        final Set<DelegationTokenToRenew> tokenList = new HashSet<DelegationTokenToRenew>();
        boolean hasHdfsToken = false;
        for (final Token<?> token : tokens) {
            if (token.isManaged()) {
                tokenList.add(new DelegationTokenToRenew(applicationId, token, this.getConfig(), now, shouldCancelAtEnd, evt.getUser()));
                if (!token.getKind().equals(new Text("HDFS_DELEGATION_TOKEN"))) {
                    continue;
                }
                DelegationTokenRenewer.LOG.info(applicationId + " found existing hdfs token " + token);
                hasHdfsToken = true;
            }
        }
        if (!tokenList.isEmpty()) {
            for (final DelegationTokenToRenew dtr : tokenList) {
                try {
                    this.renewToken(dtr);
                }
                catch (IOException ioe) {
                    throw new IOException("Failed to renew token: " + dtr.token, ioe);
                }
            }
            for (final DelegationTokenToRenew dtr : tokenList) {
                this.appTokens.get(applicationId).add(dtr);
                this.setTimerForTokenRenewal(dtr);
            }
        }
        if (!hasHdfsToken) {
            this.requestNewHdfsDelegationToken(applicationId, evt.getUser(), shouldCancelAtEnd);
        }
    }
    
    @VisibleForTesting
    protected void setTimerForTokenRenewal(final DelegationTokenToRenew token) throws IOException {
        final long expiresIn = token.expirationDate - System.currentTimeMillis();
        final long renewIn = token.expirationDate - expiresIn / 10L;
        final TimerTask tTask = new RenewalTimerTask(token);
        token.setTimerTask(tTask);
        this.renewalTimer.schedule(token.timerTask, new Date(renewIn));
        DelegationTokenRenewer.LOG.info("Renew " + token + " in " + expiresIn + " ms, appId = " + token.applicationId);
    }
    
    @VisibleForTesting
    protected void renewToken(final DelegationTokenToRenew dttr) throws IOException {
        try {
            dttr.expirationDate = UserGroupInformation.getLoginUser().doAs((PrivilegedExceptionAction<Long>)new PrivilegedExceptionAction<Long>() {
                @Override
                public Long run() throws Exception {
                    return dttr.token.renew(dttr.conf);
                }
            });
        }
        catch (InterruptedException e) {
            throw new IOException(e);
        }
        DelegationTokenRenewer.LOG.info("Renewed delegation-token= [" + dttr + "], for " + dttr.applicationId);
    }
    
    private void requestNewHdfsDelegationTokenIfNeeded(final DelegationTokenToRenew dttr) throws IOException, InterruptedException {
        if (this.hasProxyUserPrivileges && dttr.maxDate - dttr.expirationDate < this.credentialsValidTimeRemaining && dttr.token.getKind().equals(new Text("HDFS_DELEGATION_TOKEN"))) {
            final Set<DelegationTokenToRenew> tokenSet = this.appTokens.get(dttr.applicationId);
            if (tokenSet != null && !tokenSet.isEmpty()) {
                final Iterator<DelegationTokenToRenew> iter = tokenSet.iterator();
                synchronized (tokenSet) {
                    while (iter.hasNext()) {
                        final DelegationTokenToRenew t = iter.next();
                        if (t.token.getKind().equals(new Text("HDFS_DELEGATION_TOKEN"))) {
                            iter.remove();
                            if (t.timerTask != null) {
                                t.timerTask.cancel();
                            }
                            DelegationTokenRenewer.LOG.info("Removed expiring token " + t);
                        }
                    }
                }
            }
            DelegationTokenRenewer.LOG.info("Token= (" + dttr + ") is expiring, request new token.");
            this.requestNewHdfsDelegationToken(dttr.applicationId, dttr.user, dttr.shouldCancelAtEnd);
        }
    }
    
    private void requestNewHdfsDelegationToken(final ApplicationId applicationId, final String user, final boolean shouldCancelAtEnd) throws IOException, InterruptedException {
        final Credentials credentials = new Credentials();
        final Token<?>[] newTokens = this.obtainSystemTokensForUser(user, credentials);
        DelegationTokenRenewer.LOG.info("Received new tokens for " + applicationId + ". Received " + newTokens.length + " tokens.");
        if (newTokens.length > 0) {
            for (final Token<?> token : newTokens) {
                if (token.isManaged()) {
                    final DelegationTokenToRenew tokenToRenew = new DelegationTokenToRenew(applicationId, token, this.getConfig(), Time.now(), shouldCancelAtEnd, user);
                    this.renewToken(tokenToRenew);
                    this.setTimerForTokenRenewal(tokenToRenew);
                    this.appTokens.get(applicationId).add(tokenToRenew);
                    DelegationTokenRenewer.LOG.info("Received new token " + token);
                }
            }
        }
        final DataOutputBuffer dob = new DataOutputBuffer();
        credentials.writeTokenStorageToStream(dob);
        final ByteBuffer byteBuffer = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        this.rmContext.getSystemCredentialsForApps().put(applicationId, byteBuffer);
    }
    
    protected Token<?>[] obtainSystemTokensForUser(final String user, final Credentials credentials) throws IOException, InterruptedException {
        final UserGroupInformation proxyUser = UserGroupInformation.createProxyUser(user, UserGroupInformation.getLoginUser());
        final Token<?>[] newTokens = (Token<?>[])proxyUser.doAs((PrivilegedExceptionAction<Token[]>)new PrivilegedExceptionAction<Token<?>[]>() {
            @Override
            public Token<?>[] run() throws Exception {
                return FileSystem.get(DelegationTokenRenewer.this.getConfig()).addDelegationTokens(UserGroupInformation.getLoginUser().getUserName(), credentials);
            }
        });
        return newTokens;
    }
    
    private void cancelToken(final DelegationTokenToRenew t) {
        if (t.shouldCancelAtEnd) {
            this.dtCancelThread.cancelToken(t.token, t.conf);
        }
        else {
            DelegationTokenRenewer.LOG.info("Did not cancel " + t);
        }
    }
    
    private void removeFailedDelegationToken(final DelegationTokenToRenew t) {
        final ApplicationId applicationId = t.applicationId;
        DelegationTokenRenewer.LOG.error("removing failed delegation token for appid=" + applicationId + ";t=" + t.token.getService());
        this.appTokens.get(applicationId).remove(t);
        if (t.timerTask != null) {
            t.timerTask.cancel();
        }
    }
    
    public void applicationFinished(final ApplicationId applicationId) {
        this.processDelegationTokenRenewerEvent(new DelegationTokenRenewerEvent(applicationId, DelegationTokenRenewerEventType.FINISH_APPLICATION));
    }
    
    private void handleAppFinishEvent(final DelegationTokenRenewerEvent evt) {
        if (!this.tokenKeepAliveEnabled) {
            this.removeApplicationFromRenewal(evt.getApplicationId());
        }
        else {
            this.delayedRemovalMap.put(evt.getApplicationId(), System.currentTimeMillis() + this.tokenRemovalDelayMs);
        }
    }
    
    public void updateKeepAliveApplications(final List<ApplicationId> appIds) {
        if (this.tokenKeepAliveEnabled && appIds != null && appIds.size() > 0) {
            for (final ApplicationId appId : appIds) {
                this.delayedRemovalMap.put(appId, System.currentTimeMillis() + this.tokenRemovalDelayMs);
            }
        }
    }
    
    private void removeApplicationFromRenewal(final ApplicationId applicationId) {
        this.rmContext.getSystemCredentialsForApps().remove(applicationId);
        final Set<DelegationTokenToRenew> tokens = this.appTokens.get(applicationId);
        if (tokens != null && !tokens.isEmpty()) {
            synchronized (tokens) {
                final Iterator<DelegationTokenToRenew> it = tokens.iterator();
                while (it.hasNext()) {
                    final DelegationTokenToRenew dttr = it.next();
                    if (DelegationTokenRenewer.LOG.isDebugEnabled()) {
                        DelegationTokenRenewer.LOG.debug("Removing delegation token for appId=" + applicationId + "; token=" + dttr.token.getService());
                    }
                    if (dttr.timerTask != null) {
                        dttr.timerTask.cancel();
                    }
                    this.cancelToken(dttr);
                    it.remove();
                }
            }
        }
    }
    
    public void setRMContext(final RMContext rmContext) {
        this.rmContext = rmContext;
    }
    
    static {
        LOG = LogFactory.getLog(DelegationTokenRenewer.class);
    }
    
    @VisibleForTesting
    protected static class DelegationTokenToRenew
    {
        public final Token<?> token;
        public final ApplicationId applicationId;
        public final Configuration conf;
        public long expirationDate;
        public TimerTask timerTask;
        public final boolean shouldCancelAtEnd;
        public long maxDate;
        public String user;
        
        public DelegationTokenToRenew(final ApplicationId jId, final Token<?> token, final Configuration conf, final long expirationDate, final boolean shouldCancelAtEnd, final String user) {
            this.token = token;
            this.user = user;
            if (token.getKind().equals(new Text("HDFS_DELEGATION_TOKEN"))) {
                try {
                    final AbstractDelegationTokenIdentifier identifier = (AbstractDelegationTokenIdentifier)token.decodeIdentifier();
                    this.maxDate = identifier.getMaxDate();
                }
                catch (IOException e) {
                    throw new YarnRuntimeException(e);
                }
            }
            this.applicationId = jId;
            this.conf = conf;
            this.expirationDate = expirationDate;
            this.timerTask = null;
            this.shouldCancelAtEnd = shouldCancelAtEnd;
        }
        
        public void setTimerTask(final TimerTask tTask) {
            this.timerTask = tTask;
        }
        
        @Override
        public String toString() {
            return this.token + ";exp=" + this.expirationDate;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof DelegationTokenToRenew && this.token.equals(((DelegationTokenToRenew)obj).token);
        }
        
        @Override
        public int hashCode() {
            return this.token.hashCode();
        }
    }
    
    private static class DelegationTokenCancelThread extends Thread
    {
        private LinkedBlockingQueue<TokenWithConf> queue;
        
        public DelegationTokenCancelThread() {
            super("Delegation Token Canceler");
            this.queue = new LinkedBlockingQueue<TokenWithConf>();
            this.setDaemon(true);
        }
        
        public void cancelToken(final Token<?> token, final Configuration conf) {
            final TokenWithConf tokenWithConf = new TokenWithConf(token, conf);
            while (!this.queue.offer(tokenWithConf)) {
                DelegationTokenRenewer.LOG.warn("Unable to add token " + token + " for cancellation. " + "Will retry..");
                try {
                    Thread.sleep(100L);
                    continue;
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
        
        @Override
        public void run() {
            TokenWithConf tokenWithConf = null;
            while (true) {
                try {
                    while (true) {
                        final TokenWithConf current;
                        tokenWithConf = (current = this.queue.take());
                        if (DelegationTokenRenewer.LOG.isDebugEnabled()) {
                            DelegationTokenRenewer.LOG.debug("Cancelling token " + tokenWithConf.token.getService());
                        }
                        UserGroupInformation.getLoginUser().doAs((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                            @Override
                            public Void run() throws Exception {
                                current.token.cancel(current.conf);
                                return null;
                            }
                        });
                    }
                }
                catch (IOException e) {
                    DelegationTokenRenewer.LOG.warn("Failed to cancel token " + tokenWithConf.token + " " + StringUtils.stringifyException(e));
                    continue;
                }
                catch (RuntimeException e2) {
                    DelegationTokenRenewer.LOG.warn("Failed to cancel token " + tokenWithConf.token + " " + StringUtils.stringifyException(e2));
                    continue;
                }
                catch (InterruptedException ie) {}
                catch (Throwable t) {
                    DelegationTokenRenewer.LOG.warn("Got exception " + StringUtils.stringifyException(t) + ". Exiting..");
                    System.exit(-1);
                    continue;
                }
                break;
            }
        }
        
        private static class TokenWithConf
        {
            Token<?> token;
            Configuration conf;
            
            TokenWithConf(final Token<?> token, final Configuration conf) {
                this.token = token;
                this.conf = conf;
            }
        }
    }
    
    private class RenewalTimerTask extends TimerTask
    {
        private DelegationTokenToRenew dttr;
        private boolean cancelled;
        
        RenewalTimerTask(final DelegationTokenToRenew t) {
            this.cancelled = false;
            this.dttr = t;
        }
        
        @Override
        public synchronized void run() {
            if (this.cancelled) {
                return;
            }
            final Token<?> token = this.dttr.token;
            try {
                DelegationTokenRenewer.this.requestNewHdfsDelegationTokenIfNeeded(this.dttr);
                if (((Set)DelegationTokenRenewer.this.appTokens.get(this.dttr.applicationId)).contains(this.dttr)) {
                    DelegationTokenRenewer.this.renewToken(this.dttr);
                    DelegationTokenRenewer.this.setTimerForTokenRenewal(this.dttr);
                }
                else {
                    DelegationTokenRenewer.LOG.info("The token was removed already. Token = [" + this.dttr + "]");
                }
            }
            catch (Exception e) {
                DelegationTokenRenewer.LOG.error("Exception renewing token" + token + ". Not rescheduled", e);
                DelegationTokenRenewer.this.removeFailedDelegationToken(this.dttr);
            }
        }
        
        @Override
        public synchronized boolean cancel() {
            this.cancelled = true;
            return super.cancel();
        }
    }
    
    private class DelayedTokenRemovalRunnable implements Runnable
    {
        private long waitTimeMs;
        
        DelayedTokenRemovalRunnable(final Configuration conf) {
            this.waitTimeMs = conf.getLong("yarn.resourcemanager.delayed.delegation-token.removal-interval-ms", 30000L);
        }
        
        @Override
        public void run() {
            final List<ApplicationId> toCancel = new ArrayList<ApplicationId>();
            while (!Thread.currentThread().isInterrupted()) {
                final Iterator<Map.Entry<ApplicationId, Long>> it = (Iterator<Map.Entry<ApplicationId, Long>>)DelegationTokenRenewer.this.delayedRemovalMap.entrySet().iterator();
                toCancel.clear();
                while (it.hasNext()) {
                    final Map.Entry<ApplicationId, Long> e = it.next();
                    if (e.getValue() < System.currentTimeMillis()) {
                        toCancel.add(e.getKey());
                    }
                }
                for (final ApplicationId appId : toCancel) {
                    DelegationTokenRenewer.this.removeApplicationFromRenewal(appId);
                    DelegationTokenRenewer.this.delayedRemovalMap.remove(appId);
                }
                synchronized (this) {
                    try {
                        this.wait(this.waitTimeMs);
                    }
                    catch (InterruptedException e2) {
                        DelegationTokenRenewer.LOG.info("Delayed Deletion Thread Interrupted. Shutting it down");
                    }
                }
            }
        }
    }
    
    private final class DelegationTokenRenewerRunnable implements Runnable
    {
        private DelegationTokenRenewerEvent evt;
        
        public DelegationTokenRenewerRunnable(final DelegationTokenRenewerEvent evt) {
            this.evt = evt;
        }
        
        @Override
        public void run() {
            if (this.evt instanceof DelegationTokenRenewerAppSubmitEvent) {
                final DelegationTokenRenewerAppSubmitEvent appSubmitEvt = (DelegationTokenRenewerAppSubmitEvent)this.evt;
                this.handleDTRenewerAppSubmitEvent(appSubmitEvt);
            }
            else if (this.evt.getType().equals(DelegationTokenRenewerEventType.FINISH_APPLICATION)) {
                DelegationTokenRenewer.this.handleAppFinishEvent(this.evt);
            }
        }
        
        private void handleDTRenewerAppSubmitEvent(final DelegationTokenRenewerAppSubmitEvent event) {
            try {
                DelegationTokenRenewer.this.handleAppSubmitEvent(event);
                DelegationTokenRenewer.this.rmContext.getDispatcher().getEventHandler().handle(new RMAppEvent(event.getApplicationId(), RMAppEventType.START));
            }
            catch (Throwable t) {
                DelegationTokenRenewer.LOG.warn("Unable to add the application to the delegation token renewer.", t);
                DelegationTokenRenewer.this.rmContext.getDispatcher().getEventHandler().handle(new RMAppRejectedEvent(event.getApplicationId(), t.getMessage()));
            }
        }
    }
    
    static class DelegationTokenRenewerAppSubmitEvent extends DelegationTokenRenewerEvent
    {
        private Credentials credentials;
        private boolean shouldCancelAtEnd;
        private String user;
        
        public DelegationTokenRenewerAppSubmitEvent(final ApplicationId appId, final Credentials credentails, final boolean shouldCancelAtEnd, final String user) {
            super(appId, DelegationTokenRenewerEventType.VERIFY_AND_START_APPLICATION);
            this.credentials = credentails;
            this.shouldCancelAtEnd = shouldCancelAtEnd;
            this.user = user;
        }
        
        public Credentials getCredentials() {
            return this.credentials;
        }
        
        public boolean shouldCancelAtEnd() {
            return this.shouldCancelAtEnd;
        }
        
        public String getUser() {
            return this.user;
        }
    }
    
    enum DelegationTokenRenewerEventType
    {
        VERIFY_AND_START_APPLICATION, 
        FINISH_APPLICATION;
    }
    
    private static class DelegationTokenRenewerEvent extends AbstractEvent<DelegationTokenRenewerEventType>
    {
        private ApplicationId appId;
        
        public DelegationTokenRenewerEvent(final ApplicationId appId, final DelegationTokenRenewerEventType type) {
            super(type);
            this.appId = appId;
        }
        
        public ApplicationId getApplicationId() {
            return this.appId;
        }
    }
}
