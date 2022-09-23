// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo;

import org.datanucleus.asm.MethodVisitor;
import org.datanucleus.asm.Type;
import org.datanucleus.asm.FieldVisitor;
import java.util.Iterator;
import org.datanucleus.asm.Attribute;
import org.datanucleus.asm.AnnotationVisitor;
import org.datanucleus.enhancer.DataNucleusEnhancer;
import java.util.Collection;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.enhancer.ClassMethod;
import org.datanucleus.enhancer.ClassField;
import java.util.HashSet;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.util.Localiser;
import org.datanucleus.asm.ClassVisitor;

public class JDOClassChecker extends ClassVisitor
{
    protected static final Localiser LOCALISER;
    protected ClassEnhancer enhancer;
    protected HashSet<ClassField> fieldsRequired;
    protected HashSet<ClassMethod> methodsRequired;
    protected boolean enhanced;
    protected boolean logErrors;
    
    public JDOClassChecker(final ClassEnhancer enhancer, final boolean logErrors) {
        super(262144);
        this.fieldsRequired = new HashSet<ClassField>();
        this.methodsRequired = new HashSet<ClassMethod>();
        this.enhanced = false;
        this.logErrors = true;
        this.enhancer = enhancer;
        this.logErrors = logErrors;
        if (enhancer.getClassMetaData().getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
            this.fieldsRequired.addAll((Collection<?>)enhancer.getFieldsList());
            this.methodsRequired.addAll((Collection<?>)enhancer.getMethodsList());
        }
    }
    
    public boolean isEnhanced() {
        return this.enhanced;
    }
    
    protected void reportError(final String msg) {
        if (this.logErrors) {
            DataNucleusEnhancer.LOGGER.error(msg);
        }
        else if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
            DataNucleusEnhancer.LOGGER.debug(msg);
        }
        this.enhanced = false;
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String supername, final String[] interfaces) {
        this.enhanced = true;
        if (this.enhancer.getClassMetaData().getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
            if (interfaces == null) {
                this.enhanced = false;
                return;
            }
            if (!this.hasInterface(interfaces, this.enhancer.getNamer().getPersistableAsmClassName())) {
                this.reportError(JDOClassChecker.LOCALISER.msg("Enhancer.Check.InterfaceMissing", this.enhancer.getClassName(), this.enhancer.getNamer().getPersistableClass().getName()));
            }
            if (this.enhancer.getClassMetaData().isDetachable() && !this.hasInterface(interfaces, this.enhancer.getNamer().getDetachableAsmClassName())) {
                this.reportError(JDOClassChecker.LOCALISER.msg("Enhancer.Check.InterfaceMissing", this.enhancer.getClassName(), this.enhancer.getNamer().getDetachableClass().getName()));
            }
        }
    }
    
    protected boolean hasInterface(final String[] interfaces, final String intf) {
        if (interfaces == null || interfaces.length <= 0) {
            return false;
        }
        for (int i = 0; i < interfaces.length; ++i) {
            if (interfaces[i].equals(intf)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        return null;
    }
    
    @Override
    public void visitAttribute(final Attribute attr) {
    }
    
    @Override
    public void visitEnd() {
        if (this.enhancer.getClassMetaData().getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
            for (final ClassField field : this.fieldsRequired) {
                this.reportError(JDOClassChecker.LOCALISER.msg("Enhancer.Check.FieldMissing", this.enhancer.getClassName(), field.getName()));
            }
            for (final ClassMethod method : this.methodsRequired) {
                this.reportError(JDOClassChecker.LOCALISER.msg("Enhancer.Check.MethodMissing", this.enhancer.getClassName(), method.getName()));
            }
        }
        else if (this.enhancer.getClassMetaData().getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_AWARE) {
            this.enhanced = false;
        }
    }
    
    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        final Iterator iter = this.fieldsRequired.iterator();
        while (iter.hasNext()) {
            final ClassField field = iter.next();
            if (field.getName().equals(name)) {
                if (field.getAccess() != access) {
                    this.reportError(JDOClassChecker.LOCALISER.msg("Enhancer.Check.FieldIncorrectAccess", this.enhancer.getClassName(), name));
                }
                else {
                    if (desc.equals(Type.getDescriptor((Class<?>)field.getType()))) {
                        iter.remove();
                        break;
                    }
                    this.reportError(JDOClassChecker.LOCALISER.msg("Enhancer.Check.FieldIncorrectType", this.enhancer.getClassName(), name));
                }
            }
        }
        return null;
    }
    
    @Override
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final Iterator<ClassMethod> iter = this.methodsRequired.iterator();
        while (iter.hasNext()) {
            final ClassMethod method = iter.next();
            if (method.getName().equals(name) && method.getDescriptor().equals(desc)) {
                if (method.getAccess() == access) {
                    iter.remove();
                    break;
                }
                this.reportError(JDOClassChecker.LOCALISER.msg("Enhancer.Check.MethodIncorrectAccess", this.enhancer.getClassName(), name));
            }
        }
        return null;
    }
    
    @Override
    public void visitOuterClass(final String owner, final String name, final String desc) {
    }
    
    @Override
    public void visitSource(final String source, final String debug) {
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassEnhancer.class.getClassLoader());
    }
}
