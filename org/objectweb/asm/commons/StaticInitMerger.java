// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.commons;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ClassAdapter;

public class StaticInitMerger extends ClassAdapter
{
    private String name;
    private MethodVisitor clinit;
    private final String prefix;
    private int counter;
    
    public StaticInitMerger(final String prefix, final ClassVisitor classVisitor) {
        super(classVisitor);
        this.prefix = prefix;
    }
    
    public void visit(final int n, final int n2, final String name, final String s, final String s2, final String[] array) {
        this.cv.visit(n, n2, name, s, s2, array);
        this.name = name;
    }
    
    public MethodVisitor visitMethod(final int n, final String anObject, final String s, final String s2, final String[] array) {
        MethodVisitor methodVisitor;
        if ("<clinit>".equals(anObject)) {
            final int n2 = 10;
            final String string = this.prefix + this.counter++;
            methodVisitor = this.cv.visitMethod(n2, string, s, s2, array);
            if (this.clinit == null) {
                this.clinit = this.cv.visitMethod(n2, anObject, s, null, null);
            }
            this.clinit.visitMethodInsn(184, this.name, string, s);
        }
        else {
            methodVisitor = this.cv.visitMethod(n, anObject, s, s2, array);
        }
        return methodVisitor;
    }
    
    public void visitEnd() {
        if (this.clinit != null) {
            this.clinit.visitInsn(177);
            this.clinit.visitMaxs(0, 0);
        }
        this.cv.visitEnd();
    }
}
