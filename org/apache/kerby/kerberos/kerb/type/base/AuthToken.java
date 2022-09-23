// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import java.util.Map;
import java.util.Date;
import java.util.List;

public interface AuthToken
{
    String getSubject();
    
    void setSubject(final String p0);
    
    String getIssuer();
    
    void setIssuer(final String p0);
    
    List<String> getAudiences();
    
    void setAudiences(final List<String> p0);
    
    boolean isIdToken();
    
    void isIdToken(final boolean p0);
    
    boolean isAcToken();
    
    void isAcToken(final boolean p0);
    
    boolean isBearerToken();
    
    boolean isHolderOfKeyToken();
    
    Date getExpiredTime();
    
    void setExpirationTime(final Date p0);
    
    Date getNotBeforeTime();
    
    void setNotBeforeTime(final Date p0);
    
    Date getIssueTime();
    
    void setIssueTime(final Date p0);
    
    Map<String, Object> getAttributes();
    
    void addAttribute(final String p0, final Object p1);
}
