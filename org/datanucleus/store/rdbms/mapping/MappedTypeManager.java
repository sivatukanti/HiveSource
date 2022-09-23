// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping;

import org.datanucleus.ClassConstants;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.plugin.ConfigurationElement;
import org.datanucleus.util.StringUtils;
import org.datanucleus.plugin.PluginManager;
import org.datanucleus.store.types.TypeManager;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;
import org.datanucleus.util.Localiser;

public class MappedTypeManager
{
    private static final Localiser LOCALISER;
    protected final NucleusContext nucleusCtx;
    protected final ClassLoaderResolver clr;
    Map<String, MappedType> mappedTypes;
    
    public MappedTypeManager(final NucleusContext nucleusCtx) {
        this.mappedTypes = new HashMap<String, MappedType>();
        this.nucleusCtx = nucleusCtx;
        this.clr = nucleusCtx.getClassLoaderResolver(null);
        this.loadMappings(nucleusCtx.getPluginManager(), this.clr);
    }
    
    public boolean isSupportedMappedType(final String className) {
        if (className == null) {
            return false;
        }
        MappedType type = this.getMappedType(className);
        if (type == null) {
            try {
                final Class cls = this.clr.classForName(className);
                type = this.findMappedTypeForClass(cls);
                return type != null && type.javaMappingType != null;
            }
            catch (Exception e) {
                return false;
            }
        }
        return type.javaMappingType != null;
    }
    
    public Class getMappingType(final String className) {
        if (className == null) {
            return null;
        }
        MappedType type = this.getMappedType(className);
        if (type == null) {
            final TypeManager typeMgr = this.nucleusCtx.getTypeManager();
            Class cls = typeMgr.getTypeForSecondClassWrapper(className);
            if (cls != null) {
                type = this.getMappedType(cls.getName());
                if (type != null) {
                    return type.javaMappingType;
                }
            }
            try {
                cls = this.clr.classForName(className);
                type = this.findMappedTypeForClass(cls);
                return type.javaMappingType;
            }
            catch (Exception e) {
                return null;
            }
        }
        return type.javaMappingType;
    }
    
    private void loadMappings(final PluginManager mgr, final ClassLoaderResolver clr) {
        final ConfigurationElement[] elems = mgr.getConfigurationElementsForExtension("org.datanucleus.store_mapping", null, (String)null);
        if (elems != null) {
            for (int i = 0; i < elems.length; ++i) {
                final String javaName = elems[i].getAttribute("java-type").trim();
                final String mappingClassName = elems[i].getAttribute("mapping-class");
                final String javaVersionRestrict = elems[i].getAttribute("java-version-restricted");
                boolean javaRestricted = false;
                if (javaVersionRestrict != null && javaVersionRestrict.equalsIgnoreCase("true")) {
                    javaRestricted = Boolean.TRUE;
                }
                String javaVersion = elems[i].getAttribute("java-version");
                if (StringUtils.isWhitespace(javaVersion)) {
                    javaVersion = "1.3";
                }
                if (!this.mappedTypes.containsKey(javaName)) {
                    this.addMappedType(mgr, elems[i].getExtension().getPlugin().getSymbolicName(), javaName, mappingClassName, javaVersion, javaRestricted, clr);
                }
            }
        }
    }
    
    private void addMappedType(final PluginManager mgr, final String pluginId, final String className, final String mappingClassName, final String javaVersion, final boolean javaRestricted, final ClassLoaderResolver clr) {
        if (className == null) {
            return;
        }
        if ((JavaUtils.isGreaterEqualsThan(javaVersion) && !javaRestricted) || (JavaUtils.isEqualsThan(javaVersion) && javaRestricted)) {
            Class mappingType = null;
            if (!StringUtils.isWhitespace(mappingClassName)) {
                try {
                    mappingType = mgr.loadClass(pluginId, mappingClassName);
                }
                catch (NucleusException jpe) {
                    NucleusLogger.PERSISTENCE.error(MappedTypeManager.LOCALISER.msg("016004", mappingClassName));
                    return;
                }
            }
            Class cls = null;
            try {
                cls = clr.classForName(className);
            }
            catch (Exception ex) {}
            if (cls != null) {
                final MappedType type = new MappedType(cls, mappingType);
                this.mappedTypes.put(className, type);
            }
        }
    }
    
    protected MappedType findMappedTypeForClass(final Class cls) {
        MappedType type = this.getMappedType(cls.getName());
        if (type != null) {
            return type;
        }
        final Class componentCls = cls.isArray() ? cls.getComponentType() : null;
        final Collection supportedTypes = new HashSet(this.mappedTypes.values());
        final Iterator<MappedType> iter = supportedTypes.iterator();
        while (iter.hasNext()) {
            type = iter.next();
            if (type.cls == cls) {
                return type;
            }
            if (type.cls.getName().equals("java.lang.Object") || type.cls.getName().equals("java.io.Serializable")) {
                continue;
            }
            if (componentCls != null) {
                if (type.cls.isArray() && type.cls.getComponentType().isAssignableFrom(componentCls)) {
                    this.mappedTypes.put(cls.getName(), type);
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug(MappedTypeManager.LOCALISER.msg("016001", cls.getName(), type.cls.getName()));
                    }
                    return type;
                }
                continue;
            }
            else {
                if (type.cls.isAssignableFrom(cls)) {
                    this.mappedTypes.put(cls.getName(), type);
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug(MappedTypeManager.LOCALISER.msg("016001", cls.getName(), type.cls.getName()));
                    }
                    return type;
                }
                continue;
            }
        }
        return null;
    }
    
    protected MappedType getMappedType(final String className) {
        if (className == null) {
            return null;
        }
        return this.mappedTypes.get(className);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    static class MappedType
    {
        final Class cls;
        final Class javaMappingType;
        
        public MappedType(final Class cls, final Class mappingType) {
            this.cls = cls;
            this.javaMappingType = mappingType;
        }
        
        @Override
        public String toString() {
            final StringBuffer str = new StringBuffer("MappedType " + this.cls.getName() + " [");
            if (this.javaMappingType != null) {
                str.append(" mapping=" + this.javaMappingType);
            }
            str.append("]");
            return str.toString();
        }
    }
}
