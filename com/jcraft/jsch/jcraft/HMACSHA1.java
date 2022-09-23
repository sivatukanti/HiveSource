// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jcraft;

import java.security.MessageDigest;
import com.jcraft.jsch.MAC;

public class HMACSHA1 extends HMAC implements MAC
{
    private static final String name = "hmac-sha1";
    
    public HMACSHA1() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        }
        catch (Exception e) {
            System.err.println(e);
        }
        this.setH(md);
    }
    
    public String getName() {
        return "hmac-sha1";
    }
}
