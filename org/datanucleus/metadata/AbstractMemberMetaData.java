// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.Set;
import java.util.HashSet;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.types.TypeManager;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.api.ApiAdapter;
import java.lang.reflect.Modifier;
import java.io.Serializable;
import org.datanucleus.ClassNameConstants;
import java.util.Map;
import org.datanucleus.util.ClassUtils;
import java.util.Collection;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.util.NucleusLogger;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Member;

public abstract class AbstractMemberMetaData extends MetaData implements Comparable, ColumnMetaDataContainer
{
    protected static final String TYPE_CONVERTER_EXTENSION_NAME = "type-converter-name";
    protected static final String TYPE_CONVERTER_DISABLED = "type-converter-disabled";
    public static final boolean PERSIST_STATIC = false;
    public static final boolean PERSIST_FINAL = false;
    public static final boolean PERSIST_TRANSIENT = false;
    protected ColumnMetaData[] columnMetaData;
    protected ContainerMetaData containerMetaData;
    protected EmbeddedMetaData embeddedMetaData;
    protected JoinMetaData joinMetaData;
    protected ElementMetaData elementMetaData;
    protected KeyMetaData keyMetaData;
    protected ValueMetaData valueMetaData;
    protected IndexMetaData indexMetaData;
    protected IndexedValue indexed;
    protected UniqueMetaData uniqueMetaData;
    protected boolean uniqueConstraint;
    protected OrderMetaData orderMetaData;
    protected ForeignKeyMetaData foreignKeyMetaData;
    protected Boolean defaultFetchGroup;
    protected String column;
    protected String mappedBy;
    protected Boolean embedded;
    protected Boolean dependent;
    protected Boolean serialized;
    protected boolean cacheable;
    protected Boolean cascadePersist;
    protected Boolean cascadeUpdate;
    protected Boolean cascadeDelete;
    protected Boolean cascadeDetach;
    protected Boolean cascadeRefresh;
    protected boolean cascadeRemoveOrphans;
    protected String loadFetchGroup;
    public static final int DEFAULT_RECURSION_DEPTH = 1;
    public static final int UNDEFINED_RECURSION_DEPTH = 0;
    protected int recursionDepth;
    protected final String name;
    protected NullValue nullValue;
    protected FieldPersistenceModifier persistenceModifier;
    protected Boolean primaryKey;
    protected String table;
    protected String catalog;
    protected String schema;
    protected IdentityStrategy valueStrategy;
    protected String valueGeneratorName;
    protected String sequence;
    protected String className;
    protected String fullFieldName;
    protected String[] fieldTypes;
    protected Class type;
    protected Member memberRepresented;
    protected int fieldId;
    protected RelationType relationType;
    protected AbstractMemberMetaData[] relatedMemberMetaData;
    protected boolean ordered;
    protected List<ColumnMetaData> columns;
    protected String targetClassName;
    protected boolean storeInLob;
    protected byte persistenceFlags;
    
    public AbstractMemberMetaData(final MetaData parent, final AbstractMemberMetaData mmd) {
        super(parent, mmd);
        this.indexed = null;
        this.uniqueConstraint = false;
        this.cacheable = true;
        this.cascadeRemoveOrphans = false;
        this.recursionDepth = 0;
        this.nullValue = NullValue.NONE;
        this.persistenceModifier = FieldPersistenceModifier.DEFAULT;
        this.className = null;
        this.fullFieldName = null;
        this.fieldId = -1;
        this.relationType = null;
        this.relatedMemberMetaData = null;
        this.ordered = false;
        this.columns = new ArrayList<ColumnMetaData>();
        this.targetClassName = null;
        this.storeInLob = false;
        this.name = mmd.name;
        this.primaryKey = mmd.primaryKey;
        this.defaultFetchGroup = mmd.defaultFetchGroup;
        this.column = mmd.column;
        this.mappedBy = mmd.mappedBy;
        this.dependent = mmd.dependent;
        this.embedded = mmd.embedded;
        this.serialized = mmd.serialized;
        this.cascadePersist = mmd.cascadePersist;
        this.cascadeUpdate = mmd.cascadeUpdate;
        this.cascadeDelete = mmd.cascadeDelete;
        this.cascadeDetach = mmd.cascadeDetach;
        this.cascadeRefresh = mmd.cascadeRefresh;
        this.nullValue = mmd.nullValue;
        this.persistenceModifier = mmd.persistenceModifier;
        this.table = mmd.table;
        this.indexed = mmd.indexed;
        this.valueStrategy = mmd.valueStrategy;
        this.valueGeneratorName = mmd.valueGeneratorName;
        this.sequence = mmd.sequence;
        this.uniqueConstraint = mmd.uniqueConstraint;
        this.loadFetchGroup = mmd.loadFetchGroup;
        this.storeInLob = mmd.storeInLob;
        this.column = mmd.column;
        if (mmd.fieldTypes != null) {
            this.fieldTypes = new String[mmd.fieldTypes.length];
            for (int i = 0; i < mmd.fieldTypes.length; ++i) {
                this.fieldTypes[i] = mmd.fieldTypes[i];
            }
        }
        if (mmd.joinMetaData != null) {
            this.setJoinMetaData(new JoinMetaData(mmd.joinMetaData));
        }
        if (mmd.elementMetaData != null) {
            this.setElementMetaData(new ElementMetaData(mmd.elementMetaData));
        }
        if (mmd.keyMetaData != null) {
            this.setKeyMetaData(new KeyMetaData(mmd.keyMetaData));
        }
        if (mmd.valueMetaData != null) {
            this.setValueMetaData(new ValueMetaData(mmd.valueMetaData));
        }
        if (mmd.orderMetaData != null) {
            this.setOrderMetaData(new OrderMetaData(mmd.orderMetaData));
        }
        if (mmd.indexMetaData != null) {
            this.setIndexMetaData(new IndexMetaData(mmd.indexMetaData));
        }
        if (mmd.uniqueMetaData != null) {
            this.setUniqueMetaData(new UniqueMetaData(mmd.uniqueMetaData));
        }
        if (mmd.foreignKeyMetaData != null) {
            this.setForeignKeyMetaData(new ForeignKeyMetaData(mmd.foreignKeyMetaData));
        }
        if (mmd.embeddedMetaData != null) {
            this.setEmbeddedMetaData(new EmbeddedMetaData(mmd.embeddedMetaData));
        }
        if (mmd.containerMetaData != null) {
            if (mmd.containerMetaData instanceof CollectionMetaData) {
                this.setContainer(new CollectionMetaData((CollectionMetaData)mmd.containerMetaData));
            }
            else if (mmd.containerMetaData instanceof MapMetaData) {
                this.setContainer(new MapMetaData((MapMetaData)mmd.containerMetaData));
            }
            else if (mmd.containerMetaData instanceof ArrayMetaData) {
                this.setContainer(new ArrayMetaData((ArrayMetaData)mmd.containerMetaData));
            }
        }
        for (int i = 0; i < mmd.columns.size(); ++i) {
            this.addColumn(new ColumnMetaData(mmd.columns.get(i)));
        }
    }
    
    public AbstractMemberMetaData(final MetaData parent, final String name) {
        super(parent);
        this.indexed = null;
        this.uniqueConstraint = false;
        this.cacheable = true;
        this.cascadeRemoveOrphans = false;
        this.recursionDepth = 0;
        this.nullValue = NullValue.NONE;
        this.persistenceModifier = FieldPersistenceModifier.DEFAULT;
        this.className = null;
        this.fullFieldName = null;
        this.fieldId = -1;
        this.relationType = null;
        this.relatedMemberMetaData = null;
        this.ordered = false;
        this.columns = new ArrayList<ColumnMetaData>();
        this.targetClassName = null;
        this.storeInLob = false;
        if (name == null) {
            throw new NucleusUserException(AbstractMemberMetaData.LOCALISER.msg("044041", "name", this.getClassName(true), "field"));
        }
        if (name.indexOf(46) >= 0) {
            this.className = name.substring(0, name.lastIndexOf(46));
            this.name = name.substring(name.lastIndexOf(46) + 1);
        }
        else {
            this.name = name;
        }
    }
    
    public synchronized void populate(ClassLoaderResolver clr, final Field field, final Method method, final ClassLoader primary, final MetaDataManager mmgr) {
        if (this.isPopulated() || this.isInitialised()) {
            return;
        }
        if (mmgr != null) {
            final ApiAdapter apiAdapter = mmgr.getNucleusContext().getApiAdapter();
            if (this.cascadePersist == null) {
                this.cascadePersist = apiAdapter.getDefaultCascadePersistForField();
            }
            if (this.cascadeUpdate == null) {
                this.cascadeUpdate = apiAdapter.getDefaultCascadeUpdateForField();
            }
            if (this.cascadeDelete == null) {
                this.cascadeDelete = apiAdapter.getDefaultCascadeDeleteForField();
            }
            if (this.cascadeDetach == null) {
                this.cascadeDetach = false;
            }
            if (this.cascadeRefresh == null) {
                this.cascadeRefresh = apiAdapter.getDefaultCascadeRefreshForField();
            }
        }
        if (field == null && method == null) {
            NucleusLogger.METADATA.error(AbstractMemberMetaData.LOCALISER.msg("044106", this.getClassName(), this.getName()));
            throw new InvalidMemberMetaDataException(AbstractMemberMetaData.LOCALISER, "044106", this.getClassName(), this.getName());
        }
        if (clr == null) {
            NucleusLogger.METADATA.warn(AbstractMemberMetaData.LOCALISER.msg("044067", this.name, this.getClassName(true)));
            clr = mmgr.getNucleusContext().getClassLoaderResolver(null);
        }
        this.memberRepresented = ((field != null) ? field : method);
        if (field != null) {
            this.type = field.getType();
        }
        else if (method != null) {
            this.type = method.getReturnType();
        }
        if (this.className != null) {
            Class thisClass = null;
            if (this.parent instanceof EmbeddedMetaData) {
                final MetaData superMd = this.parent.getParent();
                thisClass = ((AbstractMemberMetaData)superMd).getType();
            }
            else {
                try {
                    thisClass = clr.classForName(this.getAbstractClassMetaData().getPackageName() + "." + this.getAbstractClassMetaData().getName());
                }
                catch (ClassNotResolvedException ex) {}
            }
            Class fieldClass = null;
            try {
                fieldClass = clr.classForName(this.className);
            }
            catch (ClassNotResolvedException cnre) {
                try {
                    fieldClass = clr.classForName(this.getAbstractClassMetaData().getPackageName() + "." + this.className);
                    this.className = this.getAbstractClassMetaData().getPackageName() + "." + this.className;
                }
                catch (ClassNotResolvedException cnre2) {
                    NucleusLogger.METADATA.error(AbstractMemberMetaData.LOCALISER.msg("044113", this.getClassName(), this.getName(), this.className));
                    final NucleusException ne = new InvalidMemberMetaDataException(AbstractMemberMetaData.LOCALISER, "044113", this.getClassName(), this.getName(), this.className);
                    ne.setNestedException(cnre);
                    throw ne;
                }
            }
            if (fieldClass != null && !fieldClass.isAssignableFrom(thisClass)) {
                NucleusLogger.METADATA.error(AbstractMemberMetaData.LOCALISER.msg("044114", this.getClassName(), this.getName(), this.className));
                throw new InvalidMemberMetaDataException(AbstractMemberMetaData.LOCALISER, "044114", this.getClassName(), this.getName(), this.className);
            }
        }
        if (this.primaryKey == null) {
            this.primaryKey = Boolean.FALSE;
        }
        if (this.primaryKey == Boolean.FALSE && this.embedded == null) {
            Class element_type = this.getType();
            if (element_type.isArray()) {
                element_type = element_type.getComponentType();
                if (mmgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(element_type)) {
                    this.embedded = Boolean.TRUE;
                }
            }
            else if (mmgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(element_type)) {
                this.embedded = Boolean.TRUE;
            }
        }
        if (this.embedded == null) {
            this.embedded = Boolean.FALSE;
        }
        if (FieldPersistenceModifier.DEFAULT.equals(this.persistenceModifier)) {
            boolean isPcClass = this.getType().isArray() ? this.isFieldArrayTypePersistable(mmgr) : mmgr.isFieldTypePersistable(this.type);
            if (!isPcClass) {
                if (this.getType().isArray() && this.getType().getComponentType().isInterface()) {
                    isPcClass = (mmgr.getMetaDataForClassInternal(this.getType().getComponentType(), clr) != null);
                }
                else if (this.getType().isInterface()) {
                    isPcClass = (mmgr.getMetaDataForClassInternal(this.getType(), clr) != null);
                }
            }
            this.persistenceModifier = this.getDefaultFieldPersistenceModifier(this.getType(), this.memberRepresented.getModifiers(), isPcClass, mmgr);
        }
        if (this.defaultFetchGroup == null && this.persistenceModifier.equals(FieldPersistenceModifier.NONE)) {
            this.defaultFetchGroup = Boolean.FALSE;
        }
        else if (this.defaultFetchGroup == null && this.persistenceModifier.equals(FieldPersistenceModifier.TRANSACTIONAL)) {
            this.defaultFetchGroup = Boolean.FALSE;
        }
        else if (this.defaultFetchGroup == null) {
            this.defaultFetchGroup = Boolean.FALSE;
            if (!this.primaryKey.equals(Boolean.TRUE)) {
                boolean foundGeneric = false;
                final TypeManager typeMgr = mmgr.getNucleusContext().getTypeManager();
                if (Collection.class.isAssignableFrom(this.getType())) {
                    String elementTypeName = null;
                    try {
                        if (field != null) {
                            elementTypeName = ClassUtils.getCollectionElementType(field);
                        }
                        else {
                            elementTypeName = ClassUtils.getCollectionElementType(method);
                        }
                    }
                    catch (NucleusUserException ex2) {}
                    if (elementTypeName != null) {
                        final Class elementType = clr.classForName(elementTypeName);
                        if (typeMgr.isDefaultFetchGroupForCollection(this.getType(), elementType)) {
                            foundGeneric = true;
                            this.defaultFetchGroup = Boolean.TRUE;
                        }
                    }
                }
                if (!foundGeneric && typeMgr.isDefaultFetchGroup(this.getType())) {
                    this.defaultFetchGroup = Boolean.TRUE;
                }
            }
        }
        if ((this.persistenceModifier.equals(FieldPersistenceModifier.TRANSACTIONAL) || this.persistenceModifier.equals(FieldPersistenceModifier.NONE)) && (this.defaultFetchGroup == Boolean.TRUE || this.primaryKey == Boolean.TRUE)) {
            NucleusLogger.GENERAL.info(">> Reporting exception with class=" + this.getClassName() + " name=" + this.name + " type=" + this.type);
            throw new InvalidMemberMetaDataException(AbstractMemberMetaData.LOCALISER, "044109", this.getClassName(), this.name, this.getType().getName(), this.persistenceModifier.toString());
        }
        if (this.storeInLob) {
            boolean useClob = false;
            if (this.type == String.class || (this.type.isArray() && this.type.getComponentType() == Character.class) || (this.type.isArray() && this.type.getComponentType() == Character.TYPE)) {
                useClob = true;
                if (this.columns == null || this.columns.size() == 0) {
                    final ColumnMetaData colmd = new ColumnMetaData();
                    colmd.setName(this.column);
                    colmd.setJdbcType("CLOB");
                    this.addColumn(colmd);
                }
                else {
                    final ColumnMetaData colmd = this.columns.get(0);
                    colmd.setJdbcType("CLOB");
                }
            }
            if (!useClob) {
                this.serialized = Boolean.TRUE;
            }
        }
        if (this.containerMetaData == null) {
            if (this.type.isArray()) {
                final Class arrayCls = this.type.getComponentType();
                final ArrayMetaData arrmd = new ArrayMetaData();
                arrmd.setElementType(arrayCls.getName());
                this.setContainer(arrmd);
            }
            else if (Collection.class.isAssignableFrom(this.type)) {
                if (this.targetClassName != null) {
                    final CollectionMetaData collmd = new CollectionMetaData();
                    collmd.setElementType(this.targetClassName);
                    this.setContainer(collmd);
                }
                else {
                    String elementType2 = null;
                    if (field != null) {
                        elementType2 = ClassUtils.getCollectionElementType(field);
                    }
                    else {
                        elementType2 = ClassUtils.getCollectionElementType(method);
                    }
                    if (elementType2 != null) {
                        final CollectionMetaData collmd2 = new CollectionMetaData();
                        collmd2.setElementType(elementType2);
                        this.setContainer(collmd2);
                    }
                    else {
                        final CollectionMetaData collmd2 = new CollectionMetaData();
                        collmd2.setElementType(Object.class.getName());
                        this.setContainer(collmd2);
                        NucleusLogger.METADATA.debug(AbstractMemberMetaData.LOCALISER.msg("044003", this.getClassName(), this.getName()));
                    }
                }
            }
            else if (Map.class.isAssignableFrom(this.type)) {
                if (this.targetClassName != null) {
                    final MapMetaData mapmd = new MapMetaData();
                    mapmd.setValueType(this.targetClassName);
                    this.setContainer(mapmd);
                }
                else {
                    String keyType = null;
                    String valueType = null;
                    if (field != null) {
                        keyType = ClassUtils.getMapKeyType(field);
                    }
                    else {
                        keyType = ClassUtils.getMapKeyType(method);
                    }
                    if (field != null) {
                        valueType = ClassUtils.getMapValueType(field);
                    }
                    else {
                        valueType = ClassUtils.getMapValueType(method);
                    }
                    if (keyType != null && valueType != null) {
                        final MapMetaData mapmd2 = new MapMetaData();
                        mapmd2.setKeyType(keyType);
                        mapmd2.setValueType(valueType);
                        this.setContainer(mapmd2);
                    }
                    else {
                        if (keyType == null) {
                            keyType = Object.class.getName();
                        }
                        if (valueType == null) {
                            valueType = Object.class.getName();
                        }
                        final MapMetaData mapmd2 = new MapMetaData();
                        mapmd2.setKeyType(keyType);
                        mapmd2.setValueType(valueType);
                        this.setContainer(mapmd2);
                        NucleusLogger.METADATA.debug(AbstractMemberMetaData.LOCALISER.msg("044004", this.getClassName(), this.getName()));
                    }
                }
            }
        }
        else if (this.type.isArray()) {
            if (this.getArray().element.type == null) {
                final Class arrayCls = this.type.getComponentType();
                this.getArray().setElementType(arrayCls.getName());
            }
        }
        else if (Collection.class.isAssignableFrom(this.type)) {
            String elementType2 = null;
            if (field != null) {
                elementType2 = ClassUtils.getCollectionElementType(field);
            }
            else {
                elementType2 = ClassUtils.getCollectionElementType(method);
            }
            if (elementType2 != null && (this.getCollection().element.type == null || this.getCollection().element.type.equals(ClassNameConstants.Object))) {
                this.getCollection().element.type = elementType2;
            }
        }
        else if (Map.class.isAssignableFrom(this.type)) {
            String keyType = null;
            String valueType = null;
            if (field != null) {
                keyType = ClassUtils.getMapKeyType(field);
            }
            else {
                keyType = ClassUtils.getMapKeyType(method);
            }
            if (field != null) {
                valueType = ClassUtils.getMapValueType(field);
            }
            else {
                valueType = ClassUtils.getMapValueType(method);
            }
            if (keyType != null && valueType != null) {
                if (this.getMap().key.type == null || this.getMap().key.type.equals(ClassNameConstants.Object)) {
                    this.getMap().key.type = keyType;
                }
                if (this.getMap().value.type == null || this.getMap().value.type.equals(ClassNameConstants.Object)) {
                    this.getMap().value.type = valueType;
                }
            }
        }
        if (this.hasCollection() && this.ordered && this.orderMetaData == null) {
            final OrderMetaData ordmd = new OrderMetaData();
            ordmd.setOrdering("#PK");
            this.setOrderMetaData(ordmd);
        }
        if (this.elementMetaData == null && !this.isSerialized() && !this.isEmbedded() && this.columnMetaData != null && (this.hasCollection() || this.hasArray())) {
            final ElementMetaData elemmd = new ElementMetaData();
            this.setElementMetaData(elemmd);
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                elemmd.addColumn(this.columnMetaData[i]);
            }
        }
        if (this.valueMetaData == null && this.hasMap() && !this.isEmbedded() && !this.isSerialized() && this.columnMetaData != null) {
            final ValueMetaData valmd = new ValueMetaData();
            this.setValueMetaData(valmd);
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                valmd.addColumn(this.columnMetaData[i]);
            }
        }
        if (this.containerMetaData != null && this.dependent != null) {
            NucleusLogger.METADATA.error(AbstractMemberMetaData.LOCALISER.msg("044110", this.getClassName(), this.getName(), ((ClassMetaData)this.parent).getName()));
            throw new InvalidMemberMetaDataException(AbstractMemberMetaData.LOCALISER, "044110", this.getClassName(), this.getName(), ((ClassMetaData)this.parent).getName());
        }
        if (this.elementMetaData != null) {
            this.elementMetaData.populate(clr, primary, mmgr);
        }
        if (this.keyMetaData != null) {
            this.keyMetaData.populate(clr, primary, mmgr);
        }
        if (this.valueMetaData != null) {
            this.valueMetaData.populate(clr, primary, mmgr);
        }
        if (this.embedded == Boolean.TRUE && this.embeddedMetaData == null) {
            final AbstractClassMetaData memberCmd = mmgr.getMetaDataForClassInternal(this.getType(), clr);
            if (memberCmd != null) {
                (this.embeddedMetaData = new EmbeddedMetaData()).setParent(this);
            }
        }
        if (this.embeddedMetaData != null) {
            if (this.hasExtension("null-indicator-column")) {
                this.embeddedMetaData.setNullIndicatorColumn(this.getValueForExtension("null-indicator-column"));
                if (this.hasExtension("null-indicator-value")) {
                    this.embeddedMetaData.setNullIndicatorValue(this.getValueForExtension("null-indicator-value"));
                }
            }
            this.embeddedMetaData.populate(clr, primary, mmgr);
            this.embedded = Boolean.TRUE;
        }
        if (this.elementMetaData != null && this.elementMetaData.mappedBy != null && this.mappedBy == null) {
            this.mappedBy = this.elementMetaData.mappedBy;
        }
        if (this.containerMetaData != null && this.persistenceModifier == FieldPersistenceModifier.PERSISTENT) {
            if (this.containerMetaData instanceof CollectionMetaData) {
                if (this.cascadeDelete) {
                    this.getCollection().element.dependent = Boolean.TRUE;
                }
                this.getCollection().populate(clr, primary, mmgr);
            }
            else if (this.containerMetaData instanceof MapMetaData) {
                final String keyCascadeVal = this.getValueForExtension("cascade-delete-key");
                if (this.cascadeDelete) {
                    this.getMap().key.dependent = Boolean.FALSE;
                    this.getMap().value.dependent = Boolean.TRUE;
                }
                if (keyCascadeVal != null) {
                    if (keyCascadeVal.equalsIgnoreCase("true")) {
                        this.getMap().key.dependent = Boolean.TRUE;
                    }
                    else {
                        this.getMap().key.dependent = Boolean.FALSE;
                    }
                }
                this.getMap().populate(clr, primary, mmgr);
            }
            else if (this.containerMetaData instanceof ArrayMetaData) {
                if (this.cascadeDelete) {
                    this.getArray().element.dependent = Boolean.TRUE;
                }
                this.getArray().populate(clr, primary, mmgr);
            }
        }
        if (mmgr.isFieldTypePersistable(this.type) && this.cascadeDelete) {
            this.setDependent(true);
        }
        if (this.hasExtension("implementation-classes")) {
            final StringBuilder str = new StringBuilder();
            final String[] implTypes = this.getValuesForExtension("implementation-classes");
            for (int j = 0; j < implTypes.length; ++j) {
                final String implTypeName = ClassUtils.createFullClassName(this.getAbstractClassMetaData().getPackageName(), implTypes[j]);
                if (j > 0) {
                    str.append(",");
                }
                try {
                    clr.classForName(implTypeName);
                    str.append(implTypeName);
                }
                catch (ClassNotResolvedException cnre3) {
                    try {
                        final String langClassName = ClassUtils.getJavaLangClassForType(implTypeName);
                        clr.classForName(langClassName);
                        str.append(langClassName);
                    }
                    catch (ClassNotResolvedException cnre4) {
                        throw new InvalidMemberMetaDataException(AbstractMemberMetaData.LOCALISER, "044116", this.getClassName(), this.getName(), implTypes[j]);
                    }
                }
            }
            this.addExtension("datanucleus", "implementation-classes", str.toString());
        }
        byte serializable = 0;
        if (Serializable.class.isAssignableFrom(this.getType()) || this.getType().isPrimitive()) {
            serializable = 16;
        }
        if (FieldPersistenceModifier.NONE.equals(this.persistenceModifier)) {
            this.persistenceFlags = 0;
        }
        else if (FieldPersistenceModifier.TRANSACTIONAL.equals(this.persistenceModifier) && Modifier.isTransient(this.memberRepresented.getModifiers())) {
            this.persistenceFlags = (byte)(0x4 | serializable);
        }
        else if (this.primaryKey) {
            this.persistenceFlags = (byte)(0x8 | serializable);
        }
        else if (this.defaultFetchGroup) {
            this.persistenceFlags = (byte)(0x5 | serializable);
        }
        else if (!this.defaultFetchGroup) {
            this.persistenceFlags = (byte)(0xA | serializable);
        }
        else {
            this.persistenceFlags = 0;
        }
        if (this.persistenceModifier != FieldPersistenceModifier.PERSISTENT) {
            this.relationType = RelationType.NONE;
        }
        else if (this.containerMetaData == null && !mmgr.isFieldTypePersistable(this.type) && !this.type.getName().equals(ClassNameConstants.Object) && !this.type.isInterface()) {
            this.relationType = RelationType.NONE;
        }
        this.setPopulated();
    }
    
    public final FieldPersistenceModifier getDefaultFieldPersistenceModifier(final Class c, final int modifier, final boolean isPCclass, final MetaDataManager mmgr) {
        if (Modifier.isFinal(modifier) && this instanceof FieldMetaData) {
            return FieldPersistenceModifier.NONE;
        }
        if (Modifier.isStatic(modifier)) {
            return FieldPersistenceModifier.NONE;
        }
        if (Modifier.isTransient(modifier)) {
            return FieldPersistenceModifier.NONE;
        }
        if (isPCclass) {
            return FieldPersistenceModifier.PERSISTENT;
        }
        if (c == null) {
            throw new NucleusException("class is null");
        }
        if (c.isArray() && mmgr.getNucleusContext().getApiAdapter().isPersistable(c.getComponentType())) {
            return FieldPersistenceModifier.PERSISTENT;
        }
        if (mmgr.getNucleusContext().getTypeManager().isDefaultPersistent(c)) {
            return FieldPersistenceModifier.PERSISTENT;
        }
        return FieldPersistenceModifier.NONE;
    }
    
    @Override
    public synchronized void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.persistenceModifier == FieldPersistenceModifier.NONE) {
            this.setInitialised();
            return;
        }
        if (this.columns.size() == 0 && this.column != null) {
            this.columnMetaData = new ColumnMetaData[1];
            (this.columnMetaData[0] = new ColumnMetaData()).setName(this.column);
            this.columnMetaData[0].parent = this;
            this.columnMetaData[0].initialise(clr, mmgr);
        }
        else if (this.columns.size() == 1 && this.column != null) {
            (this.columnMetaData = new ColumnMetaData[1])[0] = this.columns.get(0);
            if (this.columnMetaData[0].getName() == null) {
                this.columnMetaData[0].setName(this.column);
            }
            this.columnMetaData[0].initialise(clr, mmgr);
        }
        else {
            this.columnMetaData = new ColumnMetaData[this.columns.size()];
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                (this.columnMetaData[i] = this.columns.get(i)).initialise(clr, mmgr);
            }
        }
        if (this.containerMetaData != null) {
            this.containerMetaData.initialise(clr, mmgr);
            if (this.containerMetaData instanceof CollectionMetaData) {
                final CollectionMetaData collmd = (CollectionMetaData)this.containerMetaData;
                if (collmd.element.classMetaData != null && collmd.element.classMetaData.isEmbeddedOnly()) {
                    if (this.elementMetaData == null) {
                        this.elementMetaData = new ElementMetaData();
                        this.elementMetaData.parent = this;
                        this.elementMetaData.populate(clr, null, mmgr);
                    }
                    if (this.elementMetaData.getEmbeddedMetaData() == null) {
                        final EmbeddedMetaData elemEmbmd = new EmbeddedMetaData();
                        elemEmbmd.parent = this.elementMetaData;
                        elemEmbmd.populate(clr, null, mmgr);
                        this.elementMetaData.setEmbeddedMetaData(elemEmbmd);
                        collmd.element.embedded = Boolean.TRUE;
                    }
                }
            }
            else if (this.containerMetaData instanceof MapMetaData) {
                final MapMetaData mapmd = (MapMetaData)this.containerMetaData;
                if (mapmd.key.classMetaData != null && mapmd.key.classMetaData.isEmbeddedOnly()) {
                    if (this.keyMetaData == null) {
                        this.keyMetaData = new KeyMetaData();
                        this.keyMetaData.parent = this;
                        this.keyMetaData.populate(clr, null, mmgr);
                    }
                    if (this.keyMetaData.getEmbeddedMetaData() == null) {
                        final EmbeddedMetaData keyEmbmd = new EmbeddedMetaData();
                        keyEmbmd.parent = this.keyMetaData;
                        keyEmbmd.populate(clr, null, mmgr);
                        this.keyMetaData.setEmbeddedMetaData(keyEmbmd);
                        mapmd.key.embedded = Boolean.TRUE;
                    }
                }
                if (mapmd.value.classMetaData != null && mapmd.value.classMetaData.isEmbeddedOnly()) {
                    if (this.valueMetaData == null) {
                        this.valueMetaData = new ValueMetaData();
                        this.valueMetaData.parent = this;
                        this.valueMetaData.populate(clr, null, mmgr);
                    }
                    if (this.valueMetaData.getEmbeddedMetaData() == null) {
                        final EmbeddedMetaData valueEmbmd = new EmbeddedMetaData();
                        valueEmbmd.parent = this.valueMetaData;
                        valueEmbmd.populate(clr, null, mmgr);
                        this.valueMetaData.setEmbeddedMetaData(valueEmbmd);
                        mapmd.value.embedded = Boolean.TRUE;
                    }
                }
            }
        }
        if (this.embeddedMetaData != null) {
            this.embeddedMetaData.initialise(clr, mmgr);
        }
        if (this.joinMetaData != null) {
            this.joinMetaData.initialise(clr, mmgr);
        }
        if (this.elementMetaData != null) {
            this.elementMetaData.initialise(clr, mmgr);
        }
        if (this.keyMetaData != null) {
            this.keyMetaData.initialise(clr, mmgr);
        }
        if (this.valueMetaData != null) {
            this.valueMetaData.initialise(clr, mmgr);
        }
        if (this.indexMetaData == null && this.columnMetaData != null && this.indexed != null && this.indexed != IndexedValue.FALSE) {
            (this.indexMetaData = new IndexMetaData()).setUnique(this.indexed == IndexedValue.UNIQUE);
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                this.indexMetaData.addColumn(this.columnMetaData[i]);
            }
        }
        else if (this.indexed == IndexedValue.TRUE && this.indexMetaData != null) {
            this.indexMetaData = null;
        }
        if (this.indexMetaData != null) {
            this.indexMetaData.initialise(clr, mmgr);
        }
        if (this.uniqueMetaData == null && this.uniqueConstraint) {
            (this.uniqueMetaData = new UniqueMetaData()).setTable(this.column);
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                this.uniqueMetaData.addColumn(this.columnMetaData[i]);
            }
        }
        if (this.uniqueMetaData != null) {
            this.uniqueMetaData.initialise(clr, mmgr);
        }
        if (this.foreignKeyMetaData != null) {
            this.foreignKeyMetaData.initialise(clr, mmgr);
        }
        if (this.orderMetaData != null) {
            this.orderMetaData.initialise(clr, mmgr);
        }
        if (this.hasExtension("cascade-persist")) {
            final String cascadeValue = this.getValueForExtension("cascade-persist");
            if (cascadeValue.equalsIgnoreCase("true")) {
                this.cascadePersist = true;
            }
            else if (cascadeValue.equalsIgnoreCase("false")) {
                this.cascadePersist = false;
            }
        }
        if (this.hasExtension("cascade-update")) {
            final String cascadeValue = this.getValueForExtension("cascade-update");
            if (cascadeValue.equalsIgnoreCase("true")) {
                this.cascadeUpdate = true;
            }
            else if (cascadeValue.equalsIgnoreCase("false")) {
                this.cascadeUpdate = false;
            }
        }
        if (this.hasExtension("cascade-refresh")) {
            final String cascadeValue = this.getValueForExtension("cascade-refresh");
            if (cascadeValue.equalsIgnoreCase("true")) {
                this.cascadeRefresh = true;
            }
            else if (cascadeValue.equalsIgnoreCase("false")) {
                this.cascadeRefresh = false;
            }
        }
        this.setInitialised();
    }
    
    public boolean isFieldArrayTypePersistable(final MetaDataManager mmgr) {
        if (!this.type.isArray()) {
            return false;
        }
        if (mmgr.isEnhancing()) {
            final AbstractClassMetaData cmd = mmgr.readMetaDataForClass(this.type.getComponentType().getName());
            if (cmd != null && cmd instanceof ClassMetaData && cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                return true;
            }
        }
        return mmgr.getNucleusContext().getApiAdapter().isPersistable(this.type.getComponentType());
    }
    
    public boolean isStatic() {
        return this.isPopulated() && this.memberRepresented != null && Modifier.isStatic(this.memberRepresented.getModifiers());
    }
    
    public boolean isFinal() {
        return this.isPopulated() && this.memberRepresented != null && Modifier.isFinal(this.memberRepresented.getModifiers());
    }
    
    public boolean isTransient() {
        return this.isPopulated() && this.memberRepresented != null && Modifier.isTransient(this.memberRepresented.getModifiers());
    }
    
    public boolean isPublic() {
        return this.isPopulated() && this.memberRepresented != null && Modifier.isPublic(this.memberRepresented.getModifiers());
    }
    
    public boolean isProtected() {
        return this.isPopulated() && this.memberRepresented != null && Modifier.isProtected(this.memberRepresented.getModifiers());
    }
    
    public boolean isPrivate() {
        return this.isPopulated() && this.memberRepresented != null && Modifier.isPrivate(this.memberRepresented.getModifiers());
    }
    
    public boolean isAbstract() {
        return this.isPopulated() && this.memberRepresented != null && Modifier.isAbstract(this.memberRepresented.getModifiers());
    }
    
    public IdentityStrategy getValueStrategy() {
        return this.valueStrategy;
    }
    
    public void setValueStrategy(final IdentityStrategy valueStrategy) {
        this.valueStrategy = valueStrategy;
    }
    
    public void setValueStrategy(final String strategy) {
        this.valueStrategy = ((strategy == null) ? null : IdentityStrategy.getIdentityStrategy(strategy));
    }
    
    public String getValueGeneratorName() {
        return this.valueGeneratorName;
    }
    
    public String getSequence() {
        return this.sequence;
    }
    
    public void setSequence(final String sequence) {
        this.sequence = (StringUtils.isWhitespace(sequence) ? null : sequence);
    }
    
    public boolean isCacheable() {
        if (this.hasExtension("cacheable")) {
            return !this.getValueForExtension("cacheable").equalsIgnoreCase("false");
        }
        return this.cacheable;
    }
    
    public void setCacheable(final boolean cache) {
        this.cacheable = cache;
    }
    
    public String getLoadFetchGroup() {
        return this.loadFetchGroup;
    }
    
    public void setLoadFetchGroup(final String loadFetchGroup) {
        this.loadFetchGroup = loadFetchGroup;
    }
    
    public String getTypeConverterName() {
        if (this.hasExtension("type-converter-name")) {
            return this.getValueForExtension("type-converter-name");
        }
        return null;
    }
    
    public void setTypeConverterName(final String name) {
        this.addExtension("type-converter-name", name);
    }
    
    public boolean isTypeConversionDisabled() {
        return this.hasExtension("type-converter-disabled");
    }
    
    public void setTypeConverterDisabled() {
        this.addExtension("type-converter-disabled", "true");
    }
    
    public int getRecursionDepth() {
        return this.recursionDepth;
    }
    
    public void setRecursionDepth(final int depth) {
        this.recursionDepth = depth;
    }
    
    public void setRecursionDepth(final String depth) {
        if (!StringUtils.isWhitespace(depth)) {
            try {
                this.recursionDepth = Integer.parseInt(depth);
            }
            catch (NumberFormatException ex) {}
        }
    }
    
    public boolean fetchFKOnly() {
        if (this.hasExtension("fetch-fk-only")) {
            final String val = this.getValueForExtension("fetch-fk-only");
            return Boolean.valueOf(val);
        }
        return false;
    }
    
    protected static MetaData getOverallParentClassMetaData(final MetaData metadata) {
        if (metadata == null) {
            return null;
        }
        if (metadata instanceof AbstractClassMetaData) {
            return metadata;
        }
        return getOverallParentClassMetaData(metadata.getParent());
    }
    
    public AbstractClassMetaData getAbstractClassMetaData() {
        if (this.parent == null) {
            return null;
        }
        if (this.parent instanceof AbstractClassMetaData) {
            return (AbstractClassMetaData)this.parent;
        }
        if (this.parent instanceof EmbeddedMetaData) {
            return (AbstractClassMetaData)getOverallParentClassMetaData(this.parent.getParent().getParent());
        }
        return null;
    }
    
    public final OrderMetaData getOrderMetaData() {
        return this.orderMetaData;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getFullFieldName() {
        if (this.fullFieldName == null) {
            if (this.className != null) {
                this.fullFieldName = this.className + "." + this.name;
            }
            else {
                this.fullFieldName = this.getClassName(true) + "." + this.name;
            }
        }
        return this.fullFieldName;
    }
    
    public boolean fieldBelongsToClass() {
        return this.className == null;
    }
    
    public String getClassName() {
        return this.getClassName(true);
    }
    
    void setClassName(final String className) {
        this.className = className;
    }
    
    public String getClassName(final boolean fully_qualified) {
        if (this.className != null) {
            return this.className;
        }
        if (this.parent == null) {
            return null;
        }
        if (this.parent instanceof AbstractClassMetaData) {
            final AbstractClassMetaData cmd = (AbstractClassMetaData)this.parent;
            if (fully_qualified) {
                return cmd.getFullClassName();
            }
            return cmd.getName();
        }
        else {
            if (!(this.parent instanceof EmbeddedMetaData)) {
                if (this.parent instanceof UniqueMetaData) {
                    final MetaData grandparent = ((UniqueMetaData)this.parent).getParent();
                    if (grandparent instanceof AbstractClassMetaData) {
                        return ((AbstractClassMetaData)grandparent).getFullClassName();
                    }
                }
                return null;
            }
            final MetaData parentMd = ((EmbeddedMetaData)this.parent).getParent();
            if (parentMd instanceof AbstractMemberMetaData) {
                return ((AbstractMemberMetaData)parentMd).getTypeName();
            }
            if (parentMd instanceof ElementMetaData) {
                final AbstractMemberMetaData fmd = (AbstractMemberMetaData)((ElementMetaData)parentMd).getParent();
                return fmd.getCollection().getElementType();
            }
            if (parentMd instanceof KeyMetaData) {
                final AbstractMemberMetaData fmd = (AbstractMemberMetaData)((KeyMetaData)parentMd).getParent();
                return fmd.getMap().getKeyType();
            }
            if (parentMd instanceof ValueMetaData) {
                final AbstractMemberMetaData fmd = (AbstractMemberMetaData)((ValueMetaData)parentMd).getParent();
                return fmd.getMap().getValueType();
            }
            return null;
        }
    }
    
    public FieldPersistenceModifier getPersistenceModifier() {
        return this.persistenceModifier;
    }
    
    public void setPersistenceModifier(final FieldPersistenceModifier modifier) {
        this.persistenceModifier = modifier;
    }
    
    public void setNotPersistent() {
        this.persistenceModifier = FieldPersistenceModifier.NONE;
    }
    
    public void setTransactional() {
        this.persistenceModifier = FieldPersistenceModifier.TRANSACTIONAL;
    }
    
    public boolean isDefaultFetchGroup() {
        return this.defaultFetchGroup != null && this.defaultFetchGroup;
    }
    
    public void setDefaultFetchGroup(final boolean dfg) {
        this.defaultFetchGroup = dfg;
    }
    
    public boolean isDependent() {
        return this.dependent != null && this.dependent;
    }
    
    public void setDependent(final boolean dependent) {
        this.dependent = dependent;
    }
    
    public boolean isEmbedded() {
        return this.embedded != null && this.embedded;
    }
    
    public void setEmbedded(final boolean val) {
        this.embedded = val;
    }
    
    public boolean isSerialized() {
        return this.serialized != null && this.serialized;
    }
    
    public void setSerialised(final boolean flag) {
        this.serialized = flag;
    }
    
    public boolean isCascadePersist() {
        return this.cascadePersist;
    }
    
    public boolean isCascadeUpdate() {
        return this.cascadeUpdate;
    }
    
    public boolean isCascadeDelete() {
        return this.cascadeDelete;
    }
    
    public boolean isCascadeDetach() {
        return this.cascadeDetach;
    }
    
    public boolean isCascadeRefresh() {
        return this.cascadeRefresh;
    }
    
    public boolean isCascadeRemoveOrphans() {
        return this.cascadeRemoveOrphans;
    }
    
    public boolean isPrimaryKey() {
        return this.primaryKey != null && this.primaryKey;
    }
    
    public AbstractMemberMetaData setPrimaryKey(final boolean flag) {
        this.primaryKey = flag;
        if (this.primaryKey) {
            this.defaultFetchGroup = Boolean.TRUE;
        }
        return this;
    }
    
    public String getColumn() {
        return this.column;
    }
    
    public AbstractMemberMetaData setColumn(final String col) {
        this.column = (StringUtils.isWhitespace(col) ? null : col);
        return this;
    }
    
    public String getTable() {
        return this.table;
    }
    
    public AbstractMemberMetaData setTable(final String table) {
        this.table = (StringUtils.isWhitespace(table) ? null : table);
        return this;
    }
    
    public String getCatalog() {
        return this.catalog;
    }
    
    public AbstractMemberMetaData setCatalog(final String catalog) {
        this.catalog = (StringUtils.isWhitespace(catalog) ? null : catalog);
        return this;
    }
    
    public String getSchema() {
        return this.schema;
    }
    
    public AbstractMemberMetaData setSchema(final String schema) {
        this.schema = (StringUtils.isWhitespace(schema) ? null : schema);
        return this;
    }
    
    public boolean isUnique() {
        return this.uniqueConstraint;
    }
    
    public AbstractMemberMetaData setUnique(final String unique) {
        if (!StringUtils.isWhitespace(unique)) {
            this.uniqueConstraint = Boolean.parseBoolean(unique);
        }
        return this;
    }
    
    public AbstractMemberMetaData setUnique(final boolean unique) {
        this.uniqueConstraint = unique;
        return this;
    }
    
    public IndexedValue getIndexed() {
        return this.indexed;
    }
    
    public AbstractMemberMetaData setIndexed(final IndexedValue val) {
        this.indexed = val;
        return this;
    }
    
    public NullValue getNullValue() {
        return this.nullValue;
    }
    
    public AbstractMemberMetaData setNullValue(final NullValue val) {
        this.nullValue = val;
        return this;
    }
    
    public int getFieldId() {
        return this.fieldId;
    }
    
    public final String[] getFieldTypes() {
        return this.fieldTypes;
    }
    
    public void setFieldTypes(final String types) {
        if (!StringUtils.isWhitespace(types)) {
            this.fieldTypes = MetaDataUtils.getInstance().getValuesForCommaSeparatedAttribute(types);
        }
    }
    
    public int getAbsoluteFieldNumber() {
        if (this.className == null) {
            return this.fieldId + this.getAbstractClassMetaData().getNoOfInheritedManagedMembers();
        }
        return this.getAbstractClassMetaData().getAbsolutePositionOfMember(this.name);
    }
    
    public Member getMemberRepresented() {
        return this.memberRepresented;
    }
    
    public Class getType() {
        return this.type;
    }
    
    public String getTypeName() {
        if (this.type == null) {
            return null;
        }
        return this.type.getName();
    }
    
    public ContainerMetaData getContainer() {
        return this.containerMetaData;
    }
    
    public ArrayMetaData getArray() {
        if (this.containerMetaData != null && this.containerMetaData instanceof ArrayMetaData) {
            return (ArrayMetaData)this.containerMetaData;
        }
        return null;
    }
    
    public CollectionMetaData getCollection() {
        if (this.containerMetaData != null && this.containerMetaData instanceof CollectionMetaData) {
            return (CollectionMetaData)this.containerMetaData;
        }
        return null;
    }
    
    public MapMetaData getMap() {
        if (this.containerMetaData != null && this.containerMetaData instanceof MapMetaData) {
            return (MapMetaData)this.containerMetaData;
        }
        return null;
    }
    
    public final String getMappedBy() {
        return this.mappedBy;
    }
    
    public void setMappedBy(final String mappedBy) {
        this.mappedBy = (StringUtils.isWhitespace(mappedBy) ? null : mappedBy);
    }
    
    @Override
    public final ColumnMetaData[] getColumnMetaData() {
        return this.columnMetaData;
    }
    
    public final ElementMetaData getElementMetaData() {
        return this.elementMetaData;
    }
    
    public final KeyMetaData getKeyMetaData() {
        return this.keyMetaData;
    }
    
    public final ValueMetaData getValueMetaData() {
        return this.valueMetaData;
    }
    
    public final EmbeddedMetaData getEmbeddedMetaData() {
        return this.embeddedMetaData;
    }
    
    public void setDeleteAction(final String action) {
        if (action != null) {
            (this.foreignKeyMetaData = new ForeignKeyMetaData()).setDeleteAction(ForeignKeyAction.getForeignKeyAction(action));
        }
    }
    
    public final ForeignKeyMetaData getForeignKeyMetaData() {
        return this.foreignKeyMetaData;
    }
    
    public final IndexMetaData getIndexMetaData() {
        return this.indexMetaData;
    }
    
    public final UniqueMetaData getUniqueMetaData() {
        return this.uniqueMetaData;
    }
    
    public final JoinMetaData getJoinMetaData() {
        return this.joinMetaData;
    }
    
    @Override
    public void addColumn(final ColumnMetaData colmd) {
        this.columns.add(colmd);
        colmd.parent = this;
        this.columnMetaData = new ColumnMetaData[this.columns.size()];
        for (int i = 0; i < this.columnMetaData.length; ++i) {
            this.columnMetaData[i] = this.columns.get(i);
        }
    }
    
    public ColumnMetaData newColumnMetaData() {
        final ColumnMetaData colmd = new ColumnMetaData();
        this.addColumn(colmd);
        return colmd;
    }
    
    public boolean hasContainer() {
        return this.containerMetaData != null;
    }
    
    public boolean hasArray() {
        return this.containerMetaData != null && this.containerMetaData instanceof ArrayMetaData;
    }
    
    public boolean hasCollection() {
        return this.containerMetaData != null && this.containerMetaData instanceof CollectionMetaData;
    }
    
    public boolean hasMap() {
        return this.containerMetaData != null && this.containerMetaData instanceof MapMetaData;
    }
    
    public byte getPersistenceFlags() {
        return this.persistenceFlags;
    }
    
    public boolean isFieldToBePersisted() {
        if (this.isPopulated()) {
            if (this.isStatic()) {
                return false;
            }
            if (this.isFinal() && this instanceof FieldMetaData) {
                if (this.persistenceModifier == FieldPersistenceModifier.PERSISTENT) {
                    throw new InvalidMetaDataException(AbstractMemberMetaData.LOCALISER, "044118", this.getClassName(), this.getName());
                }
                return false;
            }
        }
        return this.persistenceModifier != null && !this.persistenceModifier.equals(FieldPersistenceModifier.NONE);
    }
    
    public void setOrdered() {
        this.ordered = true;
    }
    
    public void setTargetClassName(final String target) {
        if (!StringUtils.isWhitespace(target)) {
            this.targetClassName = target;
        }
    }
    
    public void setStoreInLob() {
        this.storeInLob = true;
    }
    
    public void setCascadePersist(final boolean cascade) {
        this.cascadePersist = cascade;
    }
    
    public void setCascadeUpdate(final boolean cascade) {
        this.cascadeUpdate = cascade;
    }
    
    public void setCascadeDelete(final boolean cascade) {
        this.cascadeDelete = cascade;
    }
    
    public void setCascadeDetach(final boolean cascade) {
        this.cascadeDetach = cascade;
    }
    
    public void setCascadeRefresh(final boolean cascade) {
        this.cascadeRefresh = cascade;
    }
    
    public void setCascadeRemoveOrphans(final boolean cascade) {
        this.cascadeRemoveOrphans = cascade;
    }
    
    public void setValueGeneratorName(final String generator) {
        if (StringUtils.isWhitespace(generator)) {
            this.valueGeneratorName = null;
        }
        else {
            this.valueGeneratorName = generator;
        }
    }
    
    public void setContainer(final ContainerMetaData conmd) {
        this.containerMetaData = conmd;
        this.containerMetaData.parent = this;
    }
    
    public CollectionMetaData newCollectionMetaData() {
        final CollectionMetaData collmd = new CollectionMetaData();
        this.setContainer(collmd);
        return collmd;
    }
    
    public ArrayMetaData newArrayMetaData() {
        final ArrayMetaData arrmd = new ArrayMetaData();
        this.setContainer(arrmd);
        return arrmd;
    }
    
    public MapMetaData newMapMetaData() {
        final MapMetaData mapmd = new MapMetaData();
        this.setContainer(mapmd);
        return mapmd;
    }
    
    public final void setElementMetaData(final ElementMetaData elementMetaData) {
        this.elementMetaData = elementMetaData;
        this.elementMetaData.parent = this;
    }
    
    public ElementMetaData newElementMetaData() {
        final ElementMetaData elemmd = new ElementMetaData();
        this.setElementMetaData(elemmd);
        return elemmd;
    }
    
    public final void setKeyMetaData(final KeyMetaData keyMetaData) {
        this.keyMetaData = keyMetaData;
        this.keyMetaData.parent = this;
    }
    
    public KeyMetaData newKeyMetaData() {
        final KeyMetaData keymd = new KeyMetaData();
        this.setKeyMetaData(keymd);
        return keymd;
    }
    
    public final void setValueMetaData(final ValueMetaData valueMetaData) {
        this.valueMetaData = valueMetaData;
        this.valueMetaData.parent = this;
    }
    
    public ValueMetaData newValueMetaData() {
        final ValueMetaData valuemd = new ValueMetaData();
        this.setValueMetaData(valuemd);
        return valuemd;
    }
    
    public final void setOrderMetaData(final OrderMetaData orderMetaData) {
        this.orderMetaData = orderMetaData;
        this.orderMetaData.parent = this;
    }
    
    public OrderMetaData newOrderMetaData() {
        final OrderMetaData ordermd = new OrderMetaData();
        this.setOrderMetaData(ordermd);
        return ordermd;
    }
    
    public final void setEmbeddedMetaData(final EmbeddedMetaData embeddedMetaData) {
        this.embeddedMetaData = embeddedMetaData;
        this.embeddedMetaData.parent = this;
    }
    
    public EmbeddedMetaData newEmbeddedMetaData() {
        final EmbeddedMetaData embmd = new EmbeddedMetaData();
        this.setEmbeddedMetaData(embmd);
        return embmd;
    }
    
    public final void setForeignKeyMetaData(final ForeignKeyMetaData foreignKeyMetaData) {
        this.foreignKeyMetaData = foreignKeyMetaData;
        this.foreignKeyMetaData.parent = this;
    }
    
    public ForeignKeyMetaData newForeignKeyMetaData() {
        final ForeignKeyMetaData fkmd = new ForeignKeyMetaData();
        this.setForeignKeyMetaData(fkmd);
        return fkmd;
    }
    
    public final void setIndexMetaData(final IndexMetaData indexMetaData) {
        this.indexMetaData = indexMetaData;
        this.indexMetaData.parent = this;
    }
    
    public IndexMetaData newIndexMetaData() {
        final IndexMetaData idxmd = new IndexMetaData();
        this.setIndexMetaData(idxmd);
        return idxmd;
    }
    
    public final void setUniqueMetaData(final UniqueMetaData uniqueMetaData) {
        this.uniqueMetaData = uniqueMetaData;
        this.uniqueMetaData.parent = this;
    }
    
    public UniqueMetaData newUniqueMetaData() {
        final UniqueMetaData unimd = new UniqueMetaData();
        this.setUniqueMetaData(unimd);
        return unimd;
    }
    
    public final void setJoinMetaData(final JoinMetaData joinMetaData) {
        this.joinMetaData = joinMetaData;
        this.joinMetaData.parent = this;
    }
    
    public JoinMetaData newJoinMetaData() {
        final JoinMetaData joinmd = new JoinMetaData();
        this.setJoinMetaData(joinmd);
        return joinmd;
    }
    
    public JoinMetaData newJoinMetadata() {
        final JoinMetaData joinmd = new JoinMetaData();
        this.setJoinMetaData(joinmd);
        return joinmd;
    }
    
    void setFieldId(final int field_id) {
        this.fieldId = field_id;
    }
    
    protected void setRelation(final ClassLoaderResolver clr) {
        if (this.relationType != null) {
            return;
        }
        final MetaDataManager mmgr = this.getAbstractClassMetaData().getPackageMetaData().getFileMetaData().metaDataManager;
        AbstractClassMetaData otherCmd = null;
        if (this.hasCollection()) {
            otherCmd = mmgr.getMetaDataForClass(this.getCollection().getElementType(), clr);
            if (otherCmd == null) {
                final Class elementCls = clr.classForName(this.getCollection().getElementType());
                if (ClassUtils.isReferenceType(elementCls)) {
                    try {
                        final String[] implNames = MetaDataUtils.getInstance().getImplementationNamesForReferenceField(this, 3, clr, mmgr);
                        if (implNames != null && implNames.length > 0) {
                            otherCmd = mmgr.getMetaDataForClass(implNames[0], clr);
                        }
                    }
                    catch (NucleusUserException jpe) {
                        if (!this.getCollection().isSerializedElement() && this.mappedBy != null) {
                            throw jpe;
                        }
                        NucleusLogger.METADATA.debug("Field " + this.getFullFieldName() + " is a collection of elements of reference type yet no implementation-classes are provided." + " Assuming they arent persistable");
                    }
                }
            }
        }
        else if (this.hasMap()) {
            otherCmd = ((MapMetaData)this.containerMetaData).getValueClassMetaData(clr, mmgr);
            if (otherCmd == null) {
                otherCmd = ((MapMetaData)this.containerMetaData).getKeyClassMetaData(clr, mmgr);
            }
            if (otherCmd == null) {}
        }
        else if (this.hasArray()) {
            otherCmd = ((ArrayMetaData)this.containerMetaData).getElementClassMetaData(clr, mmgr);
        }
        else if (this.getType().isInterface()) {
            try {
                final String[] implNames2 = MetaDataUtils.getInstance().getImplementationNamesForReferenceField(this, 2, clr, mmgr);
                if (implNames2 != null && implNames2.length > 0) {
                    otherCmd = mmgr.getMetaDataForClass(implNames2[0], clr);
                }
            }
            catch (NucleusUserException nue) {
                otherCmd = null;
            }
        }
        else if (this.getType().getName().equals(ClassNameConstants.Object) && this.fieldTypes != null && this.fieldTypes.length > 0) {
            otherCmd = mmgr.getMetaDataForClass(this.fieldTypes[0], clr);
        }
        else {
            otherCmd = mmgr.getMetaDataForClass(this.getType(), clr);
        }
        if (otherCmd == null) {
            if (this.hasArray() && this.getArray().mayContainPersistableElements()) {
                this.relatedMemberMetaData = null;
                this.relationType = RelationType.ONE_TO_MANY_UNI;
            }
            else {
                this.relatedMemberMetaData = null;
                this.relationType = RelationType.NONE;
            }
        }
        else if (this.mappedBy != null) {
            final AbstractMemberMetaData otherMmd = otherCmd.getMetaDataForMember(this.mappedBy);
            if (otherMmd == null) {
                throw new NucleusUserException(AbstractMemberMetaData.LOCALISER.msg("044115", this.getAbstractClassMetaData().getFullClassName(), this.name, this.mappedBy, otherCmd.getFullClassName())).setFatal();
            }
            this.relatedMemberMetaData = new AbstractMemberMetaData[] { otherMmd };
            if (this.hasContainer() && this.relatedMemberMetaData[0].hasContainer()) {
                this.relationType = RelationType.MANY_TO_MANY_BI;
            }
            else if (this.hasContainer() && !this.relatedMemberMetaData[0].hasContainer()) {
                this.relationType = RelationType.ONE_TO_MANY_BI;
            }
            else if (!this.hasContainer() && this.relatedMemberMetaData[0].hasContainer()) {
                this.relationType = RelationType.MANY_TO_ONE_BI;
            }
            else {
                this.relationType = RelationType.ONE_TO_ONE_BI;
            }
        }
        else {
            final int[] otherFieldNumbers = otherCmd.getAllMemberPositions();
            HashSet relatedFields = new HashSet();
            for (int i = 0; i < otherFieldNumbers.length; ++i) {
                final AbstractMemberMetaData otherFmd = otherCmd.getMetaDataForManagedMemberAtAbsolutePosition(otherFieldNumbers[i]);
                if (otherFmd.getMappedBy() != null && otherFmd.getMappedBy().equals(this.name)) {
                    if (otherFmd.hasContainer()) {
                        if ((otherFmd.hasCollection() && otherFmd.getCollection().getElementType().equals(this.getClassName(true))) || (otherFmd.hasArray() && otherFmd.getArray().getElementType().equals(this.getClassName(true))) || (otherFmd.hasMap() && otherFmd.getMap().getKeyType().equals(this.getClassName(true))) || (otherFmd.hasMap() && otherFmd.getMap().getValueType().equals(this.getClassName(true)))) {
                            relatedFields.add(otherFmd);
                            if (this.hasContainer()) {
                                this.relationType = RelationType.MANY_TO_MANY_BI;
                            }
                            else {
                                this.relationType = RelationType.MANY_TO_ONE_BI;
                            }
                        }
                        else {
                            String elementType = null;
                            if (otherFmd.hasCollection()) {
                                elementType = otherFmd.getCollection().getElementType();
                            }
                            else if (otherFmd.hasArray()) {
                                elementType = otherFmd.getArray().getElementType();
                            }
                            if (elementType != null) {
                                final Class elementCls2 = clr.classForName(elementType);
                                if (elementCls2.isInterface()) {
                                    final Class thisCls = clr.classForName(this.getClassName(true));
                                    if (elementCls2.isAssignableFrom(thisCls)) {
                                        relatedFields.add(otherFmd);
                                        if (this.hasContainer()) {
                                            this.relationType = RelationType.MANY_TO_MANY_BI;
                                        }
                                        else {
                                            this.relationType = RelationType.MANY_TO_ONE_BI;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        final Class cls = clr.classForName(this.getClassName(true));
                        if (otherFmd.getType().isAssignableFrom(cls) || cls.isAssignableFrom(otherFmd.getType())) {
                            relatedFields.add(otherFmd);
                            if (this.hasContainer()) {
                                this.relationType = RelationType.ONE_TO_MANY_BI;
                            }
                            else {
                                this.relationType = RelationType.ONE_TO_ONE_BI;
                            }
                        }
                    }
                }
            }
            if (relatedFields.size() > 0) {
                this.relatedMemberMetaData = (AbstractMemberMetaData[])relatedFields.toArray(new AbstractMemberMetaData[relatedFields.size()]);
                relatedFields.clear();
                relatedFields = null;
            }
            else if (this.hasContainer()) {
                this.relationType = RelationType.ONE_TO_MANY_UNI;
            }
            else if (this.joinMetaData != null) {
                this.relationType = RelationType.MANY_TO_ONE_UNI;
            }
            else {
                this.relationType = RelationType.ONE_TO_ONE_UNI;
            }
        }
    }
    
    public RelationType getRelationType(final ClassLoaderResolver clr) {
        if (this.relationType == null) {
            this.setRelation(clr);
        }
        return this.relationType;
    }
    
    public boolean isPersistentInterface(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.hasCollection()) {
            if (mmgr.isPersistentInterface(this.getCollection().getElementType())) {
                return true;
            }
        }
        else if (this.hasMap()) {
            if (mmgr.isPersistentInterface(this.getMap().getKeyType())) {
                return true;
            }
            if (mmgr.isPersistentInterface(this.getMap().getValueType())) {
                return true;
            }
        }
        else if (this.hasArray()) {
            if (mmgr.isPersistentInterface(this.getArray().getElementType())) {
                return true;
            }
        }
        else if (this.getType().isInterface()) {
            if (mmgr.isPersistentInterface(this.getTypeName())) {
                return true;
            }
            if (this.fieldTypes != null && mmgr.isPersistentInterface(this.fieldTypes[0])) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isRelationOwner(final ClassLoaderResolver clr) {
        if (this.relationType == null) {
            this.setRelation(clr);
        }
        if (this.relationType == RelationType.NONE) {
            return true;
        }
        if (this.relationType == RelationType.ONE_TO_MANY_UNI || this.relationType == RelationType.ONE_TO_ONE_UNI) {
            return true;
        }
        if (this.relationType == RelationType.MANY_TO_MANY_BI || this.relationType == RelationType.MANY_TO_ONE_BI || this.relationType == RelationType.ONE_TO_MANY_BI || this.relationType == RelationType.ONE_TO_ONE_BI) {
            return this.mappedBy == null;
        }
        return this.relationType == RelationType.MANY_TO_ONE_UNI;
    }
    
    public AbstractMemberMetaData[] getRelatedMemberMetaData(final ClassLoaderResolver clr) {
        if (this.relationType == null) {
            this.setRelation(clr);
        }
        return this.relatedMemberMetaData;
    }
    
    public AbstractMemberMetaData getRelatedMemberMetaDataForObject(final ClassLoaderResolver clr, final Object thisPC, final Object otherPC) {
        if (this.relationType == null) {
            this.setRelation(clr);
        }
        if (this.relatedMemberMetaData == null) {
            return null;
        }
        for (int i = 0; i < this.relatedMemberMetaData.length; ++i) {
            if (this.relationType == RelationType.ONE_TO_ONE_BI) {
                if (this.relatedMemberMetaData[i].getType().isAssignableFrom(thisPC.getClass()) && this.getType().isAssignableFrom(otherPC.getClass())) {
                    return this.relatedMemberMetaData[i];
                }
            }
            else if (this.relationType == RelationType.MANY_TO_ONE_BI) {
                if (this.relatedMemberMetaData[i].hasCollection()) {
                    final Class elementType = clr.classForName(this.relatedMemberMetaData[i].getCollection().getElementType());
                    if (elementType.isAssignableFrom(thisPC.getClass()) && this.getType().isAssignableFrom(otherPC.getClass())) {
                        return this.relatedMemberMetaData[i];
                    }
                }
                else if (this.relatedMemberMetaData[i].hasMap()) {
                    final Class valueType = clr.classForName(this.relatedMemberMetaData[i].getMap().getValueType());
                    if (valueType.isAssignableFrom(thisPC.getClass()) && this.getType().isAssignableFrom(otherPC.getClass())) {
                        return this.relatedMemberMetaData[i];
                    }
                    final Class keyType = clr.classForName(this.relatedMemberMetaData[i].getMap().getKeyType());
                    if (keyType.isAssignableFrom(thisPC.getClass()) && this.getType().isAssignableFrom(otherPC.getClass())) {
                        return this.relatedMemberMetaData[i];
                    }
                }
            }
        }
        return null;
    }
    
    void getReferencedClassMetaData(final List orderedCMDs, final Set referencedCMDs, final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        final AbstractClassMetaData type_cmd = mmgr.getMetaDataForClass(this.getType(), clr);
        if (type_cmd != null) {
            type_cmd.getReferencedClassMetaData(orderedCMDs, referencedCMDs, clr, mmgr);
        }
        if (this.containerMetaData != null) {
            if (this.containerMetaData instanceof CollectionMetaData) {
                ((CollectionMetaData)this.containerMetaData).getReferencedClassMetaData(orderedCMDs, referencedCMDs, clr, mmgr);
            }
            else if (this.containerMetaData instanceof MapMetaData) {
                ((MapMetaData)this.containerMetaData).getReferencedClassMetaData(orderedCMDs, referencedCMDs, clr, mmgr);
            }
            else if (this.containerMetaData instanceof ArrayMetaData) {
                ((ArrayMetaData)this.containerMetaData).getReferencedClassMetaData(orderedCMDs, referencedCMDs, clr, mmgr);
            }
        }
    }
    
    public boolean calcIsSecondClassMutable(final MetaDataManager mmgr) {
        if (this.hasExtension("is-second-class")) {
            final String isSecondClass = this.getValueForExtension("is-second-class");
            if (isSecondClass.equalsIgnoreCase("true")) {
                return true;
            }
            if (isSecondClass.equalsIgnoreCase("false")) {
                return false;
            }
            if (!isSecondClass.equalsIgnoreCase("default")) {
                throw new InvalidMetaDataException(AbstractMemberMetaData.LOCALISER, "044002", "is-second-class", "true/false/default", isSecondClass);
            }
        }
        return mmgr.getNucleusContext().getTypeManager().isSecondClassMutableType(this.getTypeName());
    }
    
    public boolean isInsertable() {
        if (this.hasCollection() || this.hasArray()) {
            if (this.elementMetaData != null && this.elementMetaData.getColumnMetaData() != null && this.elementMetaData.getColumnMetaData().length > 0) {
                return this.elementMetaData.getColumnMetaData()[0].getInsertable();
            }
        }
        else {
            if (this.hasMap()) {
                return true;
            }
            if (this.columnMetaData != null && this.columnMetaData.length > 0) {
                return this.columnMetaData[0].getInsertable();
            }
        }
        return true;
    }
    
    public boolean isUpdateable() {
        if (this.hasCollection() || this.hasArray()) {
            if (this.elementMetaData != null && this.elementMetaData.getColumnMetaData() != null && this.elementMetaData.getColumnMetaData().length > 0) {
                return this.elementMetaData.getColumnMetaData()[0].getUpdateable();
            }
        }
        else {
            if (this.hasMap()) {
                return true;
            }
            if (this.columnMetaData != null && this.columnMetaData.length > 0) {
                return this.columnMetaData[0].getUpdateable();
            }
        }
        return true;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        return super.toString(prefix, indent);
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o instanceof AbstractMemberMetaData) {
            final AbstractMemberMetaData c = (AbstractMemberMetaData)o;
            return this.name.compareTo(c.name);
        }
        if (o instanceof String) {
            return this.name.compareTo((String)o);
        }
        if (o == null) {
            throw new ClassCastException("object is null");
        }
        throw new ClassCastException(this.getClass().getName() + " != " + o.getClass().getName());
    }
}
