// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.cglib.core;

import java.security.AccessController;
import java.io.IOException;
import com.google.inject.internal.asm.$ClassVisitor;
import com.google.inject.internal.asm.util.$TraceClassVisitor;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import com.google.inject.internal.asm.$ClassReader;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.security.PrivilegedAction;
import com.google.inject.internal.asm.$ClassWriter;

public class $DebuggingClassWriter extends $ClassWriter
{
    public static final String DEBUG_LOCATION_PROPERTY = "cglib.debugLocation";
    private static String debugLocation;
    private static boolean traceEnabled;
    private String className;
    private String superName;
    
    public $DebuggingClassWriter(final int flags) {
        super(flags);
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.className = name.replace('/', '.');
        this.superName = superName.replace('/', '.');
        super.visit(version, access, name, signature, superName, interfaces);
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getSuperName() {
        return this.superName;
    }
    
    public byte[] toByteArray() {
        return AccessController.doPrivileged((PrivilegedAction<byte[]>)new PrivilegedAction() {
            public Object run() {
                final byte[] b = $ClassWriter.this.toByteArray();
                if ($DebuggingClassWriter.debugLocation != null) {
                    final String dirs = $DebuggingClassWriter.this.className.replace('.', File.separatorChar);
                    try {
                        new File($DebuggingClassWriter.debugLocation + File.separatorChar + dirs).getParentFile().mkdirs();
                        File file = new File(new File($DebuggingClassWriter.debugLocation), dirs + ".class");
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                        try {
                            out.write(b);
                        }
                        finally {
                            out.close();
                        }
                        if ($DebuggingClassWriter.traceEnabled) {
                            file = new File(new File($DebuggingClassWriter.debugLocation), dirs + ".asm");
                            out = new BufferedOutputStream(new FileOutputStream(file));
                            try {
                                final $ClassReader cr = new $ClassReader(b);
                                final PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                                final $TraceClassVisitor tcv = new $TraceClassVisitor(($ClassVisitor)null, pw);
                                cr.accept(($ClassVisitor)tcv, 0);
                                pw.flush();
                            }
                            finally {
                                out.close();
                            }
                        }
                    }
                    catch (IOException e) {
                        throw new $CodeGenerationException(e);
                    }
                }
                return b;
            }
        });
    }
    
    static {
        $DebuggingClassWriter.debugLocation = System.getProperty("cglib.debugLocation");
        if ($DebuggingClassWriter.debugLocation != null) {
            System.err.println("CGLIB debugging enabled, writing to '" + $DebuggingClassWriter.debugLocation + "'");
            try {
                Class.forName("com.google.inject.internal.asm.util.$TraceClassVisitor");
                $DebuggingClassWriter.traceEnabled = true;
            }
            catch (Throwable t) {}
        }
    }
}
