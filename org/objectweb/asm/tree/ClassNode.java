// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.tree;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.FieldVisitor;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassVisitor;

public class ClassNode extends MemberNode implements ClassVisitor
{
    public int version;
    public int access;
    public String name;
    public String signature;
    public String superName;
    public List interfaces;
    public String sourceFile;
    public String sourceDebug;
    public String outerClass;
    public String outerMethod;
    public String outerMethodDesc;
    public List innerClasses;
    public List fields;
    public List methods;
    
    public ClassNode() {
        this.interfaces = new ArrayList();
        this.innerClasses = new ArrayList();
        this.fields = new ArrayList();
        this.methods = new ArrayList();
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] a) {
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        if (a != null) {
            this.interfaces.addAll(Arrays.asList(a));
        }
    }
    
    public void visitSource(final String sourceFile, final String sourceDebug) {
        this.sourceFile = sourceFile;
        this.sourceDebug = sourceDebug;
    }
    
    public void visitOuterClass(final String outerClass, final String outerMethod, final String outerMethodDesc) {
        this.outerClass = outerClass;
        this.outerMethod = outerMethod;
        this.outerMethodDesc = outerMethodDesc;
    }
    
    public void visitInnerClass(final String s, final String s2, final String s3, final int n) {
        this.innerClasses.add(new InnerClassNode(s, s2, s3, n));
    }
    
    public FieldVisitor visitField(final int n, final String s, final String s2, final String s3, final Object o) {
        final FieldNode fieldNode = new FieldNode(n, s, s2, s3, o);
        this.fields.add(fieldNode);
        return (FieldVisitor)fieldNode;
    }
    
    public MethodVisitor visitMethod(final int n, final String s, final String s2, final String s3, final String[] array) {
        final MethodNode methodNode = new MethodNode(n, s, s2, s3, array);
        this.methods.add(methodNode);
        return (MethodVisitor)methodNode;
    }
    
    public void accept(final ClassVisitor classVisitor) {
        final String[] array = new String[this.interfaces.size()];
        this.interfaces.toArray(array);
        classVisitor.visit(this.version, this.access, this.name, this.signature, this.superName, array);
        if (this.sourceFile != null || this.sourceDebug != null) {
            classVisitor.visitSource(this.sourceFile, this.sourceDebug);
        }
        if (this.outerClass != null) {
            classVisitor.visitOuterClass(this.outerClass, this.outerMethod, this.outerMethodDesc);
        }
        for (int n = (this.visibleAnnotations == null) ? 0 : this.visibleAnnotations.size(), i = 0; i < n; ++i) {
            final AnnotationNode annotationNode = this.visibleAnnotations.get(i);
            annotationNode.accept(classVisitor.visitAnnotation(annotationNode.desc, true));
        }
        for (int n2 = (this.invisibleAnnotations == null) ? 0 : this.invisibleAnnotations.size(), j = 0; j < n2; ++j) {
            final AnnotationNode annotationNode2 = this.invisibleAnnotations.get(j);
            annotationNode2.accept(classVisitor.visitAnnotation(annotationNode2.desc, false));
        }
        for (int n3 = (this.attrs == null) ? 0 : this.attrs.size(), k = 0; k < n3; ++k) {
            classVisitor.visitAttribute((Attribute)this.attrs.get(k));
        }
        for (int l = 0; l < this.innerClasses.size(); ++l) {
            ((InnerClassNode)this.innerClasses.get(l)).accept(classVisitor);
        }
        for (int n4 = 0; n4 < this.fields.size(); ++n4) {
            ((FieldNode)this.fields.get(n4)).accept(classVisitor);
        }
        for (int n5 = 0; n5 < this.methods.size(); ++n5) {
            ((MethodNode)this.methods.get(n5)).accept(classVisitor);
        }
        classVisitor.visitEnd();
    }
}
