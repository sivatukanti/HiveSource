// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import java.util.Set;

abstract class ECDHCryptoProvider extends BaseJWEProvider
{
    public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
    public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS;
    public static final Set<ECKey.Curve> SUPPORTED_ELLIPTIC_CURVES;
    private final ECKey.Curve curve;
    private final ConcatKDF concatKDF;
    
    static {
        SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;
        final Set<JWEAlgorithm> algs = new LinkedHashSet<JWEAlgorithm>();
        algs.add(JWEAlgorithm.ECDH_ES);
        algs.add(JWEAlgorithm.ECDH_ES_A128KW);
        algs.add(JWEAlgorithm.ECDH_ES_A192KW);
        algs.add(JWEAlgorithm.ECDH_ES_A256KW);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet((Set<? extends JWEAlgorithm>)algs);
        final Set<ECKey.Curve> curves = new LinkedHashSet<ECKey.Curve>();
        curves.add(ECKey.Curve.P_256);
        curves.add(ECKey.Curve.P_384);
        curves.add(ECKey.Curve.P_521);
        SUPPORTED_ELLIPTIC_CURVES = Collections.unmodifiableSet((Set<? extends ECKey.Curve>)curves);
    }
    
    protected ECDHCryptoProvider(final ECKey.Curve curve) throws JOSEException {
        super(ECDHCryptoProvider.SUPPORTED_ALGORITHMS, ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS);
        final ECKey.Curve definedCurve = (curve != null) ? curve : new ECKey.Curve("unknown");
        if (!ECDHCryptoProvider.SUPPORTED_ELLIPTIC_CURVES.contains(curve)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedEllipticCurve(definedCurve, ECDHCryptoProvider.SUPPORTED_ELLIPTIC_CURVES));
        }
        this.curve = curve;
        this.concatKDF = new ConcatKDF("SHA-256");
    }
    
    protected ConcatKDF getConcatKDF() {
        return this.concatKDF;
    }
    
    public Set<ECKey.Curve> supportedEllipticCurves() {
        return ECDHCryptoProvider.SUPPORTED_ELLIPTIC_CURVES;
    }
    
    public ECKey.Curve getCurve() {
        return this.curve;
    }
}
