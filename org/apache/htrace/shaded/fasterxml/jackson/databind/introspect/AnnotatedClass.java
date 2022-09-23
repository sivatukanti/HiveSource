// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedList;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.util.Collections;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import java.util.List;

public final class AnnotatedClass extends Annotated
{
    private static final AnnotationMap[] NO_ANNOTATION_MAPS;
    protected final Class<?> _class;
    protected final List<Class<?>> _superTypes;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final ClassIntrospector.MixInResolver _mixInResolver;
    protected final Class<?> _primaryMixIn;
    protected AnnotationMap _classAnnotations;
    protected boolean _creatorsResolved;
    protected AnnotatedConstructor _defaultConstructor;
    protected List<AnnotatedConstructor> _constructors;
    protected List<AnnotatedMethod> _creatorMethods;
    protected AnnotatedMethodMap _memberMethods;
    protected List<AnnotatedField> _fields;
    
    private AnnotatedClass(final Class<?> cls, final List<Class<?>> superTypes, final AnnotationIntrospector aintr, final ClassIntrospector.MixInResolver mir, final AnnotationMap classAnnotations) {
        this._creatorsResolved = false;
        this._class = cls;
        this._superTypes = superTypes;
        this._annotationIntrospector = aintr;
        this._mixInResolver = mir;
        this._primaryMixIn = ((this._mixInResolver == null) ? null : this._mixInResolver.findMixInClassFor(this._class));
        this._classAnnotations = classAnnotations;
    }
    
    @Override
    public AnnotatedClass withAnnotations(final AnnotationMap ann) {
        return new AnnotatedClass(this._class, this._superTypes, this._annotationIntrospector, this._mixInResolver, ann);
    }
    
    public static AnnotatedClass construct(final Class<?> cls, final AnnotationIntrospector aintr, final ClassIntrospector.MixInResolver mir) {
        return new AnnotatedClass(cls, ClassUtil.findSuperTypes(cls, null), aintr, mir, null);
    }
    
    public static AnnotatedClass constructWithoutSuperTypes(final Class<?> cls, final AnnotationIntrospector aintr, final ClassIntrospector.MixInResolver mir) {
        return new AnnotatedClass(cls, Collections.emptyList(), aintr, mir, null);
    }
    
    @Override
    public Class<?> getAnnotated() {
        return this._class;
    }
    
    public int getModifiers() {
        return this._class.getModifiers();
    }
    
    @Override
    public String getName() {
        return this._class.getName();
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        if (this._classAnnotations == null) {
            this.resolveClassAnnotations();
        }
        return this._classAnnotations.get(acls);
    }
    
    @Override
    public Type getGenericType() {
        return this._class;
    }
    
    @Override
    public Class<?> getRawType() {
        return this._class;
    }
    
    @Override
    public Iterable<Annotation> annotations() {
        if (this._classAnnotations == null) {
            this.resolveClassAnnotations();
        }
        return this._classAnnotations.annotations();
    }
    
    @Override
    protected AnnotationMap getAllAnnotations() {
        if (this._classAnnotations == null) {
            this.resolveClassAnnotations();
        }
        return this._classAnnotations;
    }
    
    public Annotations getAnnotations() {
        if (this._classAnnotations == null) {
            this.resolveClassAnnotations();
        }
        return this._classAnnotations;
    }
    
    public boolean hasAnnotations() {
        if (this._classAnnotations == null) {
            this.resolveClassAnnotations();
        }
        return this._classAnnotations.size() > 0;
    }
    
    public AnnotatedConstructor getDefaultConstructor() {
        if (!this._creatorsResolved) {
            this.resolveCreators();
        }
        return this._defaultConstructor;
    }
    
    public List<AnnotatedConstructor> getConstructors() {
        if (!this._creatorsResolved) {
            this.resolveCreators();
        }
        return this._constructors;
    }
    
    public List<AnnotatedMethod> getStaticMethods() {
        if (!this._creatorsResolved) {
            this.resolveCreators();
        }
        return this._creatorMethods;
    }
    
    public Iterable<AnnotatedMethod> memberMethods() {
        if (this._memberMethods == null) {
            this.resolveMemberMethods();
        }
        return this._memberMethods;
    }
    
    public int getMemberMethodCount() {
        if (this._memberMethods == null) {
            this.resolveMemberMethods();
        }
        return this._memberMethods.size();
    }
    
    public AnnotatedMethod findMethod(final String name, final Class<?>[] paramTypes) {
        if (this._memberMethods == null) {
            this.resolveMemberMethods();
        }
        return this._memberMethods.find(name, paramTypes);
    }
    
    public int getFieldCount() {
        if (this._fields == null) {
            this.resolveFields();
        }
        return this._fields.size();
    }
    
    public Iterable<AnnotatedField> fields() {
        if (this._fields == null) {
            this.resolveFields();
        }
        return this._fields;
    }
    
    private void resolveClassAnnotations() {
        this._classAnnotations = new AnnotationMap();
        if (this._annotationIntrospector != null) {
            if (this._primaryMixIn != null) {
                this._addClassMixIns(this._classAnnotations, this._class, this._primaryMixIn);
            }
            this._addAnnotationsIfNotPresent(this._classAnnotations, this._class.getDeclaredAnnotations());
            for (final Class<?> cls : this._superTypes) {
                this._addClassMixIns(this._classAnnotations, cls);
                this._addAnnotationsIfNotPresent(this._classAnnotations, cls.getDeclaredAnnotations());
            }
            this._addClassMixIns(this._classAnnotations, Object.class);
        }
    }
    
    private void resolveCreators() {
        List<AnnotatedConstructor> constructors = null;
        final Constructor[] arr$;
        final Constructor<?>[] declaredCtors = (Constructor<?>[])(arr$ = this._class.getDeclaredConstructors());
        for (final Constructor<?> ctor : arr$) {
            if (ctor.getParameterTypes().length == 0) {
                this._defaultConstructor = this._constructConstructor(ctor, true);
            }
            else {
                if (constructors == null) {
                    constructors = new ArrayList<AnnotatedConstructor>(Math.max(10, declaredCtors.length));
                }
                constructors.add(this._constructConstructor(ctor, false));
            }
        }
        if (constructors == null) {
            this._constructors = Collections.emptyList();
        }
        else {
            this._constructors = constructors;
        }
        if (this._primaryMixIn != null && (this._defaultConstructor != null || !this._constructors.isEmpty())) {
            this._addConstructorMixIns(this._primaryMixIn);
        }
        if (this._annotationIntrospector != null) {
            if (this._defaultConstructor != null && this._annotationIntrospector.hasIgnoreMarker(this._defaultConstructor)) {
                this._defaultConstructor = null;
            }
            if (this._constructors != null) {
                int i = this._constructors.size();
                while (--i >= 0) {
                    if (this._annotationIntrospector.hasIgnoreMarker(this._constructors.get(i))) {
                        this._constructors.remove(i);
                    }
                }
            }
        }
        List<AnnotatedMethod> creatorMethods = null;
        for (final Method m : this._class.getDeclaredMethods()) {
            if (Modifier.isStatic(m.getModifiers())) {
                if (creatorMethods == null) {
                    creatorMethods = new ArrayList<AnnotatedMethod>(8);
                }
                creatorMethods.add(this._constructCreatorMethod(m));
            }
        }
        if (creatorMethods == null) {
            this._creatorMethods = Collections.emptyList();
        }
        else {
            this._creatorMethods = creatorMethods;
            if (this._primaryMixIn != null) {
                this._addFactoryMixIns(this._primaryMixIn);
            }
            if (this._annotationIntrospector != null) {
                int j = this._creatorMethods.size();
                while (--j >= 0) {
                    if (this._annotationIntrospector.hasIgnoreMarker(this._creatorMethods.get(j))) {
                        this._creatorMethods.remove(j);
                    }
                }
            }
        }
        this._creatorsResolved = true;
    }
    
    private void resolveMemberMethods() {
        this._memberMethods = new AnnotatedMethodMap();
        final AnnotatedMethodMap mixins = new AnnotatedMethodMap();
        this._addMemberMethods(this._class, this._memberMethods, this._primaryMixIn, mixins);
        for (final Class<?> cls : this._superTypes) {
            final Class<?> mixin = (this._mixInResolver == null) ? null : this._mixInResolver.findMixInClassFor(cls);
            this._addMemberMethods(cls, this._memberMethods, mixin, mixins);
        }
        if (this._mixInResolver != null) {
            final Class<?> mixin2 = this._mixInResolver.findMixInClassFor(Object.class);
            if (mixin2 != null) {
                this._addMethodMixIns(this._class, this._memberMethods, mixin2, mixins);
            }
        }
        if (this._annotationIntrospector != null && !mixins.isEmpty()) {
            for (final AnnotatedMethod mixIn : mixins) {
                try {
                    final Method m = Object.class.getDeclaredMethod(mixIn.getName(), mixIn.getRawParameterTypes());
                    if (m == null) {
                        continue;
                    }
                    final AnnotatedMethod am = this._constructMethod(m);
                    this._addMixOvers(mixIn.getAnnotated(), am, false);
                    this._memberMethods.add(am);
                }
                catch (Exception ex) {}
            }
        }
    }
    
    private void resolveFields() {
        final Map<String, AnnotatedField> foundFields = this._findFields(this._class, null);
        if (foundFields == null || foundFields.size() == 0) {
            this._fields = Collections.emptyList();
        }
        else {
            (this._fields = new ArrayList<AnnotatedField>(foundFields.size())).addAll(foundFields.values());
        }
    }
    
    protected void _addClassMixIns(final AnnotationMap annotations, final Class<?> toMask) {
        if (this._mixInResolver != null) {
            this._addClassMixIns(annotations, toMask, this._mixInResolver.findMixInClassFor(toMask));
        }
    }
    
    protected void _addClassMixIns(final AnnotationMap annotations, final Class<?> toMask, final Class<?> mixin) {
        if (mixin == null) {
            return;
        }
        this._addAnnotationsIfNotPresent(annotations, mixin.getDeclaredAnnotations());
        for (final Class<?> parent : ClassUtil.findSuperTypes(mixin, toMask)) {
            this._addAnnotationsIfNotPresent(annotations, parent.getDeclaredAnnotations());
        }
    }
    
    protected void _addConstructorMixIns(final Class<?> mixin) {
        MemberKey[] ctorKeys = null;
        final int ctorCount = (this._constructors == null) ? 0 : this._constructors.size();
        for (final Constructor<?> ctor : mixin.getDeclaredConstructors()) {
            if (ctor.getParameterTypes().length == 0) {
                if (this._defaultConstructor != null) {
                    this._addMixOvers(ctor, this._defaultConstructor, false);
                }
            }
            else {
                if (ctorKeys == null) {
                    ctorKeys = new MemberKey[ctorCount];
                    for (int i = 0; i < ctorCount; ++i) {
                        ctorKeys[i] = new MemberKey(this._constructors.get(i).getAnnotated());
                    }
                }
                final MemberKey key = new MemberKey(ctor);
                for (int j = 0; j < ctorCount; ++j) {
                    if (key.equals(ctorKeys[j])) {
                        this._addMixOvers(ctor, this._constructors.get(j), true);
                        break;
                    }
                }
            }
        }
    }
    
    protected void _addFactoryMixIns(final Class<?> mixin) {
        MemberKey[] methodKeys = null;
        final int methodCount = this._creatorMethods.size();
        for (final Method m : mixin.getDeclaredMethods()) {
            if (Modifier.isStatic(m.getModifiers())) {
                if (m.getParameterTypes().length != 0) {
                    if (methodKeys == null) {
                        methodKeys = new MemberKey[methodCount];
                        for (int i = 0; i < methodCount; ++i) {
                            methodKeys[i] = new MemberKey(this._creatorMethods.get(i).getAnnotated());
                        }
                    }
                    final MemberKey key = new MemberKey(m);
                    for (int j = 0; j < methodCount; ++j) {
                        if (key.equals(methodKeys[j])) {
                            this._addMixOvers(m, this._creatorMethods.get(j), true);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    protected void _addMemberMethods(final Class<?> cls, final AnnotatedMethodMap methods, final Class<?> mixInCls, final AnnotatedMethodMap mixIns) {
        if (mixInCls != null) {
            this._addMethodMixIns(cls, methods, mixInCls, mixIns);
        }
        if (cls == null) {
            return;
        }
        for (final Method m : cls.getDeclaredMethods()) {
            if (this._isIncludableMemberMethod(m)) {
                AnnotatedMethod old = methods.find(m);
                if (old == null) {
                    final AnnotatedMethod newM = this._constructMethod(m);
                    methods.add(newM);
                    old = mixIns.remove(m);
                    if (old != null) {
                        this._addMixOvers(old.getAnnotated(), newM, false);
                    }
                }
                else {
                    this._addMixUnders(m, old);
                    if (old.getDeclaringClass().isInterface() && !m.getDeclaringClass().isInterface()) {
                        methods.add(old.withMethod(m));
                    }
                }
            }
        }
    }
    
    protected void _addMethodMixIns(final Class<?> targetClass, final AnnotatedMethodMap methods, final Class<?> mixInCls, final AnnotatedMethodMap mixIns) {
        final List<Class<?>> parents = new ArrayList<Class<?>>();
        parents.add(mixInCls);
        ClassUtil.findSuperTypes(mixInCls, targetClass, parents);
        for (final Class<?> mixin : parents) {
            for (final Method m : mixin.getDeclaredMethods()) {
                if (this._isIncludableMemberMethod(m)) {
                    final AnnotatedMethod am = methods.find(m);
                    if (am != null) {
                        this._addMixUnders(m, am);
                    }
                    else {
                        mixIns.add(this._constructMethod(m));
                    }
                }
            }
        }
    }
    
    protected Map<String, AnnotatedField> _findFields(final Class<?> c, Map<String, AnnotatedField> fields) {
        final Class<?> parent = c.getSuperclass();
        if (parent != null) {
            fields = this._findFields(parent, fields);
            for (final Field f : c.getDeclaredFields()) {
                if (this._isIncludableField(f)) {
                    if (fields == null) {
                        fields = new LinkedHashMap<String, AnnotatedField>();
                    }
                    fields.put(f.getName(), this._constructField(f));
                }
            }
            if (this._mixInResolver != null) {
                final Class<?> mixin = this._mixInResolver.findMixInClassFor(c);
                if (mixin != null) {
                    this._addFieldMixIns(parent, mixin, fields);
                }
            }
        }
        return fields;
    }
    
    protected void _addFieldMixIns(final Class<?> targetClass, final Class<?> mixInCls, final Map<String, AnnotatedField> fields) {
        final List<Class<?>> parents = new ArrayList<Class<?>>();
        parents.add(mixInCls);
        ClassUtil.findSuperTypes(mixInCls, targetClass, parents);
        for (final Class<?> mixin : parents) {
            for (final Field mixinField : mixin.getDeclaredFields()) {
                if (this._isIncludableField(mixinField)) {
                    final String name = mixinField.getName();
                    final AnnotatedField maskedField = fields.get(name);
                    if (maskedField != null) {
                        this._addOrOverrideAnnotations(maskedField, mixinField.getDeclaredAnnotations());
                    }
                }
            }
        }
    }
    
    protected AnnotatedMethod _constructMethod(final Method m) {
        if (this._annotationIntrospector == null) {
            return new AnnotatedMethod(m, this._emptyAnnotationMap(), null);
        }
        return new AnnotatedMethod(m, this._collectRelevantAnnotations(m.getDeclaredAnnotations()), null);
    }
    
    protected AnnotatedConstructor _constructConstructor(final Constructor<?> ctor, final boolean defaultCtor) {
        if (this._annotationIntrospector == null) {
            return new AnnotatedConstructor(ctor, this._emptyAnnotationMap(), this._emptyAnnotationMaps(ctor.getParameterTypes().length));
        }
        if (defaultCtor) {
            return new AnnotatedConstructor(ctor, this._collectRelevantAnnotations(ctor.getDeclaredAnnotations()), null);
        }
        Annotation[][] paramAnns = ctor.getParameterAnnotations();
        final int paramCount = ctor.getParameterTypes().length;
        AnnotationMap[] resolvedAnnotations = null;
        if (paramCount != paramAnns.length) {
            final Class<?> dc = ctor.getDeclaringClass();
            if (dc.isEnum() && paramCount == paramAnns.length + 2) {
                final Annotation[][] old = paramAnns;
                paramAnns = new Annotation[old.length + 2][];
                System.arraycopy(old, 0, paramAnns, 2, old.length);
                resolvedAnnotations = this._collectRelevantAnnotations(paramAnns);
            }
            else if (dc.isMemberClass() && paramCount == paramAnns.length + 1) {
                final Annotation[][] old = paramAnns;
                paramAnns = new Annotation[old.length + 1][];
                System.arraycopy(old, 0, paramAnns, 1, old.length);
                resolvedAnnotations = this._collectRelevantAnnotations(paramAnns);
            }
            if (resolvedAnnotations == null) {
                throw new IllegalStateException("Internal error: constructor for " + ctor.getDeclaringClass().getName() + " has mismatch: " + paramCount + " parameters; " + paramAnns.length + " sets of annotations");
            }
        }
        else {
            resolvedAnnotations = this._collectRelevantAnnotations(paramAnns);
        }
        return new AnnotatedConstructor(ctor, this._collectRelevantAnnotations(ctor.getDeclaredAnnotations()), resolvedAnnotations);
    }
    
    protected AnnotatedMethod _constructCreatorMethod(final Method m) {
        if (this._annotationIntrospector == null) {
            return new AnnotatedMethod(m, this._emptyAnnotationMap(), this._emptyAnnotationMaps(m.getParameterTypes().length));
        }
        return new AnnotatedMethod(m, this._collectRelevantAnnotations(m.getDeclaredAnnotations()), this._collectRelevantAnnotations(m.getParameterAnnotations()));
    }
    
    protected AnnotatedField _constructField(final Field f) {
        if (this._annotationIntrospector == null) {
            return new AnnotatedField(f, this._emptyAnnotationMap());
        }
        return new AnnotatedField(f, this._collectRelevantAnnotations(f.getDeclaredAnnotations()));
    }
    
    private AnnotationMap _emptyAnnotationMap() {
        return new AnnotationMap();
    }
    
    private AnnotationMap[] _emptyAnnotationMaps(final int count) {
        if (count == 0) {
            return AnnotatedClass.NO_ANNOTATION_MAPS;
        }
        final AnnotationMap[] maps = new AnnotationMap[count];
        for (int i = 0; i < count; ++i) {
            maps[i] = this._emptyAnnotationMap();
        }
        return maps;
    }
    
    protected boolean _isIncludableMemberMethod(final Method m) {
        if (Modifier.isStatic(m.getModifiers())) {
            return false;
        }
        if (m.isSynthetic() || m.isBridge()) {
            return false;
        }
        final int pcount = m.getParameterTypes().length;
        return pcount <= 2;
    }
    
    private boolean _isIncludableField(final Field f) {
        if (f.isSynthetic()) {
            return false;
        }
        final int mods = f.getModifiers();
        return !Modifier.isStatic(mods) && !Modifier.isTransient(mods);
    }
    
    protected AnnotationMap[] _collectRelevantAnnotations(final Annotation[][] anns) {
        final int len = anns.length;
        final AnnotationMap[] result = new AnnotationMap[len];
        for (int i = 0; i < len; ++i) {
            result[i] = this._collectRelevantAnnotations(anns[i]);
        }
        return result;
    }
    
    protected AnnotationMap _collectRelevantAnnotations(final Annotation[] anns) {
        final AnnotationMap annMap = new AnnotationMap();
        this._addAnnotationsIfNotPresent(annMap, anns);
        return annMap;
    }
    
    private void _addAnnotationsIfNotPresent(final AnnotationMap result, final Annotation[] anns) {
        if (anns != null) {
            List<Annotation[]> bundles = null;
            for (final Annotation ann : anns) {
                if (this._isAnnotationBundle(ann)) {
                    if (bundles == null) {
                        bundles = new LinkedList<Annotation[]>();
                    }
                    bundles.add(ann.annotationType().getDeclaredAnnotations());
                }
                else {
                    result.addIfNotPresent(ann);
                }
            }
            if (bundles != null) {
                for (final Annotation[] annotations : bundles) {
                    this._addAnnotationsIfNotPresent(result, annotations);
                }
            }
        }
    }
    
    private void _addAnnotationsIfNotPresent(final AnnotatedMember target, final Annotation[] anns) {
        if (anns != null) {
            List<Annotation[]> bundles = null;
            for (final Annotation ann : anns) {
                if (this._isAnnotationBundle(ann)) {
                    if (bundles == null) {
                        bundles = new LinkedList<Annotation[]>();
                    }
                    bundles.add(ann.annotationType().getDeclaredAnnotations());
                }
                else {
                    target.addIfNotPresent(ann);
                }
            }
            if (bundles != null) {
                for (final Annotation[] annotations : bundles) {
                    this._addAnnotationsIfNotPresent(target, annotations);
                }
            }
        }
    }
    
    private void _addOrOverrideAnnotations(final AnnotatedMember target, final Annotation[] anns) {
        if (anns != null) {
            List<Annotation[]> bundles = null;
            for (final Annotation ann : anns) {
                if (this._isAnnotationBundle(ann)) {
                    if (bundles == null) {
                        bundles = new LinkedList<Annotation[]>();
                    }
                    bundles.add(ann.annotationType().getDeclaredAnnotations());
                }
                else {
                    target.addOrOverride(ann);
                }
            }
            if (bundles != null) {
                for (final Annotation[] annotations : bundles) {
                    this._addOrOverrideAnnotations(target, annotations);
                }
            }
        }
    }
    
    protected void _addMixOvers(final Constructor<?> mixin, final AnnotatedConstructor target, final boolean addParamAnnotations) {
        this._addOrOverrideAnnotations(target, mixin.getDeclaredAnnotations());
        if (addParamAnnotations) {
            final Annotation[][] pa = mixin.getParameterAnnotations();
            for (int i = 0, len = pa.length; i < len; ++i) {
                for (final Annotation a : pa[i]) {
                    target.addOrOverrideParam(i, a);
                }
            }
        }
    }
    
    protected void _addMixOvers(final Method mixin, final AnnotatedMethod target, final boolean addParamAnnotations) {
        this._addOrOverrideAnnotations(target, mixin.getDeclaredAnnotations());
        if (addParamAnnotations) {
            final Annotation[][] pa = mixin.getParameterAnnotations();
            for (int i = 0, len = pa.length; i < len; ++i) {
                for (final Annotation a : pa[i]) {
                    target.addOrOverrideParam(i, a);
                }
            }
        }
    }
    
    protected void _addMixUnders(final Method src, final AnnotatedMethod target) {
        this._addAnnotationsIfNotPresent(target, src.getDeclaredAnnotations());
    }
    
    private final boolean _isAnnotationBundle(final Annotation ann) {
        return this._annotationIntrospector != null && this._annotationIntrospector.isAnnotationBundle(ann);
    }
    
    @Override
    public String toString() {
        return "[AnnotedClass " + this._class.getName() + "]";
    }
    
    static {
        NO_ANNOTATION_MAPS = new AnnotationMap[0];
    }
}
