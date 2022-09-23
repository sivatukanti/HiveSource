// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key.kms;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.Arrays;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.security.GeneralSecurityException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import javax.net.ssl.SSLHandshakeException;
import org.apache.hadoop.security.AccessControlException;
import java.io.IOException;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.io.retry.RetryPolicies;
import com.google.common.base.Preconditions;
import org.apache.hadoop.util.KMSUtil;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import org.apache.hadoop.io.retry.RetryPolicy;
import org.apache.hadoop.io.Text;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.apache.hadoop.crypto.key.KeyProviderDelegationTokenExtension;
import org.apache.hadoop.crypto.key.KeyProviderCryptoExtension;
import org.apache.hadoop.crypto.key.KeyProvider;

public class LoadBalancingKMSClientProvider extends KeyProvider implements KeyProviderCryptoExtension.CryptoExtension, KeyProviderDelegationTokenExtension.DelegationTokenExtension
{
    public static Logger LOG;
    private final KMSClientProvider[] providers;
    private final AtomicInteger currentIdx;
    private final Text dtService;
    private final Text canonicalService;
    private RetryPolicy retryPolicy;
    
    public LoadBalancingKMSClientProvider(final URI providerUri, final KMSClientProvider[] providers, final Configuration conf) {
        this(providerUri, providers, Time.monotonicNow(), conf);
    }
    
    @VisibleForTesting
    LoadBalancingKMSClientProvider(final KMSClientProvider[] providers, final long seed, final Configuration conf) {
        this(URI.create("kms://testing"), providers, seed, conf);
    }
    
    private LoadBalancingKMSClientProvider(final URI uri, final KMSClientProvider[] providers, final long seed, final Configuration conf) {
        super(conf);
        this.retryPolicy = null;
        this.dtService = KMSClientProvider.getDtService(uri);
        if (KMSUtil.getKeyProviderUri(conf) == null) {
            this.canonicalService = this.dtService;
        }
        else {
            this.canonicalService = new Text(providers[0].getCanonicalServiceName());
        }
        this.providers = ((seed != 0L) ? shuffle(providers) : providers);
        for (final KMSClientProvider provider : providers) {
            provider.setClientTokenProvider(this);
        }
        this.currentIdx = new AtomicInteger((int)(seed % providers.length));
        final int maxNumRetries = conf.getInt("hadoop.security.kms.client.failover.max.retries", providers.length);
        final int sleepBaseMillis = conf.getInt("hadoop.security.kms.client.failover.sleep.base.millis", 100);
        final int sleepMaxMillis = conf.getInt("hadoop.security.kms.client.failover.sleep.max.millis", 2000);
        Preconditions.checkState(maxNumRetries >= 0);
        Preconditions.checkState(sleepBaseMillis >= 0);
        Preconditions.checkState(sleepMaxMillis >= 0);
        this.retryPolicy = RetryPolicies.failoverOnNetworkException(RetryPolicies.TRY_ONCE_THEN_FAIL, maxNumRetries, 0, sleepBaseMillis, sleepMaxMillis);
        LoadBalancingKMSClientProvider.LOG.debug("Created LoadBalancingKMSClientProvider for KMS url: {} with {} providers. delegation token service: {}, canonical service: {}", uri, providers.length, this.dtService, this.canonicalService);
    }
    
    @VisibleForTesting
    public KMSClientProvider[] getProviders() {
        return this.providers;
    }
    
    @Override
    public Token<? extends TokenIdentifier> selectDelegationToken(final Credentials creds) {
        Token<? extends TokenIdentifier> token = (Token<? extends TokenIdentifier>)KMSClientProvider.selectDelegationToken(creds, this.canonicalService);
        if (token == null) {
            for (final KMSClientProvider provider : this.getProviders()) {
                token = (Token<? extends TokenIdentifier>)provider.selectDelegationToken(creds);
                if (token != null) {
                    break;
                }
            }
        }
        return token;
    }
    
    private <T> T doOp(final ProviderCallable<T> op, final int currPos, final boolean isIdempotent) throws IOException {
        if (this.providers.length == 0) {
            throw new IOException("No providers configured !");
        }
        int numFailovers = 0;
        int i = 0;
        while (true) {
            final KMSClientProvider provider = this.providers[(currPos + i) % this.providers.length];
            try {
                return op.call(provider);
            }
            catch (AccessControlException ace) {
                throw ace;
            }
            catch (IOException ioe) {
                LoadBalancingKMSClientProvider.LOG.warn("KMS provider at [{}] threw an IOException: ", provider.getKMSUrl(), ioe);
                if (ioe instanceof SSLHandshakeException) {
                    final Exception cause = ioe;
                    ioe = new ConnectException("SSLHandshakeException: " + cause.getMessage());
                    ioe.initCause(cause);
                }
                RetryPolicy.RetryAction action = null;
                try {
                    action = this.retryPolicy.shouldRetry(ioe, 0, numFailovers, isIdempotent);
                }
                catch (Exception e) {
                    if (e instanceof IOException) {
                        throw (IOException)e;
                    }
                    throw new IOException(e);
                }
                if (action.action == RetryPolicy.RetryAction.RetryDecision.FAIL && numFailovers >= this.providers.length - 1) {
                    LoadBalancingKMSClientProvider.LOG.error("Aborting since the Request has failed with all KMS providers(depending on {}={} setting and numProviders={}) in the group OR the exception is not recoverable", "hadoop.security.kms.client.failover.max.retries", this.getConf().getInt("hadoop.security.kms.client.failover.max.retries", this.providers.length), this.providers.length);
                    throw ioe;
                }
                if ((numFailovers + 1) % this.providers.length == 0) {
                    try {
                        Thread.sleep(action.delayMillis);
                    }
                    catch (InterruptedException e3) {
                        throw new InterruptedIOException("Thread Interrupted");
                    }
                }
            }
            catch (Exception e2) {
                if (e2 instanceof RuntimeException) {
                    throw (RuntimeException)e2;
                }
                throw new WrapperException(e2);
            }
            ++i;
            ++numFailovers;
        }
    }
    
    private int nextIdx() {
        int current;
        int next;
        do {
            current = this.currentIdx.get();
            next = (current + 1) % this.providers.length;
        } while (!this.currentIdx.compareAndSet(current, next));
        return current;
    }
    
    @Override
    public String getCanonicalServiceName() {
        return this.canonicalService.toString();
    }
    
    @Override
    public Token<?> getDelegationToken(final String renewer) throws IOException {
        return this.doOp((ProviderCallable<Token<?>>)new ProviderCallable<Token<?>>() {
            @Override
            public Token<?> call(final KMSClientProvider provider) throws IOException {
                final Token<?> token = provider.getDelegationToken(renewer);
                token.setService(LoadBalancingKMSClientProvider.this.dtService);
                LoadBalancingKMSClientProvider.LOG.debug("New token service set. Token: ({})", token);
                return token;
            }
        }, this.nextIdx(), false);
    }
    
    @Override
    public long renewDelegationToken(final Token<?> token) throws IOException {
        return this.doOp((ProviderCallable<Long>)new ProviderCallable<Long>() {
            @Override
            public Long call(final KMSClientProvider provider) throws IOException {
                return provider.renewDelegationToken(token);
            }
        }, this.nextIdx(), false);
    }
    
    @Override
    public Void cancelDelegationToken(final Token<?> token) throws IOException {
        return this.doOp((ProviderCallable<Void>)new ProviderCallable<Void>() {
            @Override
            public Void call(final KMSClientProvider provider) throws IOException {
                provider.cancelDelegationToken(token);
                return null;
            }
        }, this.nextIdx(), false);
    }
    
    @Override
    public void warmUpEncryptedKeys(final String... keyNames) throws IOException {
        Preconditions.checkArgument(this.providers.length > 0, (Object)"No providers are configured");
        boolean success = false;
        IOException e = null;
        for (final KMSClientProvider provider : this.providers) {
            try {
                provider.warmUpEncryptedKeys(keyNames);
                success = true;
            }
            catch (IOException ioe) {
                e = ioe;
                LoadBalancingKMSClientProvider.LOG.error("Error warming up keys for provider with url[" + provider.getKMSUrl() + "]", ioe);
            }
        }
        if (!success && e != null) {
            throw e;
        }
    }
    
    @Override
    public void drain(final String keyName) {
        for (final KMSClientProvider provider : this.providers) {
            provider.drain(keyName);
        }
    }
    
    @Override
    public void invalidateCache(final String keyName) throws IOException {
        for (final KMSClientProvider provider : this.providers) {
            provider.invalidateCache(keyName);
        }
    }
    
    @Override
    public KeyProviderCryptoExtension.EncryptedKeyVersion generateEncryptedKey(final String encryptionKeyName) throws IOException, GeneralSecurityException {
        try {
            return this.doOp((ProviderCallable<KeyProviderCryptoExtension.EncryptedKeyVersion>)new ProviderCallable<KeyProviderCryptoExtension.EncryptedKeyVersion>() {
                @Override
                public KeyProviderCryptoExtension.EncryptedKeyVersion call(final KMSClientProvider provider) throws IOException, GeneralSecurityException {
                    return provider.generateEncryptedKey(encryptionKeyName);
                }
            }, this.nextIdx(), true);
        }
        catch (WrapperException we) {
            if (we.getCause() instanceof GeneralSecurityException) {
                throw (GeneralSecurityException)we.getCause();
            }
            throw new IOException(we.getCause());
        }
    }
    
    @Override
    public KeyVersion decryptEncryptedKey(final KeyProviderCryptoExtension.EncryptedKeyVersion encryptedKeyVersion) throws IOException, GeneralSecurityException {
        try {
            return this.doOp((ProviderCallable<KeyVersion>)new ProviderCallable<KeyVersion>() {
                @Override
                public KeyVersion call(final KMSClientProvider provider) throws IOException, GeneralSecurityException {
                    return provider.decryptEncryptedKey(encryptedKeyVersion);
                }
            }, this.nextIdx(), true);
        }
        catch (WrapperException we) {
            if (we.getCause() instanceof GeneralSecurityException) {
                throw (GeneralSecurityException)we.getCause();
            }
            throw new IOException(we.getCause());
        }
    }
    
    @Override
    public KeyProviderCryptoExtension.EncryptedKeyVersion reencryptEncryptedKey(final KeyProviderCryptoExtension.EncryptedKeyVersion ekv) throws IOException, GeneralSecurityException {
        try {
            return this.doOp((ProviderCallable<KeyProviderCryptoExtension.EncryptedKeyVersion>)new ProviderCallable<KeyProviderCryptoExtension.EncryptedKeyVersion>() {
                @Override
                public KeyProviderCryptoExtension.EncryptedKeyVersion call(final KMSClientProvider provider) throws IOException, GeneralSecurityException {
                    return provider.reencryptEncryptedKey(ekv);
                }
            }, this.nextIdx(), true);
        }
        catch (WrapperException we) {
            if (we.getCause() instanceof GeneralSecurityException) {
                throw (GeneralSecurityException)we.getCause();
            }
            throw new IOException(we.getCause());
        }
    }
    
    @Override
    public void reencryptEncryptedKeys(final List<KeyProviderCryptoExtension.EncryptedKeyVersion> ekvs) throws IOException, GeneralSecurityException {
        try {
            this.doOp((ProviderCallable<Object>)new ProviderCallable<Void>() {
                @Override
                public Void call(final KMSClientProvider provider) throws IOException, GeneralSecurityException {
                    provider.reencryptEncryptedKeys(ekvs);
                    return null;
                }
            }, this.nextIdx(), true);
        }
        catch (WrapperException we) {
            if (we.getCause() instanceof GeneralSecurityException) {
                throw (GeneralSecurityException)we.getCause();
            }
            throw new IOException(we.getCause());
        }
    }
    
    @Override
    public KeyVersion getKeyVersion(final String versionName) throws IOException {
        return this.doOp((ProviderCallable<KeyVersion>)new ProviderCallable<KeyVersion>() {
            @Override
            public KeyVersion call(final KMSClientProvider provider) throws IOException {
                return provider.getKeyVersion(versionName);
            }
        }, this.nextIdx(), true);
    }
    
    @Override
    public List<String> getKeys() throws IOException {
        return this.doOp((ProviderCallable<List<String>>)new ProviderCallable<List<String>>() {
            @Override
            public List<String> call(final KMSClientProvider provider) throws IOException {
                return provider.getKeys();
            }
        }, this.nextIdx(), true);
    }
    
    @Override
    public Metadata[] getKeysMetadata(final String... names) throws IOException {
        return this.doOp((ProviderCallable<Metadata[]>)new ProviderCallable<Metadata[]>() {
            @Override
            public Metadata[] call(final KMSClientProvider provider) throws IOException {
                return provider.getKeysMetadata(names);
            }
        }, this.nextIdx(), true);
    }
    
    @Override
    public List<KeyVersion> getKeyVersions(final String name) throws IOException {
        return this.doOp((ProviderCallable<List<KeyVersion>>)new ProviderCallable<List<KeyVersion>>() {
            @Override
            public List<KeyVersion> call(final KMSClientProvider provider) throws IOException {
                return provider.getKeyVersions(name);
            }
        }, this.nextIdx(), true);
    }
    
    @Override
    public KeyVersion getCurrentKey(final String name) throws IOException {
        return this.doOp((ProviderCallable<KeyVersion>)new ProviderCallable<KeyVersion>() {
            @Override
            public KeyVersion call(final KMSClientProvider provider) throws IOException {
                return provider.getCurrentKey(name);
            }
        }, this.nextIdx(), true);
    }
    
    @Override
    public Metadata getMetadata(final String name) throws IOException {
        return this.doOp((ProviderCallable<Metadata>)new ProviderCallable<Metadata>() {
            @Override
            public Metadata call(final KMSClientProvider provider) throws IOException {
                return provider.getMetadata(name);
            }
        }, this.nextIdx(), true);
    }
    
    @Override
    public KeyVersion createKey(final String name, final byte[] material, final Options options) throws IOException {
        return this.doOp((ProviderCallable<KeyVersion>)new ProviderCallable<KeyVersion>() {
            @Override
            public KeyVersion call(final KMSClientProvider provider) throws IOException {
                return provider.createKey(name, material, options);
            }
        }, this.nextIdx(), false);
    }
    
    @Override
    public KeyVersion createKey(final String name, final Options options) throws NoSuchAlgorithmException, IOException {
        try {
            return this.doOp((ProviderCallable<KeyVersion>)new ProviderCallable<KeyVersion>() {
                @Override
                public KeyVersion call(final KMSClientProvider provider) throws IOException, NoSuchAlgorithmException {
                    return provider.createKey(name, options);
                }
            }, this.nextIdx(), false);
        }
        catch (WrapperException e) {
            if (e.getCause() instanceof GeneralSecurityException) {
                throw (NoSuchAlgorithmException)e.getCause();
            }
            throw new IOException(e.getCause());
        }
    }
    
    @Override
    public void deleteKey(final String name) throws IOException {
        this.doOp((ProviderCallable<Object>)new ProviderCallable<Void>() {
            @Override
            public Void call(final KMSClientProvider provider) throws IOException {
                provider.deleteKey(name);
                return null;
            }
        }, this.nextIdx(), false);
    }
    
    @Override
    public KeyVersion rollNewVersion(final String name, final byte[] material) throws IOException {
        final KeyVersion newVersion = this.doOp((ProviderCallable<KeyVersion>)new ProviderCallable<KeyVersion>() {
            @Override
            public KeyVersion call(final KMSClientProvider provider) throws IOException {
                return provider.rollNewVersion(name, material);
            }
        }, this.nextIdx(), false);
        this.invalidateCache(name);
        return newVersion;
    }
    
    @Override
    public KeyVersion rollNewVersion(final String name) throws NoSuchAlgorithmException, IOException {
        try {
            final KeyVersion newVersion = this.doOp((ProviderCallable<KeyVersion>)new ProviderCallable<KeyVersion>() {
                @Override
                public KeyVersion call(final KMSClientProvider provider) throws IOException, NoSuchAlgorithmException {
                    return provider.rollNewVersion(name);
                }
            }, this.nextIdx(), false);
            this.invalidateCache(name);
            return newVersion;
        }
        catch (WrapperException e) {
            if (e.getCause() instanceof GeneralSecurityException) {
                throw (NoSuchAlgorithmException)e.getCause();
            }
            throw new IOException(e.getCause());
        }
    }
    
    @Override
    public void close() throws IOException {
        for (final KMSClientProvider provider : this.providers) {
            try {
                provider.close();
            }
            catch (IOException ioe) {
                LoadBalancingKMSClientProvider.LOG.error("Error closing provider with url[" + provider.getKMSUrl() + "]");
            }
        }
    }
    
    @Override
    public void flush() throws IOException {
        for (final KMSClientProvider provider : this.providers) {
            try {
                provider.flush();
            }
            catch (IOException ioe) {
                LoadBalancingKMSClientProvider.LOG.error("Error flushing provider with url[" + provider.getKMSUrl() + "]");
            }
        }
    }
    
    private static KMSClientProvider[] shuffle(final KMSClientProvider[] providers) {
        final List<KMSClientProvider> list = Arrays.asList(providers);
        Collections.shuffle(list);
        return list.toArray(providers);
    }
    
    static {
        LoadBalancingKMSClientProvider.LOG = LoggerFactory.getLogger(LoadBalancingKMSClientProvider.class);
    }
    
    static class WrapperException extends RuntimeException
    {
        public WrapperException(final Throwable cause) {
            super(cause);
        }
    }
    
    interface ProviderCallable<T>
    {
        T call(final KMSClientProvider p0) throws IOException, Exception;
    }
}
