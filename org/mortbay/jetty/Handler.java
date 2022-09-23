// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.component.LifeCycle;

public interface Handler extends LifeCycle
{
    public static final int DEFAULT = 0;
    public static final int REQUEST = 1;
    public static final int FORWARD = 2;
    public static final int INCLUDE = 4;
    public static final int ERROR = 8;
    public static final int ALL = 15;
    
    void handle(final String p0, final HttpServletRequest p1, final HttpServletResponse p2, final int p3) throws IOException, ServletException;
    
    void setServer(final Server p0);
    
    Server getServer();
    
    void destroy();
}
