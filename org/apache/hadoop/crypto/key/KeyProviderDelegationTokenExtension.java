// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key;

import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.Credentials;
import java.io.IOException;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.DelegationTokenIssuer;

public class KeyProviderDelegationTokenExtension extends KeyProviderExtension<DelegationTokenExtension> implements DelegationTokenIssuer
{
    private static DelegationTokenExtension DEFAULT_EXTENSION;
    
    private KeyProviderDelegationTokenExtension(final KeyProvider keyProvider, final DelegationTokenExtension extensions) {
        super(keyProvider, extensions);
    }
    
    @Override
    public String getCanonicalServiceName() {
        return this.getExtension().getCanonicalServiceName();
    }
    
    @Override
    public Token<?> getDelegationToken(final String renewer) throws IOException {
        return this.getExtension().getDelegationToken(renewer);
    }
    
    public static KeyProviderDelegationTokenExtension createKeyProviderDelegationTokenExtension(final KeyProvider keyProvider) {
        final DelegationTokenExtension delTokExtension = (keyProvider instanceof DelegationTokenExtension) ? keyProvider : KeyProviderDelegationTokenExtension.DEFAULT_EXTENSION;
        return new KeyProviderDelegationTokenExtension(keyProvider, delTokExtension);
    }
    
    static {
        KeyProviderDelegationTokenExtension.DEFAULT_EXTENSION = new DefaultDelegationTokenExtension();
    }
    
    private static class DefaultDelegationTokenExtension implements DelegationTokenExtension
    {
        @Override
        public Token<?>[] addDelegationTokens(final String renewer, final Credentials credentials) {
            return null;
        }
        
        @Override
        public String getCanonicalServiceName() {
            return null;
        }
        
        @Override
        public Token<?> getDelegationToken(final String renewer) {
            return null;
        }
        
        @Override
        public long renewDelegationToken(final Token<?> token) throws IOException {
            return 0L;
        }
        
        @Override
        public Void cancelDelegationToken(final Token<?> token) throws IOException {
            return null;
        }
        
        @Override
        public Token<?> selectDelegationToken(final Credentials creds) {
            return null;
        }
    }
    
    public interface DelegationTokenExtension extends Extension, DelegationTokenIssuer
    {
        long renewDelegationToken(final Token<?> p0) throws IOException;
        
        Void cancelDelegationToken(final Token<?> p0) throws IOException;
        
        @InterfaceAudience.Private
        @InterfaceStability.Unstable
        @VisibleForTesting
        Token<?> selectDelegationToken(final Credentials p0);
    }
}
