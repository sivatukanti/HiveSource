// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.reflect;

import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.loader.ClassFactoryContext;
import java.io.InputStream;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.services.locks.ShExQual;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.loader.JarReader;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.locks.ShExLockable;
import org.apache.derby.iapi.services.locks.LockFactory;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import org.apache.derby.iapi.services.locks.LockOwner;

final class UpdateLoader implements LockOwner
{
    private static final String[] RESTRICTED_PACKAGES;
    private JarLoader[] jarList;
    private HeaderPrintWriter vs;
    private final ClassLoader myLoader;
    private boolean initDone;
    private String thisClasspath;
    private final LockFactory lf;
    private final ShExLockable classLoaderLock;
    private int version;
    private boolean normalizeToUpper;
    private DatabaseClasses parent;
    private final CompatibilitySpace compat;
    private boolean needReload;
    private JarReader jarReader;
    
    UpdateLoader(final String s, final DatabaseClasses parent, final boolean b, final boolean normalizeToUpper) throws StandardException {
        this.normalizeToUpper = normalizeToUpper;
        this.parent = parent;
        this.lf = (LockFactory)Monitor.getServiceModule(parent, "org.apache.derby.iapi.services.locks.LockFactory");
        this.compat = ((this.lf != null) ? this.lf.createCompatibilitySpace(this) : null);
        if (b) {
            this.vs = Monitor.getStream();
        }
        this.myLoader = this.getClass().getClassLoader();
        this.classLoaderLock = new ClassLoaderLock(this);
        this.initializeFromClassPath(s);
    }
    
    private void initializeFromClassPath(final String thisClasspath) throws StandardException {
        final String[][] dbClassPath = IdUtil.parseDbClassPath(thisClasspath);
        final int length = dbClassPath.length;
        this.jarList = new JarLoader[length];
        if (length != 0) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                public Object run() {
                    for (int i = 0; i < length; ++i) {
                        UpdateLoader.this.jarList[i] = new JarLoader(UpdateLoader.this, dbClassPath[i], UpdateLoader.this.vs);
                    }
                    return null;
                }
            });
        }
        if (this.vs != null) {
            this.vs.println(MessageService.getTextMessage("C005", thisClasspath));
        }
        this.thisClasspath = thisClasspath;
        this.initDone = false;
    }
    
    Class loadClass(final String s, final boolean b) throws ClassNotFoundException {
        JarLoader jarLoader = null;
        boolean lockClassLoader = false;
        try {
            lockClassLoader = this.lockClassLoader(ShExQual.SH);
            synchronized (this) {
                if (this.needReload) {
                    this.reload();
                }
                final Class checkLoaded = this.checkLoaded(s, b);
                if (checkLoaded != null) {
                    return checkLoaded;
                }
                for (int i = 0; i < UpdateLoader.RESTRICTED_PACKAGES.length; ++i) {
                    if (s.startsWith(UpdateLoader.RESTRICTED_PACKAGES[i])) {
                        throw new ClassNotFoundException(s);
                    }
                }
                final String concat = s.replace('.', '/').concat(".class");
                if (!this.initDone) {
                    this.initLoaders();
                }
                for (int j = 0; j < this.jarList.length; ++j) {
                    jarLoader = this.jarList[j];
                    final Class loadClassData = jarLoader.loadClassData(s, concat, b);
                    if (loadClassData != null) {
                        if (this.vs != null) {
                            this.vs.println(MessageService.getTextMessage("C006", s, jarLoader.getJarName()));
                        }
                        return loadClassData;
                    }
                }
            }
            return null;
        }
        catch (StandardException ex) {
            throw new ClassNotFoundException(MessageService.getTextMessage("C007", s, (jarLoader == null) ? null : jarLoader.getJarName(), ex));
        }
        finally {
            if (lockClassLoader) {
                this.lf.unlock(this.compat, this, this.classLoaderLock, ShExQual.SH);
            }
        }
    }
    
    InputStream getResourceAsStream(final String s) {
        final InputStream inputStream = (this.myLoader == null) ? ClassLoader.getSystemResourceAsStream(s) : this.myLoader.getResourceAsStream(s);
        if (inputStream != null) {
            return inputStream;
        }
        if (s.endsWith(".class")) {
            return null;
        }
        boolean lockClassLoader = false;
        try {
            lockClassLoader = this.lockClassLoader(ShExQual.SH);
            synchronized (this) {
                if (this.needReload) {
                    this.reload();
                }
                if (!this.initDone) {
                    this.initLoaders();
                }
                for (int i = 0; i < this.jarList.length; ++i) {
                    final InputStream stream = this.jarList[i].getStream(s);
                    if (stream != null) {
                        return stream;
                    }
                }
            }
            return null;
        }
        catch (StandardException ex) {
            return null;
        }
        finally {
            if (lockClassLoader) {
                this.lf.unlock(this.compat, this, this.classLoaderLock, ShExQual.SH);
            }
        }
    }
    
    synchronized void modifyClasspath(final String s) throws StandardException {
        this.lockClassLoader(ShExQual.EX);
        ++this.version;
        this.modifyJar(false);
        this.initializeFromClassPath(s);
    }
    
    synchronized void modifyJar(final boolean b) throws StandardException {
        this.lockClassLoader(ShExQual.EX);
        ++this.version;
        if (!this.initDone) {
            return;
        }
        this.close();
        if (b) {
            this.initializeFromClassPath(this.thisClasspath);
        }
    }
    
    private boolean lockClassLoader(final ShExQual shExQual) throws StandardException {
        if (this.lf == null) {
            return false;
        }
        final ClassFactoryContext classFactoryContext = (ClassFactoryContext)ContextService.getContextOrNull("ClassFactoryContext");
        CompatibilitySpace compatibilitySpace = null;
        if (classFactoryContext != null) {
            compatibilitySpace = classFactoryContext.getLockSpace();
        }
        if (compatibilitySpace == null) {
            compatibilitySpace = this.compat;
        }
        final LockOwner owner = compatibilitySpace.getOwner();
        this.lf.lockObject(compatibilitySpace, owner, this.classLoaderLock, shExQual, -2);
        return owner == this;
    }
    
    Class checkLoaded(final String s, final boolean b) {
        for (int i = 0; i < this.jarList.length; ++i) {
            final Class checkLoaded = this.jarList[i].checkLoaded(s, b);
            if (checkLoaded != null) {
                return checkLoaded;
            }
        }
        return null;
    }
    
    void close() {
        for (int i = 0; i < this.jarList.length; ++i) {
            this.jarList[i].setInvalid();
        }
    }
    
    private void initLoaders() {
        if (this.initDone) {
            return;
        }
        for (int i = 0; i < this.jarList.length; ++i) {
            this.jarList[i].initialize();
        }
        this.initDone = true;
    }
    
    int getClassLoaderVersion() {
        return this.version;
    }
    
    synchronized void needReload() {
        ++this.version;
        this.needReload = true;
    }
    
    private void reload() throws StandardException {
        this.thisClasspath = this.getClasspath();
        this.close();
        this.initializeFromClassPath(this.thisClasspath);
        this.needReload = false;
    }
    
    private String getClasspath() throws StandardException {
        String serviceProperty = PropertyUtil.getServiceProperty(((ClassFactoryContext)ContextService.getContextOrNull("ClassFactoryContext")).getPersistentSet(), "derby.database.classpath");
        if (serviceProperty == null) {
            serviceProperty = "";
        }
        return serviceProperty;
    }
    
    JarReader getJarReader() {
        if (this.jarReader == null) {
            this.jarReader = ((ClassFactoryContext)ContextService.getContextOrNull("ClassFactoryContext")).getJarReader();
        }
        return this.jarReader;
    }
    
    public boolean noWait() {
        return false;
    }
    
    static {
        RESTRICTED_PACKAGES = new String[] { "javax.", "org.apache.derby." };
    }
}
