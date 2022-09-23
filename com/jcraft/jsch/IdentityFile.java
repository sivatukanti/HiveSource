// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class IdentityFile implements Identity
{
    private JSch jsch;
    private KeyPair kpair;
    private String identity;
    
    static IdentityFile newInstance(final String prvfile, final String pubfile, final JSch jsch) throws JSchException {
        final KeyPair kpair = KeyPair.load(jsch, prvfile, pubfile);
        return new IdentityFile(jsch, prvfile, kpair);
    }
    
    static IdentityFile newInstance(final String name, final byte[] prvkey, final byte[] pubkey, final JSch jsch) throws JSchException {
        final KeyPair kpair = KeyPair.load(jsch, prvkey, pubkey);
        return new IdentityFile(jsch, name, kpair);
    }
    
    private IdentityFile(final JSch jsch, final String name, final KeyPair kpair) throws JSchException {
        this.jsch = jsch;
        this.identity = name;
        this.kpair = kpair;
    }
    
    public boolean setPassphrase(final byte[] passphrase) throws JSchException {
        return this.kpair.decrypt(passphrase);
    }
    
    public byte[] getPublicKeyBlob() {
        return this.kpair.getPublicKeyBlob();
    }
    
    public byte[] getSignature(final byte[] data) {
        return this.kpair.getSignature(data);
    }
    
    @Deprecated
    public boolean decrypt() {
        throw new RuntimeException("not implemented");
    }
    
    public String getAlgName() {
        return new String(this.kpair.getKeyTypeName());
    }
    
    public String getName() {
        return this.identity;
    }
    
    public boolean isEncrypted() {
        return this.kpair.isEncrypted();
    }
    
    public void clear() {
        this.kpair.dispose();
        this.kpair = null;
    }
    
    public KeyPair getKeyPair() {
        return this.kpair;
    }
}
