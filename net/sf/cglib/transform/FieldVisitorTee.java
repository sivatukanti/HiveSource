// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.transform;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;

public class FieldVisitorTee implements FieldVisitor
{
    private FieldVisitor fv1;
    private FieldVisitor fv2;
    
    public FieldVisitorTee(final FieldVisitor fv1, final FieldVisitor fv2) {
        this.fv1 = fv1;
        this.fv2 = fv2;
    }
    
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        return AnnotationVisitorTee.getInstance(this.fv1.visitAnnotation(desc, visible), this.fv2.visitAnnotation(desc, visible));
    }
    
    public void visitAttribute(final Attribute attr) {
        this.fv1.visitAttribute(attr);
        this.fv2.visitAttribute(attr);
    }
    
    public void visitEnd() {
        this.fv1.visitEnd();
        this.fv2.visitEnd();
    }
}
