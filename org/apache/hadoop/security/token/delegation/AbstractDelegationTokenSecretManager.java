// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.security.token.TokenIdentifier;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import javax.crypto.SecretKey;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.HadoopKerberosName;
import org.apache.hadoop.security.AccessControlException;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import org.apache.hadoop.security.token.Token;
import java.security.MessageDigest;
import java.util.Iterator;
import org.apache.hadoop.util.Time;
import java.io.IOException;
import org.apache.hadoop.util.Daemon;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.SecretManager;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class AbstractDelegationTokenSecretManager<TokenIdent extends AbstractDelegationTokenIdentifier> extends SecretManager<TokenIdent>
{
    private static final Logger LOG;
    protected final Map<TokenIdent, DelegationTokenInformation> currentTokens;
    protected int delegationTokenSequenceNumber;
    protected final Map<Integer, DelegationKey> allKeys;
    protected int currentId;
    private DelegationKey currentKey;
    private long keyUpdateInterval;
    private long tokenMaxLifetime;
    private long tokenRemoverScanInterval;
    private long tokenRenewInterval;
    protected boolean storeTokenTrackingId;
    private Thread tokenRemoverThread;
    protected volatile boolean running;
    protected Object noInterruptsLock;
    
    private String formatTokenId(final TokenIdent id) {
        return "(" + id + ")";
    }
    
    public AbstractDelegationTokenSecretManager(final long delegationKeyUpdateInterval, final long delegationTokenMaxLifetime, final long delegationTokenRenewInterval, final long delegationTokenRemoverScanInterval) {
        this.currentTokens = new HashMap<TokenIdent, DelegationTokenInformation>();
        this.delegationTokenSequenceNumber = 0;
        this.allKeys = new HashMap<Integer, DelegationKey>();
        this.currentId = 0;
        this.noInterruptsLock = new Object();
        this.keyUpdateInterval = delegationKeyUpdateInterval;
        this.tokenMaxLifetime = delegationTokenMaxLifetime;
        this.tokenRenewInterval = delegationTokenRenewInterval;
        this.tokenRemoverScanInterval = delegationTokenRemoverScanInterval;
        this.storeTokenTrackingId = false;
    }
    
    public void startThreads() throws IOException {
        Preconditions.checkState(!this.running);
        this.updateCurrentKey();
        synchronized (this) {
            this.running = true;
            (this.tokenRemoverThread = new Daemon(new ExpiredTokenRemover())).start();
        }
    }
    
    public synchronized void reset() {
        this.setCurrentKeyId(0);
        this.allKeys.clear();
        this.setDelegationTokenSeqNum(0);
        this.currentTokens.clear();
    }
    
    public synchronized void addKey(final DelegationKey key) throws IOException {
        if (this.running) {
            throw new IOException("Can't add delegation key to a running SecretManager.");
        }
        if (key.getKeyId() > this.getCurrentKeyId()) {
            this.setCurrentKeyId(key.getKeyId());
        }
        this.allKeys.put(key.getKeyId(), key);
    }
    
    public synchronized DelegationKey[] getAllKeys() {
        return this.allKeys.values().toArray(new DelegationKey[0]);
    }
    
    protected void logUpdateMasterKey(final DelegationKey key) throws IOException {
    }
    
    protected void logExpireToken(final TokenIdent ident) throws IOException {
    }
    
    protected void storeNewMasterKey(final DelegationKey key) throws IOException {
    }
    
    protected void removeStoredMasterKey(final DelegationKey key) {
    }
    
    protected void storeNewToken(final TokenIdent ident, final long renewDate) throws IOException {
    }
    
    protected void removeStoredToken(final TokenIdent ident) throws IOException {
    }
    
    protected void updateStoredToken(final TokenIdent ident, final long renewDate) throws IOException {
    }
    
    protected synchronized int getCurrentKeyId() {
        return this.currentId;
    }
    
    protected synchronized int incrementCurrentKeyId() {
        return ++this.currentId;
    }
    
    protected synchronized void setCurrentKeyId(final int keyId) {
        this.currentId = keyId;
    }
    
    protected synchronized int getDelegationTokenSeqNum() {
        return this.delegationTokenSequenceNumber;
    }
    
    protected synchronized int incrementDelegationTokenSeqNum() {
        return ++this.delegationTokenSequenceNumber;
    }
    
    protected synchronized void setDelegationTokenSeqNum(final int seqNum) {
        this.delegationTokenSequenceNumber = seqNum;
    }
    
    protected DelegationKey getDelegationKey(final int keyId) {
        return this.allKeys.get(keyId);
    }
    
    protected void storeDelegationKey(final DelegationKey key) throws IOException {
        this.allKeys.put(key.getKeyId(), key);
        this.storeNewMasterKey(key);
    }
    
    protected void updateDelegationKey(final DelegationKey key) throws IOException {
        this.allKeys.put(key.getKeyId(), key);
    }
    
    protected DelegationTokenInformation getTokenInfo(final TokenIdent ident) {
        return this.currentTokens.get(ident);
    }
    
    protected void storeToken(final TokenIdent ident, final DelegationTokenInformation tokenInfo) throws IOException {
        this.currentTokens.put(ident, tokenInfo);
        this.storeNewToken(ident, tokenInfo.getRenewDate());
    }
    
    protected void updateToken(final TokenIdent ident, final DelegationTokenInformation tokenInfo) throws IOException {
        this.currentTokens.put(ident, tokenInfo);
        this.updateStoredToken(ident, tokenInfo.getRenewDate());
    }
    
    public synchronized void addPersistedDelegationToken(final TokenIdent identifier, final long renewDate) throws IOException {
        if (this.running) {
            throw new IOException("Can't add persisted delegation token to a running SecretManager.");
        }
        final int keyId = identifier.getMasterKeyId();
        final DelegationKey dKey = this.allKeys.get(keyId);
        if (dKey == null) {
            AbstractDelegationTokenSecretManager.LOG.warn("No KEY found for persisted identifier " + this.formatTokenId(identifier));
            return;
        }
        final byte[] password = SecretManager.createPassword(identifier.getBytes(), dKey.getKey());
        if (identifier.getSequenceNumber() > this.getDelegationTokenSeqNum()) {
            this.setDelegationTokenSeqNum(identifier.getSequenceNumber());
        }
        if (this.getTokenInfo(identifier) == null) {
            this.currentTokens.put(identifier, new DelegationTokenInformation(renewDate, password, this.getTrackingIdIfEnabled(identifier)));
            return;
        }
        throw new IOException("Same delegation token being added twice: " + this.formatTokenId(identifier));
    }
    
    private void updateCurrentKey() throws IOException {
        AbstractDelegationTokenSecretManager.LOG.info("Updating the current master key for generating delegation tokens");
        final int newCurrentId;
        synchronized (this) {
            newCurrentId = this.incrementCurrentKeyId();
        }
        final DelegationKey newKey = new DelegationKey(newCurrentId, System.currentTimeMillis() + this.keyUpdateInterval + this.tokenMaxLifetime, this.generateSecret());
        this.logUpdateMasterKey(newKey);
        synchronized (this) {
            this.storeDelegationKey(this.currentKey = newKey);
        }
    }
    
    void rollMasterKey() throws IOException {
        synchronized (this) {
            this.removeExpiredKeys();
            this.currentKey.setExpiryDate(Time.now() + this.tokenMaxLifetime);
            this.updateDelegationKey(this.currentKey);
        }
        this.updateCurrentKey();
    }
    
    private synchronized void removeExpiredKeys() {
        final long now = Time.now();
        final Iterator<Map.Entry<Integer, DelegationKey>> it = this.allKeys.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<Integer, DelegationKey> e = it.next();
            if (e.getValue().getExpiryDate() < now) {
                it.remove();
                if (e.getValue().equals(this.currentKey)) {
                    continue;
                }
                this.removeStoredMasterKey(e.getValue());
            }
        }
    }
    
    @Override
    protected synchronized byte[] createPassword(final TokenIdent identifier) {
        final long now = Time.now();
        final int sequenceNum = this.incrementDelegationTokenSeqNum();
        identifier.setIssueDate(now);
        identifier.setMaxDate(now + this.tokenMaxLifetime);
        identifier.setMasterKeyId(this.currentKey.getKeyId());
        identifier.setSequenceNumber(sequenceNum);
        AbstractDelegationTokenSecretManager.LOG.info("Creating password for identifier: " + this.formatTokenId(identifier) + ", currentKey: " + this.currentKey.getKeyId());
        final byte[] password = SecretManager.createPassword(identifier.getBytes(), this.currentKey.getKey());
        final DelegationTokenInformation tokenInfo = new DelegationTokenInformation(now + this.tokenRenewInterval, password, this.getTrackingIdIfEnabled(identifier));
        try {
            this.storeToken(identifier, tokenInfo);
        }
        catch (IOException ioe) {
            AbstractDelegationTokenSecretManager.LOG.error("Could not store token " + this.formatTokenId(identifier) + "!!", ioe);
        }
        return password;
    }
    
    protected DelegationTokenInformation checkToken(final TokenIdent identifier) throws InvalidToken {
        assert Thread.holdsLock(this);
        final DelegationTokenInformation info = this.getTokenInfo(identifier);
        if (info == null) {
            throw new InvalidToken("token " + this.formatTokenId(identifier) + " can't be found in cache");
        }
        final long now = Time.now();
        if (info.getRenewDate() < now) {
            throw new InvalidToken("token " + this.formatTokenId(identifier) + " is expired, current time: " + Time.formatTime(now) + " expected renewal time: " + Time.formatTime(info.getRenewDate()));
        }
        return info;
    }
    
    @Override
    public synchronized byte[] retrievePassword(final TokenIdent identifier) throws InvalidToken {
        return this.checkToken(identifier).getPassword();
    }
    
    protected String getTrackingIdIfEnabled(final TokenIdent ident) {
        if (this.storeTokenTrackingId) {
            return ident.getTrackingId();
        }
        return null;
    }
    
    public synchronized String getTokenTrackingId(final TokenIdent identifier) {
        final DelegationTokenInformation info = this.getTokenInfo(identifier);
        if (info == null) {
            return null;
        }
        return info.getTrackingId();
    }
    
    public synchronized void verifyToken(final TokenIdent identifier, final byte[] password) throws InvalidToken {
        final byte[] storedPassword = this.retrievePassword(identifier);
        if (!MessageDigest.isEqual(password, storedPassword)) {
            throw new InvalidToken("token " + this.formatTokenId(identifier) + " is invalid, password doesn't match");
        }
    }
    
    public synchronized long renewToken(final Token<TokenIdent> token, final String renewer) throws InvalidToken, IOException {
        final ByteArrayInputStream buf = new ByteArrayInputStream(token.getIdentifier());
        final DataInputStream in = new DataInputStream(buf);
        final TokenIdent id = this.createIdentifier();
        id.readFields(in);
        AbstractDelegationTokenSecretManager.LOG.info("Token renewal for identifier: " + this.formatTokenId(id) + "; total currentTokens " + this.currentTokens.size());
        final long now = Time.now();
        if (id.getMaxDate() < now) {
            throw new InvalidToken(renewer + " tried to renew an expired token " + this.formatTokenId(id) + " max expiration date: " + Time.formatTime(id.getMaxDate()) + " currentTime: " + Time.formatTime(now));
        }
        if (id.getRenewer() == null || id.getRenewer().toString().isEmpty()) {
            throw new AccessControlException(renewer + " tried to renew a token " + this.formatTokenId(id) + " without a renewer");
        }
        if (!id.getRenewer().toString().equals(renewer)) {
            throw new AccessControlException(renewer + " tries to renew a token " + this.formatTokenId(id) + " with non-matching renewer " + id.getRenewer());
        }
        final DelegationKey key = this.getDelegationKey(id.getMasterKeyId());
        if (key == null) {
            throw new InvalidToken("Unable to find master key for keyId=" + id.getMasterKeyId() + " from cache. Failed to renew an unexpired token " + this.formatTokenId(id) + " with sequenceNumber=" + id.getSequenceNumber());
        }
        final byte[] password = SecretManager.createPassword(token.getIdentifier(), key.getKey());
        if (!MessageDigest.isEqual(password, token.getPassword())) {
            throw new AccessControlException(renewer + " is trying to renew a token " + this.formatTokenId(id) + " with wrong password");
        }
        final long renewTime = Math.min(id.getMaxDate(), now + this.tokenRenewInterval);
        final String trackingId = this.getTrackingIdIfEnabled(id);
        final DelegationTokenInformation info = new DelegationTokenInformation(renewTime, password, trackingId);
        if (this.getTokenInfo(id) == null) {
            throw new InvalidToken("Renewal request for unknown token " + this.formatTokenId(id));
        }
        this.updateToken(id, info);
        return renewTime;
    }
    
    public synchronized TokenIdent cancelToken(final Token<TokenIdent> token, final String canceller) throws IOException {
        final ByteArrayInputStream buf = new ByteArrayInputStream(token.getIdentifier());
        final DataInputStream in = new DataInputStream(buf);
        final TokenIdent id = this.createIdentifier();
        id.readFields(in);
        AbstractDelegationTokenSecretManager.LOG.info("Token cancellation requested for identifier: " + this.formatTokenId(id));
        if (id.getUser() == null) {
            throw new InvalidToken("Token with no owner " + this.formatTokenId(id));
        }
        final String owner = id.getUser().getUserName();
        final Text renewer = id.getRenewer();
        final HadoopKerberosName cancelerKrbName = new HadoopKerberosName(canceller);
        final String cancelerShortName = cancelerKrbName.getShortName();
        if (!canceller.equals(owner) && (renewer == null || renewer.toString().isEmpty() || !cancelerShortName.equals(renewer.toString()))) {
            throw new AccessControlException(canceller + " is not authorized to cancel the token " + this.formatTokenId(id));
        }
        final DelegationTokenInformation info = this.currentTokens.remove(id);
        if (info == null) {
            throw new InvalidToken("Token not found " + this.formatTokenId(id));
        }
        this.removeStoredToken(id);
        return id;
    }
    
    public static SecretKey createSecretKey(final byte[] key) {
        return SecretManager.createSecretKey(key);
    }
    
    private void removeExpiredToken() throws IOException {
        final long now = Time.now();
        final Set<TokenIdent> expiredTokens = new HashSet<TokenIdent>();
        synchronized (this) {
            final Iterator<Map.Entry<TokenIdent, DelegationTokenInformation>> i = this.currentTokens.entrySet().iterator();
            while (i.hasNext()) {
                final Map.Entry<TokenIdent, DelegationTokenInformation> entry = i.next();
                final long renewDate = entry.getValue().getRenewDate();
                if (renewDate < now) {
                    expiredTokens.add(entry.getKey());
                    i.remove();
                }
            }
        }
        this.logExpireTokens(expiredTokens);
    }
    
    protected void logExpireTokens(final Collection<TokenIdent> expiredTokens) throws IOException {
        for (final TokenIdent ident : expiredTokens) {
            this.logExpireToken(ident);
            AbstractDelegationTokenSecretManager.LOG.info("Removing expired token " + this.formatTokenId(ident));
            this.removeStoredToken(ident);
        }
    }
    
    public void stopThreads() {
        if (AbstractDelegationTokenSecretManager.LOG.isDebugEnabled()) {
            AbstractDelegationTokenSecretManager.LOG.debug("Stopping expired delegation token remover thread");
        }
        this.running = false;
        if (this.tokenRemoverThread != null) {
            synchronized (this.noInterruptsLock) {
                this.tokenRemoverThread.interrupt();
            }
            try {
                this.tokenRemoverThread.join();
            }
            catch (InterruptedException e) {
                throw new RuntimeException("Unable to join on token removal thread", e);
            }
        }
    }
    
    public synchronized boolean isRunning() {
        return this.running;
    }
    
    public TokenIdent decodeTokenIdentifier(final Token<TokenIdent> token) throws IOException {
        return token.decodeIdentifier();
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbstractDelegationTokenSecretManager.class);
    }
    
    @InterfaceStability.Evolving
    public static class DelegationTokenInformation
    {
        long renewDate;
        byte[] password;
        String trackingId;
        
        public DelegationTokenInformation(final long renewDate, final byte[] password) {
            this(renewDate, password, null);
        }
        
        public DelegationTokenInformation(final long renewDate, final byte[] password, final String trackingId) {
            this.renewDate = renewDate;
            this.password = password;
            this.trackingId = trackingId;
        }
        
        public long getRenewDate() {
            return this.renewDate;
        }
        
        byte[] getPassword() {
            return this.password;
        }
        
        public String getTrackingId() {
            return this.trackingId;
        }
    }
    
    private class ExpiredTokenRemover extends Thread
    {
        private long lastMasterKeyUpdate;
        private long lastTokenCacheCleanup;
        
        @Override
        public void run() {
            AbstractDelegationTokenSecretManager.LOG.info("Starting expired delegation token remover thread, tokenRemoverScanInterval=" + AbstractDelegationTokenSecretManager.this.tokenRemoverScanInterval / 60000L + " min(s)");
            try {
                while (AbstractDelegationTokenSecretManager.this.running) {
                    final long now = Time.now();
                    if (this.lastMasterKeyUpdate + AbstractDelegationTokenSecretManager.this.keyUpdateInterval < now) {
                        try {
                            AbstractDelegationTokenSecretManager.this.rollMasterKey();
                            this.lastMasterKeyUpdate = now;
                        }
                        catch (IOException e) {
                            AbstractDelegationTokenSecretManager.LOG.error("Master key updating failed: ", e);
                        }
                    }
                    if (this.lastTokenCacheCleanup + AbstractDelegationTokenSecretManager.this.tokenRemoverScanInterval < now) {
                        AbstractDelegationTokenSecretManager.this.removeExpiredToken();
                        this.lastTokenCacheCleanup = now;
                    }
                    try {
                        Thread.sleep(Math.min(5000L, AbstractDelegationTokenSecretManager.this.keyUpdateInterval));
                    }
                    catch (InterruptedException ie) {
                        AbstractDelegationTokenSecretManager.LOG.error("ExpiredTokenRemover received " + ie);
                    }
                }
            }
            catch (Throwable t) {
                AbstractDelegationTokenSecretManager.LOG.error("ExpiredTokenRemover thread received unexpected exception", t);
                Runtime.getRuntime().exit(-1);
            }
        }
    }
}
