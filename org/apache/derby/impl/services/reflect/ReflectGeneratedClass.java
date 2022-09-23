// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.reflect;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import java.util.Hashtable;

public final class ReflectGeneratedClass extends LoadedGeneratedClass
{
    private final Hashtable methodCache;
    private static final GeneratedMethod[] directs;
    
    public ReflectGeneratedClass(final ClassFactory classFactory, final Class clazz) {
        super(classFactory, clazz);
        this.methodCache = new Hashtable();
    }
    
    public GeneratedMethod getMethod(final String key) throws StandardException {
        final GeneratedMethod generatedMethod = this.methodCache.get(key);
        if (generatedMethod != null) {
            return generatedMethod;
        }
        try {
            GeneratedMethod value;
            if (key.length() == 2 && key.startsWith("e")) {
                value = ReflectGeneratedClass.directs[key.charAt(1) - '0'];
            }
            else {
                value = new ReflectMethod(this.getJVMClass().getMethod(key, (Class[])null));
            }
            this.methodCache.put(key, value);
            return value;
        }
        catch (NoSuchMethodException ex) {
            throw StandardException.newException("XBCM3.S", ex, this.getName(), key);
        }
    }
    
    static {
        directs = new GeneratedMethod[10];
        for (int i = 0; i < ReflectGeneratedClass.directs.length; ++i) {
            ReflectGeneratedClass.directs[i] = new DirectCall(i);
        }
    }
}
