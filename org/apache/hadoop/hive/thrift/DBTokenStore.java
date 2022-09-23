// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.conf.Configuration;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.security.token.delegation.HiveDelegationTokenSupport;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.slf4j.Logger;

public class DBTokenStore implements DelegationTokenStore
{
    private static final Logger LOG;
    private Object rawStore;
    
    @Override
    public int addMasterKey(final String s) throws TokenStoreException {
        if (DBTokenStore.LOG.isTraceEnabled()) {
            DBTokenStore.LOG.trace("addMasterKey: s = " + s);
        }
        return (int)this.invokeOnRawStore("addMasterKey", new Object[] { s }, String.class);
    }
    
    @Override
    public void updateMasterKey(final int keySeq, final String s) throws TokenStoreException {
        if (DBTokenStore.LOG.isTraceEnabled()) {
            DBTokenStore.LOG.trace("updateMasterKey: s = " + s + ", keySeq = " + keySeq);
        }
        this.invokeOnRawStore("updateMasterKey", new Object[] { keySeq, s }, Integer.class, String.class);
    }
    
    @Override
    public boolean removeMasterKey(final int keySeq) {
        return (boolean)this.invokeOnRawStore("removeMasterKey", new Object[] { keySeq }, Integer.class);
    }
    
    @Override
    public String[] getMasterKeys() throws TokenStoreException {
        return (String[])this.invokeOnRawStore("getMasterKeys", new Object[0], (Class<?>[])new Class[0]);
    }
    
    @Override
    public boolean addToken(final DelegationTokenIdentifier tokenIdentifier, final AbstractDelegationTokenSecretManager.DelegationTokenInformation token) throws TokenStoreException {
        try {
            final String identifier = TokenStoreDelegationTokenSecretManager.encodeWritable(tokenIdentifier);
            final String tokenStr = Base64.encodeBase64URLSafeString(HiveDelegationTokenSupport.encodeDelegationTokenInformation(token));
            final boolean result = (boolean)this.invokeOnRawStore("addToken", new Object[] { identifier, tokenStr }, String.class, String.class);
            if (DBTokenStore.LOG.isTraceEnabled()) {
                DBTokenStore.LOG.trace("addToken: tokenIdentifier = " + tokenIdentifier + ", added = " + result);
            }
            return result;
        }
        catch (IOException e) {
            throw new TokenStoreException(e);
        }
    }
    
    @Override
    public AbstractDelegationTokenSecretManager.DelegationTokenInformation getToken(final DelegationTokenIdentifier tokenIdentifier) throws TokenStoreException {
        try {
            final String tokenStr = (String)this.invokeOnRawStore("getToken", new Object[] { TokenStoreDelegationTokenSecretManager.encodeWritable(tokenIdentifier) }, String.class);
            AbstractDelegationTokenSecretManager.DelegationTokenInformation result = null;
            if (tokenStr != null) {
                result = HiveDelegationTokenSupport.decodeDelegationTokenInformation(Base64.decodeBase64(tokenStr));
            }
            if (DBTokenStore.LOG.isTraceEnabled()) {
                DBTokenStore.LOG.trace("getToken: tokenIdentifier = " + tokenIdentifier + ", result = " + result);
            }
            return result;
        }
        catch (IOException e) {
            throw new TokenStoreException(e);
        }
    }
    
    @Override
    public boolean removeToken(final DelegationTokenIdentifier tokenIdentifier) throws TokenStoreException {
        try {
            final boolean result = (boolean)this.invokeOnRawStore("removeToken", new Object[] { TokenStoreDelegationTokenSecretManager.encodeWritable(tokenIdentifier) }, String.class);
            if (DBTokenStore.LOG.isTraceEnabled()) {
                DBTokenStore.LOG.trace("removeToken: tokenIdentifier = " + tokenIdentifier + ", removed = " + result);
            }
            return result;
        }
        catch (IOException e) {
            throw new TokenStoreException(e);
        }
    }
    
    @Override
    public List<DelegationTokenIdentifier> getAllDelegationTokenIdentifiers() throws TokenStoreException {
        final List<String> tokenIdents = (List<String>)this.invokeOnRawStore("getAllTokenIdentifiers", new Object[0], (Class<?>[])new Class[0]);
        final List<DelegationTokenIdentifier> delTokenIdents = new ArrayList<DelegationTokenIdentifier>(tokenIdents.size());
        for (final String tokenIdent : tokenIdents) {
            final DelegationTokenIdentifier delToken = new DelegationTokenIdentifier();
            try {
                TokenStoreDelegationTokenSecretManager.decodeWritable(delToken, tokenIdent);
            }
            catch (IOException e) {
                throw new TokenStoreException(e);
            }
            delTokenIdents.add(delToken);
        }
        return delTokenIdents;
    }
    
    @Override
    public void init(final Object rawStore, final HadoopThriftAuthBridge.Server.ServerMode smode) throws TokenStoreException {
        this.rawStore = rawStore;
    }
    
    private Object invokeOnRawStore(final String methName, final Object[] params, final Class<?>... paramTypes) throws TokenStoreException {
        try {
            return this.rawStore.getClass().getMethod(methName, paramTypes).invoke(this.rawStore, params);
        }
        catch (IllegalArgumentException e) {
            throw new TokenStoreException(e);
        }
        catch (SecurityException e2) {
            throw new TokenStoreException(e2);
        }
        catch (IllegalAccessException e3) {
            throw new TokenStoreException(e3);
        }
        catch (InvocationTargetException e4) {
            throw new TokenStoreException(e4.getCause());
        }
        catch (NoSuchMethodException e5) {
            throw new TokenStoreException(e5);
        }
    }
    
    @Override
    public void setConf(final Configuration conf) {
    }
    
    @Override
    public Configuration getConf() {
        return null;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    static {
        LOG = LoggerFactory.getLogger(DBTokenStore.class);
    }
}
