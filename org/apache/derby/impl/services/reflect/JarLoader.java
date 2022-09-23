// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.reflect;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.io.ByteArrayOutputStream;
import org.apache.derby.iapi.services.io.InputStreamUtil;
import org.apache.derby.iapi.services.io.AccessibleByteArrayOutputStream;
import java.security.cert.Certificate;
import java.security.CodeSource;
import java.util.jar.JarInputStream;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.io.FileNotFoundException;
import org.apache.derby.iapi.util.IdUtil;
import java.io.InputStream;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.error.StandardException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.io.File;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import java.util.jar.JarFile;
import org.apache.derby.io.StorageFile;
import java.security.SecureClassLoader;

final class JarLoader extends SecureClassLoader
{
    private final String[] name;
    private StorageFile installedJar;
    private JarFile jar;
    private boolean isStream;
    private UpdateLoader updateLoader;
    private HeaderPrintWriter vs;
    
    JarLoader(final UpdateLoader updateLoader, final String[] name, final HeaderPrintWriter vs) {
        this.updateLoader = updateLoader;
        this.name = name;
        this.vs = vs;
    }
    
    void initialize() {
        final String s = this.name[0];
        final String s2 = this.name[1];
        IOException ex2;
        try {
            this.installedJar = this.updateLoader.getJarReader().getJarFile(s, s2);
            if (this.installedJar instanceof File) {
                try {
                    this.jar = AccessController.doPrivileged((PrivilegedExceptionAction<JarFile>)new PrivilegedExceptionAction() {
                        public Object run() throws IOException {
                            return new JarFile((File)JarLoader.this.installedJar);
                        }
                    });
                }
                catch (PrivilegedActionException ex) {
                    throw (IOException)ex.getException();
                }
                return;
            }
            this.isStream = true;
            return;
        }
        catch (IOException ex3) {
            ex2 = ex3;
        }
        catch (StandardException ex4) {
            ex2 = (IOException)ex4;
        }
        if (this.vs != null) {
            this.vs.println(MessageService.getTextMessage("C003", this.getJarName(), ex2));
        }
        this.setInvalid();
    }
    
    protected Class loadClass(final String s, final boolean b) throws ClassNotFoundException {
        if (s.startsWith("org.apache.derby.") && !s.startsWith("org.apache.derby.jdbc.") && !s.startsWith("org.apache.derby.vti.") && !s.startsWith("org.apache.derby.agg.") && !s.startsWith("org.apache.derby.impl.tools.optional.")) {
            throw new ClassNotFoundException(s);
        }
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            if (this.updateLoader == null) {
                throw new ClassNotFoundException(MessageService.getTextMessage("C004", s));
            }
            final Class loadClass = this.updateLoader.loadClass(s, b);
            if (loadClass == null) {
                throw ex;
            }
            return loadClass;
        }
    }
    
    public InputStream getResourceAsStream(final String s) {
        if (this.updateLoader == null) {
            return null;
        }
        return this.updateLoader.getResourceAsStream(s);
    }
    
    final String getJarName() {
        return IdUtil.mkQualifiedName(this.name);
    }
    
    Class loadClassData(final String s, final String s2, final boolean b) {
        if (this.updateLoader == null) {
            return null;
        }
        try {
            if (this.jar != null) {
                return this.loadClassDataFromJar(s, s2, b);
            }
            if (this.isStream) {
                return this.loadClassData(this.installedJar.getInputStream(), s, s2, b);
            }
            return null;
        }
        catch (FileNotFoundException ex2) {
            return null;
        }
        catch (IOException ex) {
            if (this.vs != null) {
                this.vs.println(MessageService.getTextMessage("C007", s, this.getJarName(), ex));
            }
            return null;
        }
    }
    
    InputStream getStream(final String s) {
        if (this.updateLoader == null) {
            return null;
        }
        if (this.jar != null) {
            return this.getRawStream(s);
        }
        if (this.isStream) {
            try {
                return this.getRawStream(this.installedJar.getInputStream(), s);
            }
            catch (FileNotFoundException ex) {}
        }
        return null;
    }
    
    private Class loadClassDataFromJar(final String s, final String name, final boolean b) throws IOException {
        final JarEntry jarEntry = this.jar.getJarEntry(name);
        if (jarEntry == null) {
            return null;
        }
        final InputStream inputStream = this.jar.getInputStream(jarEntry);
        try {
            return this.loadClassData(jarEntry, inputStream, s, b);
        }
        finally {
            inputStream.close();
        }
    }
    
    private Class loadClassData(final InputStream in, final String s, final String anObject, final boolean b) throws IOException {
        final JarInputStream jarInputStream = new JarInputStream(in);
        while (true) {
            final JarEntry nextJarEntry = jarInputStream.getNextJarEntry();
            if (nextJarEntry == null) {
                jarInputStream.close();
                return null;
            }
            if (nextJarEntry.getName().equals(anObject)) {
                final Class loadClassData = this.loadClassData(nextJarEntry, jarInputStream, s, b);
                jarInputStream.close();
                return loadClassData;
            }
        }
    }
    
    private Class loadClassData(final JarEntry jarEntry, final InputStream inputStream, final String name, final boolean b) throws IOException {
        final byte[] data = this.readData(jarEntry, inputStream, name);
        final Certificate[] signers = this.getSigners(name, jarEntry);
        synchronized (this.updateLoader) {
            Class<?> clazz = (Class<?>)this.updateLoader.checkLoaded(name, b);
            if (clazz == null) {
                clazz = this.defineClass(name, data, 0, data.length, (CodeSource)null);
                if (signers != null) {
                    this.setSigners(clazz, signers);
                }
                if (b) {
                    this.resolveClass(clazz);
                }
            }
            return clazz;
        }
    }
    
    Class checkLoaded(final String name, final boolean b) {
        if (this.updateLoader == null) {
            return null;
        }
        final Class<?> loadedClass = this.findLoadedClass(name);
        if (loadedClass != null && b) {
            this.resolveClass(loadedClass);
        }
        return loadedClass;
    }
    
    void setInvalid() {
        this.updateLoader = null;
        if (this.jar != null) {
            try {
                this.jar.close();
            }
            catch (IOException ex) {}
            this.jar = null;
        }
        this.isStream = false;
    }
    
    private InputStream getRawStream(final String name) {
        try {
            final JarEntry jarEntry = this.jar.getJarEntry(name);
            if (jarEntry == null) {
                return null;
            }
            return this.jar.getInputStream(jarEntry);
        }
        catch (IOException ex) {
            return null;
        }
    }
    
    private InputStream getRawStream(final InputStream in, final String anObject) {
        JarInputStream jarInputStream = null;
        try {
            jarInputStream = new JarInputStream(in);
            JarEntry nextJarEntry;
            while ((nextJarEntry = jarInputStream.getNextJarEntry()) != null) {
                if (nextJarEntry.getName().equals(anObject)) {
                    int n = (int)nextJarEntry.getSize();
                    if (n == -1) {
                        n = 8192;
                    }
                    return AccessibleByteArrayOutputStream.copyStream(jarInputStream, n);
                }
            }
        }
        catch (IOException ex) {}
        finally {
            if (jarInputStream != null) {
                try {
                    jarInputStream.close();
                }
                catch (IOException ex2) {}
            }
        }
        return null;
    }
    
    byte[] readData(final JarEntry jarEntry, final InputStream inputStream, final String s) throws IOException {
        try {
            final int n = (int)jarEntry.getSize();
            if (n != -1) {
                final byte[] array = new byte[n];
                InputStreamUtil.readFully(inputStream, array, 0, n);
                return array;
            }
            final byte[] array2 = new byte[1024];
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            int read;
            while ((read = inputStream.read(array2)) != -1) {
                byteArrayOutputStream.write(array2, 0, read);
            }
            return byteArrayOutputStream.toByteArray();
        }
        catch (SecurityException ex) {
            throw this.handleException(ex, s);
        }
    }
    
    private Certificate[] getSigners(final String s, final JarEntry jarEntry) throws IOException {
        try {
            final Certificate[] certificates = jarEntry.getCertificates();
            if (certificates == null || certificates.length == 0) {
                return null;
            }
            for (int i = 0; i < certificates.length; ++i) {
                if (!(certificates[i] instanceof X509Certificate)) {
                    throw new SecurityException(MessageService.getTextMessage("C001", s, this.getJarName()));
                }
                ((X509Certificate)certificates[i]).checkValidity();
            }
            return certificates;
        }
        catch (GeneralSecurityException ex) {
            throw this.handleException(ex, s);
        }
    }
    
    private SecurityException handleException(final Exception ex, final String s) {
        return new SecurityException(MessageService.getTextMessage("C002", s, this.getJarName(), ex.getLocalizedMessage()));
    }
    
    public String toString() {
        return this.getJarName() + ":" + super.toString();
    }
}
