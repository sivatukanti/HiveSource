// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth.pkinit;

public class PkinitRequestOpts
{
    public boolean requireEku;
    public boolean acceptSecondaryEku;
    public boolean allowUpn;
    public boolean usingRsa;
    public boolean requireCrlChecking;
    public int dhSize;
    public boolean requireHostnameMatch;
    
    public PkinitRequestOpts() {
        this.requireEku = true;
        this.acceptSecondaryEku = false;
        this.allowUpn = true;
        this.usingRsa = false;
        this.requireCrlChecking = false;
        this.dhSize = 1024;
        this.requireHostnameMatch = true;
    }
}
