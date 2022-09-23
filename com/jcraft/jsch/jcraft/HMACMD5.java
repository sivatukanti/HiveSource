// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jcraft;

import java.security.MessageDigest;
import com.jcraft.jsch.MAC;

public class HMACMD5 extends HMAC implements MAC
{
    private static final String name = "hmac-md5";
    
    public HMACMD5() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        }
        catch (Exception e) {
            System.err.println(e);
        }
        this.setH(md);
    }
    
    public String getName() {
        return "hmac-md5";
    }
}
