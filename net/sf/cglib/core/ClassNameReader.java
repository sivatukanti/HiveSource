// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.core;

import org.objectweb.asm.ClassVisitor;
import java.util.List;
import org.objectweb.asm.ClassAdapter;
import java.util.ArrayList;
import org.objectweb.asm.ClassReader;

public class ClassNameReader
{
    private static final EarlyExitException EARLY_EXIT;
    
    private ClassNameReader() {
    }
    
    public static String getClassName(final ClassReader r) {
        return getClassInfo(r)[0];
    }
    
    public static String[] getClassInfo(final ClassReader r) {
        final List array = new ArrayList();
        try {
            r.accept((ClassVisitor)new ClassAdapter((ClassVisitor)null) {
                public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
                    array.add(name.replace('/', '.'));
                    if (superName != null) {
                        array.add(superName.replace('/', '.'));
                    }
                    for (int i = 0; i < interfaces.length; ++i) {
                        array.add(interfaces[i].replace('/', '.'));
                    }
                    throw ClassNameReader.EARLY_EXIT;
                }
            }, 6);
        }
        catch (EarlyExitException ex) {}
        return array.toArray(new String[0]);
    }
    
    static {
        EARLY_EXIT = new EarlyExitException();
    }
    
    private static class EarlyExitException extends RuntimeException
    {
    }
}
