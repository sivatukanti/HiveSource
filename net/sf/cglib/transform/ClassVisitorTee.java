// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.transform;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.ClassVisitor;

public class ClassVisitorTee implements ClassVisitor
{
    private ClassVisitor cv1;
    private ClassVisitor cv2;
    
    public ClassVisitorTee(final ClassVisitor cv1, final ClassVisitor cv2) {
        this.cv1 = cv1;
        this.cv2 = cv2;
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.cv1.visit(version, access, name, signature, superName, interfaces);
        this.cv2.visit(version, access, name, signature, superName, interfaces);
    }
    
    public void visitEnd() {
        this.cv1.visitEnd();
        this.cv2.visitEnd();
        final ClassVisitor classVisitor = null;
        this.cv2 = classVisitor;
        this.cv1 = classVisitor;
    }
    
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        this.cv1.visitInnerClass(name, outerName, innerName, access);
        this.cv2.visitInnerClass(name, outerName, innerName, access);
    }
    
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        final FieldVisitor fv1 = this.cv1.visitField(access, name, desc, signature, value);
        final FieldVisitor fv2 = this.cv2.visitField(access, name, desc, signature, value);
        if (fv1 == null) {
            return fv2;
        }
        if (fv2 == null) {
            return fv1;
        }
        return (FieldVisitor)new FieldVisitorTee(fv1, fv2);
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final MethodVisitor mv1 = this.cv1.visitMethod(access, name, desc, signature, exceptions);
        final MethodVisitor mv2 = this.cv2.visitMethod(access, name, desc, signature, exceptions);
        if (mv1 == null) {
            return mv2;
        }
        if (mv2 == null) {
            return mv1;
        }
        return (MethodVisitor)new MethodVisitorTee(mv1, mv2);
    }
    
    public void visitSource(final String source, final String debug) {
        this.cv1.visitSource(source, debug);
        this.cv2.visitSource(source, debug);
    }
    
    public void visitOuterClass(final String owner, final String name, final String desc) {
        this.cv1.visitOuterClass(owner, name, desc);
        this.cv2.visitOuterClass(owner, name, desc);
    }
    
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        return AnnotationVisitorTee.getInstance(this.cv1.visitAnnotation(desc, visible), this.cv2.visitAnnotation(desc, visible));
    }
    
    public void visitAttribute(final Attribute attrs) {
        this.cv1.visitAttribute(attrs);
        this.cv2.visitAttribute(attrs);
    }
}
