// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.webapp;

import java.io.Serializable;

public interface Configuration extends Serializable
{
    void setWebAppContext(final WebAppContext p0);
    
    WebAppContext getWebAppContext();
    
    void configureClassLoader() throws Exception;
    
    void configureDefaults() throws Exception;
    
    void configureWebApp() throws Exception;
    
    void deconfigureWebApp() throws Exception;
}
