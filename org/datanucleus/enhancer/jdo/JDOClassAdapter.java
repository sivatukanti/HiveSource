// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo;

import java.util.Iterator;
import java.util.List;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.enhancer.jdo.method.JdoSetNormal;
import org.datanucleus.enhancer.jdo.method.JdoSetViaCheck;
import org.datanucleus.enhancer.jdo.method.JdoSetViaMediate;
import org.datanucleus.enhancer.jdo.method.JdoGetNormal;
import org.datanucleus.enhancer.jdo.method.JdoGetViaCheck;
import org.datanucleus.enhancer.jdo.method.JdoGetViaMediate;
import org.datanucleus.enhancer.jdo.method.WriteObject;
import org.datanucleus.util.StringUtils;
import java.security.AccessController;
import java.io.ObjectStreamClass;
import java.security.PrivilegedAction;
import java.io.Serializable;
import org.datanucleus.enhancer.ClassMethod;
import org.datanucleus.enhancer.jdo.method.DefaultConstructor;
import org.datanucleus.enhancer.jdo.method.InitClass;
import org.datanucleus.asm.Type;
import org.datanucleus.enhancer.ClassField;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.metadata.PropertyMetaData;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.asm.MethodVisitor;
import org.datanucleus.asm.FieldVisitor;
import org.datanucleus.enhancer.DataNucleusEnhancer;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.util.Localiser;
import org.datanucleus.asm.ClassVisitor;

public class JDOClassAdapter extends ClassVisitor
{
    protected static final Localiser LOCALISER;
    protected ClassEnhancer enhancer;
    protected boolean hasDefaultConstructor;
    protected boolean hasSerialVersionUID;
    protected boolean hasJdoDetachedState;
    protected boolean hasWriteObject;
    protected boolean hasStaticInitialisation;
    
    public JDOClassAdapter(final ClassVisitor cv, final ClassEnhancer enhancer) {
        super(262144, cv);
        this.hasDefaultConstructor = false;
        this.hasSerialVersionUID = false;
        this.hasJdoDetachedState = false;
        this.hasWriteObject = false;
        this.hasStaticInitialisation = false;
        this.enhancer = enhancer;
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        if (this.enhancer.getClassMetaData().getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
            boolean alreadyPersistenceCapable = false;
            boolean alreadyDetachable = false;
            boolean needsPersistenceCapable = false;
            boolean needsDetachable = false;
            int numInterfaces = 0;
            if (interfaces != null && interfaces.length > 0) {
                numInterfaces = interfaces.length;
                for (int i = 0; i < interfaces.length; ++i) {
                    if (interfaces[i].equals(this.enhancer.getNamer().getDetachableAsmClassName())) {
                        alreadyDetachable = true;
                    }
                    if (interfaces[i].equals(this.enhancer.getNamer().getPersistableAsmClassName())) {
                        alreadyPersistenceCapable = true;
                    }
                }
            }
            if (!alreadyDetachable && this.enhancer.getClassMetaData().isDetachable()) {
                ++numInterfaces;
                needsDetachable = true;
            }
            if (!alreadyPersistenceCapable) {
                ++numInterfaces;
                needsPersistenceCapable = true;
            }
            String[] intfs = interfaces;
            if (needsDetachable || needsPersistenceCapable) {
                intfs = new String[numInterfaces];
                int position = 0;
                if (interfaces != null && interfaces.length > 0) {
                    for (int j = 0; j < interfaces.length; ++j) {
                        intfs[position++] = interfaces[j];
                    }
                }
                if (needsDetachable) {
                    intfs[position++] = this.enhancer.getNamer().getDetachableAsmClassName();
                    if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
                        DataNucleusEnhancer.LOGGER.debug(JDOClassAdapter.LOCALISER.msg("Enhancer.AddInterface", this.enhancer.getNamer().getDetachableClass().getName()));
                    }
                }
                if (needsPersistenceCapable) {
                    intfs[position++] = this.enhancer.getNamer().getPersistableAsmClassName();
                    if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
                        DataNucleusEnhancer.LOGGER.debug(JDOClassAdapter.LOCALISER.msg("Enhancer.AddInterface", this.enhancer.getNamer().getPersistableClass().getName()));
                    }
                }
            }
            this.cv.visit(version, access, name, signature, superName, intfs);
        }
        else {
            this.cv.visit(version, access, name, signature, superName, interfaces);
        }
    }
    
    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        if (name.equals(this.enhancer.getNamer().getSerialVersionUidFieldName())) {
            this.hasSerialVersionUID = true;
        }
        else if (name.equals(this.enhancer.getNamer().getDetachedStateFieldName())) {
            this.hasJdoDetachedState = true;
        }
        return super.visitField(access, name, desc, signature, value);
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        if (name.equals("<init>") && desc != null && desc.equals("()V")) {
            this.hasDefaultConstructor = true;
        }
        if (name.equals("writeObject") && desc != null && desc.equals("(Ljava/io/ObjectOutputStream;)V")) {
            this.hasWriteObject = true;
        }
        if (name.equals("<clinit>") && desc != null && desc.equals("()V")) {
            this.hasStaticInitialisation = true;
        }
        final MethodVisitor mv = this.cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv == null) {
            return null;
        }
        if (name.equals("jdoPreClear") || name.equals("jdoPostLoad")) {
            return mv;
        }
        if (name.equals("readObject") && (desc.equals("(Ljava/io/ObjectOutputStream;)V") || desc.equals("(Ljava/io/ObjectInputStream;)V"))) {
            return mv;
        }
        final String propGetterName = ClassUtils.getFieldNameForJavaBeanGetter(name);
        final String propSetterName = ClassUtils.getFieldNameForJavaBeanSetter(name);
        if (propGetterName != null) {
            final AbstractMemberMetaData mmd = this.enhancer.getClassMetaData().getMetaDataForMember(propGetterName);
            if (mmd != null && mmd instanceof PropertyMetaData && mmd.getPersistenceModifier() != FieldPersistenceModifier.NONE) {
                return new JDOPropertyGetterAdapter(mv, this.enhancer, name, desc, mmd, this.cv);
            }
        }
        else if (propSetterName != null) {
            final AbstractMemberMetaData mmd = this.enhancer.getClassMetaData().getMetaDataForMember(propSetterName);
            if (mmd != null && mmd instanceof PropertyMetaData && mmd.getPersistenceModifier() != FieldPersistenceModifier.NONE) {
                return new JDOPropertySetterAdapter(mv, this.enhancer, name, desc, mmd, this.cv);
            }
        }
        return new JDOMethodAdapter(mv, this.enhancer, name, desc);
    }
    
    @Override
    public void visitEnd() {
        final AbstractClassMetaData cmd = this.enhancer.getClassMetaData();
        if (cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
            final List fields = this.enhancer.getFieldsList();
            for (final ClassField field : fields) {
                if (field.getName().equals(this.enhancer.getNamer().getDetachedStateFieldName()) && this.hasJdoDetachedState) {
                    continue;
                }
                if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
                    DataNucleusEnhancer.LOGGER.debug(JDOClassAdapter.LOCALISER.msg("Enhancer.AddField", ((Class)field.getType()).getName() + " " + field.getName()));
                }
                this.cv.visitField(field.getAccess(), field.getName(), Type.getDescriptor((Class<?>)field.getType()), null, null);
            }
            if (!this.hasStaticInitialisation) {
                final InitClass method = InitClass.getInstance(this.enhancer);
                method.initialise(this.cv);
                method.execute();
                method.close();
            }
            if (!this.hasDefaultConstructor && this.enhancer.hasOption("generate-default-constructor")) {
                final DefaultConstructor ctr = DefaultConstructor.getInstance(this.enhancer);
                ctr.initialise(this.cv);
                ctr.execute();
                ctr.close();
            }
            final List methods = this.enhancer.getMethodsList();
            for (final ClassMethod method2 : methods) {
                method2.initialise(this.cv);
                method2.execute();
                method2.close();
            }
            if (Serializable.class.isAssignableFrom(this.enhancer.getClassBeingEnhanced())) {
                if (!this.hasSerialVersionUID) {
                    Long uid = null;
                    try {
                        uid = AccessController.doPrivileged((PrivilegedAction<Long>)new PrivilegedAction() {
                            @Override
                            public Object run() {
                                return ObjectStreamClass.lookup(JDOClassAdapter.this.enhancer.getClassBeingEnhanced()).getSerialVersionUID();
                            }
                        });
                    }
                    catch (Throwable e) {
                        DataNucleusEnhancer.LOGGER.warn(StringUtils.getStringFromStackTrace(e));
                    }
                    final ClassField cf = new ClassField(this.enhancer, this.enhancer.getNamer().getSerialVersionUidFieldName(), 26, Long.TYPE, uid);
                    if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
                        DataNucleusEnhancer.LOGGER.debug(JDOClassAdapter.LOCALISER.msg("Enhancer.AddField", ((Class)cf.getType()).getName() + " " + cf.getName()));
                    }
                    this.cv.visitField(cf.getAccess(), cf.getName(), Type.getDescriptor((Class<?>)cf.getType()), null, cf.getInitialValue());
                }
                if (!this.hasWriteObject) {
                    final ClassMethod method2 = WriteObject.getInstance(this.enhancer);
                    method2.initialise(this.cv);
                    method2.execute();
                    method2.close();
                }
            }
            final AbstractMemberMetaData[] fmds = cmd.getManagedMembers();
            for (int i = 0; i < fmds.length; ++i) {
                if (fmds[i].getPersistenceModifier() != FieldPersistenceModifier.NONE) {
                    final byte jdoFlag = fmds[i].getPersistenceFlags();
                    ClassMethod getMethod = null;
                    ClassMethod setMethod = null;
                    if (!(fmds[i] instanceof PropertyMetaData)) {
                        if ((jdoFlag & 0x2) == 0x2) {
                            getMethod = new JdoGetViaMediate(this.enhancer, fmds[i]);
                        }
                        else if ((jdoFlag & 0x1) == 0x1) {
                            getMethod = new JdoGetViaCheck(this.enhancer, fmds[i]);
                        }
                        else {
                            getMethod = new JdoGetNormal(this.enhancer, fmds[i]);
                        }
                        if ((jdoFlag & 0x8) == 0x8) {
                            setMethod = new JdoSetViaMediate(this.enhancer, fmds[i]);
                        }
                        else if ((jdoFlag & 0x4) == 0x4) {
                            setMethod = new JdoSetViaCheck(this.enhancer, fmds[i]);
                        }
                        else {
                            setMethod = new JdoSetNormal(this.enhancer, fmds[i]);
                        }
                    }
                    if (getMethod != null) {
                        getMethod.initialise(this.cv);
                        getMethod.execute();
                        getMethod.close();
                    }
                    if (setMethod != null) {
                        setMethod.initialise(this.cv);
                        setMethod.execute();
                        setMethod.close();
                    }
                }
            }
        }
        this.cv.visitEnd();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassEnhancer.class.getClassLoader());
    }
}
