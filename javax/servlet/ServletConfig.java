// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet;

import java.util.Enumeration;

public interface ServletConfig
{
    String getServletName();
    
    ServletContext getServletContext();
    
    String getInitParameter(final String p0);
    
    Enumeration<String> getInitParameterNames();
}
