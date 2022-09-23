// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.AlgorithmParameters;
import java.security.Provider;

class AlgorithmParametersHelper
{
    public static AlgorithmParameters getInstance(final String name, final Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            return AlgorithmParameters.getInstance(name);
        }
        return AlgorithmParameters.getInstance(name, provider);
    }
}
