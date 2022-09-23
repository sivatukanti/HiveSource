// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.util.Set;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.jca.JCAAware;

public interface JWSProvider extends JOSEProvider, JCAAware<JCAContext>
{
    Set<JWSAlgorithm> supportedJWSAlgorithms();
}
