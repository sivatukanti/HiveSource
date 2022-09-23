// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.commons;

import org.objectweb.asm.AnnotationVisitor;

public class RemappingAnnotationAdapter implements AnnotationVisitor
{
    private final AnnotationVisitor av;
    private final Remapper renamer;
    
    public RemappingAnnotationAdapter(final AnnotationVisitor av, final Remapper renamer) {
        this.av = av;
        this.renamer = renamer;
    }
    
    public void visit(final String s, final Object o) {
        this.av.visit(s, this.renamer.mapValue(o));
    }
    
    public void visitEnum(final String s, final String s2, final String s3) {
        this.av.visitEnum(s, this.renamer.mapType(s2), s3);
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final String s2) {
        final AnnotationVisitor visitAnnotation = this.av.visitAnnotation(s, this.renamer.mapType(s2));
        return (AnnotationVisitor)((visitAnnotation == null) ? null : ((visitAnnotation == this.av) ? this : new RemappingAnnotationAdapter(visitAnnotation, this.renamer)));
    }
    
    public AnnotationVisitor visitArray(final String s) {
        final AnnotationVisitor visitArray = this.av.visitArray(s);
        return (AnnotationVisitor)((visitArray == null) ? null : ((visitArray == this.av) ? this : new RemappingAnnotationAdapter(visitArray, this.renamer)));
    }
    
    public void visitEnd() {
        this.av.visitEnd();
    }
}
