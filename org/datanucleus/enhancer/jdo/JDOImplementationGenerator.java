// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo;

import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.asm.MethodVisitor;
import org.datanucleus.asm.Label;
import org.datanucleus.asm.Type;
import org.datanucleus.enhancer.ClassEnhancer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.enhancer.EnhancerClassLoader;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.enhancer.DataNucleusEnhancer;
import java.io.FileOutputStream;
import java.io.File;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ClassMetaData;
import java.util.List;
import java.util.ArrayList;
import org.datanucleus.metadata.InterfaceMetaData;
import org.datanucleus.enhancer.EnhancementNamer;
import org.datanucleus.asm.ClassWriter;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.MetaDataManager;

public class JDOImplementationGenerator
{
    protected final MetaDataManager metaDataMgr;
    protected final AbstractClassMetaData inputCmd;
    protected final String className;
    protected final String fullClassName;
    protected String fullSuperclassName;
    protected byte[] bytes;
    ClassWriter writer;
    String asmClassName;
    String asmTypeDescriptor;
    EnhancementNamer namer;
    
    public JDOImplementationGenerator(final InterfaceMetaData interfaceMetaData, final String implClassName, final MetaDataManager mmgr) {
        this.fullSuperclassName = "java.lang.Object";
        this.namer = JDOEnhancementNamer.getInstance();
        this.className = implClassName;
        this.fullClassName = interfaceMetaData.getPackageName() + '.' + this.className;
        this.inputCmd = interfaceMetaData;
        this.metaDataMgr = mmgr;
        this.asmClassName = this.fullClassName.replace('.', '/');
        this.asmTypeDescriptor = "L" + this.asmClassName + ";";
        final List<String> interfaces = new ArrayList<String>();
        InterfaceMetaData imd = interfaceMetaData;
        do {
            final String intfTypeName = imd.getFullClassName().replace('.', '/');
            interfaces.add(intfTypeName);
            imd = (InterfaceMetaData)imd.getSuperAbstractClassMetaData();
        } while (imd != null);
        (this.writer = new ClassWriter(1)).visit(47, 33, this.fullClassName.replace('.', '/'), null, this.fullSuperclassName.replace('.', '/'), interfaces.toArray(new String[interfaces.size()]));
        this.createPropertyFields();
        this.createDefaultConstructor();
        this.createPropertyMethods();
        this.writer.visitEnd();
        this.bytes = this.writer.toByteArray();
    }
    
    public JDOImplementationGenerator(final ClassMetaData cmd, final String implClassName, final MetaDataManager mmgr) {
        this.fullSuperclassName = "java.lang.Object";
        this.namer = JDOEnhancementNamer.getInstance();
        this.className = implClassName;
        this.fullClassName = cmd.getPackageName() + '.' + this.className;
        this.inputCmd = cmd;
        this.metaDataMgr = mmgr;
        this.asmClassName = this.fullClassName.replace('.', '/');
        this.asmTypeDescriptor = "L" + this.asmClassName + ";";
        this.fullSuperclassName = cmd.getFullClassName();
        (this.writer = new ClassWriter(1)).visit(47, 33, this.fullClassName.replace('.', '/'), null, this.fullSuperclassName.replace('.', '/'), null);
        this.createPropertyFields();
        this.createDefaultConstructor();
        this.createPropertyMethods();
        this.writer.visitEnd();
        this.bytes = this.writer.toByteArray();
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
    
    protected void createPropertyFields() {
        AbstractClassMetaData acmd = this.inputCmd;
        do {
            this.createPropertyFields(acmd);
            acmd = acmd.getSuperAbstractClassMetaData();
        } while (acmd != null);
    }
    
    protected void createPropertyMethods() {
        AbstractClassMetaData acmd = this.inputCmd;
        do {
            this.createPropertyMethods(acmd);
            acmd = acmd.getSuperAbstractClassMetaData();
        } while (acmd != null);
    }
    
    protected void createPropertyMethods(final AbstractClassMetaData acmd) {
        if (acmd == null) {
            return;
        }
        final AbstractMemberMetaData[] memberMetaData = acmd.getManagedMembers();
        for (int i = 0; i < memberMetaData.length; ++i) {
            this.createGetter(memberMetaData[i]);
            this.createSetter(memberMetaData[i]);
        }
    }
    
    public void dumpToFile(final String filename) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(filename));
            out.write(this.getBytes());
            DataNucleusEnhancer.LOGGER.info("Generated class for " + this.fullClassName + " dumped to " + filename);
        }
        catch (Exception e) {
            DataNucleusEnhancer.LOGGER.error("Failure to dump generated class to file", e);
        }
        finally {
            try {
                out.close();
                out = null;
            }
            catch (Exception ex) {}
        }
    }
    
    public void enhance(final ClassLoaderResolver clr) {
        final EnhancerClassLoader loader = new EnhancerClassLoader();
        loader.defineClass(this.fullClassName, this.getBytes(), clr);
        final ClassLoaderResolver genclr = new ClassLoaderResolverImpl(loader);
        ClassMetaData implementationCmd;
        if (this.inputCmd instanceof InterfaceMetaData) {
            implementationCmd = new ClassMetaData((InterfaceMetaData)this.inputCmd, this.className, true);
        }
        else {
            implementationCmd = new ClassMetaData((ClassMetaData)this.inputCmd, this.className);
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                implementationCmd.populate(genclr, null, JDOImplementationGenerator.this.metaDataMgr);
                implementationCmd.initialise(genclr, JDOImplementationGenerator.this.metaDataMgr);
                return null;
            }
        });
        final ClassEnhancer gen = new JDOClassEnhancer(implementationCmd, genclr, this.metaDataMgr, this.getBytes());
        gen.enhance();
        this.bytes = gen.getClassBytes();
    }
    
    protected void createPropertyFields(final AbstractClassMetaData acmd) {
        if (acmd == null) {
            return;
        }
        final AbstractMemberMetaData[] propertyMetaData = acmd.getManagedMembers();
        for (int i = 0; i < propertyMetaData.length; ++i) {
            this.writer.visitField(2, propertyMetaData[i].getName(), Type.getDescriptor(propertyMetaData[i].getType()), null, null).visitEnd();
        }
    }
    
    protected void createDefaultConstructor() {
        final MethodVisitor visitor = this.writer.visitMethod(1, "<init>", "()V", null, null);
        visitor.visitCode();
        final Label l0 = new Label();
        visitor.visitLabel(l0);
        visitor.visitVarInsn(25, 0);
        visitor.visitMethodInsn(183, this.fullSuperclassName.replace('.', '/'), "<init>", "()V");
        visitor.visitInsn(177);
        final Label l2 = new Label();
        visitor.visitLabel(l2);
        visitor.visitLocalVariable("this", this.asmTypeDescriptor, null, l0, l2, 0);
        visitor.visitMaxs(1, 1);
        visitor.visitEnd();
    }
    
    protected void createGetter(final AbstractMemberMetaData mmd) {
        final boolean isBoolean = mmd.getTypeName().equals("boolean");
        final String getterName = ClassUtils.getJavaBeanGetterName(mmd.getName(), isBoolean);
        final String jdoGetterName = this.namer.getGetMethodPrefixMethodName() + mmd.getName();
        if (this.inputCmd instanceof InterfaceMetaData) {
            final String fieldDesc = Type.getDescriptor(mmd.getType());
            final MethodVisitor visitor = this.writer.visitMethod(1, getterName, "()" + fieldDesc, null, null);
            visitor.visitCode();
            final Label l0 = new Label();
            visitor.visitLabel(l0);
            visitor.visitVarInsn(25, 0);
            visitor.visitFieldInsn(180, this.asmClassName, mmd.getName(), fieldDesc);
            EnhanceUtils.addReturnForType(visitor, mmd.getType());
            final Label l2 = new Label();
            visitor.visitLabel(l2);
            visitor.visitLocalVariable("this", this.asmTypeDescriptor, null, l0, l2, 0);
            visitor.visitMaxs(1, 1);
            visitor.visitEnd();
        }
        else {
            final String fieldDesc = Type.getDescriptor(mmd.getType());
            final int getAccess = (mmd.isPublic() ? 1 : 0) | (mmd.isProtected() ? 4 : 0) | (mmd.isPrivate() ? 2 : 0);
            final MethodVisitor getVisitor = this.writer.visitMethod(getAccess, getterName, "()" + fieldDesc, null, null);
            JDOPropertyGetterAdapter.generateGetXXXMethod(getVisitor, mmd, this.asmClassName, this.asmTypeDescriptor, false, false, this.namer);
            final int access = (mmd.isPublic() ? 1 : 0) | (mmd.isProtected() ? 4 : 0) | (mmd.isPrivate() ? 2 : 0);
            final MethodVisitor visitor2 = this.writer.visitMethod(access, jdoGetterName, "()" + fieldDesc, null, null);
            visitor2.visitCode();
            final Label l3 = new Label();
            visitor2.visitLabel(l3);
            visitor2.visitVarInsn(25, 0);
            visitor2.visitFieldInsn(180, this.asmClassName, mmd.getName(), fieldDesc);
            EnhanceUtils.addReturnForType(visitor2, mmd.getType());
            final Label l4 = new Label();
            visitor2.visitLabel(l4);
            visitor2.visitLocalVariable("this", this.asmTypeDescriptor, null, l3, l4, 0);
            visitor2.visitMaxs(1, 1);
            visitor2.visitEnd();
        }
    }
    
    protected void createSetter(final AbstractMemberMetaData mmd) {
        final String setterName = ClassUtils.getJavaBeanSetterName(mmd.getName());
        final String jdoSetterName = this.namer.getSetMethodPrefixMethodName() + mmd.getName();
        if (this.inputCmd instanceof InterfaceMetaData) {
            final String fieldDesc = Type.getDescriptor(mmd.getType());
            final MethodVisitor visitor = this.writer.visitMethod(1, setterName, "(" + fieldDesc + ")V", null, null);
            visitor.visitCode();
            final Label l0 = new Label();
            visitor.visitLabel(l0);
            visitor.visitVarInsn(25, 0);
            EnhanceUtils.addLoadForType(visitor, mmd.getType(), 1);
            visitor.visitFieldInsn(181, this.asmClassName, mmd.getName(), fieldDesc);
            visitor.visitInsn(177);
            final Label l2 = new Label();
            visitor.visitLabel(l2);
            visitor.visitLocalVariable("this", this.asmTypeDescriptor, null, l0, l2, 0);
            visitor.visitLocalVariable("val", fieldDesc, null, l0, l2, 1);
            visitor.visitMaxs(2, 2);
            visitor.visitEnd();
        }
        else {
            final String fieldDesc = Type.getDescriptor(mmd.getType());
            final int setAccess = (mmd.isPublic() ? 1 : 0) | (mmd.isProtected() ? 4 : 0) | (mmd.isPrivate() ? 2 : 0);
            final MethodVisitor setVisitor = this.writer.visitMethod(setAccess, setterName, "(" + fieldDesc + ")V", null, null);
            JDOPropertySetterAdapter.generateSetXXXMethod(setVisitor, mmd, this.asmClassName, this.asmTypeDescriptor, false, this.namer);
            final int access = (mmd.isPublic() ? 1 : 0) | (mmd.isProtected() ? 4 : 0) | (mmd.isPrivate() ? 2 : 0);
            final MethodVisitor visitor2 = this.writer.visitMethod(access, jdoSetterName, "(" + fieldDesc + ")V", null, null);
            visitor2.visitCode();
            final Label l3 = new Label();
            visitor2.visitLabel(l3);
            visitor2.visitVarInsn(25, 0);
            EnhanceUtils.addLoadForType(visitor2, mmd.getType(), 1);
            visitor2.visitFieldInsn(181, this.asmClassName, mmd.getName(), fieldDesc);
            visitor2.visitInsn(177);
            final Label l4 = new Label();
            visitor2.visitLabel(l4);
            visitor2.visitLocalVariable("this", this.asmTypeDescriptor, null, l3, l4, 0);
            visitor2.visitLocalVariable("val", fieldDesc, null, l3, l4, 1);
            visitor2.visitMaxs(2, 2);
            visitor2.visitEnd();
        }
    }
}
