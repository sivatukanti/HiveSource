// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet;

import java.util.EventObject;

public class ServletRequestEvent extends EventObject
{
    private static final long serialVersionUID = -7467864054698729101L;
    private final transient ServletRequest request;
    
    public ServletRequestEvent(final ServletContext sc, final ServletRequest request) {
        super(sc);
        this.request = request;
    }
    
    public ServletRequest getServletRequest() {
        return this.request;
    }
    
    public ServletContext getServletContext() {
        return (ServletContext)super.getSource();
    }
}
