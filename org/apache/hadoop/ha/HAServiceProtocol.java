// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.apache.hadoop.io.retry.Idempotent;
import java.io.IOException;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.KerberosInfo;

@KerberosInfo(serverPrincipal = "hadoop.security.service.user.name.key")
@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface HAServiceProtocol
{
    public static final long versionID = 1L;
    
    @Idempotent
    void monitorHealth() throws HealthCheckFailedException, AccessControlException, IOException;
    
    @Idempotent
    void transitionToActive(final StateChangeRequestInfo p0) throws ServiceFailedException, AccessControlException, IOException;
    
    @Idempotent
    void transitionToStandby(final StateChangeRequestInfo p0) throws ServiceFailedException, AccessControlException, IOException;
    
    @Idempotent
    HAServiceStatus getServiceStatus() throws AccessControlException, IOException;
    
    public enum HAServiceState
    {
        INITIALIZING("initializing"), 
        ACTIVE("active"), 
        STANDBY("standby"), 
        STOPPING("stopping");
        
        private String name;
        
        private HAServiceState(final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    public enum RequestSource
    {
        REQUEST_BY_USER, 
        REQUEST_BY_USER_FORCED, 
        REQUEST_BY_ZKFC;
    }
    
    public static class StateChangeRequestInfo
    {
        private final RequestSource source;
        
        public StateChangeRequestInfo(final RequestSource source) {
            this.source = source;
        }
        
        public RequestSource getSource() {
            return this.source;
        }
    }
}
