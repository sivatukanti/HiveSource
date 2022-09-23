// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth.pkinit;

import java.util.ArrayList;
import java.util.List;

public class IdentityOpts
{
    public String identity;
    public List<String> altIdentities;
    public List<String> anchors;
    public List<String> intermediates;
    public List<String> crls;
    public String ocsp;
    public IdentityType idType;
    public String certFile;
    public String keyFile;
    public String p11ModuleName;
    public int slotid;
    public String tokenLabel;
    public String certId;
    public String certLabel;
    
    public IdentityOpts() {
        this.altIdentities = new ArrayList<String>(1);
        this.anchors = new ArrayList<String>(4);
        this.intermediates = new ArrayList<String>(2);
        this.crls = new ArrayList<String>(2);
    }
}
