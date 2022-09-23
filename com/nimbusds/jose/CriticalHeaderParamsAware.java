// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.util.Set;

public interface CriticalHeaderParamsAware
{
    Set<String> getProcessedCriticalHeaderParams();
    
    Set<String> getDeferredCriticalHeaderParams();
}
