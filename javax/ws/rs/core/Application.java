// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs.core;

import java.util.Collections;
import java.util.Set;

public class Application
{
    private static final Set<Object> emptyObjectSet;
    private static final Set<Class<?>> emptyClassSet;
    
    public Set<Class<?>> getClasses() {
        return Application.emptyClassSet;
    }
    
    public Set<Object> getSingletons() {
        return Application.emptyObjectSet;
    }
    
    static {
        emptyObjectSet = Collections.emptySet();
        emptyClassSet = Collections.emptySet();
    }
}
