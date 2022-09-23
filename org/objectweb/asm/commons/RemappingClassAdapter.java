// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.commons;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassAdapter;

public class RemappingClassAdapter extends ClassAdapter
{
    protected final Remapper remapper;
    protected String className;
    
    public RemappingClassAdapter(final ClassVisitor classVisitor, final Remapper remapper) {
        super(classVisitor);
        this.remapper = remapper;
    }
    
    public void visit(final int n, final int n2, final String className, final String s, final String s2, final String[] array) {
        this.className = className;
        super.visit(n, n2, this.remapper.mapType(className), this.remapper.mapSignature(s, false), this.remapper.mapType(s2), (String[])((array == null) ? null : this.remapper.mapTypes(array)));
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        final AnnotationVisitor visitAnnotation = super.visitAnnotation(this.remapper.mapType(s), b);
        return (visitAnnotation == null) ? null : this.createRemappingAnnotationAdapter(visitAnnotation);
    }
    
    public FieldVisitor visitField(final int n, final String s, final String s2, final String s3, final Object o) {
        final FieldVisitor visitField = super.visitField(n, this.remapper.mapFieldName(this.className, s, s2), this.remapper.mapDesc(s2), this.remapper.mapSignature(s3, true), this.remapper.mapValue(o));
        return (visitField == null) ? null : this.createRemappingFieldAdapter(visitField);
    }
    
    public MethodVisitor visitMethod(final int n, final String s, final String s2, final String s3, final String[] array) {
        final String mapMethodDesc = this.remapper.mapMethodDesc(s2);
        final MethodVisitor visitMethod = super.visitMethod(n, this.remapper.mapMethodName(this.className, s, s2), mapMethodDesc, this.remapper.mapSignature(s3, false), (String[])((array == null) ? null : this.remapper.mapTypes(array)));
        return (visitMethod == null) ? null : this.createRemappingMethodAdapter(n, mapMethodDesc, visitMethod);
    }
    
    public void visitInnerClass(final String s, final String s2, final String s3, final int n) {
        super.visitInnerClass(this.remapper.mapType(s), (s2 == null) ? null : this.remapper.mapType(s2), s3, n);
    }
    
    public void visitOuterClass(final String s, final String s2, final String s3) {
        super.visitOuterClass(this.remapper.mapType(s), (s2 == null) ? null : this.remapper.mapMethodName(s, s2, s3), (s3 == null) ? null : this.remapper.mapMethodDesc(s3));
    }
    
    protected FieldVisitor createRemappingFieldAdapter(final FieldVisitor fieldVisitor) {
        return (FieldVisitor)new RemappingFieldAdapter(fieldVisitor, this.remapper);
    }
    
    protected MethodVisitor createRemappingMethodAdapter(final int n, final String s, final MethodVisitor methodVisitor) {
        return (MethodVisitor)new RemappingMethodAdapter(n, s, methodVisitor, this.remapper);
    }
    
    protected AnnotationVisitor createRemappingAnnotationAdapter(final AnnotationVisitor annotationVisitor) {
        return (AnnotationVisitor)new RemappingAnnotationAdapter(annotationVisitor, this.remapper);
    }
}
