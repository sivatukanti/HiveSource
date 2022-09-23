// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security.client;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Unstable
@InterfaceAudience.Private
public enum TimelineDelegationTokenOperation
{
    GETDELEGATIONTOKEN("GET", true), 
    RENEWDELEGATIONTOKEN("PUT", true), 
    CANCELDELEGATIONTOKEN("PUT", true);
    
    private String httpMethod;
    private boolean requiresKerberosCredentials;
    
    private TimelineDelegationTokenOperation(final String httpMethod, final boolean requiresKerberosCredentials) {
        this.httpMethod = httpMethod;
        this.requiresKerberosCredentials = requiresKerberosCredentials;
    }
    
    public String getHttpMethod() {
        return this.httpMethod;
    }
    
    public boolean requiresKerberosCredentials() {
        return this.requiresKerberosCredentials;
    }
}
