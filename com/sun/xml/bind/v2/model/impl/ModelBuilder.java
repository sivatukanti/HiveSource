// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.model.impl;

import com.sun.xml.bind.WhiteSpaceProcessor;
import com.sun.xml.bind.util.Which;
import javax.xml.bind.annotation.XmlSchema;
import com.sun.xml.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.bind.v2.model.core.RegistryInfo;
import javax.xml.bind.annotation.XmlRegistry;
import com.sun.xml.bind.v2.model.core.Ref;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlSeeAlso;
import com.sun.xml.bind.v2.model.core.PropertyKind;
import com.sun.xml.bind.v2.model.core.PropertyInfo;
import com.sun.xml.bind.v2.model.annotation.ClassLocatable;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlTransient;
import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.runtime.IllegalAnnotationException;
import java.util.HashMap;
import com.sun.xml.bind.v2.model.core.ErrorHandler;
import com.sun.xml.bind.v2.model.core.TypeInfo;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.annotation.AnnotationReader;

public class ModelBuilder<T, C, F, M>
{
    final TypeInfoSetImpl<T, C, F, M> typeInfoSet;
    public final AnnotationReader<T, C, F, M> reader;
    public final Navigator<T, C, F, M> nav;
    private final Map<QName, TypeInfo> typeNames;
    public final String defaultNsUri;
    final Map<String, RegistryInfoImpl<T, C, F, M>> registries;
    private final Map<C, C> subclassReplacements;
    private ErrorHandler errorHandler;
    private boolean hadError;
    public boolean hasSwaRef;
    private final ErrorHandler proxyErrorHandler;
    private boolean linked;
    
    public ModelBuilder(final AnnotationReader<T, C, F, M> reader, final Navigator<T, C, F, M> navigator, final Map<C, C> subclassReplacements, String defaultNamespaceRemap) {
        this.typeNames = new HashMap<QName, TypeInfo>();
        this.registries = new HashMap<String, RegistryInfoImpl<T, C, F, M>>();
        this.proxyErrorHandler = new ErrorHandler() {
            public void error(final IllegalAnnotationException e) {
                ModelBuilder.this.reportError(e);
            }
        };
        this.reader = reader;
        this.nav = navigator;
        this.subclassReplacements = subclassReplacements;
        if (defaultNamespaceRemap == null) {
            defaultNamespaceRemap = "";
        }
        this.defaultNsUri = defaultNamespaceRemap;
        reader.setErrorHandler(this.proxyErrorHandler);
        this.typeInfoSet = this.createTypeInfoSet();
    }
    
    protected TypeInfoSetImpl<T, C, F, M> createTypeInfoSet() {
        return new TypeInfoSetImpl<T, C, F, M>(this.nav, this.reader, BuiltinLeafInfoImpl.createLeaves(this.nav));
    }
    
    public NonElement<T, C> getClassInfo(final C clazz, final Locatable upstream) {
        return this.getClassInfo(clazz, false, upstream);
    }
    
    public NonElement<T, C> getClassInfo(final C clazz, final boolean searchForSuperClass, final Locatable upstream) {
        assert clazz != null;
        NonElement<T, C> r = this.typeInfoSet.getClassInfo(clazz);
        if (r != null) {
            return r;
        }
        if (this.nav.isEnum(clazz)) {
            final EnumLeafInfoImpl<T, C, F, M> li = this.createEnumLeafInfo(clazz, upstream);
            this.typeInfoSet.add(li);
            r = li;
            this.addTypeName(r);
        }
        else {
            final boolean isReplaced = this.subclassReplacements.containsKey(clazz);
            if (isReplaced && !searchForSuperClass) {
                r = this.getClassInfo(this.subclassReplacements.get(clazz), upstream);
            }
            else if (this.reader.hasClassAnnotation(clazz, XmlTransient.class) || isReplaced) {
                r = this.getClassInfo(this.nav.getSuperClass(clazz), searchForSuperClass, new ClassLocatable<Object>(upstream, clazz, this.nav));
            }
            else {
                final ClassInfoImpl<T, C, F, M> ci = this.createClassInfo(clazz, upstream);
                this.typeInfoSet.add(ci);
                for (final PropertyInfo<T, C> p : ci.getProperties()) {
                    if (p.kind() == PropertyKind.REFERENCE) {
                        final String pkg = this.nav.getPackageName(ci.getClazz());
                        if (!this.registries.containsKey(pkg)) {
                            final C c = this.nav.findClass(pkg + ".ObjectFactory", ci.getClazz());
                            if (c != null) {
                                this.addRegistry(c, (Locatable)p);
                            }
                        }
                    }
                    for (TypeInfo<T, C> t : p.ref()) {}
                }
                ci.getBaseClass();
                r = ci;
                this.addTypeName(r);
            }
        }
        final XmlSeeAlso sa = this.reader.getClassAnnotation(XmlSeeAlso.class, clazz, upstream);
        if (sa != null) {
            for (final T t2 : this.reader.getClassArrayValue(sa, "value")) {
                this.getTypeInfo(t2, (Locatable)sa);
            }
        }
        return r;
    }
    
    private void addTypeName(final NonElement<T, C> r) {
        final QName t = r.getTypeName();
        if (t == null) {
            return;
        }
        final TypeInfo old = this.typeNames.put(t, r);
        if (old != null) {
            this.reportError(new IllegalAnnotationException(Messages.CONFLICTING_XML_TYPE_MAPPING.format(r.getTypeName()), old, r));
        }
    }
    
    public NonElement<T, C> getTypeInfo(final T t, final Locatable upstream) {
        final NonElement<T, C> r = this.typeInfoSet.getTypeInfo(t);
        if (r != null) {
            return r;
        }
        if (this.nav.isArray(t)) {
            final ArrayInfoImpl<T, C, F, M> ai = this.createArrayInfo(upstream, t);
            this.addTypeName(ai);
            this.typeInfoSet.add(ai);
            return ai;
        }
        final C c = this.nav.asDecl(t);
        assert c != null : t.toString() + " must be a leaf, but we failed to recognize it.";
        return this.getClassInfo(c, upstream);
    }
    
    public NonElement<T, C> getTypeInfo(final Ref<T, C> ref) {
        assert !ref.valueList;
        final C c = this.nav.asDecl(ref.type);
        if (c != null && this.reader.getClassAnnotation(XmlRegistry.class, c, null) != null) {
            if (!this.registries.containsKey(this.nav.getPackageName(c))) {
                this.addRegistry(c, null);
            }
            return null;
        }
        return this.getTypeInfo(ref.type, null);
    }
    
    protected EnumLeafInfoImpl<T, C, F, M> createEnumLeafInfo(final C clazz, final Locatable upstream) {
        return new EnumLeafInfoImpl<T, C, F, M>(this, upstream, clazz, this.nav.use(clazz));
    }
    
    protected ClassInfoImpl<T, C, F, M> createClassInfo(final C clazz, final Locatable upstream) {
        return new ClassInfoImpl<T, C, F, M>(this, upstream, clazz);
    }
    
    protected ElementInfoImpl<T, C, F, M> createElementInfo(final RegistryInfoImpl<T, C, F, M> registryInfo, final M m) throws IllegalAnnotationException {
        return new ElementInfoImpl<T, C, F, M>(this, registryInfo, m);
    }
    
    protected ArrayInfoImpl<T, C, F, M> createArrayInfo(final Locatable upstream, final T arrayType) {
        return new ArrayInfoImpl<T, C, F, M>(this, upstream, arrayType);
    }
    
    public RegistryInfo<T, C> addRegistry(final C registryClass, final Locatable upstream) {
        return new RegistryInfoImpl<T, C, Object, Object>(this, upstream, registryClass);
    }
    
    public RegistryInfo<T, C> getRegistry(final String packageName) {
        return this.registries.get(packageName);
    }
    
    public TypeInfoSet<T, C, F, M> link() {
        assert !this.linked;
        this.linked = true;
        for (final ElementInfoImpl ei : this.typeInfoSet.getAllElements()) {
            ei.link();
        }
        for (final ClassInfoImpl ci : this.typeInfoSet.beans().values()) {
            ci.link();
        }
        for (final EnumLeafInfoImpl li : this.typeInfoSet.enums().values()) {
            li.link();
        }
        if (this.hadError) {
            return null;
        }
        return this.typeInfoSet;
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    public final void reportError(final IllegalAnnotationException e) {
        this.hadError = true;
        if (this.errorHandler != null) {
            this.errorHandler.error(e);
        }
    }
    
    public boolean isReplaced(final C sc) {
        return this.subclassReplacements.containsKey(sc);
    }
    
    static {
        try {
            final XmlSchema s = null;
            s.location();
        }
        catch (NullPointerException e) {}
        catch (NoSuchMethodError e2) {
            Messages res;
            if (XmlSchema.class.getClassLoader() == null) {
                res = Messages.INCOMPATIBLE_API_VERSION_MUSTANG;
            }
            else {
                res = Messages.INCOMPATIBLE_API_VERSION;
            }
            throw new LinkageError(res.format(Which.which(XmlSchema.class), Which.which(ModelBuilder.class)));
        }
        try {
            WhiteSpaceProcessor.isWhiteSpace("xyz");
        }
        catch (NoSuchMethodError e2) {
            throw new LinkageError(Messages.RUNNING_WITH_1_0_RUNTIME.format(Which.which(WhiteSpaceProcessor.class), Which.which(ModelBuilder.class)));
        }
    }
}
