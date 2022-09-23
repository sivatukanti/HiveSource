// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo;

import org.datanucleus.asm.Attribute;
import org.datanucleus.asm.MethodVisitor;
import org.datanucleus.asm.FieldVisitor;
import org.datanucleus.asm.AnnotationVisitor;
import org.datanucleus.asm.ClassWriter;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.InvalidMetaDataException;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.enhancer.DataNucleusEnhancer;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.enhancer.ClassField;
import org.datanucleus.enhancer.jdo.method.JdoSuperClone;
import org.datanucleus.enhancer.jdo.method.LoadClass;
import org.datanucleus.enhancer.jdo.method.InitPersistenceCapableSuperclass;
import org.datanucleus.enhancer.jdo.method.JdoGetManagedFieldCount;
import org.datanucleus.enhancer.jdo.method.JdoGetInheritedFieldCount;
import org.datanucleus.enhancer.jdo.method.InitFieldFlags;
import org.datanucleus.enhancer.jdo.method.InitFieldTypes;
import org.datanucleus.enhancer.jdo.method.InitFieldNames;
import org.datanucleus.enhancer.jdo.method.JdoCopyFields;
import org.datanucleus.enhancer.jdo.method.JdoCopyField;
import org.datanucleus.enhancer.jdo.method.JdoProvideField;
import org.datanucleus.enhancer.jdo.method.JdoReplaceField;
import org.datanucleus.enhancer.jdo.method.JdoNewInstance2;
import org.datanucleus.enhancer.jdo.method.JdoNewInstance1;
import org.datanucleus.enhancer.jdo.method.JdoIsDetached;
import org.datanucleus.enhancer.jdo.method.JdoReplaceDetachedState;
import org.datanucleus.enhancer.jdo.method.JdoReplaceStateManager;
import org.datanucleus.enhancer.jdo.method.JdoReplaceFlags;
import org.datanucleus.enhancer.jdo.method.JdoReplaceFields;
import org.datanucleus.enhancer.jdo.method.JdoProvideFields;
import org.datanucleus.enhancer.jdo.method.JdoNewObjectIdInstance2;
import org.datanucleus.enhancer.jdo.method.JdoNewObjectIdInstance1;
import org.datanucleus.enhancer.jdo.method.JdoMakeDirty;
import org.datanucleus.enhancer.jdo.method.JdoIsTransactional;
import org.datanucleus.enhancer.jdo.method.JdoIsPersistent;
import org.datanucleus.enhancer.jdo.method.JdoIsNew;
import org.datanucleus.enhancer.jdo.method.JdoIsDirty;
import org.datanucleus.enhancer.jdo.method.JdoIsDeleted;
import org.datanucleus.enhancer.jdo.method.JdoGetTransactionalObjectId;
import org.datanucleus.enhancer.jdo.method.JdoGetPersistenceManager;
import org.datanucleus.enhancer.jdo.method.JdoPreSerialize;
import org.datanucleus.enhancer.jdo.method.JdoGetVersion;
import org.datanucleus.enhancer.jdo.method.JdoGetObjectId;
import org.datanucleus.enhancer.jdo.method.JdoCopyKeyFieldsToObjectId2;
import org.datanucleus.enhancer.jdo.method.JdoCopyKeyFieldsToObjectId;
import org.datanucleus.enhancer.jdo.method.JdoCopyKeyFieldsFromObjectId2;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.jdo.method.JdoCopyKeyFieldsFromObjectId;
import java.io.IOException;
import org.datanucleus.asm.ClassVisitor;
import java.io.InputStream;
import org.datanucleus.asm.ClassReader;
import java.io.FileInputStream;
import org.datanucleus.asm.Type;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.enhancer.EnhancementNamer;
import org.datanucleus.enhancer.AbstractClassEnhancer;

public class JDOClassEnhancer extends AbstractClassEnhancer
{
    protected String inputResourceName;
    protected byte[] inputBytes;
    protected final Class cls;
    protected byte[] classBytes;
    protected byte[] pkClassBytes;
    protected String asmClassName;
    protected String classDescriptor;
    protected EnhancementNamer namer;
    
    public JDOClassEnhancer(final ClassMetaData cmd, final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        super(cmd, clr, mmgr);
        this.classBytes = null;
        this.pkClassBytes = null;
        this.asmClassName = null;
        this.classDescriptor = null;
        this.namer = null;
        this.namer = JDOEnhancementNamer.getInstance();
        this.cls = clr.classForName(cmd.getFullClassName());
        this.asmClassName = cmd.getFullClassName().replace('.', '/');
        this.classDescriptor = Type.getDescriptor(this.cls);
        this.inputResourceName = "/" + this.className.replace('.', '/') + ".class";
    }
    
    public JDOClassEnhancer(final ClassMetaData cmd, final ClassLoaderResolver clr, final MetaDataManager mmgr, final byte[] classBytes) {
        super(cmd, clr, mmgr);
        this.classBytes = null;
        this.pkClassBytes = null;
        this.asmClassName = null;
        this.classDescriptor = null;
        this.namer = null;
        this.namer = JDOEnhancementNamer.getInstance();
        this.cls = clr.classForName(cmd.getFullClassName());
        this.asmClassName = cmd.getFullClassName().replace('.', '/');
        this.classDescriptor = Type.getDescriptor(this.cls);
        this.inputBytes = classBytes;
    }
    
    @Override
    public void setNamer(final EnhancementNamer namer) {
        this.namer = namer;
    }
    
    public static String getClassNameForFileName(final String filename) {
        final MyClassVisitor vis = new MyClassVisitor();
        try {
            new ClassReader(new FileInputStream(filename)).accept(vis, 0);
            return vis.getClassName();
        }
        catch (IOException ioe) {
            return null;
        }
    }
    
    @Override
    public Class getClassBeingEnhanced() {
        return this.cls;
    }
    
    @Override
    public String getASMClassName() {
        return this.asmClassName;
    }
    
    @Override
    public String getClassDescriptor() {
        return this.classDescriptor;
    }
    
    @Override
    protected void initialiseMethodsList() {
        if (this.cmd.getPersistenceCapableSuperclass() == null) {
            this.methodsToAdd.add(JdoCopyKeyFieldsFromObjectId.getInstance(this));
            this.methodsToAdd.add(JdoCopyKeyFieldsFromObjectId2.getInstance(this));
            this.methodsToAdd.add(JdoCopyKeyFieldsToObjectId.getInstance(this));
            this.methodsToAdd.add(JdoCopyKeyFieldsToObjectId2.getInstance(this));
            this.methodsToAdd.add(JdoGetObjectId.getInstance(this));
            this.methodsToAdd.add(JdoGetVersion.getInstance(this));
            this.methodsToAdd.add(JdoPreSerialize.getInstance(this));
            this.methodsToAdd.add(JdoGetPersistenceManager.getInstance(this));
            this.methodsToAdd.add(JdoGetTransactionalObjectId.getInstance(this));
            this.methodsToAdd.add(JdoIsDeleted.getInstance(this));
            this.methodsToAdd.add(JdoIsDirty.getInstance(this));
            this.methodsToAdd.add(JdoIsNew.getInstance(this));
            this.methodsToAdd.add(JdoIsPersistent.getInstance(this));
            this.methodsToAdd.add(JdoIsTransactional.getInstance(this));
            this.methodsToAdd.add(JdoMakeDirty.getInstance(this));
            this.methodsToAdd.add(JdoNewObjectIdInstance1.getInstance(this));
            this.methodsToAdd.add(JdoNewObjectIdInstance2.getInstance(this));
            this.methodsToAdd.add(JdoProvideFields.getInstance(this));
            this.methodsToAdd.add(JdoReplaceFields.getInstance(this));
            this.methodsToAdd.add(JdoReplaceFlags.getInstance(this));
            this.methodsToAdd.add(JdoReplaceStateManager.getInstance(this));
        }
        if (this.cmd.getPersistenceCapableSuperclass() != null && this.cmd.isRootInstantiableClass()) {
            this.methodsToAdd.add(JdoCopyKeyFieldsFromObjectId.getInstance(this));
            this.methodsToAdd.add(JdoCopyKeyFieldsFromObjectId2.getInstance(this));
            this.methodsToAdd.add(JdoCopyKeyFieldsToObjectId.getInstance(this));
            this.methodsToAdd.add(JdoCopyKeyFieldsToObjectId2.getInstance(this));
            this.methodsToAdd.add(JdoNewObjectIdInstance1.getInstance(this));
            this.methodsToAdd.add(JdoNewObjectIdInstance2.getInstance(this));
        }
        if (this.requiresDetachable()) {
            this.methodsToAdd.add(JdoReplaceDetachedState.getInstance(this));
        }
        if (this.cmd.isDetachable() && this.cmd.getPersistenceCapableSuperclass() != null) {
            this.methodsToAdd.add(JdoMakeDirty.getInstance(this));
        }
        this.methodsToAdd.add(JdoIsDetached.getInstance(this));
        this.methodsToAdd.add(JdoNewInstance1.getInstance(this));
        this.methodsToAdd.add(JdoNewInstance2.getInstance(this));
        this.methodsToAdd.add(JdoReplaceField.getInstance(this));
        this.methodsToAdd.add(JdoProvideField.getInstance(this));
        this.methodsToAdd.add(JdoCopyField.getInstance(this));
        this.methodsToAdd.add(JdoCopyFields.getInstance(this));
        this.methodsToAdd.add(InitFieldNames.getInstance(this));
        this.methodsToAdd.add(InitFieldTypes.getInstance(this));
        this.methodsToAdd.add(InitFieldFlags.getInstance(this));
        this.methodsToAdd.add(JdoGetInheritedFieldCount.getInstance(this));
        this.methodsToAdd.add(JdoGetManagedFieldCount.getInstance(this));
        this.methodsToAdd.add(InitPersistenceCapableSuperclass.getInstance(this));
        this.methodsToAdd.add(LoadClass.getInstance(this));
        this.methodsToAdd.add(JdoSuperClone.getInstance(this));
    }
    
    @Override
    protected void initialiseFieldsList() {
        if (this.cmd.getPersistenceCapableSuperclass() == null) {
            this.fieldsToAdd.add(new ClassField(this, this.namer.getStateManagerFieldName(), 132, this.namer.getStateManagerClass()));
            this.fieldsToAdd.add(new ClassField(this, this.namer.getFlagsFieldName(), 132, Byte.TYPE));
        }
        if (this.requiresDetachable()) {
            this.fieldsToAdd.add(new ClassField(this, this.namer.getDetachedStateFieldName(), 4, Object[].class));
        }
        this.fieldsToAdd.add(new ClassField(this, this.namer.getFieldFlagsFieldName(), 26, byte[].class));
        this.fieldsToAdd.add(new ClassField(this, this.namer.getPersistableSuperclassFieldName(), 26, Class.class));
        this.fieldsToAdd.add(new ClassField(this, this.namer.getFieldTypesFieldName(), 26, Class[].class));
        this.fieldsToAdd.add(new ClassField(this, this.namer.getFieldNamesFieldName(), 26, String[].class));
        this.fieldsToAdd.add(new ClassField(this, this.namer.getInheritedFieldCountFieldName(), 26, Integer.TYPE));
    }
    
    @Override
    public boolean enhance() {
        if (this.cmd.getPersistenceModifier() != ClassPersistenceModifier.PERSISTENCE_CAPABLE && this.cmd.getPersistenceModifier() != ClassPersistenceModifier.PERSISTENCE_AWARE) {
            return false;
        }
        this.initialise();
        if (this.checkClassIsEnhanced(false)) {
            DataNucleusEnhancer.LOGGER.info(JDOClassEnhancer.LOCALISER.msg("Enhancer.ClassIsAlreadyEnhanced", this.className));
            return true;
        }
        try {
            if (this.cmd.getIdentityType() == IdentityType.APPLICATION && this.cmd.getObjectidClass() == null && this.cmd.getNoOfPrimaryKeyMembers() > 1) {
                if (!this.hasOption("generate-primary-key")) {
                    throw new InvalidMetaDataException(JDOClassEnhancer.LOCALISER, "044065", this.cmd.getFullClassName(), this.cmd.getNoOfPrimaryKeyMembers());
                }
                final String pkClassName = this.cmd.getFullClassName() + "_PK";
                if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
                    DataNucleusEnhancer.LOGGER.debug(JDOClassEnhancer.LOCALISER.msg("Enhancer.GeneratePrimaryKey", this.cmd.getFullClassName(), pkClassName));
                }
                this.cmd.setObjectIdClass(pkClassName);
                final PrimaryKeyGenerator pkGen = new PrimaryKeyGenerator(this.cmd, this);
                this.pkClassBytes = pkGen.generate();
            }
            final ClassWriter cw = new ClassWriter(1);
            final JDOClassAdapter cv = new JDOClassAdapter(cw, this);
            ClassReader cr = null;
            InputStream classReaderInputStream = null;
            try {
                if (this.inputBytes != null) {
                    cr = new ClassReader(this.inputBytes);
                }
                else {
                    classReaderInputStream = this.clr.getResource(this.inputResourceName, null).openStream();
                    cr = new ClassReader(classReaderInputStream);
                }
                cr.accept(cv, 0);
                this.classBytes = cw.toByteArray();
            }
            finally {
                if (classReaderInputStream != null) {
                    classReaderInputStream.close();
                }
            }
        }
        catch (Exception e) {
            DataNucleusEnhancer.LOGGER.error("Error thrown enhancing with ASMClassEnhancer", e);
            return false;
        }
        return this.update = true;
    }
    
    @Override
    public byte[] getClassBytes() {
        return this.classBytes;
    }
    
    @Override
    public byte[] getPrimaryKeyClassBytes() {
        return this.pkClassBytes;
    }
    
    @Override
    public boolean validate() {
        if (this.cmd.getPersistenceModifier() != ClassPersistenceModifier.PERSISTENCE_CAPABLE && this.cmd.getPersistenceModifier() != ClassPersistenceModifier.PERSISTENCE_AWARE) {
            return false;
        }
        this.initialise();
        return this.checkClassIsEnhanced(true);
    }
    
    protected boolean checkClassIsEnhanced(final boolean logErrors) {
        try {
            final JDOClassChecker checker = new JDOClassChecker(this, logErrors);
            InputStream classReaderInputStream = null;
            try {
                ClassReader cr = null;
                if (this.inputBytes != null) {
                    cr = new ClassReader(this.inputBytes);
                }
                else {
                    classReaderInputStream = this.clr.getResource(this.inputResourceName, null).openStream();
                    cr = new ClassReader(classReaderInputStream);
                }
                cr.accept(checker, 0);
            }
            finally {
                if (classReaderInputStream != null) {
                    classReaderInputStream.close();
                }
            }
            return checker.isEnhanced();
        }
        catch (Exception e) {
            DataNucleusEnhancer.LOGGER.error("Error thrown enhancing with ASMClassEnhancer", e);
            return false;
        }
    }
    
    @Override
    public EnhancementNamer getNamer() {
        return this.namer;
    }
    
    public static class MyClassVisitor extends ClassVisitor
    {
        String className;
        
        public MyClassVisitor() {
            super(262144);
            this.className = null;
        }
        
        public String getClassName() {
            return this.className;
        }
        
        @Override
        public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        }
        
        @Override
        public void visit(final int version, final int access, final String name, final String sig, final String supername, final String[] intfs) {
            this.className = name.replace('/', '.');
        }
        
        @Override
        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
            return null;
        }
        
        @Override
        public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
            return null;
        }
        
        @Override
        public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] excpts) {
            return null;
        }
        
        @Override
        public void visitAttribute(final Attribute attr) {
        }
        
        @Override
        public void visitOuterClass(final String owner, final String name, final String desc) {
        }
        
        @Override
        public void visitSource(final String source, final String debug) {
        }
        
        @Override
        public void visitEnd() {
        }
    }
}
