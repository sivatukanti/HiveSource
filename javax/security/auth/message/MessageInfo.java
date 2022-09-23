// 
// Decompiled by Procyon v0.5.36
// 

package javax.security.auth.message;

import java.util.Map;

public interface MessageInfo
{
    Map getMap();
    
    Object getRequestMessage();
    
    Object getResponseMessage();
    
    void setRequestMessage(final Object p0);
    
    void setResponseMessage(final Object p0);
}
