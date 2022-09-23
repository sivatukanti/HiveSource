// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm;

public class ClassAdapter implements ClassVisitor
{
    protected ClassVisitor cv;
    
    public ClassAdapter(final ClassVisitor cv) {
        this.cv = cv;
    }
    
    public void visit(final int n, final int n2, final String s, final String s2, final String s3, final String[] array) {
        this.cv.visit(n, n2, s, s2, s3, array);
    }
    
    public void visitSource(final String s, final String s2) {
        this.cv.visitSource(s, s2);
    }
    
    public void visitOuterClass(final String s, final String s2, final String s3) {
        this.cv.visitOuterClass(s, s2, s3);
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        return this.cv.visitAnnotation(s, b);
    }
    
    public void visitAttribute(final Attribute attribute) {
        this.cv.visitAttribute(attribute);
    }
    
    public void visitInnerClass(final String s, final String s2, final String s3, final int n) {
        this.cv.visitInnerClass(s, s2, s3, n);
    }
    
    public FieldVisitor visitField(final int n, final String s, final String s2, final String s3, final Object o) {
        return this.cv.visitField(n, s, s2, s3, o);
    }
    
    public MethodVisitor visitMethod(final int n, final String s, final String s2, final String s3, final String[] array) {
        return this.cv.visitMethod(n, s, s2, s3, array);
    }
    
    public void visitEnd() {
        this.cv.visitEnd();
    }
}
