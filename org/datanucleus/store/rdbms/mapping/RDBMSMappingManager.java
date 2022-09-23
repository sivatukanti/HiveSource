// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping;

import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.metadata.ColumnMetaDataContainer;
import org.datanucleus.metadata.NullValue;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMappingFactory;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import java.util.Iterator;
import java.util.Collection;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedValuePCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedValuePCMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedKeyPCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedKeyPCMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedElementPCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedElementPCMapping;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.rdbms.mapping.java.ObjectMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedMapping;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.store.rdbms.mapping.java.ArrayMapping;
import org.datanucleus.store.rdbms.mapping.java.InterfaceMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedPCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedPCMapping;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.types.converters.TypeConverter;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.metadata.EmbeddedMetaData;
import org.datanucleus.store.rdbms.mapping.java.SerialisedLocalFileMapping;
import java.io.Serializable;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.mapping.java.TypeConverterMapping;
import org.datanucleus.store.rdbms.mapping.java.TypeConverterLongMapping;
import org.datanucleus.store.rdbms.mapping.java.TypeConverterStringMapping;
import org.datanucleus.store.types.TypeManager;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.exceptions.NoTableManagedException;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import java.util.Set;
import org.datanucleus.plugin.ConfigurationElement;
import java.util.HashSet;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.plugin.PluginManager;
import org.datanucleus.util.MultiMap;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;

public class RDBMSMappingManager implements MappingManager
{
    protected static final Localiser LOCALISER_RDBMS;
    protected final RDBMSStoreManager storeMgr;
    protected final ClassLoaderResolver clr;
    protected MultiMap datastoreMappingsByJavaType;
    protected MultiMap datastoreMappingsByJDBCType;
    protected MultiMap datastoreMappingsBySQLType;
    
    public RDBMSMappingManager(final RDBMSStoreManager storeMgr) {
        this.storeMgr = storeMgr;
        this.clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
    }
    
    @Override
    public void loadDatastoreMapping(final PluginManager mgr, final ClassLoaderResolver clr, final String vendorId) {
        if (this.datastoreMappingsByJavaType != null) {
            return;
        }
        this.datastoreMappingsByJDBCType = new MultiMap();
        this.datastoreMappingsBySQLType = new MultiMap();
        this.datastoreMappingsByJavaType = new MultiMap();
        final ConfigurationElement[] elems = mgr.getConfigurationElementsForExtension("org.datanucleus.store.rdbms.rdbms_mapping", null, (String)null);
        if (elems != null) {
            for (int i = 0; i < elems.length; ++i) {
                final String javaName = elems[i].getAttribute("java-type").trim();
                final String rdbmsMappingClassName = elems[i].getAttribute("rdbms-mapping-class");
                final String jdbcType = elems[i].getAttribute("jdbc-type");
                final String sqlType = elems[i].getAttribute("sql-type");
                final String defaultJava = elems[i].getAttribute("default");
                String javaVersion = elems[i].getAttribute("java-version");
                final String javaVersionRestricted = elems[i].getAttribute("java-version-restricted");
                boolean defaultForJavaType = false;
                if (defaultJava != null && defaultJava.equalsIgnoreCase("true")) {
                    defaultForJavaType = Boolean.TRUE;
                }
                boolean javaRestricted = false;
                if (javaVersionRestricted != null && javaVersionRestricted.equalsIgnoreCase("true")) {
                    javaRestricted = Boolean.TRUE;
                }
                if (javaVersion == null || javaVersion.length() < 1) {
                    javaVersion = "1.3";
                }
                if ((JavaUtils.isGreaterEqualsThan(javaVersion) && !javaRestricted) || (JavaUtils.isEqualsThan(javaVersion) && javaRestricted)) {
                    Class mappingType = null;
                    if (!StringUtils.isWhitespace(rdbmsMappingClassName)) {
                        try {
                            mappingType = mgr.loadClass(elems[i].getExtension().getPlugin().getSymbolicName(), rdbmsMappingClassName);
                        }
                        catch (NucleusException ne) {
                            NucleusLogger.DATASTORE.error(RDBMSMappingManager.LOCALISER_RDBMS.msg("041013", rdbmsMappingClassName));
                        }
                        final Set includes = new HashSet();
                        final Set excludes = new HashSet();
                        final ConfigurationElement[] childElm = elems[i].getChildren();
                        for (int j = 0; j < childElm.length; ++j) {
                            if (childElm[j].getName().equals("includes")) {
                                includes.add(childElm[j].getAttribute("vendor-id"));
                            }
                            else if (childElm[j].getName().equals("excludes")) {
                                excludes.add(childElm[j].getAttribute("vendor-id"));
                            }
                        }
                        if (!excludes.contains(vendorId) && (includes.isEmpty() || includes.contains(vendorId))) {
                            this.registerDatastoreMapping(javaName, mappingType, jdbcType, sqlType, defaultForJavaType);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public JavaTypeMapping getMappingWithDatastoreMapping(final Class c, final boolean serialised, final boolean embedded, final ClassLoaderResolver clr) {
        try {
            final DatastoreClass datastoreClass = this.storeMgr.getDatastoreClass(c.getName(), clr);
            return datastoreClass.getIdMapping();
        }
        catch (NoTableManagedException ex) {
            Class mc = this.getMappingClass(c, serialised, embedded, null);
            mc = this.getOverrideMappingClass(mc, null, -1);
            JavaTypeMapping m = null;
            try {
                m = mc.newInstance();
            }
            catch (Exception e) {
                throw new NucleusException(RDBMSMappingManager.LOCALISER_RDBMS.msg("041009", mc.getName(), e), e).setFatal();
            }
            m.initialize(this.storeMgr, c.getName());
            if (m.hasSimpleDatastoreRepresentation()) {
                this.createDatastoreMapping(m, null, m.getJavaTypeForDatastoreMapping(0));
            }
            return m;
        }
    }
    
    @Override
    public JavaTypeMapping getMapping(final Class c) {
        return this.getMapping(c, false, false, null);
    }
    
    @Override
    public JavaTypeMapping getMapping(final Class c, final boolean serialised, final boolean embedded, final String fieldName) {
        Class mc = this.getMappingClass(c, serialised, embedded, fieldName);
        mc = this.getOverrideMappingClass(mc, null, -1);
        JavaTypeMapping m = null;
        try {
            m = mc.newInstance();
        }
        catch (Exception e) {
            throw new NucleusException(RDBMSMappingManager.LOCALISER_RDBMS.msg("041009", mc.getName(), e), e).setFatal();
        }
        m.initialize(this.storeMgr, c.getName());
        return m;
    }
    
    @Override
    public JavaTypeMapping getMapping(final Table table, final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final int fieldRole) {
        Class mc = null;
        AbstractMemberMetaData overrideMmd = null;
        final TypeManager typeMgr = table.getStoreManager().getNucleusContext().getTypeManager();
        TypeConverter conv = null;
        if (!mmd.isTypeConversionDisabled()) {
            final TypeConverter autoApplyConv = typeMgr.getAutoApplyTypeConverterFortype(mmd.getType());
            if (mmd.getTypeConverterName() != null) {
                conv = typeMgr.getTypeConverterForName(mmd.getTypeConverterName());
            }
            else if (autoApplyConv != null) {
                conv = autoApplyConv;
            }
        }
        Label_0507: {
            if (conv != null) {
                if (TypeManager.getDatastoreTypeForTypeConverter(conv, mmd.getType()) == String.class) {
                    mc = TypeConverterStringMapping.class;
                }
                else if (TypeManager.getDatastoreTypeForTypeConverter(conv, mmd.getType()) == Long.class) {
                    mc = TypeConverterLongMapping.class;
                }
                else {
                    mc = TypeConverterMapping.class;
                }
            }
            else if (fieldRole == 3 || fieldRole == 4) {
                mc = this.getElementMappingClass(table, mmd, clr);
            }
            else if (fieldRole == 5) {
                mc = this.getKeyMappingClass(table, mmd, clr);
            }
            else if (fieldRole == 6) {
                mc = this.getValueMappingClass(table, mmd, clr);
            }
            else {
                final String userMappingClassName = mmd.getValueForExtension("mapping-class");
                if (userMappingClassName != null) {
                    try {
                        mc = clr.classForName(userMappingClassName);
                        break Label_0507;
                    }
                    catch (NucleusException ne) {
                        throw new NucleusUserException(RDBMSMappingManager.LOCALISER_RDBMS.msg("041014", mmd.getFullFieldName(), userMappingClassName)).setFatal();
                    }
                }
                AbstractClassMetaData acmd = null;
                if (mmd.getType().isInterface()) {
                    acmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForInterface(mmd.getType(), clr);
                }
                else {
                    acmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(mmd.getType(), clr);
                }
                if (mmd.hasExtension("serializeToFileLocation") && Serializable.class.isAssignableFrom(mmd.getType())) {
                    mc = SerialisedLocalFileMapping.class;
                }
                else if (mmd.isSerialized()) {
                    mc = this.getMappingClass(mmd.getType(), true, false, mmd.getFullFieldName());
                }
                else if (mmd.getEmbeddedMetaData() != null) {
                    mc = this.getMappingClass(mmd.getType(), false, true, mmd.getFullFieldName());
                }
                else if (acmd != null && acmd.isEmbeddedOnly()) {
                    mc = this.getMappingClass(mmd.getType(), false, true, mmd.getFullFieldName());
                }
                else if (mmd.isEmbedded()) {
                    mc = this.getMappingClass(mmd.getType(), true, false, mmd.getFullFieldName());
                }
                else {
                    mc = this.getMappingClass(mmd.getType(), false, false, mmd.getFullFieldName());
                    if (mmd.getParent() instanceof EmbeddedMetaData && mmd.getRelationType(clr) != RelationType.NONE) {
                        final AbstractClassMetaData cmdForFmd = table.getStoreManager().getMetaDataManager().getMetaDataForClass(mmd.getClassName(), clr);
                        overrideMmd = cmdForFmd.getMetaDataForMember(mmd.getName());
                    }
                }
            }
        }
        mc = this.getOverrideMappingClass(mc, mmd, fieldRole);
        JavaTypeMapping m = null;
        try {
            m = mc.newInstance();
            if (fieldRole >= 0) {
                m.setRoleForMember(fieldRole);
            }
        }
        catch (Exception e) {
            throw new NucleusException(RDBMSMappingManager.LOCALISER_RDBMS.msg("041009", mc.getName(), e), e).setFatal();
        }
        if (conv == null) {
            m.initialize(mmd, table, clr);
            if (overrideMmd != null) {
                m.setMemberMetaData(overrideMmd);
            }
        }
        else {
            ((TypeConverterMapping)m).initialize(mmd, table, clr, conv);
        }
        return m;
    }
    
    protected Class getOverrideMappingClass(final Class mappingClass, final AbstractMemberMetaData mmd, final int fieldRole) {
        return mappingClass;
    }
    
    protected Class getMappingClass(final Class c, final boolean serialised, final boolean embedded, final String fieldName) {
        final ApiAdapter api = this.storeMgr.getApiAdapter();
        if (api.isPersistable(c)) {
            if (serialised) {
                return SerialisedPCMapping.class;
            }
            if (embedded) {
                return EmbeddedPCMapping.class;
            }
            return PersistableMapping.class;
        }
        else if (c.isInterface() && !this.storeMgr.getMappedTypeManager().isSupportedMappedType(c.getName())) {
            if (serialised) {
                return SerialisedReferenceMapping.class;
            }
            if (embedded) {
                return EmbeddedPCMapping.class;
            }
            return InterfaceMapping.class;
        }
        else {
            if (c != Object.class) {
                if (c.isArray()) {
                    if (api.isPersistable(c.getComponentType())) {
                        return ArrayMapping.class;
                    }
                    if (c.getComponentType().isInterface() && !this.storeMgr.getMappedTypeManager().isSupportedMappedType(c.getComponentType().getName())) {
                        return ArrayMapping.class;
                    }
                    if (c.getComponentType() == Object.class) {
                        return ArrayMapping.class;
                    }
                }
                Class mappingClass = this.getDefaultJavaTypeMapping(c);
                if (mappingClass == null) {
                    for (Class superClass = c.getSuperclass(); superClass != null && !superClass.getName().equals(ClassNameConstants.Object) && mappingClass == null; mappingClass = this.getDefaultJavaTypeMapping(superClass), superClass = superClass.getSuperclass()) {}
                }
                if (mappingClass == null) {
                    if (this.storeMgr.getMappedTypeManager().isSupportedMappedType(c.getName())) {
                        throw new NucleusUserException(RDBMSMappingManager.LOCALISER_RDBMS.msg("041001", fieldName, c.getName()));
                    }
                    for (Class superClass = c; superClass != null && !superClass.getName().equals(ClassNameConstants.Object) && mappingClass == null; superClass = superClass.getSuperclass()) {
                        final Class[] interfaces = superClass.getInterfaces();
                        for (int i = 0; i < interfaces.length && mappingClass == null; mappingClass = this.getDefaultJavaTypeMapping(interfaces[i]), ++i) {}
                    }
                    if (mappingClass == null) {
                        mappingClass = SerialisedMapping.class;
                    }
                }
                return mappingClass;
            }
            if (serialised) {
                return SerialisedReferenceMapping.class;
            }
            if (embedded) {
                throw new NucleusUserException(RDBMSMappingManager.LOCALISER_RDBMS.msg("041042", fieldName)).setFatal();
            }
            return ObjectMapping.class;
        }
    }
    
    protected Class getElementMappingClass(final Table table, final AbstractMemberMetaData mmd, final ClassLoaderResolver clr) {
        if (!mmd.hasCollection() && !mmd.hasArray()) {
            throw new NucleusException("Attempt to get element mapping for field " + mmd.getFullFieldName() + " that has no collection/array!").setFatal();
        }
        if (mmd.getJoinMetaData() == null) {
            final AbstractMemberMetaData[] refMmds = mmd.getRelatedMemberMetaData(clr);
            if (refMmds == null || refMmds.length == 0) {
                throw new NucleusException("Attempt to get element mapping for field " + mmd.getFullFieldName() + " that has no join table defined for the collection/array").setFatal();
            }
            if (refMmds[0].getJoinMetaData() == null) {
                throw new NucleusException("Attempt to get element mapping for field " + mmd.getFullFieldName() + " that has no join table defined for the collection/array").setFatal();
            }
        }
        String userMappingClassName = null;
        if (mmd.getElementMetaData() != null) {
            userMappingClassName = mmd.getElementMetaData().getValueForExtension("mapping-class");
        }
        if (userMappingClassName != null) {
            try {
                return clr.classForName(userMappingClassName);
            }
            catch (NucleusException jpe) {
                throw new NucleusUserException(RDBMSMappingManager.LOCALISER_RDBMS.msg("041014", userMappingClassName)).setFatal();
            }
        }
        final boolean serialised = (mmd.hasCollection() && mmd.getCollection().isSerializedElement()) || (mmd.hasArray() && mmd.getArray().isSerializedElement());
        final boolean embeddedPC = mmd.getElementMetaData() != null && mmd.getElementMetaData().getEmbeddedMetaData() != null;
        final boolean elementPC = (mmd.hasCollection() && mmd.getCollection().elementIsPersistent()) || (mmd.hasArray() && mmd.getArray().elementIsPersistent());
        boolean embedded = true;
        if (mmd.hasCollection()) {
            embedded = mmd.getCollection().isEmbeddedElement();
        }
        else if (mmd.hasArray()) {
            embedded = mmd.getArray().isEmbeddedElement();
        }
        Class elementCls = null;
        if (mmd.hasCollection()) {
            elementCls = clr.classForName(mmd.getCollection().getElementType());
        }
        else if (mmd.hasArray()) {
            elementCls = mmd.getType().getComponentType();
        }
        final boolean elementReference = ClassUtils.isReferenceType(elementCls);
        Class mc = null;
        if (serialised) {
            if (elementPC) {
                mc = SerialisedElementPCMapping.class;
            }
            else if (elementReference) {
                mc = SerialisedReferenceMapping.class;
            }
            else {
                mc = SerialisedMapping.class;
            }
        }
        else if (embedded) {
            if (embeddedPC) {
                mc = EmbeddedElementPCMapping.class;
            }
            else if (elementPC) {
                mc = PersistableMapping.class;
            }
            else {
                mc = this.getMappingClass(elementCls, serialised, embedded, mmd.getFullFieldName());
            }
        }
        else {
            mc = this.getMappingClass(elementCls, serialised, embedded, mmd.getFullFieldName());
        }
        return mc;
    }
    
    protected Class getKeyMappingClass(final Table table, final AbstractMemberMetaData mmd, final ClassLoaderResolver clr) {
        if (mmd.getMap() == null) {
            throw new NucleusException("Attempt to get key mapping for field " + mmd.getFullFieldName() + " that has no map!").setFatal();
        }
        String userMappingClassName = null;
        if (mmd.getKeyMetaData() != null) {
            userMappingClassName = mmd.getKeyMetaData().getValueForExtension("mapping-class");
        }
        if (userMappingClassName != null) {
            try {
                return clr.classForName(userMappingClassName);
            }
            catch (NucleusException jpe) {
                throw new NucleusUserException(RDBMSMappingManager.LOCALISER_RDBMS.msg("041014", userMappingClassName)).setFatal();
            }
        }
        final boolean serialised = mmd.hasMap() && mmd.getMap().isSerializedKey();
        final boolean embedded = mmd.hasMap() && mmd.getMap().isEmbeddedKey();
        final boolean embeddedPC = mmd.getKeyMetaData() != null && mmd.getKeyMetaData().getEmbeddedMetaData() != null;
        final boolean keyPC = mmd.hasMap() && mmd.getMap().keyIsPersistent();
        final Class keyCls = clr.classForName(mmd.getMap().getKeyType());
        final boolean keyReference = ClassUtils.isReferenceType(keyCls);
        Class mc = null;
        if (serialised) {
            if (keyPC) {
                mc = SerialisedKeyPCMapping.class;
            }
            else if (keyReference) {
                mc = SerialisedReferenceMapping.class;
            }
            else {
                mc = SerialisedMapping.class;
            }
        }
        else if (embedded) {
            if (embeddedPC) {
                mc = EmbeddedKeyPCMapping.class;
            }
            else if (keyPC) {
                mc = PersistableMapping.class;
            }
            else {
                mc = this.getMappingClass(keyCls, serialised, embedded, mmd.getFullFieldName());
            }
        }
        else {
            mc = this.getMappingClass(keyCls, serialised, embedded, mmd.getFullFieldName());
        }
        return mc;
    }
    
    protected Class getValueMappingClass(final Table table, final AbstractMemberMetaData mmd, final ClassLoaderResolver clr) {
        if (mmd.getMap() == null) {
            throw new NucleusException("Attempt to get value mapping for field " + mmd.getFullFieldName() + " that has no map!").setFatal();
        }
        String userMappingClassName = null;
        if (mmd.getValueMetaData() != null) {
            userMappingClassName = mmd.getValueMetaData().getValueForExtension("mapping-class");
        }
        if (userMappingClassName != null) {
            try {
                return clr.classForName(userMappingClassName);
            }
            catch (NucleusException jpe) {
                throw new NucleusUserException(RDBMSMappingManager.LOCALISER_RDBMS.msg("041014", userMappingClassName)).setFatal();
            }
        }
        final boolean serialised = mmd.hasMap() && mmd.getMap().isSerializedValue();
        final boolean embedded = mmd.hasMap() && mmd.getMap().isEmbeddedValue();
        final boolean embeddedPC = mmd.getValueMetaData() != null && mmd.getValueMetaData().getEmbeddedMetaData() != null;
        final boolean valuePC = mmd.hasMap() && mmd.getMap().valueIsPersistent();
        final Class valueCls = clr.classForName(mmd.getMap().getValueType());
        final boolean valueReference = ClassUtils.isReferenceType(valueCls);
        Class mc = null;
        if (serialised) {
            if (valuePC) {
                mc = SerialisedValuePCMapping.class;
            }
            else if (valueReference) {
                mc = SerialisedReferenceMapping.class;
            }
            else {
                mc = SerialisedMapping.class;
            }
        }
        else if (embedded) {
            if (embeddedPC) {
                mc = EmbeddedValuePCMapping.class;
            }
            else if (valuePC) {
                mc = PersistableMapping.class;
            }
            else {
                mc = this.getMappingClass(valueCls, serialised, embedded, mmd.getFullFieldName());
            }
        }
        else {
            mc = this.getMappingClass(valueCls, serialised, embedded, mmd.getFullFieldName());
        }
        return mc;
    }
    
    protected Class getDefaultJavaTypeMapping(final Class javaType) {
        final Class cls = this.storeMgr.getMappedTypeManager().getMappingType(javaType.getName());
        if (cls != null) {
            return cls;
        }
        final TypeConverter conv = this.storeMgr.getNucleusContext().getTypeManager().getDefaultTypeConverterForType(javaType);
        if (conv == null) {
            NucleusLogger.PERSISTENCE.debug(RDBMSMappingManager.LOCALISER_RDBMS.msg("041000", javaType.getName()), new Exception());
            return null;
        }
        if (TypeManager.getDatastoreTypeForTypeConverter(conv, javaType) == String.class) {
            return TypeConverterStringMapping.class;
        }
        if (TypeManager.getDatastoreTypeForTypeConverter(conv, javaType) == Long.class) {
            return TypeConverterLongMapping.class;
        }
        return TypeConverterMapping.class;
    }
    
    public void registerDatastoreMapping(final String javaTypeName, final Class datastoreMappingType, final String jdbcType, final String sqlType, final boolean dflt) {
        boolean mappingRequired = true;
        final Collection coll = this.datastoreMappingsByJavaType.get(javaTypeName);
        if (coll != null && coll.size() > 0) {
            for (final RDBMSTypeMapping typeMapping : coll) {
                if (typeMapping.jdbcType.equals(jdbcType) && typeMapping.sqlType.equals(sqlType)) {
                    mappingRequired = false;
                    if (typeMapping.isDefault() == dflt) {
                        continue;
                    }
                    typeMapping.setDefault(dflt);
                }
                else {
                    if (!dflt) {
                        continue;
                    }
                    typeMapping.setDefault(false);
                }
            }
        }
        if (mappingRequired) {
            final RDBMSTypeMapping mapping = new RDBMSTypeMapping(datastoreMappingType, dflt, javaTypeName, jdbcType, sqlType);
            this.datastoreMappingsByJDBCType.put(jdbcType, mapping);
            this.datastoreMappingsBySQLType.put(sqlType, mapping);
            this.datastoreMappingsByJavaType.put(javaTypeName, mapping);
            if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                NucleusLogger.DATASTORE.debug(RDBMSMappingManager.LOCALISER_RDBMS.msg("054009", javaTypeName, jdbcType, sqlType, datastoreMappingType.getName(), "" + dflt));
            }
        }
    }
    
    public void deregisterDatastoreMappingsForJDBCType(final String jdbcTypeName) {
        final Collection coll = this.datastoreMappingsByJDBCType.get(jdbcTypeName);
        if (coll == null || coll.size() == 0) {
            return;
        }
        final Collection mappings = new HashSet(coll);
        for (final RDBMSTypeMapping mapping : mappings) {
            this.datastoreMappingsByJavaType.remove(mapping.javaType, mapping);
            this.datastoreMappingsBySQLType.remove(mapping.sqlType, mapping);
            this.datastoreMappingsByJDBCType.remove(mapping.jdbcType, mapping);
            if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                NucleusLogger.DATASTORE.debug(RDBMSMappingManager.LOCALISER_RDBMS.msg("054010", mapping.javaType, mapping.jdbcType, mapping.sqlType));
            }
        }
    }
    
    protected Class getDatastoreMappingClass(final String fieldName, String javaType, final String jdbcType, final String sqlType, final ClassLoaderResolver clr) {
        if (javaType == null) {
            return null;
        }
        javaType = ClassUtils.getWrapperTypeNameForPrimitiveTypeName(javaType);
        RDBMSTypeMapping datastoreMapping = null;
        if (sqlType != null) {
            if (this.datastoreMappingsBySQLType.get(sqlType.toUpperCase()) == null) {
                if (jdbcType == null) {
                    if (fieldName != null) {
                        throw new NucleusException(RDBMSMappingManager.LOCALISER_RDBMS.msg("054001", javaType, sqlType, fieldName)).setFatal();
                    }
                    throw new NucleusException(RDBMSMappingManager.LOCALISER_RDBMS.msg("054000", javaType, sqlType)).setFatal();
                }
                else if (fieldName != null) {
                    NucleusLogger.DATASTORE_SCHEMA.info(RDBMSMappingManager.LOCALISER_RDBMS.msg("054012", javaType, sqlType, fieldName, jdbcType));
                }
                else {
                    NucleusLogger.DATASTORE_SCHEMA.info(RDBMSMappingManager.LOCALISER_RDBMS.msg("054011", javaType, sqlType, jdbcType));
                }
            }
            else {
                for (final RDBMSTypeMapping sqlTypeMapping : this.datastoreMappingsBySQLType.get(sqlType.toUpperCase())) {
                    if (sqlTypeMapping.javaType.equals(javaType)) {
                        datastoreMapping = sqlTypeMapping;
                        break;
                    }
                }
            }
        }
        if (datastoreMapping == null && jdbcType != null) {
            if (this.datastoreMappingsByJDBCType.get(jdbcType.toUpperCase()) == null) {
                if (fieldName != null) {
                    throw new NucleusException(RDBMSMappingManager.LOCALISER_RDBMS.msg("054003", javaType, jdbcType, fieldName)).setFatal();
                }
                throw new NucleusException(RDBMSMappingManager.LOCALISER_RDBMS.msg("054002", javaType, jdbcType)).setFatal();
            }
            else {
                for (final RDBMSTypeMapping jdbcTypeMapping : this.datastoreMappingsByJDBCType.get(jdbcType.toUpperCase())) {
                    if (jdbcTypeMapping.javaType.equals(javaType)) {
                        datastoreMapping = jdbcTypeMapping;
                        break;
                    }
                }
                if (datastoreMapping == null) {
                    if (fieldName != null) {
                        throw new NucleusException(RDBMSMappingManager.LOCALISER_RDBMS.msg("054003", javaType, jdbcType, fieldName)).setFatal();
                    }
                    throw new NucleusException(RDBMSMappingManager.LOCALISER_RDBMS.msg("054002", javaType, jdbcType)).setFatal();
                }
            }
        }
        if (datastoreMapping == null) {
            final String type = ClassUtils.getWrapperTypeNameForPrimitiveTypeName(javaType);
            Collection mappings = this.datastoreMappingsByJavaType.get(type);
            if (mappings == null) {
                final Class javaTypeClass = clr.classForName(type);
                for (Class superClass = javaTypeClass.getSuperclass(); superClass != null && !superClass.getName().equals(ClassNameConstants.Object) && mappings == null; mappings = this.datastoreMappingsByJavaType.get(superClass.getName()), superClass = superClass.getSuperclass()) {}
            }
            if (mappings != null) {
                if (mappings.size() == 1) {
                    datastoreMapping = mappings.iterator().next();
                }
                else {
                    for (final RDBMSTypeMapping rdbmsMapping : mappings) {
                        if (rdbmsMapping.isDefault()) {
                            datastoreMapping = rdbmsMapping;
                            break;
                        }
                    }
                    if (datastoreMapping == null && mappings.size() > 0) {
                        datastoreMapping = mappings.iterator().next();
                    }
                }
            }
        }
        if (datastoreMapping != null) {
            return datastoreMapping.getMappingType();
        }
        if (fieldName != null) {
            throw new NucleusException(RDBMSMappingManager.LOCALISER_RDBMS.msg("054005", javaType, jdbcType, sqlType, fieldName)).setFatal();
        }
        throw new NucleusException(RDBMSMappingManager.LOCALISER_RDBMS.msg("054004", javaType, jdbcType, sqlType)).setFatal();
    }
    
    @Override
    public DatastoreMapping createDatastoreMapping(final JavaTypeMapping mapping, final AbstractMemberMetaData mmd, final int index, final Column column) {
        Class datastoreMappingClass = null;
        if (mmd.getColumnMetaData().length > 0 && mmd.getColumnMetaData()[index].hasExtension("datastore-mapping-class")) {
            datastoreMappingClass = this.clr.classForName(mmd.getColumnMetaData()[index].getValueForExtension("datastore-mapping-class"));
        }
        if (datastoreMappingClass == null) {
            String javaType = mapping.getJavaTypeForDatastoreMapping(index);
            String jdbcType = null;
            String sqlType = null;
            if (mapping.getRoleForMember() == 4 || mapping.getRoleForMember() == 3) {
                final ColumnMetaData[] colmds = (ColumnMetaData[])((mmd.getElementMetaData() != null) ? mmd.getElementMetaData().getColumnMetaData() : null);
                if (colmds != null && colmds.length > 0) {
                    jdbcType = colmds[index].getJdbcType();
                    sqlType = colmds[index].getSqlType();
                }
                if (mmd.getCollection() != null && mmd.getCollection().isSerializedElement()) {
                    javaType = ClassNameConstants.JAVA_IO_SERIALIZABLE;
                }
                if (mmd.getArray() != null && mmd.getArray().isSerializedElement()) {
                    javaType = ClassNameConstants.JAVA_IO_SERIALIZABLE;
                }
            }
            else if (mapping.getRoleForMember() == 5) {
                final ColumnMetaData[] colmds = (ColumnMetaData[])((mmd.getKeyMetaData() != null) ? mmd.getKeyMetaData().getColumnMetaData() : null);
                if (colmds != null && colmds.length > 0) {
                    jdbcType = colmds[index].getJdbcType();
                    sqlType = colmds[index].getSqlType();
                }
                if (mmd.getMap().isSerializedKey()) {
                    javaType = ClassNameConstants.JAVA_IO_SERIALIZABLE;
                }
            }
            else if (mapping.getRoleForMember() == 6) {
                final ColumnMetaData[] colmds = (ColumnMetaData[])((mmd.getValueMetaData() != null) ? mmd.getValueMetaData().getColumnMetaData() : null);
                if (colmds != null && colmds.length > 0) {
                    jdbcType = colmds[index].getJdbcType();
                    sqlType = colmds[index].getSqlType();
                }
                if (mmd.getMap().isSerializedValue()) {
                    javaType = ClassNameConstants.JAVA_IO_SERIALIZABLE;
                }
            }
            else {
                if (mmd.getColumnMetaData().length > 0) {
                    jdbcType = mmd.getColumnMetaData()[index].getJdbcType();
                    sqlType = mmd.getColumnMetaData()[index].getSqlType();
                }
                if (mmd.isSerialized()) {
                    javaType = ClassNameConstants.JAVA_IO_SERIALIZABLE;
                }
            }
            datastoreMappingClass = this.getDatastoreMappingClass(mmd.getFullFieldName(), javaType, jdbcType, sqlType, this.clr);
        }
        final DatastoreMapping datastoreMapping = DatastoreMappingFactory.createMapping(datastoreMappingClass, mapping, this.storeMgr, column);
        if (column != null) {
            column.setDatastoreMapping(datastoreMapping);
        }
        return datastoreMapping;
    }
    
    @Override
    public DatastoreMapping createDatastoreMapping(final JavaTypeMapping mapping, final Column column, final String javaType) {
        final Column col = column;
        String jdbcType = null;
        String sqlType = null;
        if (col != null && col.getColumnMetaData() != null) {
            jdbcType = col.getColumnMetaData().getJdbcType();
            sqlType = col.getColumnMetaData().getSqlType();
        }
        final Class datastoreMappingClass = this.getDatastoreMappingClass(null, javaType, jdbcType, sqlType, this.clr);
        final DatastoreMapping datastoreMapping = DatastoreMappingFactory.createMapping(datastoreMappingClass, mapping, this.storeMgr, column);
        if (column != null) {
            column.setDatastoreMapping(datastoreMapping);
        }
        return datastoreMapping;
    }
    
    @Override
    public Column createColumn(final JavaTypeMapping mapping, final String javaType, final int datastoreFieldIndex) {
        final AbstractMemberMetaData fmd = mapping.getMemberMetaData();
        final int roleForField = mapping.getRoleForMember();
        final Table tbl = mapping.getTable();
        ColumnMetaData colmd = null;
        ColumnMetaDataContainer columnContainer = fmd;
        if (roleForField == 3 || roleForField == 4) {
            columnContainer = fmd.getElementMetaData();
        }
        else if (roleForField == 5) {
            columnContainer = fmd.getKeyMetaData();
        }
        else if (roleForField == 6) {
            columnContainer = fmd.getValueMetaData();
        }
        ColumnMetaData[] colmds;
        if (columnContainer != null && columnContainer.getColumnMetaData().length > datastoreFieldIndex) {
            colmd = columnContainer.getColumnMetaData()[datastoreFieldIndex];
            colmds = columnContainer.getColumnMetaData();
        }
        else {
            colmd = new ColumnMetaData();
            colmd.setName(fmd.getColumn());
            if (columnContainer != null) {
                columnContainer.addColumn(colmd);
                colmds = columnContainer.getColumnMetaData();
            }
            else {
                colmds = new ColumnMetaData[] { colmd };
            }
        }
        final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
        DatastoreIdentifier identifier = null;
        if (colmd.getName() == null) {
            if (roleForField == 3) {
                identifier = idFactory.newJoinTableFieldIdentifier(fmd, null, null, true, 3);
            }
            else if (roleForField == 4) {
                identifier = idFactory.newJoinTableFieldIdentifier(fmd, null, null, true, 4);
            }
            else if (roleForField == 5) {
                identifier = idFactory.newJoinTableFieldIdentifier(fmd, null, null, true, 5);
            }
            else if (roleForField == 6) {
                identifier = idFactory.newJoinTableFieldIdentifier(fmd, null, null, true, 6);
            }
            else {
                identifier = idFactory.newIdentifier(IdentifierType.COLUMN, fmd.getName());
                for (int i = 0; tbl.hasColumn(identifier); identifier = idFactory.newIdentifier(IdentifierType.COLUMN, fmd.getName() + "_" + i), ++i) {}
            }
            colmd.setName(identifier.getIdentifierName());
        }
        else {
            identifier = idFactory.newColumnIdentifier(colmds[datastoreFieldIndex].getName(), this.storeMgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(fmd.getType()), -1);
        }
        final Column col = tbl.addColumn(javaType, identifier, mapping, colmd);
        if (fmd.isPrimaryKey()) {
            col.setAsPrimaryKey();
        }
        if (fmd.getParent() instanceof AbstractClassMetaData) {
            if (this.storeMgr.isStrategyDatastoreAttributed(fmd.getAbstractClassMetaData(), fmd.getAbsoluteFieldNumber()) && tbl instanceof DatastoreClass && ((fmd.isPrimaryKey() && ((DatastoreClass)tbl).isBaseDatastoreClass()) || !fmd.isPrimaryKey())) {
                col.setIdentity(true);
            }
        }
        if (fmd.getValueForExtension("select-function") != null) {
            col.setWrapperFunction(fmd.getValueForExtension("select-function"), 0);
        }
        if (fmd.getValueForExtension("insert-function") != null) {
            col.setWrapperFunction(fmd.getValueForExtension("insert-function"), 1);
        }
        if (fmd.getValueForExtension("update-function") != null) {
            col.setWrapperFunction(fmd.getValueForExtension("update-function"), 2);
        }
        this.setColumnNullability(fmd, colmd, col);
        if (fmd.getNullValue() == NullValue.DEFAULT) {
            col.setDefaultable();
            if (colmd.getDefaultValue() != null) {
                col.setDefaultValue(colmd.getDefaultValue());
            }
        }
        return col;
    }
    
    @Override
    public Column createColumn(final JavaTypeMapping mapping, final String javaType, ColumnMetaData colmd) {
        final AbstractMemberMetaData fmd = mapping.getMemberMetaData();
        final Table tbl = mapping.getTable();
        if (colmd == null) {
            colmd = new ColumnMetaData();
            colmd.setName(fmd.getColumn());
            fmd.addColumn(colmd);
        }
        final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
        Column col;
        if (colmd.getName() == null) {
            DatastoreIdentifier identifier = idFactory.newIdentifier(IdentifierType.COLUMN, fmd.getName());
            for (int i = 0; tbl.hasColumn(identifier); identifier = idFactory.newIdentifier(IdentifierType.COLUMN, fmd.getName() + "_" + i), ++i) {}
            colmd.setName(identifier.getIdentifierName());
            col = tbl.addColumn(javaType, identifier, mapping, colmd);
        }
        else {
            col = tbl.addColumn(javaType, idFactory.newColumnIdentifier(colmd.getName(), this.storeMgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(fmd.getType()), -1), mapping, colmd);
        }
        this.setColumnNullability(fmd, colmd, col);
        if (fmd.getNullValue() == NullValue.DEFAULT) {
            col.setDefaultable();
            if (colmd.getDefaultValue() != null) {
                col.setDefaultValue(colmd.getDefaultValue());
            }
        }
        return col;
    }
    
    @Override
    public Column createColumn(final AbstractMemberMetaData mmd, final Table table, final JavaTypeMapping mapping, final ColumnMetaData colmd, final Column reference, final ClassLoaderResolver clr) {
        final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
        DatastoreIdentifier identifier = null;
        if (colmd.getName() == null) {
            final AbstractMemberMetaData[] relatedMmds = mmd.getRelatedMemberMetaData(clr);
            identifier = idFactory.newForeignKeyFieldIdentifier((relatedMmds != null) ? relatedMmds[0] : null, mmd, reference.getIdentifier(), this.storeMgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(mmd.getType()), 1);
            colmd.setName(identifier.getIdentifierName());
        }
        else {
            identifier = idFactory.newColumnIdentifier(colmd.getName(), false, -1);
        }
        final Column col = table.addColumn(mmd.getType().getName(), identifier, mapping, colmd);
        reference.copyConfigurationTo(col);
        if (mmd.isPrimaryKey()) {
            col.setAsPrimaryKey();
        }
        if (mmd.getParent() instanceof AbstractClassMetaData) {
            if (this.storeMgr.isStrategyDatastoreAttributed(mmd.getAbstractClassMetaData(), mmd.getAbsoluteFieldNumber()) && ((mmd.isPrimaryKey() && ((DatastoreClass)table).isBaseDatastoreClass()) || !mmd.isPrimaryKey())) {
                col.setIdentity(true);
            }
        }
        if (mmd.getValueForExtension("select-function") != null) {
            col.setWrapperFunction(mmd.getValueForExtension("select-function"), 0);
        }
        if (mmd.getValueForExtension("insert-function") != null) {
            col.setWrapperFunction(mmd.getValueForExtension("insert-function"), 1);
        }
        if (mmd.getValueForExtension("update-function") != null) {
            col.setWrapperFunction(mmd.getValueForExtension("update-function"), 2);
        }
        this.setColumnNullability(mmd, colmd, col);
        if (mmd.getNullValue() == NullValue.DEFAULT) {
            col.setDefaultable();
            if (colmd.getDefaultValue() != null) {
                col.setDefaultValue(colmd.getDefaultValue());
            }
        }
        return col;
    }
    
    private void setColumnNullability(final AbstractMemberMetaData mmd, final ColumnMetaData colmd, final Column col) {
        if (colmd != null && colmd.getAllowsNull() == null) {
            if (mmd.isPrimaryKey()) {
                colmd.setAllowsNull(false);
            }
            else if (!mmd.getType().isPrimitive() && mmd.getNullValue() != NullValue.EXCEPTION) {
                colmd.setAllowsNull(true);
            }
            else {
                colmd.setAllowsNull(false);
            }
            if (colmd.isAllowsNull()) {
                col.setNullable();
            }
        }
        else if (colmd != null && colmd.getAllowsNull() != null) {
            if (colmd.isAllowsNull()) {
                col.setNullable();
            }
        }
        else if (!mmd.isPrimaryKey()) {
            if (!mmd.getType().isPrimitive() && mmd.getNullValue() != NullValue.EXCEPTION) {
                col.setNullable();
            }
        }
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
    
    protected class RDBMSTypeMapping
    {
        private String javaType;
        private String jdbcType;
        private String sqlType;
        private Class javaMappingType;
        private boolean isDefault;
        
        public RDBMSTypeMapping(final Class mappingType, final boolean isDefault, final String javaType, final String jdbcType, final String sqlType) {
            this.javaMappingType = mappingType;
            this.isDefault = isDefault;
            this.javaType = javaType;
            this.jdbcType = jdbcType;
            this.sqlType = sqlType;
        }
        
        public boolean isDefault() {
            return this.isDefault;
        }
        
        public void setDefault(final boolean isDefault) {
            this.isDefault = isDefault;
        }
        
        public Class getMappingType() {
            return this.javaMappingType;
        }
        
        public void setMappingType(final Class type) {
            this.javaMappingType = type;
        }
    }
}
