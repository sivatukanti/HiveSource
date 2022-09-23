// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth.pkinit;

import org.apache.kerby.kerberos.kerb.type.pa.pkinit.TrustedCertifiers;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.x509.type.AlgorithmIdentifier;
import org.apache.kerby.kerberos.kerb.type.pa.pkinit.AlgorithmIdentifiers;

public class PluginOpts
{
    public boolean requireEku;
    public boolean acceptSecondaryEku;
    public boolean allowUpn;
    public boolean usingRsa;
    public boolean requireCrlChecking;
    public int dhMinBits;
    
    public PluginOpts() {
        this.requireEku = true;
        this.acceptSecondaryEku = false;
        this.allowUpn = true;
        this.usingRsa = false;
        this.requireCrlChecking = false;
        this.dhMinBits = 1024;
    }
    
    public AlgorithmIdentifiers createSupportedCMSTypes() throws KrbException {
        final AlgorithmIdentifiers cmsAlgorithms = new AlgorithmIdentifiers();
        final AlgorithmIdentifier des3Alg = new AlgorithmIdentifier();
        final String content = "0x06 08 2A 86 48 86 F7 0D 03 07";
        final Asn1ObjectIdentifier des3Oid = PkinitCrypto.createOid(content);
        des3Alg.setAlgorithm(des3Oid.getValue());
        cmsAlgorithms.add(des3Alg);
        return cmsAlgorithms;
    }
    
    public TrustedCertifiers createTrustedCertifiers() {
        final TrustedCertifiers trustedCertifiers = new TrustedCertifiers();
        return trustedCertifiers;
    }
    
    public byte[] createIssuerAndSerial() {
        return null;
    }
}
