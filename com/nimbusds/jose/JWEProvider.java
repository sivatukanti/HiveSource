// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.util.Set;
import com.nimbusds.jose.jca.JWEJCAContext;
import com.nimbusds.jose.jca.JCAAware;

public interface JWEProvider extends JOSEProvider, JCAAware<JWEJCAContext>
{
    Set<JWEAlgorithm> supportedJWEAlgorithms();
    
    Set<EncryptionMethod> supportedEncryptionMethods();
}
