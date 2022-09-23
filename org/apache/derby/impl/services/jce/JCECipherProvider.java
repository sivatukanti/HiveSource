// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.jce;

import java.security.GeneralSecurityException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import org.apache.derby.iapi.error.StandardException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;
import org.apache.derby.iapi.services.crypto.CipherProvider;

class JCECipherProvider implements CipherProvider
{
    private Cipher cipher;
    private int mode;
    private boolean ivUsed;
    private final IvParameterSpec ivspec;
    private final int encryptionBlockSize;
    private boolean sunjce;
    private SecretKey cryptixKey;
    
    JCECipherProvider(final int mode, SecretKey translateKey, final byte[] iv, final String s, String s2) throws StandardException {
        this.ivUsed = true;
        this.ivspec = new IvParameterSpec(iv);
        InvalidKeyException ex2;
        try {
            if (s2 == null) {
                this.cipher = Cipher.getInstance(s);
                if ("SunJCE".equals(this.cipher.getProvider().getName())) {
                    this.sunjce = true;
                }
            }
            else {
                if (s2.equals("SunJCE")) {
                    this.sunjce = true;
                }
                else if (s2.equals("BouncyCastleProvider")) {
                    s2 = "BC";
                }
                this.cipher = Cipher.getInstance(s, s2);
            }
            this.encryptionBlockSize = this.cipher.getBlockSize();
            this.mode = mode;
            try {
                if (mode == 1) {
                    if (s.indexOf("/ECB") > -1) {
                        this.cipher.init(1, translateKey);
                    }
                    else {
                        this.cipher.init(1, translateKey, this.ivspec);
                    }
                }
                else {
                    if (mode != 2) {
                        throw StandardException.newException("XBCX1.S");
                    }
                    if (s.indexOf("/ECB") > -1) {
                        this.cipher.init(2, translateKey);
                    }
                    else {
                        this.cipher.init(2, translateKey, this.ivspec);
                    }
                }
            }
            catch (InvalidKeyException ex) {
                if (!s.startsWith("DES")) {
                    throw StandardException.newException("XBCX0.S", ex);
                }
                SecretKeyFactory secretKeyFactory;
                if (s2 == null) {
                    secretKeyFactory = SecretKeyFactory.getInstance(translateKey.getAlgorithm());
                }
                else {
                    secretKeyFactory = SecretKeyFactory.getInstance(translateKey.getAlgorithm(), s2);
                }
                translateKey = secretKeyFactory.translateKey(new SecretKeySpec(translateKey.getEncoded(), translateKey.getAlgorithm()));
                if (mode == 1) {
                    if (s.indexOf("/ECB") > -1) {
                        this.cipher.init(1, translateKey);
                    }
                    else {
                        this.cipher.init(1, translateKey, this.ivspec);
                    }
                }
                else if (mode == 2) {
                    if (s.indexOf("/ECB") > -1) {
                        this.cipher.init(2, translateKey);
                    }
                    else {
                        this.cipher.init(2, translateKey, this.ivspec);
                    }
                }
            }
            this.cryptixKey = translateKey;
            if (this.cipher.getIV() == null) {
                this.ivUsed = false;
            }
            return;
        }
        catch (InvalidKeyException ex3) {
            ex2 = ex3;
        }
        catch (NoSuchAlgorithmException ex5) {
            throw StandardException.newException("XBCXC.S", s, JCECipherFactory.providerErrorName(s2));
        }
        catch (NoSuchProviderException ex6) {
            throw StandardException.newException("XBCXG.S", JCECipherFactory.providerErrorName(s2));
        }
        catch (GeneralSecurityException ex4) {
            ex2 = (InvalidKeyException)ex4;
        }
        throw StandardException.newException("XBCX0.S", ex2);
    }
    
    public int encrypt(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws StandardException {
        int doFinal = 0;
        try {
            synchronized (this) {
                if (!this.sunjce) {
                    try {
                        if (this.mode == 1) {
                            if (this.ivUsed) {
                                this.cipher.init(1, this.cryptixKey, this.ivspec);
                            }
                            else {
                                this.cipher.init(1, this.cryptixKey);
                            }
                        }
                        else if (this.mode == 2) {
                            if (this.ivUsed) {
                                this.cipher.init(2, this.cryptixKey, this.ivspec);
                            }
                            else {
                                this.cipher.init(2, this.cryptixKey);
                            }
                        }
                    }
                    catch (InvalidKeyException obj) {
                        System.out.println("A " + obj);
                        throw StandardException.newException("XBCX0.S", obj);
                    }
                }
                doFinal = this.cipher.doFinal(input, inputOffset, inputLen, output, outputOffset);
            }
        }
        catch (IllegalStateException ex) {}
        catch (GeneralSecurityException obj2) {
            System.out.println("B " + obj2);
            throw StandardException.newException("XBCX0.S", obj2);
        }
        return doFinal;
    }
    
    public int decrypt(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws StandardException {
        int doFinal = 0;
        try {
            synchronized (this) {
                if (!this.sunjce) {
                    try {
                        if (this.mode == 1) {
                            if (this.ivUsed) {
                                this.cipher.init(1, this.cryptixKey, this.ivspec);
                            }
                            else {
                                this.cipher.init(1, this.cryptixKey);
                            }
                        }
                        else if (this.mode == 2) {
                            if (this.ivUsed) {
                                this.cipher.init(2, this.cryptixKey, this.ivspec);
                            }
                            else {
                                this.cipher.init(2, this.cryptixKey);
                            }
                        }
                    }
                    catch (InvalidKeyException obj) {
                        System.out.println("C " + obj);
                        throw StandardException.newException("XBCX0.S", obj);
                    }
                }
                doFinal = this.cipher.doFinal(input, inputOffset, inputLen, output, outputOffset);
            }
        }
        catch (IllegalStateException ex) {}
        catch (GeneralSecurityException obj2) {
            System.out.println("D " + obj2);
            throw StandardException.newException("XBCX0.S", obj2);
        }
        return doFinal;
    }
    
    boolean verifyIV(final byte[] array) {
        final byte[] iv = this.cipher.getIV();
        if (iv == null) {
            return !this.ivUsed;
        }
        if (iv.length != array.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (iv[i] != array[i]) {
                return false;
            }
        }
        return true;
    }
    
    public int getEncryptionBlockSize() {
        return this.encryptionBlockSize;
    }
}
