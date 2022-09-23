// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import java.util.Collection;
import com.nimbusds.jose.Header;
import java.util.Collections;
import java.util.Set;

class CriticalHeaderParamsDeferral
{
    private Set<String> deferredParams;
    
    CriticalHeaderParamsDeferral() {
        this.deferredParams = Collections.emptySet();
    }
    
    public Set<String> getProcessedCriticalHeaderParams() {
        return Collections.emptySet();
    }
    
    public Set<String> getDeferredCriticalHeaderParams() {
        return Collections.unmodifiableSet((Set<? extends String>)this.deferredParams);
    }
    
    public void setDeferredCriticalHeaderParams(final Set<String> defCritHeaders) {
        if (defCritHeaders == null) {
            this.deferredParams = Collections.emptySet();
        }
        else {
            this.deferredParams = defCritHeaders;
        }
    }
    
    public boolean headerPasses(final Header header) {
        final Set<String> crit = header.getCriticalParams();
        return crit == null || crit.isEmpty() || (this.deferredParams != null && this.deferredParams.containsAll(crit));
    }
    
    public void ensureHeaderPasses(final JWEHeader header) throws JOSEException {
        if (!this.headerPasses(header)) {
            throw new JOSEException("Unsupported critical header parameter(s)");
        }
    }
}
