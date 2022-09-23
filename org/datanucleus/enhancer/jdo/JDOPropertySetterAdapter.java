// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo;

import org.datanucleus.asm.Attribute;
import org.datanucleus.asm.AnnotationVisitor;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.asm.Type;
import org.datanucleus.enhancer.EnhancementNamer;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.enhancer.ClassMethod;
import org.datanucleus.enhancer.DataNucleusEnhancer;
import org.datanucleus.asm.ClassVisitor;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.util.Localiser;
import org.datanucleus.asm.MethodVisitor;

public class JDOPropertySetterAdapter extends MethodVisitor
{
    protected static final Localiser LOCALISER;
    protected ClassEnhancer enhancer;
    protected String methodName;
    protected String methodDescriptor;
    protected AbstractMemberMetaData mmd;
    protected MethodVisitor visitor;
    
    public JDOPropertySetterAdapter(final MethodVisitor mv, final ClassEnhancer enhancer, final String methodName, final String methodDesc, final AbstractMemberMetaData mmd, final ClassVisitor cv) {
        super(262144, mv);
        this.visitor = null;
        this.enhancer = enhancer;
        this.methodName = methodName;
        this.methodDescriptor = methodDesc;
        this.mmd = mmd;
        final int access = (mmd.isPublic() ? 1 : 0) | (mmd.isProtected() ? 4 : 0) | (mmd.isPrivate() ? 2 : 0) | (mmd.isAbstract() ? 1024 : 0);
        this.visitor = cv.visitMethod(access, enhancer.getNamer().getSetMethodPrefixMethodName() + mmd.getName(), methodDesc, null, null);
    }
    
    @Override
    public void visitEnd() {
        this.visitor.visitEnd();
        if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
            final String msg = ClassMethod.getMethodAdditionMessage(this.enhancer.getNamer().getSetMethodPrefixMethodName() + this.mmd.getName(), null, new Object[] { this.mmd.getType() }, new String[] { "val" });
            DataNucleusEnhancer.LOGGER.debug(JDOPropertySetterAdapter.LOCALISER.msg("Enhancer.AddMethod", msg));
        }
        if (!this.mmd.isAbstract()) {
            generateSetXXXMethod(this.mv, this.mmd, this.enhancer.getASMClassName(), this.enhancer.getClassDescriptor(), JavaUtils.useStackMapFrames(), this.enhancer.getNamer());
        }
    }
    
    public static void generateSetXXXMethod(final MethodVisitor mv, final AbstractMemberMetaData mmd, final String asmClassName, final String asmClassDesc, final boolean includeFrames, final EnhancementNamer namer) {
        final String[] argNames = { "objPC", "val" };
        final String fieldTypeDesc = Type.getDescriptor(mmd.getType());
        mv.visitCode();
        final AbstractClassMetaData cmd = mmd.getAbstractClassMetaData();
        if ((mmd.getPersistenceFlags() & 0x8) == 0x8) {
            final Label startLabel = new Label();
            mv.visitLabel(startLabel);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, asmClassName, namer.getStateManagerFieldName(), "L" + namer.getStateManagerAsmClassName() + ";");
            final Label l1 = new Label();
            mv.visitJumpInsn(199, l1);
            mv.visitVarInsn(25, 0);
            EnhanceUtils.addLoadForType(mv, mmd.getType(), 1);
            mv.visitMethodInsn(182, asmClassName, namer.getSetMethodPrefixMethodName() + mmd.getName(), "(" + fieldTypeDesc + ")V");
            final Label l2 = new Label();
            mv.visitJumpInsn(167, l2);
            mv.visitLabel(l1);
            if (includeFrames) {
                mv.visitFrame(3, 0, null, 0, null);
            }
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, asmClassName, namer.getStateManagerFieldName(), "L" + namer.getStateManagerAsmClassName() + ";");
            mv.visitVarInsn(25, 0);
            EnhanceUtils.addBIPUSHToMethod(mv, mmd.getFieldId());
            if (cmd.getPersistenceCapableSuperclass() != null) {
                mv.visitFieldInsn(178, asmClassName, namer.getInheritedFieldCountFieldName(), "I");
                mv.visitInsn(96);
            }
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(182, asmClassName, namer.getGetMethodPrefixMethodName() + mmd.getName(), "()" + fieldTypeDesc);
            EnhanceUtils.addLoadForType(mv, mmd.getType(), 1);
            final String methodName = "set" + EnhanceUtils.getTypeNameForJDOMethod(mmd.getType()) + "Field";
            String argTypeDesc = fieldTypeDesc;
            if (methodName.equals("setObjectField")) {
                argTypeDesc = EnhanceUtils.CD_Object;
            }
            mv.visitMethodInsn(185, namer.getStateManagerAsmClassName(), methodName, "(L" + namer.getPersistableAsmClassName() + ";I" + argTypeDesc + argTypeDesc + ")V");
            mv.visitLabel(l2);
            if (cmd.isDetachable()) {
                if (includeFrames) {
                    mv.visitFrame(3, 0, null, 0, null);
                }
                mv.visitVarInsn(25, 0);
                mv.visitMethodInsn(182, asmClassName, namer.getIsDetachedMethodName(), "()Z");
                final Label l3 = new Label();
                mv.visitJumpInsn(153, l3);
                mv.visitVarInsn(25, 0);
                mv.visitFieldInsn(180, asmClassName, namer.getDetachedStateFieldName(), "[Ljava/lang/Object;");
                mv.visitInsn(6);
                mv.visitInsn(50);
                mv.visitTypeInsn(192, "java/util/BitSet");
                EnhanceUtils.addBIPUSHToMethod(mv, mmd.getFieldId());
                if (cmd.getPersistenceCapableSuperclass() != null) {
                    mv.visitFieldInsn(178, asmClassName, namer.getInheritedFieldCountFieldName(), "I");
                    mv.visitInsn(96);
                }
                mv.visitMethodInsn(182, "java/util/BitSet", "set", "(I)V");
                mv.visitLabel(l3);
            }
            if (includeFrames) {
                mv.visitFrame(3, 0, null, 0, null);
            }
            mv.visitInsn(177);
            final Label endLabel = new Label();
            mv.visitLabel(endLabel);
            mv.visitLocalVariable(argNames[0], asmClassDesc, null, startLabel, endLabel, 0);
            mv.visitLocalVariable(argNames[1], fieldTypeDesc, null, startLabel, endLabel, 1);
            mv.visitMaxs(5, 2);
        }
        else if ((mmd.getPersistenceFlags() & 0x4) == 0x4) {
            final Label startLabel = new Label();
            mv.visitLabel(startLabel);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, asmClassName, namer.getFlagsFieldName(), "B");
            final Label l1 = new Label();
            mv.visitJumpInsn(153, l1);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, asmClassName, namer.getStateManagerFieldName(), "L" + namer.getStateManagerAsmClassName() + ";");
            mv.visitJumpInsn(198, l1);
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, asmClassName, namer.getStateManagerFieldName(), "L" + namer.getStateManagerAsmClassName() + ";");
            mv.visitVarInsn(25, 0);
            EnhanceUtils.addBIPUSHToMethod(mv, mmd.getFieldId());
            if (cmd.getPersistenceCapableSuperclass() != null) {
                mv.visitFieldInsn(178, asmClassName, namer.getInheritedFieldCountFieldName(), "I");
                mv.visitInsn(96);
            }
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(182, asmClassName, namer.getGetMethodPrefixMethodName() + mmd.getName(), "()" + fieldTypeDesc);
            EnhanceUtils.addLoadForType(mv, mmd.getType(), 1);
            final String methodName2 = "set" + EnhanceUtils.getTypeNameForJDOMethod(mmd.getType()) + "Field";
            String argTypeDesc2 = fieldTypeDesc;
            if (methodName2.equals("setObjectField")) {
                argTypeDesc2 = EnhanceUtils.CD_Object;
            }
            mv.visitMethodInsn(185, namer.getStateManagerAsmClassName(), methodName2, "(L" + namer.getPersistableAsmClassName() + ";I" + argTypeDesc2 + argTypeDesc2 + ")V");
            final Label l4 = new Label();
            mv.visitJumpInsn(167, l4);
            mv.visitLabel(l1);
            if (includeFrames) {
                mv.visitFrame(3, 0, null, 0, null);
            }
            mv.visitVarInsn(25, 0);
            EnhanceUtils.addLoadForType(mv, mmd.getType(), 1);
            mv.visitMethodInsn(182, asmClassName, namer.getSetMethodPrefixMethodName() + mmd.getName(), "(" + fieldTypeDesc + ")V");
            if (cmd.isDetachable()) {
                mv.visitVarInsn(25, 0);
                mv.visitMethodInsn(182, asmClassName, namer.getIsDetachedMethodName(), "()Z");
                mv.visitJumpInsn(153, l4);
                mv.visitVarInsn(25, 0);
                mv.visitFieldInsn(180, asmClassName, namer.getDetachedStateFieldName(), "[Ljava/lang/Object;");
                mv.visitInsn(6);
                mv.visitInsn(50);
                mv.visitTypeInsn(192, "java/util/BitSet");
                EnhanceUtils.addBIPUSHToMethod(mv, mmd.getFieldId());
                if (cmd.getPersistenceCapableSuperclass() != null) {
                    mv.visitFieldInsn(178, asmClassName, namer.getInheritedFieldCountFieldName(), "I");
                    mv.visitInsn(96);
                }
                mv.visitMethodInsn(182, "java/util/BitSet", "set", "(I)V");
            }
            mv.visitLabel(l4);
            if (includeFrames) {
                mv.visitFrame(3, 0, null, 0, null);
            }
            mv.visitInsn(177);
            final Label endLabel = new Label();
            mv.visitLabel(endLabel);
            mv.visitLocalVariable(argNames[0], asmClassDesc, null, startLabel, endLabel, 0);
            mv.visitLocalVariable(argNames[1], fieldTypeDesc, null, startLabel, endLabel, 1);
            mv.visitMaxs(5, 2);
        }
        else {
            final Label startLabel = new Label();
            mv.visitLabel(startLabel);
            mv.visitVarInsn(25, 0);
            EnhanceUtils.addLoadForType(mv, mmd.getType(), 1);
            mv.visitMethodInsn(182, asmClassName, namer.getSetMethodPrefixMethodName() + mmd.getName(), "(" + fieldTypeDesc + ")V");
            mv.visitInsn(177);
            final Label endLabel2 = new Label();
            mv.visitLabel(endLabel2);
            mv.visitLocalVariable(argNames[0], asmClassDesc, null, startLabel, endLabel2, 0);
            mv.visitLocalVariable(argNames[1], fieldTypeDesc, null, startLabel, endLabel2, 1);
            mv.visitMaxs(2, 2);
        }
        mv.visitEnd();
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String arg0, final boolean arg1) {
        return this.mv.visitAnnotation(arg0, arg1);
    }
    
    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return this.visitor.visitAnnotationDefault();
    }
    
    @Override
    public void visitAttribute(final Attribute arg0) {
        this.visitor.visitAttribute(arg0);
    }
    
    @Override
    public void visitCode() {
        this.visitor.visitCode();
    }
    
    @Override
    public void visitFieldInsn(final int arg0, final String arg1, final String arg2, final String arg3) {
        this.visitor.visitFieldInsn(arg0, arg1, arg2, arg3);
    }
    
    @Override
    public void visitFrame(final int arg0, final int arg1, final Object[] arg2, final int arg3, final Object[] arg4) {
        this.visitor.visitFrame(arg0, arg1, arg2, arg3, arg4);
    }
    
    @Override
    public void visitIincInsn(final int arg0, final int arg1) {
        this.visitor.visitIincInsn(arg0, arg1);
    }
    
    @Override
    public void visitInsn(final int arg0) {
        this.visitor.visitInsn(arg0);
    }
    
    @Override
    public void visitIntInsn(final int arg0, final int arg1) {
        this.visitor.visitIntInsn(arg0, arg1);
    }
    
    @Override
    public void visitJumpInsn(final int arg0, final Label arg1) {
        this.visitor.visitJumpInsn(arg0, arg1);
    }
    
    @Override
    public void visitLabel(final Label arg0) {
        this.visitor.visitLabel(arg0);
    }
    
    @Override
    public void visitLdcInsn(final Object arg0) {
        this.visitor.visitLdcInsn(arg0);
    }
    
    @Override
    public void visitLineNumber(final int arg0, final Label arg1) {
        this.visitor.visitLineNumber(arg0, arg1);
    }
    
    @Override
    public void visitLocalVariable(final String arg0, final String arg1, final String arg2, final Label arg3, final Label arg4, final int arg5) {
        this.visitor.visitLocalVariable(arg0, arg1, arg2, arg3, arg4, arg5);
    }
    
    @Override
    public void visitLookupSwitchInsn(final Label arg0, final int[] arg1, final Label[] arg2) {
        this.visitor.visitLookupSwitchInsn(arg0, arg1, arg2);
    }
    
    @Override
    public void visitMaxs(final int arg0, final int arg1) {
        this.visitor.visitMaxs(arg0, arg1);
    }
    
    @Override
    public void visitMethodInsn(final int arg0, final String arg1, final String arg2, final String arg3) {
        this.visitor.visitMethodInsn(arg0, arg1, arg2, arg3);
    }
    
    @Override
    public void visitMultiANewArrayInsn(final String arg0, final int arg1) {
        this.visitor.visitMultiANewArrayInsn(arg0, arg1);
    }
    
    @Override
    public AnnotationVisitor visitParameterAnnotation(final int arg0, final String arg1, final boolean arg2) {
        return this.visitor.visitParameterAnnotation(arg0, arg1, arg2);
    }
    
    @Override
    public void visitTableSwitchInsn(final int arg0, final int arg1, final Label arg2, final Label... arg3) {
        this.visitor.visitTableSwitchInsn(arg0, arg1, arg2, arg3);
    }
    
    @Override
    public void visitTryCatchBlock(final Label arg0, final Label arg1, final Label arg2, final String arg3) {
        this.visitor.visitTryCatchBlock(arg0, arg1, arg2, arg3);
    }
    
    @Override
    public void visitTypeInsn(final int arg0, final String arg1) {
        this.visitor.visitTypeInsn(arg0, arg1);
    }
    
    @Override
    public void visitVarInsn(final int arg0, final int arg1) {
        this.visitor.visitVarInsn(arg0, arg1);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassEnhancer.class.getClassLoader());
    }
}
