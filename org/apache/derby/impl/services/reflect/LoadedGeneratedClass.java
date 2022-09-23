// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.reflect;

import org.apache.derby.iapi.error.StandardException;
import java.lang.reflect.InvocationTargetException;
import org.apache.derby.iapi.services.loader.GeneratedByteCode;
import org.apache.derby.iapi.services.context.Context;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.services.loader.ClassInfo;
import org.apache.derby.iapi.services.loader.GeneratedClass;

public abstract class LoadedGeneratedClass implements GeneratedClass
{
    private final ClassInfo ci;
    private final int classLoaderVersion;
    
    public LoadedGeneratedClass(final ClassFactory classFactory, final Class clazz) {
        this.ci = new ClassInfo(clazz);
        this.classLoaderVersion = classFactory.getClassLoaderVersion();
    }
    
    public String getName() {
        return this.ci.getClassName();
    }
    
    public Object newInstance(final Context context) throws StandardException {
        InstantiationException ex;
        try {
            final GeneratedByteCode generatedByteCode = (GeneratedByteCode)this.ci.getNewInstance();
            generatedByteCode.initFromContext(context);
            generatedByteCode.setGC(this);
            generatedByteCode.postConstructor();
            return generatedByteCode;
        }
        catch (InstantiationException ex2) {
            ex = ex2;
        }
        catch (IllegalAccessException ex3) {
            ex = (InstantiationException)ex3;
        }
        catch (InvocationTargetException ex4) {
            ex = (InstantiationException)ex4;
        }
        catch (LinkageError linkageError) {
            ex = (InstantiationException)linkageError;
        }
        throw StandardException.newException("XBCM2.S", ex, this.getName());
    }
    
    public final int getClassLoaderVersion() {
        return this.classLoaderVersion;
    }
    
    protected Class getJVMClass() {
        return this.ci.getClassObject();
    }
}
