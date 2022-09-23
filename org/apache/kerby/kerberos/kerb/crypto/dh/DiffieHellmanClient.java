// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.dh;

import org.apache.kerby.kerberos.kerb.crypto.EncTypeHandler;
import org.apache.kerby.kerberos.kerb.crypto.EncryptionHandler;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.security.KeyPairGenerator;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import javax.crypto.KeyAgreement;

public class DiffieHellmanClient
{
    private KeyAgreement clientKeyAgree;
    private EncryptionKey clientKey;
    private DHParameterSpec dhParameterSpec;
    
    public DHParameterSpec getDhParam() {
        return this.dhParameterSpec;
    }
    
    public DHPublicKey init(final DHParameterSpec dhParamSpec) throws Exception {
        this.dhParameterSpec = dhParamSpec;
        final KeyPairGenerator clientKpairGen = KeyPairGenerator.getInstance("DH");
        clientKpairGen.initialize(dhParamSpec);
        final KeyPair clientKpair = clientKpairGen.generateKeyPair();
        (this.clientKeyAgree = KeyAgreement.getInstance("DH")).init(clientKpair.getPrivate());
        return (DHPublicKey)clientKpair.getPublic();
    }
    
    public void doPhase(final byte[] serverPubKeyEnc) throws Exception {
        final KeyFactory clientKeyFac = KeyFactory.getInstance("DH");
        final X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc);
        final PublicKey serverPubKey = clientKeyFac.generatePublic(x509KeySpec);
        this.clientKeyAgree.doPhase(serverPubKey, true);
    }
    
    public EncryptionKey generateKey(final byte[] clientDhNonce, final byte[] serverDhNonce, final EncryptionType type) {
        byte[] x;
        final byte[] dhSharedSecret = x = this.clientKeyAgree.generateSecret();
        if (clientDhNonce != null && clientDhNonce.length > 0 && serverDhNonce != null && serverDhNonce.length > 0) {
            x = this.concatenateBytes(dhSharedSecret, clientDhNonce);
            x = this.concatenateBytes(x, serverDhNonce);
        }
        final byte[] secret = OctetString2Key.kTruncate(dhSharedSecret.length, x);
        return this.clientKey = new EncryptionKey(type, secret);
    }
    
    public byte[] decrypt(final byte[] cipherText, final KeyUsage usage) throws Exception {
        final EncTypeHandler encType = EncryptionHandler.getEncHandler(this.clientKey.getKeyType());
        return encType.decrypt(cipherText, this.clientKey.getKeyData(), usage.getValue());
    }
    
    private byte[] concatenateBytes(final byte[] array1, final byte[] array2) {
        final byte[] concatenatedBytes = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, concatenatedBytes, 0, array1.length);
        for (int j = array1.length; j < concatenatedBytes.length; ++j) {
            concatenatedBytes[j] = array2[j - array1.length];
        }
        return concatenatedBytes;
    }
}
