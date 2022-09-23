// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

import java.util.EventObject;

public class ELContextEvent extends EventObject
{
    public ELContextEvent(final ELContext source) {
        super(source);
    }
    
    public ELContext getELContext() {
        return (ELContext)this.getSource();
    }
}
