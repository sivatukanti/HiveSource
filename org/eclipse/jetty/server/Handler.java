// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.component.LifeCycle;

@ManagedObject("Jetty Handler")
public interface Handler extends LifeCycle, Destroyable
{
    void handle(final String p0, final Request p1, final HttpServletRequest p2, final HttpServletResponse p3) throws IOException, ServletException;
    
    void setServer(final Server p0);
    
    @ManagedAttribute(value = "the jetty server for this handler", readonly = true)
    Server getServer();
    
    @ManagedOperation(value = "destroy associated resources", impact = "ACTION")
    void destroy();
}
