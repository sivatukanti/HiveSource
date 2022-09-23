// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.slf4j.Logger;

public class MemoryTokenStore implements DelegationTokenStore
{
    private static final Logger LOG;
    private final Map<Integer, String> masterKeys;
    private final ConcurrentHashMap<DelegationTokenIdentifier, AbstractDelegationTokenSecretManager.DelegationTokenInformation> tokens;
    private final AtomicInteger masterKeySeq;
    private Configuration conf;
    
    public MemoryTokenStore() {
        this.masterKeys = new ConcurrentHashMap<Integer, String>();
        this.tokens = new ConcurrentHashMap<DelegationTokenIdentifier, AbstractDelegationTokenSecretManager.DelegationTokenInformation>();
        this.masterKeySeq = new AtomicInteger();
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public int addMasterKey(final String s) {
        final int keySeq = this.masterKeySeq.getAndIncrement();
        if (MemoryTokenStore.LOG.isTraceEnabled()) {
            MemoryTokenStore.LOG.trace("addMasterKey: s = " + s + ", keySeq = " + keySeq);
        }
        this.masterKeys.put(keySeq, s);
        return keySeq;
    }
    
    @Override
    public void updateMasterKey(final int keySeq, final String s) {
        if (MemoryTokenStore.LOG.isTraceEnabled()) {
            MemoryTokenStore.LOG.trace("updateMasterKey: s = " + s + ", keySeq = " + keySeq);
        }
        this.masterKeys.put(keySeq, s);
    }
    
    @Override
    public boolean removeMasterKey(final int keySeq) {
        if (MemoryTokenStore.LOG.isTraceEnabled()) {
            MemoryTokenStore.LOG.trace("removeMasterKey: keySeq = " + keySeq);
        }
        return this.masterKeys.remove(keySeq) != null;
    }
    
    @Override
    public String[] getMasterKeys() {
        return this.masterKeys.values().toArray(new String[0]);
    }
    
    @Override
    public boolean addToken(final DelegationTokenIdentifier tokenIdentifier, final AbstractDelegationTokenSecretManager.DelegationTokenInformation token) {
        final AbstractDelegationTokenSecretManager.DelegationTokenInformation tokenInfo = this.tokens.putIfAbsent(tokenIdentifier, token);
        if (MemoryTokenStore.LOG.isTraceEnabled()) {
            MemoryTokenStore.LOG.trace("addToken: tokenIdentifier = " + tokenIdentifier + ", added = " + (tokenInfo == null));
        }
        return tokenInfo == null;
    }
    
    @Override
    public boolean removeToken(final DelegationTokenIdentifier tokenIdentifier) {
        final AbstractDelegationTokenSecretManager.DelegationTokenInformation tokenInfo = this.tokens.remove(tokenIdentifier);
        if (MemoryTokenStore.LOG.isTraceEnabled()) {
            MemoryTokenStore.LOG.trace("removeToken: tokenIdentifier = " + tokenIdentifier + ", removed = " + (tokenInfo != null));
        }
        return tokenInfo != null;
    }
    
    @Override
    public AbstractDelegationTokenSecretManager.DelegationTokenInformation getToken(final DelegationTokenIdentifier tokenIdentifier) {
        final AbstractDelegationTokenSecretManager.DelegationTokenInformation result = this.tokens.get(tokenIdentifier);
        if (MemoryTokenStore.LOG.isTraceEnabled()) {
            MemoryTokenStore.LOG.trace("getToken: tokenIdentifier = " + tokenIdentifier + ", result = " + result);
        }
        return result;
    }
    
    @Override
    public List<DelegationTokenIdentifier> getAllDelegationTokenIdentifiers() {
        final List<DelegationTokenIdentifier> result = new ArrayList<DelegationTokenIdentifier>(this.tokens.size());
        for (final DelegationTokenIdentifier id : this.tokens.keySet()) {
            result.add(id);
        }
        return result;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public void init(final Object hmsHandler, final HadoopThriftAuthBridge.Server.ServerMode smode) throws TokenStoreException {
    }
    
    static {
        LOG = LoggerFactory.getLogger(MemoryTokenStore.class);
    }
}
