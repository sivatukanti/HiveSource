// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet;

public class ServletRequestAttributeEvent extends ServletRequestEvent
{
    private static final long serialVersionUID = -1466635426192317793L;
    private String name;
    private Object value;
    
    public ServletRequestAttributeEvent(final ServletContext sc, final ServletRequest request, final String name, final Object value) {
        super(sc, request);
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Object getValue() {
        return this.value;
    }
}
