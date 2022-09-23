// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.util.Enumeration;
import java.io.InputStream;
import java.util.Vector;
import java.util.Hashtable;

public class JSch
{
    public static final String VERSION = "0.1.54";
    static Hashtable config;
    private Vector sessionPool;
    private IdentityRepository defaultIdentityRepository;
    private IdentityRepository identityRepository;
    private ConfigRepository configRepository;
    private HostKeyRepository known_hosts;
    private static final Logger DEVNULL;
    static Logger logger;
    
    public synchronized void setIdentityRepository(final IdentityRepository identityRepository) {
        if (identityRepository == null) {
            this.identityRepository = this.defaultIdentityRepository;
        }
        else {
            this.identityRepository = identityRepository;
        }
    }
    
    public synchronized IdentityRepository getIdentityRepository() {
        return this.identityRepository;
    }
    
    public ConfigRepository getConfigRepository() {
        return this.configRepository;
    }
    
    public void setConfigRepository(final ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }
    
    public JSch() {
        this.sessionPool = new Vector();
        this.defaultIdentityRepository = new LocalIdentityRepository(this);
        this.identityRepository = this.defaultIdentityRepository;
        this.configRepository = null;
        this.known_hosts = null;
    }
    
    public Session getSession(final String host) throws JSchException {
        return this.getSession(null, host, 22);
    }
    
    public Session getSession(final String username, final String host) throws JSchException {
        return this.getSession(username, host, 22);
    }
    
    public Session getSession(final String username, final String host, final int port) throws JSchException {
        if (host == null) {
            throw new JSchException("host must not be null.");
        }
        final Session s = new Session(this, username, host, port);
        return s;
    }
    
    protected void addSession(final Session session) {
        synchronized (this.sessionPool) {
            this.sessionPool.addElement(session);
        }
    }
    
    protected boolean removeSession(final Session session) {
        synchronized (this.sessionPool) {
            return this.sessionPool.remove(session);
        }
    }
    
    public void setHostKeyRepository(final HostKeyRepository hkrepo) {
        this.known_hosts = hkrepo;
    }
    
    public void setKnownHosts(final String filename) throws JSchException {
        if (this.known_hosts == null) {
            this.known_hosts = new KnownHosts(this);
        }
        if (this.known_hosts instanceof KnownHosts) {
            synchronized (this.known_hosts) {
                ((KnownHosts)this.known_hosts).setKnownHosts(filename);
            }
        }
    }
    
    public void setKnownHosts(final InputStream stream) throws JSchException {
        if (this.known_hosts == null) {
            this.known_hosts = new KnownHosts(this);
        }
        if (this.known_hosts instanceof KnownHosts) {
            synchronized (this.known_hosts) {
                ((KnownHosts)this.known_hosts).setKnownHosts(stream);
            }
        }
    }
    
    public HostKeyRepository getHostKeyRepository() {
        if (this.known_hosts == null) {
            this.known_hosts = new KnownHosts(this);
        }
        return this.known_hosts;
    }
    
    public void addIdentity(final String prvkey) throws JSchException {
        this.addIdentity(prvkey, (byte[])null);
    }
    
    public void addIdentity(final String prvkey, final String passphrase) throws JSchException {
        byte[] _passphrase = null;
        if (passphrase != null) {
            _passphrase = Util.str2byte(passphrase);
        }
        this.addIdentity(prvkey, _passphrase);
        if (_passphrase != null) {
            Util.bzero(_passphrase);
        }
    }
    
    public void addIdentity(final String prvkey, final byte[] passphrase) throws JSchException {
        final Identity identity = IdentityFile.newInstance(prvkey, null, this);
        this.addIdentity(identity, passphrase);
    }
    
    public void addIdentity(final String prvkey, final String pubkey, final byte[] passphrase) throws JSchException {
        final Identity identity = IdentityFile.newInstance(prvkey, pubkey, this);
        this.addIdentity(identity, passphrase);
    }
    
    public void addIdentity(final String name, final byte[] prvkey, final byte[] pubkey, final byte[] passphrase) throws JSchException {
        final Identity identity = IdentityFile.newInstance(name, prvkey, pubkey, this);
        this.addIdentity(identity, passphrase);
    }
    
    public void addIdentity(final Identity identity, byte[] passphrase) throws JSchException {
        if (passphrase != null) {
            try {
                final byte[] goo = new byte[passphrase.length];
                System.arraycopy(passphrase, 0, goo, 0, passphrase.length);
                passphrase = goo;
                identity.setPassphrase(passphrase);
            }
            finally {
                Util.bzero(passphrase);
            }
        }
        if (this.identityRepository instanceof LocalIdentityRepository) {
            ((LocalIdentityRepository)this.identityRepository).add(identity);
        }
        else if (identity instanceof IdentityFile && !identity.isEncrypted()) {
            this.identityRepository.add(((IdentityFile)identity).getKeyPair().forSSHAgent());
        }
        else {
            synchronized (this) {
                if (!(this.identityRepository instanceof IdentityRepository.Wrapper)) {
                    this.setIdentityRepository(new IdentityRepository.Wrapper(this.identityRepository));
                }
            }
            ((IdentityRepository.Wrapper)this.identityRepository).add(identity);
        }
    }
    
    @Deprecated
    public void removeIdentity(final String name) throws JSchException {
        final Vector identities = this.identityRepository.getIdentities();
        for (int i = 0; i < identities.size(); ++i) {
            final Identity identity = identities.elementAt(i);
            if (identity.getName().equals(name)) {
                if (this.identityRepository instanceof LocalIdentityRepository) {
                    ((LocalIdentityRepository)this.identityRepository).remove(identity);
                }
                else {
                    this.identityRepository.remove(identity.getPublicKeyBlob());
                }
            }
        }
    }
    
    public void removeIdentity(final Identity identity) throws JSchException {
        this.identityRepository.remove(identity.getPublicKeyBlob());
    }
    
    public Vector getIdentityNames() throws JSchException {
        final Vector foo = new Vector();
        final Vector identities = this.identityRepository.getIdentities();
        for (int i = 0; i < identities.size(); ++i) {
            final Identity identity = identities.elementAt(i);
            foo.addElement(identity.getName());
        }
        return foo;
    }
    
    public void removeAllIdentity() throws JSchException {
        this.identityRepository.removeAll();
    }
    
    public static String getConfig(final String key) {
        synchronized (JSch.config) {
            return JSch.config.get(key);
        }
    }
    
    public static void setConfig(final Hashtable newconf) {
        synchronized (JSch.config) {
            final Enumeration e = newconf.keys();
            while (e.hasMoreElements()) {
                final String key = e.nextElement();
                JSch.config.put(key, newconf.get(key));
            }
        }
    }
    
    public static void setConfig(final String key, final String value) {
        JSch.config.put(key, value);
    }
    
    public static void setLogger(Logger logger) {
        if (logger == null) {
            logger = JSch.DEVNULL;
        }
        JSch.logger = logger;
    }
    
    static Logger getLogger() {
        return JSch.logger;
    }
    
    static {
        (JSch.config = new Hashtable()).put("kex", "ecdh-sha2-nistp256,ecdh-sha2-nistp384,ecdh-sha2-nistp521,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha256,diffie-hellman-group-exchange-sha1,diffie-hellman-group1-sha1");
        JSch.config.put("server_host_key", "ssh-rsa,ssh-dss,ecdsa-sha2-nistp256,ecdsa-sha2-nistp384,ecdsa-sha2-nistp521");
        JSch.config.put("cipher.s2c", "aes128-ctr,aes128-cbc,3des-ctr,3des-cbc,blowfish-cbc,aes192-ctr,aes192-cbc,aes256-ctr,aes256-cbc");
        JSch.config.put("cipher.c2s", "aes128-ctr,aes128-cbc,3des-ctr,3des-cbc,blowfish-cbc,aes192-ctr,aes192-cbc,aes256-ctr,aes256-cbc");
        JSch.config.put("mac.s2c", "hmac-md5,hmac-sha1,hmac-sha2-256,hmac-sha1-96,hmac-md5-96");
        JSch.config.put("mac.c2s", "hmac-md5,hmac-sha1,hmac-sha2-256,hmac-sha1-96,hmac-md5-96");
        JSch.config.put("compression.s2c", "none");
        JSch.config.put("compression.c2s", "none");
        JSch.config.put("lang.s2c", "");
        JSch.config.put("lang.c2s", "");
        JSch.config.put("compression_level", "6");
        JSch.config.put("diffie-hellman-group-exchange-sha1", "com.jcraft.jsch.DHGEX");
        JSch.config.put("diffie-hellman-group1-sha1", "com.jcraft.jsch.DHG1");
        JSch.config.put("diffie-hellman-group14-sha1", "com.jcraft.jsch.DHG14");
        JSch.config.put("diffie-hellman-group-exchange-sha256", "com.jcraft.jsch.DHGEX256");
        JSch.config.put("ecdsa-sha2-nistp256", "com.jcraft.jsch.jce.SignatureECDSA");
        JSch.config.put("ecdsa-sha2-nistp384", "com.jcraft.jsch.jce.SignatureECDSA");
        JSch.config.put("ecdsa-sha2-nistp521", "com.jcraft.jsch.jce.SignatureECDSA");
        JSch.config.put("ecdh-sha2-nistp256", "com.jcraft.jsch.DHEC256");
        JSch.config.put("ecdh-sha2-nistp384", "com.jcraft.jsch.DHEC384");
        JSch.config.put("ecdh-sha2-nistp521", "com.jcraft.jsch.DHEC521");
        JSch.config.put("ecdh-sha2-nistp", "com.jcraft.jsch.jce.ECDHN");
        JSch.config.put("dh", "com.jcraft.jsch.jce.DH");
        JSch.config.put("3des-cbc", "com.jcraft.jsch.jce.TripleDESCBC");
        JSch.config.put("blowfish-cbc", "com.jcraft.jsch.jce.BlowfishCBC");
        JSch.config.put("hmac-sha1", "com.jcraft.jsch.jce.HMACSHA1");
        JSch.config.put("hmac-sha1-96", "com.jcraft.jsch.jce.HMACSHA196");
        JSch.config.put("hmac-sha2-256", "com.jcraft.jsch.jce.HMACSHA256");
        JSch.config.put("hmac-md5", "com.jcraft.jsch.jce.HMACMD5");
        JSch.config.put("hmac-md5-96", "com.jcraft.jsch.jce.HMACMD596");
        JSch.config.put("sha-1", "com.jcraft.jsch.jce.SHA1");
        JSch.config.put("sha-256", "com.jcraft.jsch.jce.SHA256");
        JSch.config.put("sha-384", "com.jcraft.jsch.jce.SHA384");
        JSch.config.put("sha-512", "com.jcraft.jsch.jce.SHA512");
        JSch.config.put("md5", "com.jcraft.jsch.jce.MD5");
        JSch.config.put("signature.dss", "com.jcraft.jsch.jce.SignatureDSA");
        JSch.config.put("signature.rsa", "com.jcraft.jsch.jce.SignatureRSA");
        JSch.config.put("signature.ecdsa", "com.jcraft.jsch.jce.SignatureECDSA");
        JSch.config.put("keypairgen.dsa", "com.jcraft.jsch.jce.KeyPairGenDSA");
        JSch.config.put("keypairgen.rsa", "com.jcraft.jsch.jce.KeyPairGenRSA");
        JSch.config.put("keypairgen.ecdsa", "com.jcraft.jsch.jce.KeyPairGenECDSA");
        JSch.config.put("random", "com.jcraft.jsch.jce.Random");
        JSch.config.put("none", "com.jcraft.jsch.CipherNone");
        JSch.config.put("aes128-cbc", "com.jcraft.jsch.jce.AES128CBC");
        JSch.config.put("aes192-cbc", "com.jcraft.jsch.jce.AES192CBC");
        JSch.config.put("aes256-cbc", "com.jcraft.jsch.jce.AES256CBC");
        JSch.config.put("aes128-ctr", "com.jcraft.jsch.jce.AES128CTR");
        JSch.config.put("aes192-ctr", "com.jcraft.jsch.jce.AES192CTR");
        JSch.config.put("aes256-ctr", "com.jcraft.jsch.jce.AES256CTR");
        JSch.config.put("3des-ctr", "com.jcraft.jsch.jce.TripleDESCTR");
        JSch.config.put("arcfour", "com.jcraft.jsch.jce.ARCFOUR");
        JSch.config.put("arcfour128", "com.jcraft.jsch.jce.ARCFOUR128");
        JSch.config.put("arcfour256", "com.jcraft.jsch.jce.ARCFOUR256");
        JSch.config.put("userauth.none", "com.jcraft.jsch.UserAuthNone");
        JSch.config.put("userauth.password", "com.jcraft.jsch.UserAuthPassword");
        JSch.config.put("userauth.keyboard-interactive", "com.jcraft.jsch.UserAuthKeyboardInteractive");
        JSch.config.put("userauth.publickey", "com.jcraft.jsch.UserAuthPublicKey");
        JSch.config.put("userauth.gssapi-with-mic", "com.jcraft.jsch.UserAuthGSSAPIWithMIC");
        JSch.config.put("gssapi-with-mic.krb5", "com.jcraft.jsch.jgss.GSSContextKrb5");
        JSch.config.put("zlib", "com.jcraft.jsch.jcraft.Compression");
        JSch.config.put("zlib@openssh.com", "com.jcraft.jsch.jcraft.Compression");
        JSch.config.put("pbkdf", "com.jcraft.jsch.jce.PBKDF");
        JSch.config.put("StrictHostKeyChecking", "ask");
        JSch.config.put("HashKnownHosts", "no");
        JSch.config.put("PreferredAuthentications", "gssapi-with-mic,publickey,keyboard-interactive,password");
        JSch.config.put("CheckCiphers", "aes256-ctr,aes192-ctr,aes128-ctr,aes256-cbc,aes192-cbc,aes128-cbc,3des-ctr,arcfour,arcfour128,arcfour256");
        JSch.config.put("CheckKexes", "diffie-hellman-group14-sha1,ecdh-sha2-nistp256,ecdh-sha2-nistp384,ecdh-sha2-nistp521");
        JSch.config.put("CheckSignatures", "ecdsa-sha2-nistp256,ecdsa-sha2-nistp384,ecdsa-sha2-nistp521");
        JSch.config.put("MaxAuthTries", "6");
        JSch.config.put("ClearAllForwardings", "no");
        DEVNULL = new Logger() {
            public boolean isEnabled(final int level) {
                return false;
            }
            
            public void log(final int level, final String message) {
            }
        };
        JSch.logger = JSch.DEVNULL;
    }
}
