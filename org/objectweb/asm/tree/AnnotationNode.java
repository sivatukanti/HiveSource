// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.AnnotationVisitor;

public class AnnotationNode implements AnnotationVisitor
{
    public String desc;
    public List values;
    
    public AnnotationNode(final String desc) {
        this.desc = desc;
    }
    
    AnnotationNode(final List values) {
        this.values = values;
    }
    
    public void visit(final String s, final Object o) {
        if (this.values == null) {
            this.values = new ArrayList((this.desc != null) ? 2 : 1);
        }
        if (this.desc != null) {
            this.values.add(s);
        }
        this.values.add(o);
    }
    
    public void visitEnum(final String s, final String s2, final String s3) {
        if (this.values == null) {
            this.values = new ArrayList((this.desc != null) ? 2 : 1);
        }
        if (this.desc != null) {
            this.values.add(s);
        }
        this.values.add(new String[] { s2, s3 });
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final String s2) {
        if (this.values == null) {
            this.values = new ArrayList((this.desc != null) ? 2 : 1);
        }
        if (this.desc != null) {
            this.values.add(s);
        }
        final AnnotationNode annotationNode = new AnnotationNode(s2);
        this.values.add(annotationNode);
        return (AnnotationVisitor)annotationNode;
    }
    
    public AnnotationVisitor visitArray(final String s) {
        if (this.values == null) {
            this.values = new ArrayList((this.desc != null) ? 2 : 1);
        }
        if (this.desc != null) {
            this.values.add(s);
        }
        final ArrayList list = new ArrayList();
        this.values.add(list);
        return (AnnotationVisitor)new AnnotationNode(list);
    }
    
    public void visitEnd() {
    }
    
    public void accept(final AnnotationVisitor annotationVisitor) {
        if (annotationVisitor != null) {
            if (this.values != null) {
                for (int i = 0; i < this.values.size(); i += 2) {
                    accept(annotationVisitor, (String)this.values.get(i), this.values.get(i + 1));
                }
            }
            annotationVisitor.visitEnd();
        }
    }
    
    static void accept(final AnnotationVisitor annotationVisitor, final String s, final Object o) {
        if (annotationVisitor != null) {
            if (o instanceof String[]) {
                final String[] array = (String[])o;
                annotationVisitor.visitEnum(s, array[0], array[1]);
            }
            else if (o instanceof AnnotationNode) {
                final AnnotationNode annotationNode = (AnnotationNode)o;
                annotationNode.accept(annotationVisitor.visitAnnotation(s, annotationNode.desc));
            }
            else if (o instanceof List) {
                final AnnotationVisitor visitArray = annotationVisitor.visitArray(s);
                final List list = (List)o;
                for (int i = 0; i < list.size(); ++i) {
                    accept(visitArray, null, list.get(i));
                }
                visitArray.visitEnd();
            }
            else {
                annotationVisitor.visit(s, o);
            }
        }
    }
}
