// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.reflect;

import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.util.ByteArray;

final class ReflectLoaderJava2 extends ClassLoader
{
    private final DatabaseClasses cf;
    
    ReflectLoaderJava2(final ClassLoader parent, final DatabaseClasses cf) {
        super(parent);
        this.cf = cf;
    }
    
    protected Class findClass(final String s) throws ClassNotFoundException {
        return this.cf.loadApplicationClass(s);
    }
    
    public LoadedGeneratedClass loadGeneratedClass(final String name, final ByteArray byteArray) {
        final Class<?> defineClass = this.defineClass(name, byteArray.getArray(), byteArray.getOffset(), byteArray.getLength());
        this.resolveClass(defineClass);
        return new ReflectGeneratedClass(this.cf, defineClass);
    }
}
