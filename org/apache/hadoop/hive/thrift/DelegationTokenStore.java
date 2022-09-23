// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift;

import java.util.List;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import java.io.Closeable;
import org.apache.hadoop.conf.Configurable;

public interface DelegationTokenStore extends Configurable, Closeable
{
    int addMasterKey(final String p0) throws TokenStoreException;
    
    void updateMasterKey(final int p0, final String p1) throws TokenStoreException;
    
    boolean removeMasterKey(final int p0);
    
    String[] getMasterKeys() throws TokenStoreException;
    
    boolean addToken(final DelegationTokenIdentifier p0, final AbstractDelegationTokenSecretManager.DelegationTokenInformation p1) throws TokenStoreException;
    
    AbstractDelegationTokenSecretManager.DelegationTokenInformation getToken(final DelegationTokenIdentifier p0) throws TokenStoreException;
    
    boolean removeToken(final DelegationTokenIdentifier p0) throws TokenStoreException;
    
    List<DelegationTokenIdentifier> getAllDelegationTokenIdentifiers() throws TokenStoreException;
    
    void init(final Object p0, final HadoopThriftAuthBridge.Server.ServerMode p1);
    
    public static class TokenStoreException extends RuntimeException
    {
        private static final long serialVersionUID = -8693819817623074083L;
        
        public TokenStoreException(final Throwable cause) {
            super(cause);
        }
        
        public TokenStoreException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
