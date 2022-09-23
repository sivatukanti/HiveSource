// 
// Decompiled by Procyon v0.5.36
// 

package javax.security.auth.message.config;

import javax.security.auth.message.MessageInfo;

public interface AuthConfig
{
    String getAppContext();
    
    String getAuthContextID(final MessageInfo p0) throws IllegalArgumentException;
    
    String getMessageLayer();
    
    boolean isProtected();
    
    void refresh();
}
