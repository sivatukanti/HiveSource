// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface Identity
{
    boolean setPassphrase(final byte[] p0) throws JSchException;
    
    byte[] getPublicKeyBlob();
    
    byte[] getSignature(final byte[] p0);
    
    @Deprecated
    boolean decrypt();
    
    String getAlgName();
    
    String getName();
    
    boolean isEncrypted();
    
    void clear();
}
