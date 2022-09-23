// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.reflect;

import java.io.ObjectStreamClass;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.io.FileOutputStream;
import org.apache.derby.iapi.services.io.FileUtil;
import java.io.File;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.loader.GeneratedClass;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.iapi.services.loader.ClassInspector;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.services.loader.ClassFactory;

abstract class DatabaseClasses implements ClassFactory, ModuleControl
{
    private ClassInspector classInspector;
    private UpdateLoader applicationLoader;
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.classInspector = this.makeClassInspector(this);
        String property = null;
        if (properties != null) {
            property = properties.getProperty("derby.__rt.database.classpath");
        }
        if (property != null) {
            this.applicationLoader = new UpdateLoader(property, this, true, true);
        }
    }
    
    public void stop() {
        if (this.applicationLoader != null) {
            this.applicationLoader.close();
        }
    }
    
    protected ClassInspector makeClassInspector(final DatabaseClasses databaseClasses) {
        return new ClassInspector(databaseClasses);
    }
    
    public final GeneratedClass loadGeneratedClass(final String s, final ByteArray byteArray) throws StandardException {
        try {
            return this.loadGeneratedClassFromData(s, byteArray);
        }
        catch (LinkageError linkageError) {
            WriteClassFile(s, byteArray, linkageError);
            throw StandardException.newException("XBCM1.S", linkageError, s);
        }
        catch (VirtualMachineError virtualMachineError) {
            WriteClassFile(s, byteArray, virtualMachineError);
            throw virtualMachineError;
        }
    }
    
    private static void WriteClassFile(final String s, final ByteArray byteArray, final Throwable t) {
        final String concat = s.substring(s.lastIndexOf(46) + 1, s.length()).concat(".class");
        final Object environment = Monitor.getMonitor().getEnvironment();
        final File file = FileUtil.newFile((environment instanceof File) ? ((File)environment) : null, concat);
        final HeaderPrintWriter stream = Monitor.getStream();
        try {
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = AccessController.doPrivileged((PrivilegedExceptionAction<FileOutputStream>)new PrivilegedExceptionAction() {
                    public Object run() throws IOException {
                        return new FileOutputStream(file);
                    }
                });
            }
            catch (PrivilegedActionException ex) {
                throw (IOException)ex.getCause();
            }
            fileOutputStream.write(byteArray.getArray(), byteArray.getOffset(), byteArray.getLength());
            fileOutputStream.flush();
            if (t != null) {
                stream.printlnWithHeader(MessageService.getTextMessage("C000", s, file, t));
            }
            fileOutputStream.close();
        }
        catch (IOException ex2) {}
    }
    
    public ClassInspector getClassInspector() {
        return this.classInspector;
    }
    
    public final Class loadApplicationClass(final String s) throws ClassNotFoundException {
        while (true) {
            if (s.startsWith("org.apache.derby.")) {
                try {
                    return Class.forName(s);
                }
                catch (ClassNotFoundException ex4) {}
                SecurityException ex2;
                try {
                    try {
                        return this.loadClassNotInDatabaseJar(s);
                    }
                    catch (ClassNotFoundException ex) {
                        if (this.applicationLoader == null) {
                            throw ex;
                        }
                        final Class loadClass = this.applicationLoader.loadClass(s, true);
                        if (loadClass == null) {
                            throw ex;
                        }
                        return loadClass;
                    }
                }
                catch (SecurityException ex3) {
                    ex2 = ex3;
                }
                catch (LinkageError linkageError) {
                    ex2 = (SecurityException)linkageError;
                }
                throw new ClassNotFoundException(s + " : " + ex2.getMessage());
            }
            continue;
        }
    }
    
    abstract Class loadClassNotInDatabaseJar(final String p0) throws ClassNotFoundException;
    
    public final Class loadApplicationClass(final ObjectStreamClass objectStreamClass) throws ClassNotFoundException {
        return this.loadApplicationClass(objectStreamClass.getName());
    }
    
    public boolean isApplicationClass(final Class clazz) {
        return clazz.getClassLoader() instanceof JarLoader;
    }
    
    public void notifyModifyJar(final boolean b) throws StandardException {
        if (this.applicationLoader != null) {
            this.applicationLoader.modifyJar(b);
        }
    }
    
    public void notifyModifyClasspath(final String s) throws StandardException {
        if (this.applicationLoader != null) {
            this.applicationLoader.modifyClasspath(s);
        }
    }
    
    public int getClassLoaderVersion() {
        if (this.applicationLoader != null) {
            return this.applicationLoader.getClassLoaderVersion();
        }
        return -1;
    }
    
    abstract LoadedGeneratedClass loadGeneratedClassFromData(final String p0, final ByteArray p1);
}
