// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.dh;

import org.apache.kerby.kerberos.kerb.crypto.EncTypeHandler;
import org.apache.kerby.kerberos.kerb.crypto.EncryptionHandler;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.security.KeyPair;
import javax.crypto.spec.DHParameterSpec;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.security.KeyPairGenerator;
import javax.crypto.interfaces.DHPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import javax.crypto.KeyAgreement;

public class DiffieHellmanServer
{
    private KeyAgreement serverKeyAgree;
    private EncryptionKey serverKey;
    
    public PublicKey initAndDoPhase(final byte[] clientPubKeyEnc) throws Exception {
        final KeyFactory serverKeyFac = KeyFactory.getInstance("DH");
        final X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(clientPubKeyEnc);
        final PublicKey clientPubKey = serverKeyFac.generatePublic(x509KeySpec);
        final DHParameterSpec dhParamSpec = ((DHPublicKey)clientPubKey).getParams();
        final KeyPairGenerator serverKpairGen = KeyPairGenerator.getInstance("DH");
        serverKpairGen.initialize(dhParamSpec);
        final KeyPair serverKpair = serverKpairGen.generateKeyPair();
        (this.serverKeyAgree = KeyAgreement.getInstance("DH")).init(serverKpair.getPrivate());
        this.serverKeyAgree.doPhase(clientPubKey, true);
        return serverKpair.getPublic();
    }
    
    public EncryptionKey generateKey(final byte[] clientDhNonce, final byte[] serverDhNonce, final EncryptionType type) {
        byte[] x;
        final byte[] dhSharedSecret = x = this.serverKeyAgree.generateSecret();
        if (clientDhNonce != null && clientDhNonce.length > 0 && serverDhNonce != null && serverDhNonce.length > 0) {
            x = this.concatenateBytes(dhSharedSecret, clientDhNonce);
            x = this.concatenateBytes(x, serverDhNonce);
        }
        final byte[] secret = OctetString2Key.kTruncate(dhSharedSecret.length, x);
        return this.serverKey = new EncryptionKey(type, secret);
    }
    
    public byte[] encrypt(final byte[] clearText, final KeyUsage usage) throws Exception {
        final EncTypeHandler encType = EncryptionHandler.getEncHandler(this.serverKey.getKeyType());
        return encType.encrypt(clearText, this.serverKey.getKeyData(), usage.getValue());
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
