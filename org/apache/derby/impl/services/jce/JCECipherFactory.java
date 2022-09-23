// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.jce;

import org.apache.derby.io.StorageRandomAccessFile;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.IOException;
import java.io.DataInputStream;
import org.apache.derby.io.StorageFactory;
import java.io.FileNotFoundException;
import java.security.Provider;
import java.util.Enumeration;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.Security;
import java.security.SecureRandom;
import java.security.InvalidKeyException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.DESKeySpec;
import org.apache.derby.iapi.services.crypto.CipherProvider;
import org.apache.derby.iapi.util.StringUtil;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.io.StorageFile;
import java.util.Properties;
import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.security.PrivilegedExceptionAction;
import org.apache.derby.iapi.services.crypto.CipherFactory;

public final class JCECipherFactory implements CipherFactory, PrivilegedExceptionAction
{
    private static final String MESSAGE_DIGEST = "MD5";
    private static final String DEFAULT_ALGORITHM = "DES/CBC/NoPadding";
    private static final String DES = "DES";
    private static final String DESede = "DESede";
    private static final String TripleDES = "TripleDES";
    private static final String AES = "AES";
    private static final int BLOCK_LENGTH = 8;
    private static final int AES_IV_LENGTH = 16;
    private int keyLengthBits;
    private int encodedKeyLength;
    private String cryptoAlgorithm;
    private String cryptoAlgorithmShort;
    private String cryptoProvider;
    private String cryptoProviderShort;
    private MessageDigest messageDigest;
    private SecretKey mainSecretKey;
    private byte[] mainIV;
    private Properties persistentProperties;
    private static final int VERIFYKEY_DATALEN = 4096;
    private StorageFile activeFile;
    private int action;
    private String activePerms;
    
    public JCECipherFactory(final boolean b, final Properties properties, final boolean b2) throws StandardException {
        this.init(b, properties, b2);
    }
    
    static String providerErrorName(final String s) {
        return (s == null) ? "default" : s;
    }
    
    private byte[] generateUniqueBytes() throws StandardException {
        try {
            String cryptoProviderShort = this.cryptoProviderShort;
            KeyGenerator keyGenerator;
            if (cryptoProviderShort == null) {
                keyGenerator = KeyGenerator.getInstance(this.cryptoAlgorithmShort);
            }
            else {
                if (cryptoProviderShort.equals("BouncyCastleProvider")) {
                    cryptoProviderShort = "BC";
                }
                keyGenerator = KeyGenerator.getInstance(this.cryptoAlgorithmShort, cryptoProviderShort);
            }
            keyGenerator.init(this.keyLengthBits);
            return keyGenerator.generateKey().getEncoded();
        }
        catch (NoSuchAlgorithmException ex) {
            throw StandardException.newException("XBCXC.S", this.cryptoAlgorithm, providerErrorName(this.cryptoProviderShort));
        }
        catch (NoSuchProviderException ex2) {
            throw StandardException.newException("XBCXG.S", providerErrorName(this.cryptoProviderShort));
        }
    }
    
    private EncryptedKeyResult encryptKey(byte[] padKey, final byte[] array) throws StandardException {
        int length = padKey.length;
        if (this.cryptoAlgorithmShort.equals("AES")) {
            length = 16;
        }
        final byte[] muckFromBootPassword = this.getMuckFromBootPassword(array, length);
        final CipherProvider newCipher = this.createNewCipher(1, this.generateKey(muckFromBootPassword), this.generateIV(muckFromBootPassword));
        this.encodedKeyLength = padKey.length;
        padKey = this.padKey(padKey, newCipher.getEncryptionBlockSize());
        final byte[] array2 = new byte[padKey.length];
        newCipher.encrypt(padKey, 0, padKey.length, array2, 0);
        return new EncryptedKeyResult(StringUtil.toHexString(array2, 0, array2.length), padKey);
    }
    
    private byte[] padKey(final byte[] array, final int n) {
        byte[] array2 = array;
        if (array.length % n != 0) {
            array2 = new byte[array.length + n - array.length % n];
            System.arraycopy(array, 0, array2, 0, array.length);
        }
        return array2;
    }
    
    private byte[] decryptKey(final String s, final int n, final byte[] array) throws StandardException {
        final byte[] fromHexString = StringUtil.fromHexString(s, 0, n);
        int length;
        if (this.cryptoAlgorithmShort.equals("AES")) {
            length = 16;
        }
        else {
            length = fromHexString.length;
        }
        final byte[] muckFromBootPassword = this.getMuckFromBootPassword(array, length);
        this.createNewCipher(2, this.generateKey(muckFromBootPassword), this.generateIV(muckFromBootPassword)).decrypt(fromHexString, 0, fromHexString.length, fromHexString, 0);
        return fromHexString;
    }
    
    private byte[] getMuckFromBootPassword(final byte[] array, final int n) {
        final int length = array.length;
        final byte[] array2 = new byte[n];
        int n2 = 0;
        for (int i = 0; i < array.length; ++i) {
            n2 += array[i];
        }
        for (byte b = 0; b < n; ++b) {
            array2[b] = (byte)(array[(b + n2) % length] ^ array[b % length] << 4);
        }
        return array2;
    }
    
    private SecretKey generateKey(final byte[] array) throws StandardException {
        if (array.length < 8) {
            throw StandardException.newException("XBCX2.S", new Integer(8));
        }
        try {
            if (this.cryptoAlgorithmShort.equals("DES") && DESKeySpec.isWeak(array, 0)) {
                final byte[] asciiBytes = StringUtil.getAsciiBytes("louDScap");
                for (int i = 0; i < 7; ++i) {
                    array[i] ^= (byte)(asciiBytes[i] << 3);
                }
            }
            return new SecretKeySpec(array, this.cryptoAlgorithmShort);
        }
        catch (InvalidKeyException ex) {
            throw StandardException.newException("XBCX0.S", ex);
        }
    }
    
    private byte[] generateIV(final byte[] array) {
        byte[] array2;
        if (this.cryptoAlgorithmShort.equals("AES")) {
            array2 = new byte[16];
            array2[0] = (byte)((array[array.length - 1] << 2 | 0xF) ^ array[0]);
            for (int i = 1; i < 8; ++i) {
                array2[i] = (byte)((array[i - 1] << i % 5 | 0xF) ^ array[i]);
            }
            for (int j = 8; j < 16; ++j) {
                array2[j] = array2[j - 8];
            }
        }
        else {
            array2 = new byte[8];
            array2[0] = (byte)((array[array.length - 1] << 2 | 0xF) ^ array[0]);
            for (int k = 1; k < 8; ++k) {
                array2[k] = (byte)((array[k - 1] << k % 5 | 0xF) ^ array[k]);
            }
        }
        return array2;
    }
    
    private int digest(final byte[] input) {
        this.messageDigest.reset();
        final byte[] digest = this.messageDigest.digest(input);
        final byte[] array = new byte[2];
        for (int i = 0; i < digest.length; ++i) {
            final byte[] array2 = array;
            final int n = i % 2;
            array2[n] ^= digest[i];
        }
        return (array[0] & 0xFF) | (array[1] << 8 & 0xFF00);
    }
    
    public SecureRandom getSecureRandom() {
        return new SecureRandom(this.mainIV);
    }
    
    public CipherProvider createNewCipher(final int n) throws StandardException {
        return this.createNewCipher(n, this.mainSecretKey, this.mainIV);
    }
    
    private CipherProvider createNewCipher(final int n, final SecretKey secretKey, final byte[] array) throws StandardException {
        return new JCECipherProvider(n, secretKey, array, this.cryptoAlgorithm, this.cryptoProviderShort);
    }
    
    private void init(final boolean b, final Properties properties, final boolean b2) throws StandardException {
        boolean b3 = false;
        boolean b4 = b;
        this.persistentProperties = new Properties();
        final String property = properties.getProperty(b2 ? "newEncryptionKey" : "encryptionKey");
        if (property != null) {
            b4 = false;
        }
        this.cryptoProvider = properties.getProperty("encryptionProvider");
        if (this.cryptoProvider != null) {
            b3 = true;
            final int lastIndex = this.cryptoProvider.lastIndexOf(46);
            if (lastIndex == -1) {
                this.cryptoProviderShort = this.cryptoProvider;
            }
            else {
                this.cryptoProviderShort = this.cryptoProvider.substring(lastIndex + 1);
            }
        }
        this.cryptoAlgorithm = properties.getProperty("encryptionAlgorithm");
        if (this.cryptoAlgorithm == null) {
            this.cryptoAlgorithm = "DES/CBC/NoPadding";
        }
        else {
            b3 = true;
        }
        if (b4) {
            this.persistentProperties.put("encryptionAlgorithm", this.cryptoAlgorithm);
        }
        final int index = this.cryptoAlgorithm.indexOf(47);
        final int lastIndex2 = this.cryptoAlgorithm.lastIndexOf(47);
        if (index < 0 || lastIndex2 < 0 || index == lastIndex2) {
            throw StandardException.newException("XBCXH.S", this.cryptoAlgorithm);
        }
        this.cryptoAlgorithmShort = this.cryptoAlgorithm.substring(0, index);
        if (b3) {
            try {
                Class.forName("javax.crypto.ExemptionMechanism");
            }
            catch (Throwable t) {
                throw StandardException.newException("XBCXJ.S");
            }
        }
        if (!b && properties.getProperty("encryptionKeyLength") != null) {
            final String property2 = properties.getProperty("encryptionKeyLength");
            final int lastIndex3 = property2.lastIndexOf(45);
            this.encodedKeyLength = Integer.parseInt(property2.substring(lastIndex3 + 1));
            if (lastIndex3 != -1) {
                this.keyLengthBits = Integer.parseInt(property2.substring(0, lastIndex3));
            }
        }
        if (property == null && b) {
            if (properties.getProperty("encryptionKeyLength") != null) {
                this.keyLengthBits = Integer.parseInt(properties.getProperty("encryptionKeyLength"));
            }
            else if (this.cryptoAlgorithmShort.equals("DES")) {
                this.keyLengthBits = 56;
            }
            else if (this.cryptoAlgorithmShort.equals("DESede") || this.cryptoAlgorithmShort.equals("TripleDES")) {
                this.keyLengthBits = 168;
            }
            else {
                this.keyLengthBits = 128;
            }
        }
        final String substring = this.cryptoAlgorithm.substring(index + 1, lastIndex2);
        if (!substring.equals("CBC") && !substring.equals("CFB") && !substring.equals("ECB") && !substring.equals("OFB")) {
            throw StandardException.newException("XBCXI.S", substring);
        }
        final String substring2 = this.cryptoAlgorithm.substring(lastIndex2 + 1, this.cryptoAlgorithm.length());
        if (!substring2.equals("NoPadding")) {
            throw StandardException.newException("XBCXB.S", substring2);
        }
        Exception exception;
        try {
            if (this.cryptoProvider != null && Security.getProvider(this.cryptoProviderShort) == null) {
                this.action = 1;
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
            }
            this.messageDigest = MessageDigest.getInstance("MD5");
            byte[] array;
            if (property != null) {
                if (properties.getProperty(b2 ? "newBootPassword" : "bootPassword") != null) {
                    throw StandardException.newException("XBM06.D");
                }
                array = StringUtil.fromHexString(property, 0, property.length());
                if (array == null) {
                    throw StandardException.newException((property.length() % 2 == 0) ? "XBCXN.S" : "XBCXM.S");
                }
            }
            else {
                array = this.handleBootPassword(b, properties, b2);
                if (b || b2) {
                    this.persistentProperties.put("encryptionKeyLength", this.keyLengthBits + "-" + array.length);
                }
            }
            this.mainSecretKey = this.generateKey(array);
            this.mainIV = this.generateIV(array);
            if (b) {
                this.persistentProperties.put("dataEncryption", "true");
                this.persistentProperties.put("data_encrypt_algorithm_version", String.valueOf(1));
                this.persistentProperties.put("log_encrypt_algorithm_version", String.valueOf(1));
            }
            return;
        }
        catch (PrivilegedActionException ex) {
            exception = ex.getException();
        }
        catch (NoSuchAlgorithmException ex2) {
            exception = ex2;
        }
        catch (SecurityException ex3) {
            exception = ex3;
        }
        catch (LinkageError linkageError) {
            exception = (Exception)linkageError;
        }
        catch (ClassCastException ex4) {
            exception = ex4;
        }
        throw StandardException.newException("XBM0G.D", exception);
    }
    
    private byte[] handleBootPassword(final boolean b, final Properties properties, final boolean b2) throws StandardException {
        final String property = properties.getProperty(b2 ? "newBootPassword" : "bootPassword");
        if (property == null) {
            throw StandardException.newException("XBM06.D");
        }
        final byte[] asciiBytes = StringUtil.getAsciiBytes(property);
        if (asciiBytes.length < 8) {
            throw StandardException.newException(b ? "XBM07.D" : "XBM06.D");
        }
        byte[] array;
        if (b || b2) {
            array = this.generateUniqueBytes();
            this.persistentProperties.put("encryptedBootPassword", this.saveSecretKey(array, asciiBytes));
        }
        else {
            array = this.getDatabaseSecretKey(properties, asciiBytes, "XBM06.D");
        }
        return array;
    }
    
    public void saveProperties(final Properties properties) {
        final Enumeration<Object> keys = this.persistentProperties.keys();
        while (keys.hasMoreElements()) {
            final String s = keys.nextElement();
            properties.put(s, this.persistentProperties.get(s));
        }
        this.persistentProperties = null;
    }
    
    private byte[] getDatabaseSecretKey(final Properties properties, final byte[] array, final String s) throws StandardException {
        final String property = properties.getProperty("encryptedBootPassword");
        if (property == null) {
            throw StandardException.newException(s);
        }
        final int index = property.indexOf(45);
        if (index == -1) {
            throw StandardException.newException(s);
        }
        final int int1 = Integer.parseInt(property.substring(index + 1));
        final byte[] decryptKey = this.decryptKey(property, index, array);
        if (this.digest(decryptKey) != int1) {
            throw StandardException.newException(s);
        }
        if (this.encodedKeyLength != 0) {
            final byte[] array2 = new byte[this.encodedKeyLength];
            System.arraycopy(decryptKey, 0, array2, 0, this.encodedKeyLength);
            return array2;
        }
        return decryptKey;
    }
    
    private String saveSecretKey(final byte[] array, final byte[] array2) throws StandardException {
        final EncryptedKeyResult encryptKey = this.encryptKey(array, array2);
        return encryptKey.hexOutput.concat("-" + this.digest(encryptKey.paddedInputKey));
    }
    
    public String changeBootPassword(final String s, final Properties properties, final CipherProvider cipherProvider) throws StandardException {
        final int index = s.indexOf(44);
        if (index == -1) {
            throw StandardException.newException("XBCX7.S");
        }
        final byte[] asciiBytes = StringUtil.getAsciiBytes(s.substring(0, index).trim());
        if (asciiBytes == null || asciiBytes.length < 8) {
            throw StandardException.newException("XBCXA.S");
        }
        final byte[] asciiBytes2 = StringUtil.getAsciiBytes(s.substring(index + 1).trim());
        if (asciiBytes2 == null || asciiBytes2.length < 8) {
            throw StandardException.newException("XBCX2.S", new Integer(8));
        }
        final byte[] databaseSecretKey = this.getDatabaseSecretKey(properties, asciiBytes, "XBCXA.S");
        final byte[] generateIV = this.generateIV(databaseSecretKey);
        if (!((JCECipherProvider)cipherProvider).verifyIV(generateIV)) {
            throw StandardException.newException("XBCXA.S");
        }
        this.vetCipherProviders(this.createNewCipher(2, this.generateKey(databaseSecretKey), generateIV), cipherProvider, "XBCXA.S");
        this.saveSecretKey(databaseSecretKey, asciiBytes2);
        properties.put("encryptionKeyLength", this.keyLengthBits + "-" + this.encodedKeyLength);
        return this.saveSecretKey(databaseSecretKey, asciiBytes2);
    }
    
    private void vetCipherProviders(final CipherProvider cipherProvider, final CipherProvider cipherProvider2, final String s) throws StandardException {
        final int n = 1024;
        final int n2 = 256;
        final byte[] array = new byte[n];
        final byte[] array2 = new byte[n];
        final byte[] array3 = new byte[n];
        for (int i = 0; i < n; ++i) {
            array[i] = (byte)(i % n2);
        }
        final int encrypt = cipherProvider2.encrypt(array, 0, n, array2, 0);
        final int decrypt = cipherProvider.decrypt(array2, 0, encrypt, array3, 0);
        if (encrypt != n || decrypt != n) {
            throw StandardException.newException(s);
        }
        for (int j = 0; j < n; ++j) {
            if (array[j] != array3[j]) {
                throw StandardException.newException(s);
            }
        }
    }
    
    public final Object run() throws StandardException, InstantiationException, IllegalAccessException {
        try {
            switch (this.action) {
                case 1: {
                    Security.addProvider((Provider)Class.forName(this.cryptoProvider).newInstance());
                    break;
                }
                case 2: {
                    return this.activeFile.getRandomAccessFile(this.activePerms);
                }
                case 3: {
                    return this.activeFile.getInputStream();
                }
            }
        }
        catch (ClassNotFoundException ex) {
            throw StandardException.newException("XBCXF.S", this.cryptoProvider);
        }
        catch (FileNotFoundException ex2) {
            throw StandardException.newException("XBCXL.S", this.cryptoProvider);
        }
        return null;
    }
    
    public void verifyKey(final boolean b, final StorageFactory storageFactory, final Properties properties) throws StandardException {
        if (properties.getProperty("encryptionKey") == null) {
            return;
        }
        InputStream privAccessGetInputStream = null;
        DataOutput privAccessFile = null;
        final byte[] array = new byte[4096];
        try {
            if (b) {
                this.getSecureRandom().nextBytes(array);
                final byte[] md5Checksum = this.getMD5Checksum(array);
                this.createNewCipher(1, this.mainSecretKey, this.mainIV).encrypt(array, 0, array.length, array, 0);
                privAccessFile = this.privAccessFile(storageFactory, "verifyKey.dat", "rw");
                privAccessFile.writeInt(md5Checksum.length);
                privAccessFile.write(md5Checksum);
                privAccessFile.write(array);
                ((StorageRandomAccessFile)privAccessFile).sync();
            }
            else {
                privAccessGetInputStream = this.privAccessGetInputStream(storageFactory, "verifyKey.dat");
                final DataInputStream dataInputStream = new DataInputStream(privAccessGetInputStream);
                final byte[] array2 = new byte[dataInputStream.readInt()];
                dataInputStream.readFully(array2);
                dataInputStream.readFully(array);
                this.createNewCipher(2, this.mainSecretKey, this.mainIV).decrypt(array, 0, array.length, array, 0);
                if (!MessageDigest.isEqual(array2, this.getMD5Checksum(array))) {
                    throw StandardException.newException("XBCXK.S");
                }
            }
        }
        catch (IOException ex) {
            throw StandardException.newException("XBCXL.S", ex);
        }
        finally {
            try {
                if (privAccessFile != null) {
                    ((StorageRandomAccessFile)privAccessFile).close();
                }
                if (privAccessGetInputStream != null) {
                    privAccessGetInputStream.close();
                }
            }
            catch (IOException ex2) {
                throw StandardException.newException("XBCXL.S", ex2);
            }
        }
    }
    
    private byte[] getMD5Checksum(final byte[] input) throws StandardException {
        try {
            return MessageDigest.getInstance("MD5").digest(input);
        }
        catch (NoSuchAlgorithmException ex) {
            throw StandardException.newException("XBCXH.S", "MD5");
        }
    }
    
    private StorageRandomAccessFile privAccessFile(final StorageFactory storageFactory, final String s, final String activePerms) throws IOException {
        this.activeFile = storageFactory.newStorageFile("", s);
        this.action = 2;
        this.activePerms = activePerms;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<StorageRandomAccessFile>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    private InputStream privAccessGetInputStream(final StorageFactory storageFactory, final String s) throws StandardException {
        this.activeFile = storageFactory.newStorageFile("", s);
        this.action = 3;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getException();
        }
    }
    
    private static final class EncryptedKeyResult
    {
        public String hexOutput;
        public byte[] paddedInputKey;
        
        public EncryptedKeyResult(final String hexOutput, final byte[] paddedInputKey) {
            this.hexOutput = hexOutput;
            this.paddedInputKey = paddedInputKey;
        }
    }
}
