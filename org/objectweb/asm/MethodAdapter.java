// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm;

public class MethodAdapter implements MethodVisitor
{
    protected MethodVisitor mv;
    
    public MethodAdapter(final MethodVisitor mv) {
        this.mv = mv;
    }
    
    public AnnotationVisitor visitAnnotationDefault() {
        return this.mv.visitAnnotationDefault();
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        return this.mv.visitAnnotation(s, b);
    }
    
    public AnnotationVisitor visitParameterAnnotation(final int n, final String s, final boolean b) {
        return this.mv.visitParameterAnnotation(n, s, b);
    }
    
    public void visitAttribute(final Attribute attribute) {
        this.mv.visitAttribute(attribute);
    }
    
    public void visitCode() {
        this.mv.visitCode();
    }
    
    public void visitFrame(final int n, final int n2, final Object[] array, final int n3, final Object[] array2) {
        this.mv.visitFrame(n, n2, array, n3, array2);
    }
    
    public void visitInsn(final int n) {
        this.mv.visitInsn(n);
    }
    
    public void visitIntInsn(final int n, final int n2) {
        this.mv.visitIntInsn(n, n2);
    }
    
    public void visitVarInsn(final int n, final int n2) {
        this.mv.visitVarInsn(n, n2);
    }
    
    public void visitTypeInsn(final int n, final String s) {
        this.mv.visitTypeInsn(n, s);
    }
    
    public void visitFieldInsn(final int n, final String s, final String s2, final String s3) {
        this.mv.visitFieldInsn(n, s, s2, s3);
    }
    
    public void visitMethodInsn(final int n, final String s, final String s2, final String s3) {
        this.mv.visitMethodInsn(n, s, s2, s3);
    }
    
    public void visitJumpInsn(final int n, final Label label) {
        this.mv.visitJumpInsn(n, label);
    }
    
    public void visitLabel(final Label label) {
        this.mv.visitLabel(label);
    }
    
    public void visitLdcInsn(final Object o) {
        this.mv.visitLdcInsn(o);
    }
    
    public void visitIincInsn(final int n, final int n2) {
        this.mv.visitIincInsn(n, n2);
    }
    
    public void visitTableSwitchInsn(final int n, final int n2, final Label label, final Label[] array) {
        this.mv.visitTableSwitchInsn(n, n2, label, array);
    }
    
    public void visitLookupSwitchInsn(final Label label, final int[] array, final Label[] array2) {
        this.mv.visitLookupSwitchInsn(label, array, array2);
    }
    
    public void visitMultiANewArrayInsn(final String s, final int n) {
        this.mv.visitMultiANewArrayInsn(s, n);
    }
    
    public void visitTryCatchBlock(final Label label, final Label label2, final Label label3, final String s) {
        this.mv.visitTryCatchBlock(label, label2, label3, s);
    }
    
    public void visitLocalVariable(final String s, final String s2, final String s3, final Label label, final Label label2, final int n) {
        this.mv.visitLocalVariable(s, s2, s3, label, label2, n);
    }
    
    public void visitLineNumber(final int n, final Label label) {
        this.mv.visitLineNumber(n, label);
    }
    
    public void visitMaxs(final int n, final int n2) {
        this.mv.visitMaxs(n, n2);
    }
    
    public void visitEnd() {
        this.mv.visitEnd();
    }
}
