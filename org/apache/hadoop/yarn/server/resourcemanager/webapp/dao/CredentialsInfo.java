// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "credentials-info")
@XmlAccessorType(XmlAccessType.FIELD)
public class CredentialsInfo
{
    @XmlElementWrapper(name = "tokens")
    HashMap<String, String> tokens;
    @XmlElementWrapper(name = "secrets")
    HashMap<String, String> secrets;
    
    public CredentialsInfo() {
        this.tokens = new HashMap<String, String>();
        this.secrets = new HashMap<String, String>();
    }
    
    public HashMap<String, String> getTokens() {
        return this.tokens;
    }
    
    public HashMap<String, String> getSecrets() {
        return this.secrets;
    }
    
    public void setTokens(final HashMap<String, String> tokens) {
        this.tokens = tokens;
    }
    
    public void setSecrets(final HashMap<String, String> secrets) {
        this.secrets = secrets;
    }
}
