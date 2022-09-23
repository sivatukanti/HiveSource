// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.http;

public class HttpSessionBindingEvent extends HttpSessionEvent
{
    private static final long serialVersionUID = 7308000419984825907L;
    private String name;
    private Object value;
    
    public HttpSessionBindingEvent(final HttpSession session, final String name) {
        super(session);
        this.name = name;
    }
    
    public HttpSessionBindingEvent(final HttpSession session, final String name, final Object value) {
        super(session);
        this.name = name;
        this.value = value;
    }
    
    @Override
    public HttpSession getSession() {
        return super.getSession();
    }
    
    public String getName() {
        return this.name;
    }
    
    public Object getValue() {
        return this.value;
    }
}
