// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.loader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

public class ClassInfo implements InstanceGetter
{
    private static final Class[] noParameters;
    private static final Object[] noArguments;
    private final Class clazz;
    private boolean useConstructor;
    private Constructor noArgConstructor;
    
    public ClassInfo(final Class clazz) {
        this.useConstructor = true;
        this.clazz = clazz;
    }
    
    public final String getClassName() {
        return this.clazz.getName();
    }
    
    public final Class getClassObject() {
        return this.clazz;
    }
    
    public Object getNewInstance() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (!this.useConstructor) {
            return this.clazz.newInstance();
        }
        if (this.noArgConstructor == null) {
            try {
                this.noArgConstructor = this.clazz.getConstructor((Class[])ClassInfo.noParameters);
            }
            catch (NoSuchMethodException ex) {
                this.useConstructor = false;
                return this.getNewInstance();
            }
            catch (SecurityException ex2) {
                this.useConstructor = false;
                return this.getNewInstance();
            }
        }
        try {
            return this.noArgConstructor.newInstance(ClassInfo.noArguments);
        }
        catch (IllegalArgumentException ex3) {
            return null;
        }
    }
    
    static {
        noParameters = new Class[0];
        noArguments = new Object[0];
    }
}
