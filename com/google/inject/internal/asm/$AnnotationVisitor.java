// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.asm;

public interface $AnnotationVisitor
{
    void visit(final String p0, final Object p1);
    
    void visitEnum(final String p0, final String p1, final String p2);
    
    $AnnotationVisitor visitAnnotation(final String p0, final String p1);
    
    $AnnotationVisitor visitArray(final String p0);
    
    void visitEnd();
}
