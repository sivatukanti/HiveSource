// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.loader;

import java.io.ObjectStreamClass;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.ByteArray;

public interface ClassFactory
{
    GeneratedClass loadGeneratedClass(final String p0, final ByteArray p1) throws StandardException;
    
    ClassInspector getClassInspector();
    
    Class loadApplicationClass(final String p0) throws ClassNotFoundException;
    
    Class loadApplicationClass(final ObjectStreamClass p0) throws ClassNotFoundException;
    
    boolean isApplicationClass(final Class p0);
    
    void notifyModifyJar(final boolean p0) throws StandardException;
    
    void notifyModifyClasspath(final String p0) throws StandardException;
    
    int getClassLoaderVersion();
}
