// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types;

import org.datanucleus.ClassConstants;
import java.lang.reflect.Method;
import org.datanucleus.plugin.ConfigurationElement;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.StringUtils;
import org.datanucleus.plugin.PluginManager;
import org.datanucleus.util.NucleusLogger;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import org.datanucleus.store.types.converters.TypeConverter;
import java.util.Map;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;
import org.datanucleus.util.Localiser;
import java.io.Serializable;

public class TypeManager implements Serializable
{
    private static final Localiser LOCALISER;
    protected NucleusContext nucCtx;
    protected transient ClassLoaderResolver clr;
    protected Map<String, JavaType> javaTypes;
    protected Map<String, TypeConverter> convertersByName;
    protected Map<String, TypeConverter> autoApplyConvertersByType;
    protected Map<Class, Map<Class, TypeConverter>> typeConverterMap;
    
    public TypeManager(final NucleusContext nucCtx) {
        this.javaTypes = new HashMap<String, JavaType>();
        this.convertersByName = null;
        this.autoApplyConvertersByType = null;
        this.typeConverterMap = null;
        this.nucCtx = nucCtx;
        this.loadJavaTypes(nucCtx.getPluginManager());
        this.loadTypeConverters(nucCtx.getPluginManager());
    }
    
    protected ClassLoaderResolver getClassLoaderResolver() {
        if (this.clr == null) {
            this.clr = this.nucCtx.getClassLoaderResolver(null);
        }
        return this.clr;
    }
    
    public Set<String> getSupportedSecondClassTypes() {
        return new HashSet<String>(this.javaTypes.keySet());
    }
    
    public boolean isSupportedSecondClassType(final String className) {
        if (className == null) {
            return false;
        }
        JavaType type = this.javaTypes.get(className);
        if (type == null) {
            try {
                final Class cls = this.getClassLoaderResolver().classForName(className);
                type = this.findJavaTypeForClass(cls);
                return type != null;
            }
            catch (Exception e) {
                return false;
            }
        }
        return true;
    }
    
    public String[] filterOutSupportedSecondClassNames(final String[] inputClassNames) {
        int filteredClasses = 0;
        for (int i = 0; i < inputClassNames.length; ++i) {
            if (this.isSupportedSecondClassType(inputClassNames[i])) {
                inputClassNames[i] = null;
                ++filteredClasses;
            }
        }
        if (filteredClasses == 0) {
            return inputClassNames;
        }
        final String[] restClasses = new String[inputClassNames.length - filteredClasses];
        int m = 0;
        for (int j = 0; j < inputClassNames.length; ++j) {
            if (inputClassNames[j] != null) {
                restClasses[m++] = inputClassNames[j];
            }
        }
        return restClasses;
    }
    
    public boolean isDefaultPersistent(final Class c) {
        if (c == null) {
            return false;
        }
        JavaType type = this.javaTypes.get(c.getName());
        if (type != null) {
            return true;
        }
        type = this.findJavaTypeForClass(c);
        return type != null;
    }
    
    public boolean isDefaultFetchGroup(final Class c) {
        if (c == null) {
            return false;
        }
        if (this.nucCtx.getApiAdapter().isPersistable(c)) {
            return this.nucCtx.getApiAdapter().getDefaultDFGForPersistableField();
        }
        JavaType type = this.javaTypes.get(c.getName());
        if (type != null) {
            return type.dfg;
        }
        type = this.findJavaTypeForClass(c);
        return type != null && type.dfg;
    }
    
    public boolean isDefaultFetchGroupForCollection(final Class c, final Class genericType) {
        if (c != null && genericType == null) {
            return this.isDefaultFetchGroup(c);
        }
        if (c == null) {
            return false;
        }
        final String name = c.getName() + "<" + genericType.getName() + ">";
        JavaType type = this.javaTypes.get(name);
        if (type != null) {
            return type.dfg;
        }
        type = this.findJavaTypeForCollectionClass(c, genericType);
        return type != null && type.dfg;
    }
    
    public boolean isDefaultEmbeddedType(final Class c) {
        if (c == null) {
            return false;
        }
        JavaType type = this.javaTypes.get(c.getName());
        if (type != null) {
            return type.embedded;
        }
        type = this.findJavaTypeForClass(c);
        return type != null && type.embedded;
    }
    
    public boolean isSecondClassMutableType(final String className) {
        return this.getWrapperTypeForType(className) != null;
    }
    
    public Class getWrapperTypeForType(final String className) {
        if (className == null) {
            return null;
        }
        final JavaType type = this.javaTypes.get(className);
        return (type == null) ? null : type.wrapperType;
    }
    
    public Class getWrappedTypeBackedForType(final String className) {
        if (className == null) {
            return null;
        }
        final JavaType type = this.javaTypes.get(className);
        return (type == null) ? null : type.wrapperTypeBacked;
    }
    
    public boolean isSecondClassWrapper(final String className) {
        if (className == null) {
            return false;
        }
        for (final JavaType type : this.javaTypes.values()) {
            if (type.wrapperType != null && type.wrapperType.getName().equals(className)) {
                return true;
            }
            if (type.wrapperTypeBacked != null && type.wrapperTypeBacked.getName().equals(className)) {
                return true;
            }
        }
        return false;
    }
    
    public Class getTypeForSecondClassWrapper(final String className) {
        for (final JavaType type : this.javaTypes.values()) {
            if (type.wrapperType != null && type.wrapperType.getName().equals(className)) {
                return type.cls;
            }
            if (type.wrapperTypeBacked != null && type.wrapperTypeBacked.getName().equals(className)) {
                return type.cls;
            }
        }
        return null;
    }
    
    public TypeConverter getTypeConverterForName(final String converterName) {
        if (this.convertersByName == null) {
            return null;
        }
        return this.convertersByName.get(converterName);
    }
    
    public void registerConverter(final String name, final TypeConverter converter, final boolean autoApply, final String autoApplyType) {
        if (this.convertersByName == null) {
            this.convertersByName = new HashMap<String, TypeConverter>();
        }
        this.convertersByName.put(name, converter);
        if (autoApply) {
            if (this.autoApplyConvertersByType == null) {
                this.autoApplyConvertersByType = new HashMap<String, TypeConverter>();
            }
            this.autoApplyConvertersByType.put(autoApplyType, converter);
        }
    }
    
    public void registerConverter(final String name, final TypeConverter converter) {
        this.registerConverter(name, converter, false, null);
    }
    
    public TypeConverter getAutoApplyTypeConverterFortype(final Class memberType) {
        if (this.autoApplyConvertersByType == null) {
            return null;
        }
        return this.autoApplyConvertersByType.get(memberType.getName());
    }
    
    public TypeConverter getDefaultTypeConverterForType(final Class memberType) {
        final JavaType javaType = this.javaTypes.get(memberType.getName());
        if (javaType == null) {
            return null;
        }
        final String typeConverterName = javaType.typeConverterName;
        if (typeConverterName == null) {
            return null;
        }
        return this.getTypeConverterForName(typeConverterName);
    }
    
    public TypeConverter getTypeConverterForType(final Class memberType, final Class datastoreType) {
        if (this.typeConverterMap == null) {
            return null;
        }
        final Map<Class, TypeConverter> convertersForMember = this.typeConverterMap.get(memberType);
        if (convertersForMember == null) {
            return null;
        }
        return convertersForMember.get(datastoreType);
    }
    
    protected JavaType findJavaTypeForClass(final Class cls) {
        if (cls == null) {
            return null;
        }
        JavaType type = this.javaTypes.get(cls.getName());
        if (type != null) {
            return type;
        }
        final Collection supportedTypes = new HashSet(this.javaTypes.values());
        final Iterator iter = supportedTypes.iterator();
        while (iter.hasNext()) {
            type = iter.next();
            if (type.cls == cls && type.genericType == null) {
                return type;
            }
            if (type.cls.getName().equals("java.lang.Object") || type.cls.getName().equals("java.io.Serializable")) {
                continue;
            }
            final Class componentCls = cls.isArray() ? cls.getComponentType() : null;
            if (componentCls != null) {
                if (type.cls.isArray() && type.cls.getComponentType().isAssignableFrom(componentCls)) {
                    this.javaTypes.put(cls.getName(), type);
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug(TypeManager.LOCALISER.msg("016001", cls.getName(), type.cls.getName()));
                    }
                    return type;
                }
                continue;
            }
            else {
                if (type.cls.isAssignableFrom(cls) && type.genericType == null) {
                    this.javaTypes.put(cls.getName(), type);
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug(TypeManager.LOCALISER.msg("016001", cls.getName(), type.cls.getName()));
                    }
                    return type;
                }
                continue;
            }
        }
        return null;
    }
    
    protected JavaType findJavaTypeForCollectionClass(final Class cls, final Class genericType) {
        if (cls == null) {
            return null;
        }
        if (genericType == null) {
            return this.findJavaTypeForClass(cls);
        }
        final String typeName = cls.getName() + "<" + genericType.getName() + ">";
        JavaType type = this.javaTypes.get(typeName);
        if (type != null) {
            return type;
        }
        final Collection supportedTypes = new HashSet(this.javaTypes.values());
        final Iterator iter = supportedTypes.iterator();
        while (iter.hasNext()) {
            type = iter.next();
            if (type.cls.isAssignableFrom(cls) && type.genericType != null && type.genericType.isAssignableFrom(genericType)) {
                this.javaTypes.put(typeName, type);
                return type;
            }
        }
        return this.findJavaTypeForClass(cls);
    }
    
    private void loadJavaTypes(final PluginManager mgr) {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(TypeManager.LOCALISER.msg("016003"));
        }
        final ClassLoaderResolver clr = this.getClassLoaderResolver();
        final ConfigurationElement[] elems = mgr.getConfigurationElementsForExtension("org.datanucleus.java_type", null, (String)null);
        if (elems != null) {
            for (int i = 0; i < elems.length; ++i) {
                final String javaName = elems[i].getAttribute("name").trim();
                final String genericTypeName = elems[i].getAttribute("generic-type");
                final String embeddedString = elems[i].getAttribute("embedded");
                final String dfgString = elems[i].getAttribute("dfg");
                String wrapperType = elems[i].getAttribute("wrapper-type");
                String wrapperTypeBacked = elems[i].getAttribute("wrapper-type-backed");
                final String typeConverterName = elems[i].getAttribute("converter-name");
                boolean embedded = false;
                if (embeddedString != null && embeddedString.equalsIgnoreCase("true")) {
                    embedded = true;
                }
                boolean dfg = false;
                if (dfgString != null && dfgString.equalsIgnoreCase("true")) {
                    dfg = true;
                }
                if (!StringUtils.isWhitespace(wrapperType)) {
                    wrapperType = wrapperType.trim();
                }
                else {
                    wrapperType = null;
                }
                if (!StringUtils.isWhitespace(wrapperTypeBacked)) {
                    wrapperTypeBacked = wrapperTypeBacked.trim();
                }
                else {
                    wrapperTypeBacked = null;
                }
                try {
                    final Class cls = clr.classForName(javaName);
                    Class genericType = null;
                    String javaTypeName = cls.getName();
                    if (!StringUtils.isWhitespace(genericTypeName)) {
                        genericType = clr.classForName(genericTypeName);
                        javaTypeName = javaTypeName + "<" + genericTypeName + ">";
                    }
                    if (!this.javaTypes.containsKey(javaTypeName)) {
                        Class wrapperClass = null;
                        if (wrapperType != null) {
                            try {
                                wrapperClass = mgr.loadClass(elems[i].getExtension().getPlugin().getSymbolicName(), wrapperType);
                            }
                            catch (NucleusException jpe) {
                                NucleusLogger.PERSISTENCE.error(TypeManager.LOCALISER.msg("016004", wrapperType));
                                throw new NucleusException(TypeManager.LOCALISER.msg("016004", wrapperType));
                            }
                        }
                        Class wrapperClassBacked = null;
                        if (wrapperTypeBacked != null) {
                            try {
                                wrapperClassBacked = mgr.loadClass(elems[i].getExtension().getPlugin().getSymbolicName(), wrapperTypeBacked);
                            }
                            catch (NucleusException jpe2) {
                                NucleusLogger.PERSISTENCE.error(TypeManager.LOCALISER.msg("016004", wrapperTypeBacked));
                                throw new NucleusException(TypeManager.LOCALISER.msg("016004", wrapperTypeBacked));
                            }
                        }
                        final JavaType type = new JavaType(cls, genericType, embedded, dfg, wrapperClass, wrapperClassBacked, typeConverterName);
                        this.addJavaType(type);
                    }
                }
                catch (Exception e) {
                    NucleusLogger.PERSISTENCE.debug("Error in loading java type support for " + javaName + " : " + e.getMessage());
                }
            }
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(TypeManager.LOCALISER.msg("016006", StringUtils.collectionToString(this.javaTypes.keySet())));
        }
    }
    
    protected void addJavaType(final JavaType type) {
        String typeName = type.cls.getName();
        if (type.genericType != null) {
            typeName = typeName + "<" + type.genericType.getName() + ">";
        }
        this.javaTypes.put(typeName, type);
    }
    
    private void loadTypeConverters(final PluginManager mgr) {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(TypeManager.LOCALISER.msg("016007"));
        }
        final ClassLoaderResolver clr = this.getClassLoaderResolver();
        final ConfigurationElement[] elems = mgr.getConfigurationElementsForExtension("org.datanucleus.type_converter", null, (String)null);
        if (elems != null) {
            for (int i = 0; i < elems.length; ++i) {
                final String name = elems[i].getAttribute("name").trim();
                final String memberTypeName = elems[i].getAttribute("member-type").trim();
                final String datastoreTypeName = elems[i].getAttribute("datastore-type").trim();
                final String converterClsName = elems[i].getAttribute("converter-class").trim();
                Class memberType = null;
                try {
                    final TypeConverter conv = (TypeConverter)mgr.createExecutableExtension("org.datanucleus.type_converter", "name", name, "converter-class", null, null);
                    this.registerConverter(name, conv);
                    if (this.typeConverterMap == null) {
                        this.typeConverterMap = new HashMap<Class, Map<Class, TypeConverter>>();
                    }
                    memberType = clr.classForName(memberTypeName);
                    final Class datastoreType = clr.classForName(datastoreTypeName);
                    Map<Class, TypeConverter> convertersForMember = this.typeConverterMap.get(memberType);
                    if (convertersForMember == null) {
                        convertersForMember = new HashMap<Class, TypeConverter>();
                        this.typeConverterMap.put(memberType, convertersForMember);
                    }
                    convertersForMember.put(datastoreType, conv);
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug("Added converter for " + memberTypeName + "<->" + datastoreTypeName + " using " + converterClsName);
                    }
                }
                catch (Exception e) {
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        if (memberType != null) {
                            NucleusLogger.PERSISTENCE.debug("TypeConverter for " + memberTypeName + "<->" + datastoreTypeName + " using " + converterClsName + " not instantiable (missing dependencies?) so ignoring");
                        }
                        else {
                            NucleusLogger.PERSISTENCE.debug("TypeConverter for " + memberTypeName + "<->" + datastoreTypeName + " ignored since java type not present in CLASSPATH");
                        }
                    }
                }
            }
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(TypeManager.LOCALISER.msg("016008"));
        }
    }
    
    public static Class getMemberTypeForTypeConverter(final TypeConverter conv, final Class datastoreType) {
        try {
            final Method m = conv.getClass().getDeclaredMethod("toMemberType", datastoreType);
            return m.getReturnType();
        }
        catch (Exception e) {
            try {
                final Method i = conv.getClass().getDeclaredMethod("getMemberClass", (Class<?>[])null);
                return (Class)i.invoke(conv, (Object[])null);
            }
            catch (Exception e2) {
                NucleusLogger.GENERAL.warn(">> Converter " + conv + " didn't have adequate information from toMemberType nor from getDatastoreClass");
                return null;
            }
        }
    }
    
    public static Class getDatastoreTypeForTypeConverter(final TypeConverter conv, final Class memberType) {
        try {
            final Method m = conv.getClass().getDeclaredMethod("toDatastoreType", memberType);
            return m.getReturnType();
        }
        catch (Exception e) {
            try {
                final Method i = conv.getClass().getDeclaredMethod("getDatastoreClass", (Class<?>[])null);
                return (Class)i.invoke(conv, (Object[])null);
            }
            catch (Exception e2) {
                try {
                    final Method[] methods = conv.getClass().getDeclaredMethods();
                    if (methods != null) {
                        for (int j = 0; j < methods.length; ++j) {
                            if (methods[j].getName().equals("toDatastoreType")) {
                                return methods[j].getReturnType();
                            }
                        }
                    }
                }
                catch (Exception e3) {
                    NucleusLogger.GENERAL.warn(">> Converter " + conv + " didn't have adequate information from toDatastoreType nor from getDatastoreClass");
                }
                return null;
            }
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    static class JavaType implements Serializable
    {
        final Class cls;
        final Class genericType;
        final boolean embedded;
        final boolean dfg;
        final Class wrapperType;
        final Class wrapperTypeBacked;
        final String typeConverterName;
        
        public JavaType(final Class cls, final Class genericType, final boolean embedded, final boolean dfg, final Class wrapperType, final Class wrapperTypeBacked, final String typeConverterName) {
            this.cls = cls;
            this.genericType = genericType;
            this.embedded = embedded;
            this.dfg = dfg;
            this.wrapperType = wrapperType;
            this.wrapperTypeBacked = ((wrapperTypeBacked != null) ? wrapperTypeBacked : wrapperType);
            this.typeConverterName = typeConverterName;
        }
    }
}
