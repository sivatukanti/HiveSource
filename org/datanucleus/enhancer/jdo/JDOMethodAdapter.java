// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo;

import org.datanucleus.enhancer.jdo.method.InitClass;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.enhancer.DataNucleusEnhancer;
import org.datanucleus.metadata.FieldMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.util.Localiser;
import org.datanucleus.asm.MethodVisitor;

public class JDOMethodAdapter extends MethodVisitor
{
    protected static final Localiser LOCALISER;
    protected ClassEnhancer enhancer;
    protected String methodName;
    protected String methodDescriptor;
    
    public JDOMethodAdapter(final MethodVisitor mv, final ClassEnhancer enhancer, final String methodName, final String methodDesc) {
        super(262144, mv);
        this.enhancer = enhancer;
        this.methodName = methodName;
        this.methodDescriptor = methodDesc;
    }
    
    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        final String ownerName = owner.replace('/', '.');
        if (this.enhancer.isPersistable(ownerName)) {
            AbstractClassMetaData cmd = null;
            boolean fieldInThisClass = true;
            if (this.enhancer.getClassMetaData().getFullClassName().equals(ownerName)) {
                cmd = this.enhancer.getClassMetaData();
            }
            else {
                fieldInThisClass = false;
                cmd = this.enhancer.getMetaDataManager().getMetaDataForClass(ownerName, this.enhancer.getClassLoaderResolver());
            }
            if (!fieldInThisClass || !this.methodName.equals("<init>")) {
                final AbstractMemberMetaData fmd = cmd.getMetaDataForMember(name);
                if (fmd != null && !fmd.isStatic() && !fmd.isFinal() && fmd.getPersistenceModifier() != FieldPersistenceModifier.NONE && fmd.getPersistenceFlags() != 0 && fmd instanceof FieldMetaData) {
                    final String fieldOwner = fmd.getClassName(true).replace('.', '/');
                    if (opcode == 180) {
                        this.mv.visitMethodInsn(184, fieldOwner, this.enhancer.getNamer().getGetMethodPrefixMethodName() + name, "(L" + fieldOwner + ";)" + desc);
                        if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
                            DataNucleusEnhancer.LOGGER.debug(JDOMethodAdapter.LOCALISER.msg("Enhancer.EnhanceOriginalMethodField", this.enhancer.getClassName() + "." + this.methodName, fmd.getClassName(true) + "." + name, this.enhancer.getNamer().getGetMethodPrefixMethodName() + name + "()"));
                        }
                        return;
                    }
                    if (opcode == 181) {
                        this.mv.visitMethodInsn(184, fieldOwner, this.enhancer.getNamer().getSetMethodPrefixMethodName() + name, "(L" + fieldOwner + ";" + desc + ")V");
                        if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
                            DataNucleusEnhancer.LOGGER.debug(JDOMethodAdapter.LOCALISER.msg("Enhancer.EnhanceOriginalMethodField", this.enhancer.getClassName() + "." + this.methodName, fmd.getClassName(true) + "." + name, this.enhancer.getNamer().getSetMethodPrefixMethodName() + name + "()"));
                        }
                        return;
                    }
                }
            }
            else {
                DataNucleusEnhancer.LOGGER.debug(JDOMethodAdapter.LOCALISER.msg("Enhancer.EnhanceOriginalMethodFieldOmit", this.enhancer.getClassName() + "." + this.methodName, (opcode == 180) ? "get" : "set", ownerName + "." + name));
            }
        }
        super.visitFieldInsn(opcode, owner, name, desc);
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        if (this.methodName.equals("clone") && this.methodDescriptor.equals("()Ljava/lang/Object;") && this.enhancer.getClassMetaData().getPersistenceCapableSuperclass() == null && opcode == 183 && name.equals("clone") && desc.equals("()Ljava/lang/Object;")) {
            this.mv.visitMethodInsn(183, this.enhancer.getASMClassName(), "jdoSuperClone", "()Ljava/lang/Object;");
            return;
        }
        super.visitMethodInsn(opcode, owner, name, desc);
    }
    
    @Override
    public void visitInsn(final int opcode) {
        if (this.enhancer.getClassMetaData().getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE && this.methodName.equals("<clinit>") && this.methodDescriptor.equals("()V") && opcode == 177) {
            final InitClass initMethod = InitClass.getInstance(this.enhancer);
            initMethod.addInitialiseInstructions(this.mv);
            this.mv.visitInsn(177);
            return;
        }
        super.visitInsn(opcode);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassEnhancer.class.getClassLoader());
    }
}
