// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata.annotations;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.NucleusLogger;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.lang.reflect.Method;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import org.datanucleus.metadata.FieldMetaData;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.PropertyMetaData;
import java.util.HashMap;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.PackageMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.util.Localiser;

public abstract class AbstractAnnotationReader implements AnnotationReader
{
    protected static final Localiser LOCALISER;
    protected MetaDataManager mgr;
    protected String[] supportedPackages;
    
    public AbstractAnnotationReader(final MetaDataManager mgr) {
        this.mgr = mgr;
    }
    
    @Override
    public String[] getSupportedAnnotationPackages() {
        return this.supportedPackages;
    }
    
    protected void setSupportedAnnotationPackages(final String[] packages) {
        this.supportedPackages = packages;
    }
    
    protected boolean isSupportedAnnotation(final String annotationClassName) {
        if (this.supportedPackages == null) {
            return false;
        }
        boolean supported = false;
        for (int j = 0; j < this.supportedPackages.length; ++j) {
            if (annotationClassName.startsWith(this.supportedPackages[j])) {
                supported = true;
                break;
            }
        }
        return supported;
    }
    
    @Override
    public AbstractClassMetaData getMetaDataForClass(final Class cls, final PackageMetaData pmd, final ClassLoaderResolver clr) {
        final AnnotationObject[] classAnnotations = this.getClassAnnotationsForClass(cls);
        final AbstractClassMetaData cmd = this.processClassAnnotations(pmd, cls, classAnnotations, clr);
        if (cmd != null) {
            final AnnotationManager annMgr = this.mgr.getAnnotationManager();
            for (int i = 0; i < classAnnotations.length; ++i) {
                final String annName = classAnnotations[i].getName();
                final ClassAnnotationHandler handler = annMgr.getHandlerForClassAnnotation(annName);
                if (handler != null) {
                    handler.processClassAnnotation(classAnnotations[i], cmd, clr);
                }
            }
            if (cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                boolean propertyAccessor = false;
                final Collection<AnnotatedMember> annotatedMethods = this.getJavaBeanAccessorAnnotationsForClass(cls);
                final Map<String, AnnotatedMember> m = new HashMap<String, AnnotatedMember>();
                for (final AnnotatedMember method : annotatedMethods) {
                    m.put(method.getName(), method);
                    if (method.getAnnotations().length > 0) {
                        propertyAccessor = true;
                    }
                }
                final Collection<AnnotatedMember> annotatedFields = this.getFieldAnnotationsForClass(cls);
                final Map<String, AnnotatedMember> f = new HashMap<String, AnnotatedMember>();
                for (final AnnotatedMember field : annotatedFields) {
                    f.put(field.getName(), field);
                }
                for (final AnnotatedMember method2 : m.values()) {
                    final AnnotationObject[] annotations = method2.getAnnotations();
                    AbstractMemberMetaData mmd = this.processMemberAnnotations(cmd, method2.getMember(), annotations, propertyAccessor);
                    if (annotations != null && annotations.length > 0) {
                        for (int j = 0; j < annotations.length; ++j) {
                            final String annName2 = annotations[j].getName();
                            final MemberAnnotationHandler handler2 = annMgr.getHandlerForMemberAnnotation(annName2);
                            if (handler2 != null) {
                                if (mmd == null) {
                                    mmd = new PropertyMetaData(cmd, method2.getMember().getName());
                                    cmd.addMember(mmd);
                                }
                                handler2.processMemberAnnotation(annotations[j], mmd, clr);
                            }
                        }
                    }
                }
                for (final AnnotatedMember field : f.values()) {
                    final AnnotationObject[] annotations = field.getAnnotations();
                    AbstractMemberMetaData mmd = this.processMemberAnnotations(cmd, field.getMember(), annotations, propertyAccessor);
                    if (annotations != null && annotations.length > 0) {
                        for (int j = 0; j < annotations.length; ++j) {
                            final String annName2 = annotations[j].getName();
                            final MemberAnnotationHandler handler2 = annMgr.getHandlerForMemberAnnotation(annName2);
                            if (handler2 != null) {
                                if (mmd == null) {
                                    mmd = new FieldMetaData(cmd, field.getMember().getName());
                                    cmd.addMember(mmd);
                                }
                                handler2.processMemberAnnotation(annotations[j], mmd, clr);
                            }
                        }
                    }
                }
                final Method[] methods = cls.getDeclaredMethods();
                for (int numberOfMethods = methods.length, k = 0; k < numberOfMethods; ++k) {
                    this.processMethodAnnotations(cmd, methods[k]);
                }
            }
        }
        return cmd;
    }
    
    protected abstract AbstractClassMetaData processClassAnnotations(final PackageMetaData p0, final Class p1, final AnnotationObject[] p2, final ClassLoaderResolver p3);
    
    protected abstract AbstractMemberMetaData processMemberAnnotations(final AbstractClassMetaData p0, final Member p1, final AnnotationObject[] p2, final boolean p3);
    
    protected abstract void processMethodAnnotations(final AbstractClassMetaData p0, final Method p1);
    
    protected AnnotationObject[] getClassAnnotationsForClass(final Class cls) {
        final Annotation[] annotations = cls.getAnnotations();
        final List<Annotation> supportedAnnots = new ArrayList<Annotation>();
        if (annotations != null && annotations.length > 0) {
            final AnnotationManager annMgr = this.mgr.getAnnotationManager();
            for (int j = 0; j < annotations.length; ++j) {
                final String annName = annotations[j].annotationType().getName();
                if (this.isSupportedAnnotation(annName) || annMgr.getClassAnnotationHasHandler(annName)) {
                    supportedAnnots.add(annotations[j]);
                }
            }
        }
        return this.getAnnotationObjectsForAnnotations(cls.getName(), supportedAnnots.toArray(new Annotation[supportedAnnots.size()]));
    }
    
    protected Collection<AnnotatedMember> getJavaBeanAccessorAnnotationsForClass(final Class cls) {
        final Collection<AnnotatedMember> annotatedMethods = new HashSet<AnnotatedMember>();
        final Method[] methods = cls.getDeclaredMethods();
        for (int numberOfMethods = methods.length, i = 0; i < numberOfMethods; ++i) {
            final String methodName = methods[i].getName();
            if ((methodName.startsWith("get") && methodName.length() > 3) || (methodName.startsWith("is") && methodName.length() > 2)) {
                final Annotation[] annotations = methods[i].getAnnotations();
                final List<Annotation> supportedAnnots = new ArrayList<Annotation>();
                if (annotations != null && annotations.length > 0) {
                    final AnnotationManager annMgr = this.mgr.getAnnotationManager();
                    for (int j = 0; j < annotations.length; ++j) {
                        final String annName = annotations[j].annotationType().getName();
                        if (this.isSupportedAnnotation(annName) || annMgr.getMemberAnnotationHasHandler(annName)) {
                            supportedAnnots.add(annotations[j]);
                        }
                    }
                }
                final AnnotationObject[] objects = this.getAnnotationObjectsForAnnotations(cls.getName(), supportedAnnots.toArray(new Annotation[supportedAnnots.size()]));
                final AnnotatedMember annMember = new AnnotatedMember(new Member(methods[i]), objects);
                annotatedMethods.add(annMember);
            }
        }
        return annotatedMethods;
    }
    
    protected Collection<Annotation[]> getMethodAnnotationsForClass(final Class cls) {
        final Collection<Annotation[]> annotatedMethods = new HashSet<Annotation[]>();
        final Method[] methods = cls.getDeclaredMethods();
        for (int numberOfMethods = methods.length, i = 0; i < numberOfMethods; ++i) {
            final Annotation[] annotations = methods[i].getAnnotations();
            if (annotations != null && annotations.length > 0) {
                final AnnotationManager annMgr = this.mgr.getAnnotationManager();
                final List<Annotation> supportedAnnots = new ArrayList<Annotation>();
                for (int j = 0; j < annotations.length; ++j) {
                    final String annName = annotations[j].annotationType().getName();
                    if (this.isSupportedAnnotation(annName) || annMgr.getMemberAnnotationHasHandler(annName)) {
                        supportedAnnots.add(annotations[j]);
                    }
                }
                annotatedMethods.add(supportedAnnots.toArray(new Annotation[supportedAnnots.size()]));
            }
        }
        return annotatedMethods;
    }
    
    protected Collection<AnnotatedMember> getFieldAnnotationsForClass(final Class cls) {
        final Collection<AnnotatedMember> annotatedFields = new HashSet<AnnotatedMember>();
        final Field[] fields = cls.getDeclaredFields();
        for (int numberOfFields = fields.length, i = 0; i < numberOfFields; ++i) {
            final Annotation[] annotations = fields[i].getAnnotations();
            final List<Annotation> supportedAnnots = new ArrayList<Annotation>();
            if (annotations != null && annotations.length > 0) {
                final AnnotationManager annMgr = this.mgr.getAnnotationManager();
                for (int j = 0; j < annotations.length; ++j) {
                    final String annName = annotations[j].annotationType().getName();
                    if (this.isSupportedAnnotation(annName) || annMgr.getMemberAnnotationHasHandler(annName)) {
                        supportedAnnots.add(annotations[j]);
                    }
                }
            }
            final AnnotationObject[] objects = this.getAnnotationObjectsForAnnotations(cls.getName(), supportedAnnots.toArray(new Annotation[supportedAnnots.size()]));
            final AnnotatedMember annField = new AnnotatedMember(new Member(fields[i]), objects);
            annotatedFields.add(annField);
        }
        return annotatedFields;
    }
    
    protected AnnotationObject[] getAnnotationObjectsForAnnotations(final String clsName, final Annotation[] annotations) {
        if (annotations == null) {
            return null;
        }
        final AnnotationObject[] objects = new AnnotationObject[annotations.length];
        for (int numberOfAnns = annotations.length, i = 0; i < numberOfAnns; ++i) {
            final HashMap<String, Object> map = new HashMap<String, Object>();
            final Method[] annMethods = annotations[i].annotationType().getDeclaredMethods();
            for (int numberOfAnnotateMethods = annMethods.length, j = 0; j < numberOfAnnotateMethods; ++j) {
                try {
                    map.put(annMethods[j].getName(), annMethods[j].invoke(annotations[i], new Object[0]));
                }
                catch (Exception ex) {
                    NucleusLogger.METADATA.warn(AbstractAnnotationReader.LOCALISER.msg("044201", clsName, annotations[i].annotationType().getName(), annMethods[j].getName()));
                }
            }
            objects[i] = new AnnotationObject(annotations[i].annotationType().getName(), map);
        }
        return objects;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
