// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift;

import org.apache.hadoop.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.security.token.TokenIdentifier;
import java.util.List;
import java.util.Arrays;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import org.apache.hadoop.security.token.delegation.HiveDelegationTokenSupport;
import java.util.Iterator;
import java.lang.reflect.Method;
import org.apache.hadoop.util.Daemon;
import org.apache.commons.codec.binary.Base64;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.io.Writable;
import java.util.HashMap;
import org.apache.hadoop.security.token.delegation.DelegationKey;
import java.util.Map;
import java.io.IOException;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import org.apache.hadoop.security.token.Token;
import org.slf4j.Logger;

public class TokenStoreDelegationTokenSecretManager extends DelegationTokenSecretManager
{
    private static final Logger LOGGER;
    private final long keyUpdateInterval;
    private final long tokenRemoverScanInterval;
    private Thread tokenRemoverThread;
    private final DelegationTokenStore tokenStore;
    
    public TokenStoreDelegationTokenSecretManager(final long delegationKeyUpdateInterval, final long delegationTokenMaxLifetime, final long delegationTokenRenewInterval, final long delegationTokenRemoverScanInterval, final DelegationTokenStore sharedStore) {
        super(delegationKeyUpdateInterval, delegationTokenMaxLifetime, delegationTokenRenewInterval, delegationTokenRemoverScanInterval);
        this.keyUpdateInterval = delegationKeyUpdateInterval;
        this.tokenRemoverScanInterval = delegationTokenRemoverScanInterval;
        this.tokenStore = sharedStore;
    }
    
    protected DelegationTokenIdentifier getTokenIdentifier(final Token<DelegationTokenIdentifier> token) throws IOException {
        final ByteArrayInputStream buf = new ByteArrayInputStream(token.getIdentifier());
        final DataInputStream in = new DataInputStream(buf);
        final DelegationTokenIdentifier id = this.createIdentifier();
        id.readFields(in);
        return id;
    }
    
    protected Map<Integer, DelegationKey> reloadKeys() {
        final String[] allKeys = this.tokenStore.getMasterKeys();
        final Map<Integer, DelegationKey> keys = new HashMap<Integer, DelegationKey>(allKeys.length);
        for (final String keyStr : allKeys) {
            final DelegationKey key = new DelegationKey();
            try {
                decodeWritable(key, keyStr);
                keys.put(key.getKeyId(), key);
            }
            catch (IOException ex) {
                TokenStoreDelegationTokenSecretManager.LOGGER.error("Failed to load master key.", ex);
            }
        }
        synchronized (this) {
            super.allKeys.clear();
            super.allKeys.putAll(keys);
        }
        return keys;
    }
    
    @Override
    public byte[] retrievePassword(final DelegationTokenIdentifier identifier) throws InvalidToken {
        final DelegationTokenInformation info = this.tokenStore.getToken(identifier);
        if (info == null) {
            throw new InvalidToken("token expired or does not exist: " + identifier);
        }
        synchronized (this) {
            try {
                super.currentTokens.put(identifier, info);
                return super.retrievePassword(identifier);
            }
            finally {
                super.currentTokens.remove(identifier);
            }
        }
    }
    
    @Override
    public DelegationTokenIdentifier cancelToken(final Token<DelegationTokenIdentifier> token, final String canceller) throws IOException {
        final DelegationTokenIdentifier id = this.getTokenIdentifier(token);
        TokenStoreDelegationTokenSecretManager.LOGGER.info("Token cancelation requested for identifier: " + id);
        this.tokenStore.removeToken(id);
        return id;
    }
    
    @Override
    protected byte[] createPassword(final DelegationTokenIdentifier id) {
        final byte[] password;
        final DelegationTokenInformation info;
        synchronized (this) {
            password = super.createPassword(id);
            info = super.currentTokens.remove(id);
            if (info == null) {
                throw new IllegalStateException("Failed to retrieve token after creation");
            }
        }
        this.tokenStore.addToken(id, info);
        return password;
    }
    
    @Override
    public long renewToken(final Token<DelegationTokenIdentifier> token, final String renewer) throws InvalidToken, IOException {
        final DelegationTokenIdentifier id = this.getTokenIdentifier(token);
        final DelegationTokenInformation tokenInfo = this.tokenStore.getToken(id);
        if (tokenInfo == null) {
            throw new InvalidToken("token does not exist: " + id);
        }
        if (!super.allKeys.containsKey(id.getMasterKeyId())) {
            TokenStoreDelegationTokenSecretManager.LOGGER.info("Unknown master key (id={}), (re)loading keys from token store.", (Object)id.getMasterKeyId());
            this.reloadKeys();
        }
        synchronized (this) {
            super.currentTokens.put(id, tokenInfo);
            try {
                return super.renewToken(token, renewer);
            }
            finally {
                super.currentTokens.remove(id);
            }
        }
    }
    
    public static String encodeWritable(final Writable key) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);
        key.write(dos);
        dos.flush();
        return Base64.encodeBase64URLSafeString(bos.toByteArray());
    }
    
    public static void decodeWritable(final Writable w, final String idStr) throws IOException {
        final DataInputStream in = new DataInputStream(new ByteArrayInputStream(Base64.decodeBase64(idStr)));
        w.readFields(in);
    }
    
    @Override
    protected void logUpdateMasterKey(final DelegationKey key) throws IOException {
        final int keySeq = this.tokenStore.addMasterKey(encodeWritable(key));
        final DelegationKey keyWithSeq = new DelegationKey(keySeq, key.getExpiryDate(), key.getKey());
        final String keyStr = encodeWritable(keyWithSeq);
        this.tokenStore.updateMasterKey(keySeq, keyStr);
        decodeWritable(key, keyStr);
        TokenStoreDelegationTokenSecretManager.LOGGER.info("New master key with key id={}", (Object)key.getKeyId());
        super.logUpdateMasterKey(key);
    }
    
    @Override
    public synchronized void startThreads() throws IOException {
        try {
            final Method m = AbstractDelegationTokenSecretManager.class.getDeclaredMethod("updateCurrentKey", (Class<?>[])new Class[0]);
            m.setAccessible(true);
            m.invoke(this, new Object[0]);
        }
        catch (Exception e) {
            throw new IOException("Failed to initialize master key", e);
        }
        this.running = true;
        (this.tokenRemoverThread = new Daemon(new ExpiredTokenRemover())).start();
    }
    
    @Override
    public synchronized void stopThreads() {
        if (TokenStoreDelegationTokenSecretManager.LOGGER.isDebugEnabled()) {
            TokenStoreDelegationTokenSecretManager.LOGGER.debug("Stopping expired delegation token remover thread");
        }
        this.running = false;
        if (this.tokenRemoverThread != null) {
            this.tokenRemoverThread.interrupt();
        }
    }
    
    protected void removeExpiredTokens() {
        final long now = System.currentTimeMillis();
        for (final DelegationTokenIdentifier id : this.tokenStore.getAllDelegationTokenIdentifiers()) {
            if (now > id.getMaxDate()) {
                this.tokenStore.removeToken(id);
            }
            else {
                final DelegationTokenInformation tokenInfo = this.tokenStore.getToken(id);
                if (tokenInfo == null || now <= tokenInfo.getRenewDate()) {
                    continue;
                }
                this.tokenStore.removeToken(id);
            }
        }
    }
    
    protected void rollMasterKeyExt() throws IOException {
        final Map<Integer, DelegationKey> keys = this.reloadKeys();
        final int currentKeyId = super.currentId;
        HiveDelegationTokenSupport.rollMasterKey(this);
        final List<DelegationKey> keysAfterRoll = Arrays.asList(this.getAllKeys());
        for (final DelegationKey key : keysAfterRoll) {
            keys.remove(key.getKeyId());
            if (key.getKeyId() == currentKeyId) {
                this.tokenStore.updateMasterKey(currentKeyId, encodeWritable(key));
            }
        }
        for (final DelegationKey expiredKey : keys.values()) {
            TokenStoreDelegationTokenSecretManager.LOGGER.info("Removing expired key id={}", (Object)expiredKey.getKeyId());
            try {
                this.tokenStore.removeMasterKey(expiredKey.getKeyId());
            }
            catch (Exception e) {
                TokenStoreDelegationTokenSecretManager.LOGGER.error("Error removing expired key id={}", (Object)expiredKey.getKeyId(), e);
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TokenStoreDelegationTokenSecretManager.class.getName());
    }
    
    protected class ExpiredTokenRemover extends Thread
    {
        private long lastMasterKeyUpdate;
        private long lastTokenCacheCleanup;
        
        @Override
        public void run() {
            TokenStoreDelegationTokenSecretManager.LOGGER.info("Starting expired delegation token remover thread, tokenRemoverScanInterval=" + TokenStoreDelegationTokenSecretManager.this.tokenRemoverScanInterval / 60000L + " min(s)");
            try {
                while (TokenStoreDelegationTokenSecretManager.this.running) {
                    final long now = System.currentTimeMillis();
                    if (this.lastMasterKeyUpdate + TokenStoreDelegationTokenSecretManager.this.keyUpdateInterval < now) {
                        try {
                            TokenStoreDelegationTokenSecretManager.this.rollMasterKeyExt();
                            this.lastMasterKeyUpdate = now;
                        }
                        catch (IOException e) {
                            TokenStoreDelegationTokenSecretManager.LOGGER.error("Master key updating failed. " + StringUtils.stringifyException(e));
                        }
                    }
                    if (this.lastTokenCacheCleanup + TokenStoreDelegationTokenSecretManager.this.tokenRemoverScanInterval < now) {
                        TokenStoreDelegationTokenSecretManager.this.removeExpiredTokens();
                        this.lastTokenCacheCleanup = now;
                    }
                    try {
                        Thread.sleep(5000L);
                    }
                    catch (InterruptedException ie) {
                        TokenStoreDelegationTokenSecretManager.LOGGER.error("InterruptedExcpetion recieved for ExpiredTokenRemover thread " + ie);
                    }
                }
            }
            catch (Throwable t) {
                TokenStoreDelegationTokenSecretManager.LOGGER.error("ExpiredTokenRemover thread received unexpected exception. " + t, t);
                Runtime.getRuntime().exit(-1);
            }
        }
    }
}
