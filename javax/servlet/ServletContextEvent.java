// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet;

import java.util.EventObject;

public class ServletContextEvent extends EventObject
{
    private static final long serialVersionUID = -7501701636134222423L;
    
    public ServletContextEvent(final ServletContext source) {
        super(source);
    }
    
    public ServletContext getServletContext() {
        return (ServletContext)super.getSource();
    }
}
