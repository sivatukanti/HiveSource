// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

import java.util.HashMap;
import java.util.Locale;

public abstract class ELContext
{
    private Locale locale;
    private boolean resolved;
    private HashMap map;
    
    public ELContext() {
        this.map = new HashMap();
    }
    
    public void setPropertyResolved(final boolean resolved) {
        this.resolved = resolved;
    }
    
    public boolean isPropertyResolved() {
        return this.resolved;
    }
    
    public void putContext(final Class key, final Object contextObject) {
        if (key == null || contextObject == null) {
            throw new NullPointerException();
        }
        this.map.put(key, contextObject);
    }
    
    public Object getContext(final Class key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return this.map.get(key);
    }
    
    public abstract ELResolver getELResolver();
    
    public abstract FunctionMapper getFunctionMapper();
    
    public Locale getLocale() {
        return this.locale;
    }
    
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
    
    public abstract VariableMapper getVariableMapper();
}
