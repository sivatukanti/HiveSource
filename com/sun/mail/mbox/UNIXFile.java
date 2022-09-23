// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.util.StringTokenizer;
import java.io.FileDescriptor;
import java.io.File;

public class UNIXFile extends File
{
    protected static final boolean loaded;
    private static final long serialVersionUID = -7972156315284146651L;
    
    public UNIXFile(final String name) {
        super(name);
    }
    
    public static long lastAccessed(final File file) {
        return lastAccessed0(file.getPath());
    }
    
    public long lastAccessed() {
        return lastAccessed0(this.getPath());
    }
    
    private static native void initIDs(final Class p0, final FileDescriptor p1);
    
    public static boolean lock(final FileDescriptor fd, final String mode) {
        return lock(fd, mode, false);
    }
    
    private static boolean lock(final FileDescriptor fd, final String mode, final boolean block) {
        if (UNIXFile.loaded) {
            final boolean ret = lock0(fd, mode, block);
            return ret;
        }
        return false;
    }
    
    private static native boolean lock0(final FileDescriptor p0, final String p1, final boolean p2);
    
    public static native long lastAccessed0(final String p0);
    
    static {
        boolean lloaded = false;
        try {
            System.loadLibrary("mbox");
            lloaded = true;
        }
        catch (UnsatisfiedLinkError e) {
            final String classpath = System.getProperty("java.class.path");
            final String sep = System.getProperty("path.separator");
            final StringTokenizer st = new StringTokenizer(classpath, sep);
            while (st.hasMoreTokens()) {
                final String path = st.nextToken();
                int i = path.length() - 7;
                if (i > 0 && path.substring(i).equals("classes")) {
                    final String lib = path.substring(0, i) + "lib/sparc/libmbox.so";
                    try {
                        System.load(lib);
                        lloaded = true;
                        break;
                    }
                    catch (UnsatisfiedLinkError e2) {
                        continue;
                    }
                }
                if ((i = path.length() - 8) > 0 && path.substring(i).equals("mail.jar")) {
                    final String lib = path.substring(0, i) + "lib/sparc/libmbox.so";
                    try {
                        System.load(lib);
                        lloaded = true;
                        break;
                    }
                    catch (UnsatisfiedLinkError e2) {}
                }
            }
        }
        loaded = lloaded;
        if (UNIXFile.loaded) {
            initIDs(FileDescriptor.class, FileDescriptor.in);
        }
    }
}
