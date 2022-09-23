// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "delegation-token")
@XmlAccessorType(XmlAccessType.FIELD)
public class DelegationToken
{
    String token;
    String renewer;
    String owner;
    String kind;
    @XmlElement(name = "expiration-time")
    Long nextExpirationTime;
    @XmlElement(name = "max-validity")
    Long maxValidity;
    
    public DelegationToken() {
    }
    
    public DelegationToken(final String token, final String renewer, final String owner, final String kind, final Long nextExpirationTime, final Long maxValidity) {
        this.token = token;
        this.renewer = renewer;
        this.owner = owner;
        this.kind = kind;
        this.nextExpirationTime = nextExpirationTime;
        this.maxValidity = maxValidity;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public String getRenewer() {
        return this.renewer;
    }
    
    public Long getNextExpirationTime() {
        return this.nextExpirationTime;
    }
    
    public void setToken(final String token) {
        this.token = token;
    }
    
    public void setRenewer(final String renewer) {
        this.renewer = renewer;
    }
    
    public void setNextExpirationTime(final long nextExpirationTime) {
        this.nextExpirationTime = nextExpirationTime;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Long getMaxValidity() {
        return this.maxValidity;
    }
    
    public void setOwner(final String owner) {
        this.owner = owner;
    }
    
    public void setKind(final String kind) {
        this.kind = kind;
    }
    
    public void setMaxValidity(final Long maxValidity) {
        this.maxValidity = maxValidity;
    }
}
