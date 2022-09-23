// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.math.BigInteger;
import java.util.Vector;

public class KeyPairPKCS8 extends KeyPair
{
    private static final byte[] rsaEncryption;
    private static final byte[] dsaEncryption;
    private static final byte[] pbes2;
    private static final byte[] pbkdf2;
    private static final byte[] aes128cbc;
    private static final byte[] aes192cbc;
    private static final byte[] aes256cbc;
    private static final byte[] pbeWithMD5AndDESCBC;
    private KeyPair kpair;
    private static final byte[] begin;
    private static final byte[] end;
    
    public KeyPairPKCS8(final JSch jsch) {
        super(jsch);
        this.kpair = null;
    }
    
    @Override
    void generate(final int key_size) throws JSchException {
    }
    
    @Override
    byte[] getBegin() {
        return KeyPairPKCS8.begin;
    }
    
    @Override
    byte[] getEnd() {
        return KeyPairPKCS8.end;
    }
    
    @Override
    byte[] getPrivateKey() {
        return null;
    }
    
    @Override
    boolean parse(byte[] plain) {
        try {
            final Vector values = new Vector();
            ASN1[] contents = null;
            ASN1 asn1 = new ASN1(plain);
            contents = asn1.getContents();
            final ASN1 privateKeyAlgorithm = contents[1];
            final ASN1 privateKey = contents[2];
            contents = privateKeyAlgorithm.getContents();
            final byte[] privateKeyAlgorithmID = contents[0].getContent();
            contents = contents[1].getContents();
            if (contents.length > 0) {
                for (int i = 0; i < contents.length; ++i) {
                    values.addElement(contents[i].getContent());
                }
            }
            final byte[] _data = privateKey.getContent();
            KeyPair _kpair = null;
            if (Util.array_equals(privateKeyAlgorithmID, KeyPairPKCS8.rsaEncryption)) {
                _kpair = new KeyPairRSA(this.jsch);
                _kpair.copy(this);
                if (_kpair.parse(_data)) {
                    this.kpair = _kpair;
                }
            }
            else if (Util.array_equals(privateKeyAlgorithmID, KeyPairPKCS8.dsaEncryption)) {
                asn1 = new ASN1(_data);
                if (values.size() == 0) {
                    contents = asn1.getContents();
                    final byte[] bar = contents[1].getContent();
                    contents = contents[0].getContents();
                    for (int j = 0; j < contents.length; ++j) {
                        values.addElement(contents[j].getContent());
                    }
                    values.addElement(bar);
                }
                else {
                    values.addElement(asn1.getContent());
                }
                final byte[] P_array = values.elementAt(0);
                final byte[] Q_array = values.elementAt(1);
                final byte[] G_array = values.elementAt(2);
                final byte[] prv_array = values.elementAt(3);
                final byte[] pub_array = new BigInteger(G_array).modPow(new BigInteger(prv_array), new BigInteger(P_array)).toByteArray();
                final KeyPairDSA _key = new KeyPairDSA(this.jsch, P_array, Q_array, G_array, pub_array, prv_array);
                plain = _key.getPrivateKey();
                _kpair = new KeyPairDSA(this.jsch);
                _kpair.copy(this);
                if (_kpair.parse(plain)) {
                    this.kpair = _kpair;
                }
            }
        }
        catch (ASN1Exception e) {
            return false;
        }
        catch (Exception e2) {
            return false;
        }
        return this.kpair != null;
    }
    
    @Override
    public byte[] getPublicKeyBlob() {
        return this.kpair.getPublicKeyBlob();
    }
    
    @Override
    byte[] getKeyTypeName() {
        return this.kpair.getKeyTypeName();
    }
    
    @Override
    public int getKeyType() {
        return this.kpair.getKeyType();
    }
    
    public int getKeySize() {
        return this.kpair.getKeySize();
    }
    
    @Override
    public byte[] getSignature(final byte[] data) {
        return this.kpair.getSignature(data);
    }
    
    @Override
    public Signature getVerifier() {
        return this.kpair.getVerifier();
    }
    
    @Override
    public byte[] forSSHAgent() throws JSchException {
        return this.kpair.forSSHAgent();
    }
    
    @Override
    public boolean decrypt(final byte[] _passphrase) {
        if (!this.isEncrypted()) {
            return true;
        }
        if (_passphrase == null) {
            return !this.isEncrypted();
        }
        try {
            ASN1[] contents = null;
            final ASN1 asn1 = new ASN1(this.data);
            contents = asn1.getContents();
            final byte[] _data = contents[1].getContent();
            final ASN1 pbes = contents[0];
            contents = pbes.getContents();
            final byte[] pbesid = contents[0].getContent();
            final ASN1 pbesparam = contents[1];
            byte[] salt = null;
            int iterations = 0;
            byte[] iv = null;
            byte[] encryptfuncid = null;
            if (!Util.array_equals(pbesid, KeyPairPKCS8.pbes2)) {
                return Util.array_equals(pbesid, KeyPairPKCS8.pbeWithMD5AndDESCBC) && false;
            }
            contents = pbesparam.getContents();
            final ASN1 pbkdf = contents[0];
            final ASN1 encryptfunc = contents[1];
            contents = pbkdf.getContents();
            final byte[] pbkdfid = contents[0].getContent();
            final ASN1 pbkdffunc = contents[1];
            contents = pbkdffunc.getContents();
            salt = contents[0].getContent();
            iterations = Integer.parseInt(new BigInteger(contents[1].getContent()).toString());
            contents = encryptfunc.getContents();
            encryptfuncid = contents[0].getContent();
            iv = contents[1].getContent();
            final Cipher cipher = this.getCipher(encryptfuncid);
            if (cipher == null) {
                return false;
            }
            byte[] key = null;
            try {
                final JSch jsch = this.jsch;
                final Class c = Class.forName(JSch.getConfig("pbkdf"));
                final PBKDF tmp = c.newInstance();
                key = tmp.getKey(_passphrase, salt, iterations, cipher.getBlockSize());
            }
            catch (Exception ex) {}
            if (key == null) {
                return false;
            }
            cipher.init(1, key, iv);
            Util.bzero(key);
            final byte[] plain = new byte[_data.length];
            cipher.update(_data, 0, _data.length, plain, 0);
            if (this.parse(plain)) {
                this.encrypted = false;
                return true;
            }
        }
        catch (ASN1Exception e) {}
        catch (Exception ex2) {}
        return false;
    }
    
    Cipher getCipher(final byte[] id) {
        Cipher cipher = null;
        String name = null;
        try {
            if (Util.array_equals(id, KeyPairPKCS8.aes128cbc)) {
                name = "aes128-cbc";
            }
            else if (Util.array_equals(id, KeyPairPKCS8.aes192cbc)) {
                name = "aes192-cbc";
            }
            else if (Util.array_equals(id, KeyPairPKCS8.aes256cbc)) {
                name = "aes256-cbc";
            }
            final JSch jsch = this.jsch;
            final Class c = Class.forName(JSch.getConfig(name));
            cipher = c.newInstance();
        }
        catch (Exception e) {
            if (JSch.getLogger().isEnabled(4)) {
                String message = "";
                if (name == null) {
                    message = "unknown oid: " + Util.toHex(id);
                }
                else {
                    message = "function " + name + " is not supported";
                }
                JSch.getLogger().log(4, "PKCS8: " + message);
            }
        }
        return cipher;
    }
    
    static {
        rsaEncryption = new byte[] { 42, -122, 72, -122, -9, 13, 1, 1, 1 };
        dsaEncryption = new byte[] { 42, -122, 72, -50, 56, 4, 1 };
        pbes2 = new byte[] { 42, -122, 72, -122, -9, 13, 1, 5, 13 };
        pbkdf2 = new byte[] { 42, -122, 72, -122, -9, 13, 1, 5, 12 };
        aes128cbc = new byte[] { 96, -122, 72, 1, 101, 3, 4, 1, 2 };
        aes192cbc = new byte[] { 96, -122, 72, 1, 101, 3, 4, 1, 22 };
        aes256cbc = new byte[] { 96, -122, 72, 1, 101, 3, 4, 1, 42 };
        pbeWithMD5AndDESCBC = new byte[] { 42, -122, 72, -122, -9, 13, 1, 5, 3 };
        begin = Util.str2byte("-----BEGIN DSA PRIVATE KEY-----");
        end = Util.str2byte("-----END DSA PRIVATE KEY-----");
    }
}
