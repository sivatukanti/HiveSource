// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.commons;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class RemappingMethodAdapter extends LocalVariablesSorter
{
    protected final Remapper remapper;
    
    public RemappingMethodAdapter(final int n, final String s, final MethodVisitor methodVisitor, final Remapper remapper) {
        super(n, s, methodVisitor);
        this.remapper = remapper;
    }
    
    public void visitFieldInsn(final int n, final String s, final String s2, final String s3) {
        super.visitFieldInsn(n, this.remapper.mapType(s), this.remapper.mapFieldName(s, s2, s3), this.remapper.mapDesc(s3));
    }
    
    public void visitMethodInsn(final int n, final String s, final String s2, final String s3) {
        super.visitMethodInsn(n, this.remapper.mapType(s), this.remapper.mapMethodName(s, s2, s3), this.remapper.mapMethodDesc(s3));
    }
    
    public void visitTypeInsn(final int n, final String s) {
        super.visitTypeInsn(n, this.remapper.mapType(s));
    }
    
    public void visitLdcInsn(final Object o) {
        super.visitLdcInsn(this.remapper.mapValue(o));
    }
    
    public void visitMultiANewArrayInsn(final String s, final int n) {
        super.visitMultiANewArrayInsn(this.remapper.mapDesc(s), n);
    }
    
    public void visitTryCatchBlock(final Label label, final Label label2, final Label label3, final String s) {
        super.visitTryCatchBlock(label, label2, label3, (s == null) ? null : this.remapper.mapType(s));
    }
    
    public void visitLocalVariable(final String s, final String s2, final String s3, final Label label, final Label label2, final int n) {
        super.visitLocalVariable(s, this.remapper.mapDesc(s2), this.remapper.mapSignature(s3, true), label, label2, n);
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        final AnnotationVisitor visitAnnotation = this.mv.visitAnnotation(s, b);
        return (AnnotationVisitor)((visitAnnotation == null) ? visitAnnotation : new RemappingAnnotationAdapter(visitAnnotation, this.remapper));
    }
    
    public AnnotationVisitor visitAnnotationDefault() {
        final AnnotationVisitor visitAnnotationDefault = this.mv.visitAnnotationDefault();
        return (AnnotationVisitor)((visitAnnotationDefault == null) ? visitAnnotationDefault : new RemappingAnnotationAdapter(visitAnnotationDefault, this.remapper));
    }
    
    public AnnotationVisitor visitParameterAnnotation(final int n, final String s, final boolean b) {
        final AnnotationVisitor visitParameterAnnotation = this.mv.visitParameterAnnotation(n, s, b);
        return (AnnotationVisitor)((visitParameterAnnotation == null) ? visitParameterAnnotation : new RemappingAnnotationAdapter(visitParameterAnnotation, this.remapper));
    }
    
    public void visitFrame(final int n, final int n2, final Object[] array, final int n3, final Object[] array2) {
        super.visitFrame(n, n2, this.remapEntries(n2, array), n3, this.remapEntries(n3, array2));
    }
    
    private Object[] remapEntries(final int n, final Object[] array) {
        for (int i = 0; i < n; ++i) {
            if (array[i] instanceof String) {
                final Object[] array2 = new Object[n];
                if (i > 0) {
                    System.arraycopy(array, 0, array2, 0, i);
                }
                do {
                    final Object o = array[i];
                    array2[i++] = ((o instanceof String) ? this.remapper.mapType((String)o) : o);
                } while (i < n);
                return array2;
            }
        }
        return array;
    }
}
