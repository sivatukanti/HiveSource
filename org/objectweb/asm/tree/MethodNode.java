// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.tree;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.AnnotationVisitor;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.MethodVisitor;

public class MethodNode extends MemberNode implements MethodVisitor
{
    public int access;
    public String name;
    public String desc;
    public String signature;
    public List exceptions;
    public Object annotationDefault;
    public List[] visibleParameterAnnotations;
    public List[] invisibleParameterAnnotations;
    public InsnList instructions;
    public List tryCatchBlocks;
    public int maxStack;
    public int maxLocals;
    public List localVariables;
    
    public MethodNode() {
        this.instructions = new InsnList();
    }
    
    public MethodNode(final int access, final String name, final String desc, final String signature, final String[] a) {
        this();
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = new ArrayList((a == null) ? 0 : a.length);
        if ((access & 0x400) == 0x0) {
            this.localVariables = new ArrayList(5);
        }
        this.tryCatchBlocks = new ArrayList();
        if (a != null) {
            this.exceptions.addAll(Arrays.asList(a));
        }
    }
    
    public AnnotationVisitor visitAnnotationDefault() {
        return (AnnotationVisitor)new AnnotationNode(new MethodNode$1(this, 0));
    }
    
    public AnnotationVisitor visitParameterAnnotation(final int n, final String s, final boolean b) {
        final AnnotationNode annotationNode = new AnnotationNode(s);
        if (b) {
            if (this.visibleParameterAnnotations == null) {
                this.visibleParameterAnnotations = new List[Type.getArgumentTypes(this.desc).length];
            }
            if (this.visibleParameterAnnotations[n] == null) {
                this.visibleParameterAnnotations[n] = new ArrayList(1);
            }
            this.visibleParameterAnnotations[n].add(annotationNode);
        }
        else {
            if (this.invisibleParameterAnnotations == null) {
                this.invisibleParameterAnnotations = new List[Type.getArgumentTypes(this.desc).length];
            }
            if (this.invisibleParameterAnnotations[n] == null) {
                this.invisibleParameterAnnotations[n] = new ArrayList(1);
            }
            this.invisibleParameterAnnotations[n].add(annotationNode);
        }
        return (AnnotationVisitor)annotationNode;
    }
    
    public void visitCode() {
    }
    
    public void visitFrame(final int n, final int n2, final Object[] array, final int n3, final Object[] array2) {
        this.instructions.add(new FrameNode(n, n2, (Object[])((array == null) ? null : this.getLabelNodes(array)), n3, (Object[])((array2 == null) ? null : this.getLabelNodes(array2))));
    }
    
    public void visitInsn(final int n) {
        this.instructions.add(new InsnNode(n));
    }
    
    public void visitIntInsn(final int n, final int n2) {
        this.instructions.add(new IntInsnNode(n, n2));
    }
    
    public void visitVarInsn(final int n, final int n2) {
        this.instructions.add(new VarInsnNode(n, n2));
    }
    
    public void visitTypeInsn(final int n, final String s) {
        this.instructions.add(new TypeInsnNode(n, s));
    }
    
    public void visitFieldInsn(final int n, final String s, final String s2, final String s3) {
        this.instructions.add(new FieldInsnNode(n, s, s2, s3));
    }
    
    public void visitMethodInsn(final int n, final String s, final String s2, final String s3) {
        this.instructions.add(new MethodInsnNode(n, s, s2, s3));
    }
    
    public void visitJumpInsn(final int n, final Label label) {
        this.instructions.add(new JumpInsnNode(n, this.getLabelNode(label)));
    }
    
    public void visitLabel(final Label label) {
        this.instructions.add(this.getLabelNode(label));
    }
    
    public void visitLdcInsn(final Object o) {
        this.instructions.add(new LdcInsnNode(o));
    }
    
    public void visitIincInsn(final int n, final int n2) {
        this.instructions.add(new IincInsnNode(n, n2));
    }
    
    public void visitTableSwitchInsn(final int n, final int n2, final Label label, final Label[] array) {
        this.instructions.add(new TableSwitchInsnNode(n, n2, this.getLabelNode(label), this.getLabelNodes(array)));
    }
    
    public void visitLookupSwitchInsn(final Label label, final int[] array, final Label[] array2) {
        this.instructions.add(new LookupSwitchInsnNode(this.getLabelNode(label), array, this.getLabelNodes(array2)));
    }
    
    public void visitMultiANewArrayInsn(final String s, final int n) {
        this.instructions.add(new MultiANewArrayInsnNode(s, n));
    }
    
    public void visitTryCatchBlock(final Label label, final Label label2, final Label label3, final String s) {
        this.tryCatchBlocks.add(new TryCatchBlockNode(this.getLabelNode(label), this.getLabelNode(label2), this.getLabelNode(label3), s));
    }
    
    public void visitLocalVariable(final String s, final String s2, final String s3, final Label label, final Label label2, final int n) {
        this.localVariables.add(new LocalVariableNode(s, s2, s3, this.getLabelNode(label), this.getLabelNode(label2), n));
    }
    
    public void visitLineNumber(final int n, final Label label) {
        this.instructions.add(new LineNumberNode(n, this.getLabelNode(label)));
    }
    
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
    }
    
    protected LabelNode getLabelNode(final Label label) {
        if (!(label.info instanceof LabelNode)) {
            label.info = new LabelNode(label);
        }
        return (LabelNode)label.info;
    }
    
    private LabelNode[] getLabelNodes(final Label[] array) {
        final LabelNode[] array2 = new LabelNode[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = this.getLabelNode(array[i]);
        }
        return array2;
    }
    
    private Object[] getLabelNodes(final Object[] array) {
        final Object[] array2 = new Object[array.length];
        for (int i = 0; i < array.length; ++i) {
            Object labelNode = array[i];
            if (labelNode instanceof Label) {
                labelNode = this.getLabelNode((Label)labelNode);
            }
            array2[i] = labelNode;
        }
        return array2;
    }
    
    public void accept(final ClassVisitor classVisitor) {
        final String[] array = new String[this.exceptions.size()];
        this.exceptions.toArray(array);
        final MethodVisitor visitMethod = classVisitor.visitMethod(this.access, this.name, this.desc, this.signature, array);
        if (visitMethod != null) {
            this.accept(visitMethod);
        }
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        if (this.annotationDefault != null) {
            final AnnotationVisitor visitAnnotationDefault = methodVisitor.visitAnnotationDefault();
            AnnotationNode.accept(visitAnnotationDefault, null, this.annotationDefault);
            if (visitAnnotationDefault != null) {
                visitAnnotationDefault.visitEnd();
            }
        }
        for (int n = (this.visibleAnnotations == null) ? 0 : this.visibleAnnotations.size(), i = 0; i < n; ++i) {
            final AnnotationNode annotationNode = this.visibleAnnotations.get(i);
            annotationNode.accept(methodVisitor.visitAnnotation(annotationNode.desc, true));
        }
        for (int n2 = (this.invisibleAnnotations == null) ? 0 : this.invisibleAnnotations.size(), j = 0; j < n2; ++j) {
            final AnnotationNode annotationNode2 = this.invisibleAnnotations.get(j);
            annotationNode2.accept(methodVisitor.visitAnnotation(annotationNode2.desc, false));
        }
        for (int n3 = (this.visibleParameterAnnotations == null) ? 0 : this.visibleParameterAnnotations.length, k = 0; k < n3; ++k) {
            final List list = this.visibleParameterAnnotations[k];
            if (list != null) {
                for (int l = 0; l < list.size(); ++l) {
                    final AnnotationNode annotationNode3 = list.get(l);
                    annotationNode3.accept(methodVisitor.visitParameterAnnotation(k, annotationNode3.desc, true));
                }
            }
        }
        for (int n4 = (this.invisibleParameterAnnotations == null) ? 0 : this.invisibleParameterAnnotations.length, n5 = 0; n5 < n4; ++n5) {
            final List list2 = this.invisibleParameterAnnotations[n5];
            if (list2 != null) {
                for (int n6 = 0; n6 < list2.size(); ++n6) {
                    final AnnotationNode annotationNode4 = list2.get(n6);
                    annotationNode4.accept(methodVisitor.visitParameterAnnotation(n5, annotationNode4.desc, false));
                }
            }
        }
        for (int n7 = (this.attrs == null) ? 0 : this.attrs.size(), n8 = 0; n8 < n7; ++n8) {
            methodVisitor.visitAttribute((Attribute)this.attrs.get(n8));
        }
        if (this.instructions.size() > 0) {
            methodVisitor.visitCode();
            for (int n9 = 0; n9 < this.tryCatchBlocks.size(); ++n9) {
                ((TryCatchBlockNode)this.tryCatchBlocks.get(n9)).accept(methodVisitor);
            }
            this.instructions.accept(methodVisitor);
            for (int n10 = (this.localVariables == null) ? 0 : this.localVariables.size(), n11 = 0; n11 < n10; ++n11) {
                ((LocalVariableNode)this.localVariables.get(n11)).accept(methodVisitor);
            }
            methodVisitor.visitMaxs(this.maxStack, this.maxLocals);
        }
        methodVisitor.visitEnd();
    }
}
