// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.reflect;

import java.security.AccessController;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.util.ByteArray;
import java.util.HashMap;
import java.security.PrivilegedAction;

public class ReflectClassesJava2 extends DatabaseClasses implements PrivilegedAction
{
    private HashMap preCompiled;
    private int action;
    
    public ReflectClassesJava2() {
        this.action = -1;
    }
    
    synchronized LoadedGeneratedClass loadGeneratedClassFromData(final String key, final ByteArray byteArray) {
        if (byteArray == null || byteArray.getArray() == null) {
            if (this.preCompiled == null) {
                this.preCompiled = new HashMap();
            }
            else {
                final ReflectGeneratedClass reflectGeneratedClass = this.preCompiled.get(key);
                if (reflectGeneratedClass != null) {
                    return reflectGeneratedClass;
                }
            }
            try {
                final ReflectGeneratedClass value = new ReflectGeneratedClass(this, Class.forName(key));
                this.preCompiled.put(key, value);
                return value;
            }
            catch (ClassNotFoundException ex) {
                throw new NoClassDefFoundError(ex.toString());
            }
        }
        this.action = 1;
        return AccessController.doPrivileged((PrivilegedAction<ReflectLoaderJava2>)this).loadGeneratedClass(key, byteArray);
    }
    
    public final Object run() {
        try {
            switch (this.action) {
                case 1: {
                    return new ReflectLoaderJava2(this.getClass().getClassLoader(), this);
                }
                case 2: {
                    return Thread.currentThread().getContextClassLoader();
                }
                default: {
                    return null;
                }
            }
        }
        finally {
            this.action = -1;
        }
    }
    
    Class loadClassNotInDatabaseJar(final String className) throws ClassNotFoundException {
        Class<?> forName;
        try {
            final ClassLoader classLoader;
            synchronized (this) {
                this.action = 2;
                classLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)this);
            }
            forName = ((classLoader != null) ? classLoader.loadClass(className) : Class.forName(className));
        }
        catch (ClassNotFoundException ex) {
            forName = Class.forName(className);
        }
        return forName;
    }
}
