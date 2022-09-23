// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.tree;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

public class FieldNode extends MemberNode implements FieldVisitor
{
    public int access;
    public String name;
    public String desc;
    public String signature;
    public Object value;
    
    public FieldNode(final int access, final String name, final String desc, final String signature, final Object value) {
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.value = value;
    }
    
    public void accept(final ClassVisitor classVisitor) {
        final FieldVisitor visitField = classVisitor.visitField(this.access, this.name, this.desc, this.signature, this.value);
        if (visitField == null) {
            return;
        }
        for (int n = (this.visibleAnnotations == null) ? 0 : this.visibleAnnotations.size(), i = 0; i < n; ++i) {
            final AnnotationNode annotationNode = this.visibleAnnotations.get(i);
            annotationNode.accept(visitField.visitAnnotation(annotationNode.desc, true));
        }
        for (int n2 = (this.invisibleAnnotations == null) ? 0 : this.invisibleAnnotations.size(), j = 0; j < n2; ++j) {
            final AnnotationNode annotationNode2 = this.invisibleAnnotations.get(j);
            annotationNode2.accept(visitField.visitAnnotation(annotationNode2.desc, false));
        }
        for (int n3 = (this.attrs == null) ? 0 : this.attrs.size(), k = 0; k < n3; ++k) {
            visitField.visitAttribute((Attribute)this.attrs.get(k));
        }
        visitField.visitEnd();
    }
}
