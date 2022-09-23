// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.util.Vector;
import java.util.Hashtable;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public abstract class KeyPair
{
    public static final int ERROR = 0;
    public static final int DSA = 1;
    public static final int RSA = 2;
    public static final int ECDSA = 3;
    public static final int UNKNOWN = 4;
    static final int VENDOR_OPENSSH = 0;
    static final int VENDOR_FSECURE = 1;
    static final int VENDOR_PUTTY = 2;
    static final int VENDOR_PKCS8 = 3;
    int vendor;
    private static final byte[] cr;
    protected String publicKeyComment;
    JSch jsch;
    private Cipher cipher;
    private HASH hash;
    private Random random;
    private byte[] passphrase;
    static byte[][] header;
    private static byte[] space;
    protected boolean encrypted;
    protected byte[] data;
    private byte[] iv;
    private byte[] publickeyblob;
    private static final String[] header1;
    private static final String[] header2;
    private static final String[] header3;
    
    public static KeyPair genKeyPair(final JSch jsch, final int type) throws JSchException {
        return genKeyPair(jsch, type, 1024);
    }
    
    public static KeyPair genKeyPair(final JSch jsch, final int type, final int key_size) throws JSchException {
        KeyPair kpair = null;
        if (type == 1) {
            kpair = new KeyPairDSA(jsch);
        }
        else if (type == 2) {
            kpair = new KeyPairRSA(jsch);
        }
        else if (type == 3) {
            kpair = new KeyPairECDSA(jsch);
        }
        if (kpair != null) {
            kpair.generate(key_size);
        }
        return kpair;
    }
    
    abstract void generate(final int p0) throws JSchException;
    
    abstract byte[] getBegin();
    
    abstract byte[] getEnd();
    
    abstract int getKeySize();
    
    public abstract byte[] getSignature(final byte[] p0);
    
    public abstract Signature getVerifier();
    
    public abstract byte[] forSSHAgent() throws JSchException;
    
    public String getPublicKeyComment() {
        return this.publicKeyComment;
    }
    
    public void setPublicKeyComment(final String publicKeyComment) {
        this.publicKeyComment = publicKeyComment;
    }
    
    public KeyPair(final JSch jsch) {
        this.vendor = 0;
        this.publicKeyComment = "no comment";
        this.jsch = null;
        this.encrypted = false;
        this.data = null;
        this.iv = null;
        this.publickeyblob = null;
        this.jsch = jsch;
    }
    
    abstract byte[] getPrivateKey();
    
    public void writePrivateKey(final OutputStream out) {
        this.writePrivateKey(out, null);
    }
    
    public void writePrivateKey(final OutputStream out, byte[] passphrase) {
        if (passphrase == null) {
            passphrase = this.passphrase;
        }
        final byte[] plain = this.getPrivateKey();
        final byte[][] _iv = { null };
        final byte[] encoded = this.encrypt(plain, _iv, passphrase);
        if (encoded != plain) {
            Util.bzero(plain);
        }
        final byte[] iv = _iv[0];
        final byte[] prv = Util.toBase64(encoded, 0, encoded.length);
        try {
            out.write(this.getBegin());
            out.write(KeyPair.cr);
            if (passphrase != null) {
                out.write(KeyPair.header[0]);
                out.write(KeyPair.cr);
                out.write(KeyPair.header[1]);
                for (int i = 0; i < iv.length; ++i) {
                    out.write(b2a((byte)(iv[i] >>> 4 & 0xF)));
                    out.write(b2a((byte)(iv[i] & 0xF)));
                }
                out.write(KeyPair.cr);
                out.write(KeyPair.cr);
            }
            for (int i = 0; i < prv.length; i += 64) {
                if (i + 64 >= prv.length) {
                    out.write(prv, i, prv.length - i);
                    out.write(KeyPair.cr);
                    break;
                }
                out.write(prv, i, 64);
                out.write(KeyPair.cr);
            }
            out.write(this.getEnd());
            out.write(KeyPair.cr);
        }
        catch (Exception ex) {}
    }
    
    abstract byte[] getKeyTypeName();
    
    public abstract int getKeyType();
    
    public byte[] getPublicKeyBlob() {
        return this.publickeyblob;
    }
    
    public void writePublicKey(final OutputStream out, final String comment) {
        final byte[] pubblob = this.getPublicKeyBlob();
        final byte[] pub = Util.toBase64(pubblob, 0, pubblob.length);
        try {
            out.write(this.getKeyTypeName());
            out.write(KeyPair.space);
            out.write(pub, 0, pub.length);
            out.write(KeyPair.space);
            out.write(Util.str2byte(comment));
            out.write(KeyPair.cr);
        }
        catch (Exception ex) {}
    }
    
    public void writePublicKey(final String name, final String comment) throws FileNotFoundException, IOException {
        final FileOutputStream fos = new FileOutputStream(name);
        this.writePublicKey(fos, comment);
        fos.close();
    }
    
    public void writeSECSHPublicKey(final OutputStream out, final String comment) {
        final byte[] pubblob = this.getPublicKeyBlob();
        final byte[] pub = Util.toBase64(pubblob, 0, pubblob.length);
        try {
            out.write(Util.str2byte("---- BEGIN SSH2 PUBLIC KEY ----"));
            out.write(KeyPair.cr);
            out.write(Util.str2byte("Comment: \"" + comment + "\""));
            out.write(KeyPair.cr);
            int len;
            for (int index = 0; index < pub.length; index += len) {
                len = 70;
                if (pub.length - index < len) {
                    len = pub.length - index;
                }
                out.write(pub, index, len);
                out.write(KeyPair.cr);
            }
            out.write(Util.str2byte("---- END SSH2 PUBLIC KEY ----"));
            out.write(KeyPair.cr);
        }
        catch (Exception ex) {}
    }
    
    public void writeSECSHPublicKey(final String name, final String comment) throws FileNotFoundException, IOException {
        final FileOutputStream fos = new FileOutputStream(name);
        this.writeSECSHPublicKey(fos, comment);
        fos.close();
    }
    
    public void writePrivateKey(final String name) throws FileNotFoundException, IOException {
        this.writePrivateKey(name, null);
    }
    
    public void writePrivateKey(final String name, final byte[] passphrase) throws FileNotFoundException, IOException {
        final FileOutputStream fos = new FileOutputStream(name);
        this.writePrivateKey(fos, passphrase);
        fos.close();
    }
    
    public String getFingerPrint() {
        if (this.hash == null) {
            this.hash = this.genHash();
        }
        final byte[] kblob = this.getPublicKeyBlob();
        if (kblob == null) {
            return null;
        }
        return Util.getFingerPrint(this.hash, kblob);
    }
    
    private byte[] encrypt(final byte[] plain, final byte[][] _iv, final byte[] passphrase) {
        if (passphrase == null) {
            return plain;
        }
        if (this.cipher == null) {
            this.cipher = this.genCipher();
        }
        final int n = 0;
        final byte[] array = new byte[this.cipher.getIVSize()];
        _iv[n] = array;
        final byte[] iv = array;
        if (this.random == null) {
            this.random = this.genRandom();
        }
        this.random.fill(iv, 0, iv.length);
        final byte[] key = this.genKey(passphrase, iv);
        byte[] encoded = plain;
        final int bsize = this.cipher.getIVSize();
        final byte[] foo = new byte[(encoded.length / bsize + 1) * bsize];
        System.arraycopy(encoded, 0, foo, 0, encoded.length);
        for (int padding = bsize - encoded.length % bsize, i = foo.length - 1; foo.length - padding <= i; --i) {
            foo[i] = (byte)padding;
        }
        encoded = foo;
        try {
            this.cipher.init(0, key, iv);
            this.cipher.update(encoded, 0, encoded.length, encoded, 0);
        }
        catch (Exception ex) {}
        Util.bzero(key);
        return encoded;
    }
    
    abstract boolean parse(final byte[] p0);
    
    private byte[] decrypt(final byte[] data, final byte[] passphrase, final byte[] iv) {
        try {
            final byte[] key = this.genKey(passphrase, iv);
            this.cipher.init(1, key, iv);
            Util.bzero(key);
            final byte[] plain = new byte[data.length];
            this.cipher.update(data, 0, data.length, plain, 0);
            return plain;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    int writeSEQUENCE(final byte[] buf, int index, final int len) {
        buf[index++] = 48;
        index = this.writeLength(buf, index, len);
        return index;
    }
    
    int writeINTEGER(final byte[] buf, int index, final byte[] data) {
        buf[index++] = 2;
        index = this.writeLength(buf, index, data.length);
        System.arraycopy(data, 0, buf, index, data.length);
        index += data.length;
        return index;
    }
    
    int writeOCTETSTRING(final byte[] buf, int index, final byte[] data) {
        buf[index++] = 4;
        index = this.writeLength(buf, index, data.length);
        System.arraycopy(data, 0, buf, index, data.length);
        index += data.length;
        return index;
    }
    
    int writeDATA(final byte[] buf, final byte n, int index, final byte[] data) {
        buf[index++] = n;
        index = this.writeLength(buf, index, data.length);
        System.arraycopy(data, 0, buf, index, data.length);
        index += data.length;
        return index;
    }
    
    int countLength(int len) {
        int i = 1;
        if (len <= 127) {
            return i;
        }
        while (len > 0) {
            len >>>= 8;
            ++i;
        }
        return i;
    }
    
    int writeLength(final byte[] data, int index, int len) {
        int i = this.countLength(len) - 1;
        if (i == 0) {
            data[index++] = (byte)len;
            return index;
        }
        data[index++] = (byte)(0x80 | i);
        final int j = index + i;
        while (i > 0) {
            data[index + i - 1] = (byte)(len & 0xFF);
            len >>>= 8;
            --i;
        }
        return j;
    }
    
    private Random genRandom() {
        if (this.random == null) {
            try {
                final JSch jsch = this.jsch;
                final Class c = Class.forName(JSch.getConfig("random"));
                this.random = c.newInstance();
            }
            catch (Exception e) {
                System.err.println("connect: random " + e);
            }
        }
        return this.random;
    }
    
    private HASH genHash() {
        try {
            final JSch jsch = this.jsch;
            final Class c = Class.forName(JSch.getConfig("md5"));
            (this.hash = c.newInstance()).init();
        }
        catch (Exception ex) {}
        return this.hash;
    }
    
    private Cipher genCipher() {
        try {
            final JSch jsch = this.jsch;
            final Class c = Class.forName(JSch.getConfig("3des-cbc"));
            this.cipher = c.newInstance();
        }
        catch (Exception ex) {}
        return this.cipher;
    }
    
    synchronized byte[] genKey(final byte[] passphrase, final byte[] iv) {
        if (this.cipher == null) {
            this.cipher = this.genCipher();
        }
        if (this.hash == null) {
            this.hash = this.genHash();
        }
        byte[] key = new byte[this.cipher.getBlockSize()];
        final int hsize = this.hash.getBlockSize();
        final byte[] hn = new byte[key.length / hsize * hsize + ((key.length % hsize == 0) ? 0 : hsize)];
        try {
            byte[] tmp = null;
            if (this.vendor == 0) {
                for (int index = 0; index + hsize <= hn.length; index += tmp.length) {
                    if (tmp != null) {
                        this.hash.update(tmp, 0, tmp.length);
                    }
                    this.hash.update(passphrase, 0, passphrase.length);
                    this.hash.update(iv, 0, (iv.length > 8) ? 8 : iv.length);
                    tmp = this.hash.digest();
                    System.arraycopy(tmp, 0, hn, index, tmp.length);
                }
                System.arraycopy(hn, 0, key, 0, key.length);
            }
            else if (this.vendor == 1) {
                for (int index = 0; index + hsize <= hn.length; index += tmp.length) {
                    if (tmp != null) {
                        this.hash.update(tmp, 0, tmp.length);
                    }
                    this.hash.update(passphrase, 0, passphrase.length);
                    tmp = this.hash.digest();
                    System.arraycopy(tmp, 0, hn, index, tmp.length);
                }
                System.arraycopy(hn, 0, key, 0, key.length);
            }
            else if (this.vendor == 2) {
                final JSch jsch = this.jsch;
                final Class c = Class.forName(JSch.getConfig("sha-1"));
                final HASH sha1 = c.newInstance();
                tmp = new byte[4];
                key = new byte[40];
                for (int i = 0; i < 2; ++i) {
                    sha1.init();
                    tmp[3] = (byte)i;
                    sha1.update(tmp, 0, tmp.length);
                    sha1.update(passphrase, 0, passphrase.length);
                    System.arraycopy(sha1.digest(), 0, key, i * 20, 20);
                }
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }
        return key;
    }
    
    @Deprecated
    public void setPassphrase(final String passphrase) {
        if (passphrase == null || passphrase.length() == 0) {
            this.setPassphrase((byte[])null);
        }
        else {
            this.setPassphrase(Util.str2byte(passphrase));
        }
    }
    
    @Deprecated
    public void setPassphrase(byte[] passphrase) {
        if (passphrase != null && passphrase.length == 0) {
            passphrase = null;
        }
        this.passphrase = passphrase;
    }
    
    public boolean isEncrypted() {
        return this.encrypted;
    }
    
    public boolean decrypt(final String _passphrase) {
        if (_passphrase == null || _passphrase.length() == 0) {
            return !this.encrypted;
        }
        return this.decrypt(Util.str2byte(_passphrase));
    }
    
    public boolean decrypt(byte[] _passphrase) {
        if (!this.encrypted) {
            return true;
        }
        if (_passphrase == null) {
            return !this.encrypted;
        }
        final byte[] bar = new byte[_passphrase.length];
        System.arraycopy(_passphrase, 0, bar, 0, bar.length);
        _passphrase = bar;
        final byte[] foo = this.decrypt(this.data, _passphrase, this.iv);
        Util.bzero(_passphrase);
        if (this.parse(foo)) {
            this.encrypted = false;
        }
        return !this.encrypted;
    }
    
    public static KeyPair load(final JSch jsch, final String prvkey) throws JSchException {
        String pubkey = prvkey + ".pub";
        if (!new File(pubkey).exists()) {
            pubkey = null;
        }
        return load(jsch, prvkey, pubkey);
    }
    
    public static KeyPair load(final JSch jsch, final String prvfile, final String pubfile) throws JSchException {
        byte[] prvkey = null;
        byte[] pubkey = null;
        try {
            prvkey = Util.fromFile(prvfile);
        }
        catch (IOException e) {
            throw new JSchException(e.toString(), e);
        }
        String _pubfile = pubfile;
        if (pubfile == null) {
            _pubfile = prvfile + ".pub";
        }
        try {
            pubkey = Util.fromFile(_pubfile);
        }
        catch (IOException e2) {
            if (pubfile != null) {
                throw new JSchException(e2.toString(), e2);
            }
        }
        try {
            return load(jsch, prvkey, pubkey);
        }
        finally {
            Util.bzero(prvkey);
        }
    }
    
    public static KeyPair load(final JSch jsch, final byte[] prvkey, final byte[] pubkey) throws JSchException {
        byte[] iv = new byte[8];
        boolean encrypted = true;
        byte[] data = null;
        byte[] publickeyblob = null;
        int type = 0;
        int vendor = 0;
        String publicKeyComment = "";
        Cipher cipher = null;
        if (pubkey == null && prvkey != null && prvkey.length > 11 && prvkey[0] == 0 && prvkey[1] == 0 && prvkey[2] == 0 && (prvkey[3] == 7 || prvkey[3] == 19)) {
            final Buffer buf = new Buffer(prvkey);
            buf.skip(prvkey.length);
            final String _type = new String(buf.getString());
            buf.rewind();
            KeyPair kpair = null;
            if (_type.equals("ssh-rsa")) {
                kpair = KeyPairRSA.fromSSHAgent(jsch, buf);
            }
            else if (_type.equals("ssh-dss")) {
                kpair = KeyPairDSA.fromSSHAgent(jsch, buf);
            }
            else {
                if (!_type.equals("ecdsa-sha2-nistp256") && !_type.equals("ecdsa-sha2-nistp384") && !_type.equals("ecdsa-sha2-nistp512")) {
                    throw new JSchException("privatekey: invalid key " + new String(prvkey, 4, 7));
                }
                kpair = KeyPairECDSA.fromSSHAgent(jsch, buf);
            }
            return kpair;
        }
        try {
            byte[] buf2 = prvkey;
            if (buf2 != null) {
                final KeyPair ppk = loadPPK(jsch, buf2);
                if (ppk != null) {
                    return ppk;
                }
            }
            int len;
            int i;
            for (len = ((buf2 != null) ? buf2.length : 0), i = 0; i < len; ++i) {
                if (buf2[i] == 45 && i + 4 < len && buf2[i + 1] == 45 && buf2[i + 2] == 45 && buf2[i + 3] == 45 && buf2[i + 4] == 45) {
                    break;
                }
            }
            while (i < len) {
                if (buf2[i] == 66 && i + 3 < len && buf2[i + 1] == 69 && buf2[i + 2] == 71 && buf2[i + 3] == 73) {
                    i += 6;
                    if (i + 2 >= len) {
                        throw new JSchException("invalid privatekey: " + prvkey);
                    }
                    if (buf2[i] == 68 && buf2[i + 1] == 83 && buf2[i + 2] == 65) {
                        type = 1;
                    }
                    else if (buf2[i] == 82 && buf2[i + 1] == 83 && buf2[i + 2] == 65) {
                        type = 2;
                    }
                    else if (buf2[i] == 69 && buf2[i + 1] == 67) {
                        type = 3;
                    }
                    else if (buf2[i] == 83 && buf2[i + 1] == 83 && buf2[i + 2] == 72) {
                        type = 4;
                        vendor = 1;
                    }
                    else if (i + 6 < len && buf2[i] == 80 && buf2[i + 1] == 82 && buf2[i + 2] == 73 && buf2[i + 3] == 86 && buf2[i + 4] == 65 && buf2[i + 5] == 84 && buf2[i + 6] == 69) {
                        type = 4;
                        vendor = 3;
                        encrypted = false;
                        i += 3;
                    }
                    else {
                        if (i + 8 >= len || buf2[i] != 69 || buf2[i + 1] != 78 || buf2[i + 2] != 67 || buf2[i + 3] != 82 || buf2[i + 4] != 89 || buf2[i + 5] != 80 || buf2[i + 6] != 84 || buf2[i + 7] != 69 || buf2[i + 8] != 68) {
                            throw new JSchException("invalid privatekey: " + prvkey);
                        }
                        type = 4;
                        vendor = 3;
                        i += 5;
                    }
                    i += 3;
                }
                else if (buf2[i] == 65 && i + 7 < len && buf2[i + 1] == 69 && buf2[i + 2] == 83 && buf2[i + 3] == 45 && buf2[i + 4] == 50 && buf2[i + 5] == 53 && buf2[i + 6] == 54 && buf2[i + 7] == 45) {
                    i += 8;
                    if (!Session.checkCipher(JSch.getConfig("aes256-cbc"))) {
                        throw new JSchException("privatekey: aes256-cbc is not available " + prvkey);
                    }
                    final Class c = Class.forName(JSch.getConfig("aes256-cbc"));
                    cipher = c.newInstance();
                    iv = new byte[cipher.getIVSize()];
                }
                else if (buf2[i] == 65 && i + 7 < len && buf2[i + 1] == 69 && buf2[i + 2] == 83 && buf2[i + 3] == 45 && buf2[i + 4] == 49 && buf2[i + 5] == 57 && buf2[i + 6] == 50 && buf2[i + 7] == 45) {
                    i += 8;
                    if (!Session.checkCipher(JSch.getConfig("aes192-cbc"))) {
                        throw new JSchException("privatekey: aes192-cbc is not available " + prvkey);
                    }
                    final Class c = Class.forName(JSch.getConfig("aes192-cbc"));
                    cipher = c.newInstance();
                    iv = new byte[cipher.getIVSize()];
                }
                else if (buf2[i] == 65 && i + 7 < len && buf2[i + 1] == 69 && buf2[i + 2] == 83 && buf2[i + 3] == 45 && buf2[i + 4] == 49 && buf2[i + 5] == 50 && buf2[i + 6] == 56 && buf2[i + 7] == 45) {
                    i += 8;
                    if (!Session.checkCipher(JSch.getConfig("aes128-cbc"))) {
                        throw new JSchException("privatekey: aes128-cbc is not available " + prvkey);
                    }
                    final Class c = Class.forName(JSch.getConfig("aes128-cbc"));
                    cipher = c.newInstance();
                    iv = new byte[cipher.getIVSize()];
                }
                else if (buf2[i] == 67 && i + 3 < len && buf2[i + 1] == 66 && buf2[i + 2] == 67 && buf2[i + 3] == 44) {
                    i += 4;
                    for (int ii = 0; ii < iv.length; ++ii) {
                        iv[ii] = (byte)((a2b(buf2[i++]) << 4 & 0xF0) + (a2b(buf2[i++]) & 0xF));
                    }
                }
                else if (buf2[i] == 13 && i + 1 < buf2.length && buf2[i + 1] == 10) {
                    ++i;
                }
                else {
                    if (buf2[i] == 10 && i + 1 < buf2.length) {
                        if (buf2[i + 1] == 10) {
                            i += 2;
                            break;
                        }
                        if (buf2[i + 1] == 13 && i + 2 < buf2.length && buf2[i + 2] == 10) {
                            i += 3;
                            break;
                        }
                        boolean inheader = false;
                        for (int j = i + 1; j < buf2.length; ++j) {
                            if (buf2[j] == 10) {
                                break;
                            }
                            if (buf2[j] == 58) {
                                inheader = true;
                                break;
                            }
                        }
                        if (!inheader) {
                            ++i;
                            if (vendor != 3) {
                                encrypted = false;
                                break;
                            }
                            break;
                        }
                    }
                    ++i;
                }
            }
            if (buf2 != null) {
                if (type == 0) {
                    throw new JSchException("invalid privatekey: " + prvkey);
                }
                int start = i;
                while (i < len && buf2[i] != 45) {
                    ++i;
                }
                if (len - i == 0 || i - start == 0) {
                    throw new JSchException("invalid privatekey: " + prvkey);
                }
                final byte[] tmp = new byte[i - start];
                System.arraycopy(buf2, start, tmp, 0, tmp.length);
                final byte[] _buf = tmp;
                start = 0;
                i = 0;
                int _len = _buf.length;
                while (i < _len) {
                    if (_buf[i] == 10) {
                        final boolean xd = _buf[i - 1] == 13;
                        System.arraycopy(_buf, i + 1, _buf, i - (xd ? 1 : 0), _len - (i + 1));
                        if (xd) {
                            --_len;
                        }
                        --_len;
                    }
                    else {
                        if (_buf[i] == 45) {
                            break;
                        }
                        ++i;
                    }
                }
                if (i - start > 0) {
                    data = Util.fromBase64(_buf, start, i - start);
                }
                Util.bzero(_buf);
            }
            if (data != null && data.length > 4 && data[0] == 63 && data[1] == 111 && data[2] == -7 && data[3] == -21) {
                final Buffer _buf2 = new Buffer(data);
                _buf2.getInt();
                _buf2.getInt();
                final byte[] _type2 = _buf2.getString();
                final String _cipher = Util.byte2str(_buf2.getString());
                if (_cipher.equals("3des-cbc")) {
                    _buf2.getInt();
                    final byte[] foo = new byte[data.length - _buf2.getOffSet()];
                    _buf2.getByte(foo);
                    data = foo;
                    encrypted = true;
                    throw new JSchException("unknown privatekey format: " + prvkey);
                }
                if (_cipher.equals("none")) {
                    _buf2.getInt();
                    _buf2.getInt();
                    encrypted = false;
                    final byte[] foo = new byte[data.length - _buf2.getOffSet()];
                    _buf2.getByte(foo);
                    data = foo;
                }
            }
            if (pubkey != null) {
                try {
                    buf2 = pubkey;
                    len = buf2.length;
                    if (buf2.length > 4 && buf2[0] == 45 && buf2[1] == 45 && buf2[2] == 45 && buf2[3] == 45) {
                        boolean valid = true;
                        i = 0;
                        do {
                            ++i;
                        } while (buf2.length > i && buf2[i] != 10);
                        if (buf2.length <= i) {
                            valid = false;
                        }
                        while (valid) {
                            if (buf2[i] == 10) {
                                boolean inheader2 = false;
                                for (int k = i + 1; k < buf2.length; ++k) {
                                    if (buf2[k] == 10) {
                                        break;
                                    }
                                    if (buf2[k] == 58) {
                                        inheader2 = true;
                                        break;
                                    }
                                }
                                if (!inheader2) {
                                    ++i;
                                    break;
                                }
                            }
                            ++i;
                        }
                        if (buf2.length <= i) {
                            valid = false;
                        }
                        final int start2 = i;
                        while (valid && i < len) {
                            if (buf2[i] == 10) {
                                System.arraycopy(buf2, i + 1, buf2, i, len - i - 1);
                                --len;
                            }
                            else {
                                if (buf2[i] == 45) {
                                    break;
                                }
                                ++i;
                            }
                        }
                        if (valid) {
                            publickeyblob = Util.fromBase64(buf2, start2, i - start2);
                            if (prvkey == null || type == 4) {
                                if (publickeyblob[8] == 100) {
                                    type = 1;
                                }
                                else if (publickeyblob[8] == 114) {
                                    type = 2;
                                }
                            }
                        }
                    }
                    else if (buf2[0] == 115 && buf2[1] == 115 && buf2[2] == 104 && buf2[3] == 45) {
                        if (prvkey == null && buf2.length > 7) {
                            if (buf2[4] == 100) {
                                type = 1;
                            }
                            else if (buf2[4] == 114) {
                                type = 2;
                            }
                        }
                        for (i = 0; i < len && buf2[i] != 32; ++i) {}
                        if (++i < len) {
                            final int start = i;
                            while (i < len && buf2[i] != 32) {
                                ++i;
                            }
                            publickeyblob = Util.fromBase64(buf2, start, i - start);
                        }
                        if (i++ < len) {
                            final int start = i;
                            while (i < len && buf2[i] != 10) {
                                ++i;
                            }
                            if (i > 0 && buf2[i - 1] == 13) {
                                --i;
                            }
                            if (start < i) {
                                publicKeyComment = new String(buf2, start, i - start);
                            }
                        }
                    }
                    else if (buf2[0] == 101 && buf2[1] == 99 && buf2[2] == 100 && buf2[3] == 115) {
                        if (prvkey == null && buf2.length > 7) {
                            type = 3;
                        }
                        for (i = 0; i < len && buf2[i] != 32; ++i) {}
                        if (++i < len) {
                            final int start = i;
                            while (i < len && buf2[i] != 32) {
                                ++i;
                            }
                            publickeyblob = Util.fromBase64(buf2, start, i - start);
                        }
                        if (i++ < len) {
                            final int start = i;
                            while (i < len && buf2[i] != 10) {
                                ++i;
                            }
                            if (i > 0 && buf2[i - 1] == 13) {
                                --i;
                            }
                            if (start < i) {
                                publicKeyComment = new String(buf2, start, i - start);
                            }
                        }
                    }
                }
                catch (Exception ex) {}
            }
        }
        catch (Exception e) {
            if (e instanceof JSchException) {
                throw (JSchException)e;
            }
            if (e instanceof Throwable) {
                throw new JSchException(e.toString(), e);
            }
            throw new JSchException(e.toString());
        }
        KeyPair kpair2 = null;
        if (type == 1) {
            kpair2 = new KeyPairDSA(jsch);
        }
        else if (type == 2) {
            kpair2 = new KeyPairRSA(jsch);
        }
        else if (type == 3) {
            kpair2 = new KeyPairECDSA(jsch);
        }
        else if (vendor == 3) {
            kpair2 = new KeyPairPKCS8(jsch);
        }
        if (kpair2 != null) {
            kpair2.encrypted = encrypted;
            kpair2.publickeyblob = publickeyblob;
            kpair2.vendor = vendor;
            kpair2.publicKeyComment = publicKeyComment;
            kpair2.cipher = cipher;
            if (encrypted) {
                kpair2.encrypted = true;
                kpair2.iv = iv;
                kpair2.data = data;
            }
            else {
                if (kpair2.parse(data)) {
                    kpair2.encrypted = false;
                    return kpair2;
                }
                throw new JSchException("invalid privatekey: " + prvkey);
            }
        }
        return kpair2;
    }
    
    private static byte a2b(final byte c) {
        if (48 <= c && c <= 57) {
            return (byte)(c - 48);
        }
        return (byte)(c - 97 + 10);
    }
    
    private static byte b2a(final byte c) {
        if (0 <= c && c <= 9) {
            return (byte)(c + 48);
        }
        return (byte)(c - 10 + 65);
    }
    
    public void dispose() {
        Util.bzero(this.passphrase);
    }
    
    public void finalize() {
        this.dispose();
    }
    
    static KeyPair loadPPK(final JSch jsch, final byte[] buf) throws JSchException {
        byte[] pubkey = null;
        byte[] prvkey = null;
        int lines = 0;
        final Buffer buffer = new Buffer(buf);
        final Hashtable v = new Hashtable();
        while (parseHeader(buffer, v)) {}
        final String typ = v.get("PuTTY-User-Key-File-2");
        if (typ == null) {
            return null;
        }
        lines = Integer.parseInt(v.get("Public-Lines"));
        pubkey = parseLines(buffer, lines);
        while (parseHeader(buffer, v)) {}
        lines = Integer.parseInt(v.get("Private-Lines"));
        prvkey = parseLines(buffer, lines);
        while (parseHeader(buffer, v)) {}
        prvkey = Util.fromBase64(prvkey, 0, prvkey.length);
        pubkey = Util.fromBase64(pubkey, 0, pubkey.length);
        KeyPair kpair = null;
        if (typ.equals("ssh-rsa")) {
            final Buffer _buf = new Buffer(pubkey);
            _buf.skip(pubkey.length);
            final int len = _buf.getInt();
            _buf.getByte(new byte[len]);
            final byte[] pub_array = new byte[_buf.getInt()];
            _buf.getByte(pub_array);
            final byte[] n_array = new byte[_buf.getInt()];
            _buf.getByte(n_array);
            kpair = new KeyPairRSA(jsch, n_array, pub_array, null);
        }
        else {
            if (!typ.equals("ssh-dss")) {
                return null;
            }
            final Buffer _buf = new Buffer(pubkey);
            _buf.skip(pubkey.length);
            final int len = _buf.getInt();
            _buf.getByte(new byte[len]);
            final byte[] p_array = new byte[_buf.getInt()];
            _buf.getByte(p_array);
            final byte[] q_array = new byte[_buf.getInt()];
            _buf.getByte(q_array);
            final byte[] g_array = new byte[_buf.getInt()];
            _buf.getByte(g_array);
            final byte[] y_array = new byte[_buf.getInt()];
            _buf.getByte(y_array);
            kpair = new KeyPairDSA(jsch, p_array, q_array, g_array, y_array, null);
        }
        if (kpair == null) {
            return null;
        }
        kpair.encrypted = !v.get("Encryption").equals("none");
        kpair.vendor = 2;
        kpair.publicKeyComment = v.get("Comment");
        if (kpair.encrypted) {
            if (Session.checkCipher(JSch.getConfig("aes256-cbc"))) {
                Label_0517: {
                    try {
                        final Class c = Class.forName(JSch.getConfig("aes256-cbc"));
                        kpair.cipher = c.newInstance();
                        kpair.iv = new byte[kpair.cipher.getIVSize()];
                        break Label_0517;
                    }
                    catch (Exception e) {
                        throw new JSchException("The cipher 'aes256-cbc' is required, but it is not available.");
                    }
                    throw new JSchException("The cipher 'aes256-cbc' is required, but it is not available.");
                }
                kpair.data = prvkey;
                return kpair;
            }
            throw new JSchException("The cipher 'aes256-cbc' is required, but it is not available.");
        }
        kpair.parse(kpair.data = prvkey);
        return kpair;
    }
    
    private static byte[] parseLines(final Buffer buffer, int lines) {
        final byte[] buf = buffer.buffer;
        int index = buffer.index;
        byte[] data = null;
        int i = index;
        while (lines-- > 0) {
            while (buf.length > i) {
                if (buf[i++] == 13) {
                    if (data == null) {
                        data = new byte[i - index - 1];
                        System.arraycopy(buf, index, data, 0, i - index - 1);
                        break;
                    }
                    final byte[] tmp = new byte[data.length + i - index - 1];
                    System.arraycopy(data, 0, tmp, 0, data.length);
                    System.arraycopy(buf, index, tmp, data.length, i - index - 1);
                    for (int j = 0; j < data.length; ++j) {
                        data[j] = 0;
                    }
                    data = tmp;
                    break;
                }
            }
            if (buf[i] == 10) {
                ++i;
            }
            index = i;
        }
        if (data != null) {
            buffer.index = index;
        }
        return data;
    }
    
    private static boolean parseHeader(final Buffer buffer, final Hashtable v) {
        final byte[] buf = buffer.buffer;
        int index = buffer.index;
        String key = null;
        String value = null;
        for (int i = index; i < buf.length; ++i) {
            if (buf[i] == 13) {
                break;
            }
            if (buf[i] == 58) {
                key = new String(buf, index, i - index);
                if (++i < buf.length && buf[i] == 32) {
                    ++i;
                }
                index = i;
                break;
            }
        }
        if (key == null) {
            return false;
        }
        for (int i = index; i < buf.length; ++i) {
            if (buf[i] == 13) {
                value = new String(buf, index, i - index);
                if (++i < buf.length && buf[i] == 10) {
                    ++i;
                }
                index = i;
                break;
            }
        }
        if (value != null) {
            v.put(key, value);
            buffer.index = index;
        }
        return key != null && value != null;
    }
    
    void copy(final KeyPair kpair) {
        this.publickeyblob = kpair.publickeyblob;
        this.vendor = kpair.vendor;
        this.publicKeyComment = kpair.publicKeyComment;
        this.cipher = kpair.cipher;
    }
    
    static {
        cr = Util.str2byte("\n");
        KeyPair.header = new byte[][] { Util.str2byte("Proc-Type: 4,ENCRYPTED"), Util.str2byte("DEK-Info: DES-EDE3-CBC,") };
        KeyPair.space = Util.str2byte(" ");
        header1 = new String[] { "PuTTY-User-Key-File-2: ", "Encryption: ", "Comment: ", "Public-Lines: " };
        header2 = new String[] { "Private-Lines: " };
        header3 = new String[] { "Private-MAC: " };
    }
    
    class ASN1Exception extends Exception
    {
    }
    
    class ASN1
    {
        byte[] buf;
        int start;
        int length;
        
        ASN1(final KeyPair keyPair, final byte[] buf) throws ASN1Exception {
            this(keyPair, buf, 0, buf.length);
        }
        
        ASN1(final byte[] buf, final int start, final int length) throws ASN1Exception {
            this.buf = buf;
            this.start = start;
            this.length = length;
            if (start + length > buf.length) {
                throw new ASN1Exception();
            }
        }
        
        int getType() {
            return this.buf[this.start] & 0xFF;
        }
        
        boolean isSEQUENCE() {
            return this.getType() == 48;
        }
        
        boolean isINTEGER() {
            return this.getType() == 2;
        }
        
        boolean isOBJECT() {
            return this.getType() == 6;
        }
        
        boolean isOCTETSTRING() {
            return this.getType() == 4;
        }
        
        private int getLength(final int[] indexp) {
            int index = indexp[0];
            int length = this.buf[index++] & 0xFF;
            if ((length & 0x80) != 0x0) {
                int foo = length & 0x7F;
                length = 0;
                while (foo-- > 0) {
                    length = (length << 8) + (this.buf[index++] & 0xFF);
                }
            }
            indexp[0] = index;
            return length;
        }
        
        byte[] getContent() {
            final int[] indexp = { this.start + 1 };
            final int length = this.getLength(indexp);
            final int index = indexp[0];
            final byte[] tmp = new byte[length];
            System.arraycopy(this.buf, index, tmp, 0, tmp.length);
            return tmp;
        }
        
        ASN1[] getContents() throws ASN1Exception {
            final int typ = this.buf[this.start];
            final int[] indexp = { this.start + 1 };
            int length = this.getLength(indexp);
            if (typ == 5) {
                return new ASN1[0];
            }
            int index = indexp[0];
            final Vector values = new Vector();
            while (length > 0) {
                ++index;
                --length;
                final int tmp = index;
                indexp[0] = index;
                final int l = this.getLength(indexp);
                index = indexp[0];
                length -= index - tmp;
                values.addElement(new ASN1(this.buf, tmp - 1, 1 + (index - tmp) + l));
                index += l;
                length -= l;
            }
            final ASN1[] result = new ASN1[values.size()];
            for (int i = 0; i < values.size(); ++i) {
                result[i] = values.elementAt(i);
            }
            return result;
        }
    }
}
