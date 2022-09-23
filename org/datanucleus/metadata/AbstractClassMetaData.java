// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.ViewUtils;
import org.datanucleus.util.MacroString;
import java.util.HashMap;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.StoreManager;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.exceptions.NucleusException;
import java.util.Iterator;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.util.StringUtils;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import org.datanucleus.util.Localiser;

public abstract class AbstractClassMetaData extends MetaData
{
    protected static final Localiser LOCALISER_API;
    public static final String GENERATED_PK_SUFFIX = "_PK";
    protected final String name;
    protected String entityName;
    protected boolean mappedSuperclass;
    protected boolean instantiable;
    protected IdentityType identityType;
    protected ClassPersistenceModifier persistenceModifier;
    protected String persistableSuperclass;
    protected String objectidClass;
    protected boolean requiresExtent;
    protected boolean detachable;
    protected boolean embeddedOnly;
    protected String catalog;
    protected String schema;
    protected String table;
    protected Boolean cacheable;
    protected final String fullName;
    protected VersionMetaData versionMetaData;
    protected IdentityMetaData identityMetaData;
    protected boolean identitySpecified;
    protected InheritanceMetaData inheritanceMetaData;
    protected PrimaryKeyMetaData primaryKeyMetaData;
    protected List listeners;
    protected Boolean excludeSuperClassListeners;
    protected Boolean excludeDefaultListeners;
    protected Map<String, FetchGroupMetaData> fetchGroupMetaDataByName;
    protected AbstractClassMetaData pcSuperclassMetaData;
    protected boolean metaDataComplete;
    protected boolean serializeRead;
    protected Collection<QueryMetaData> queries;
    protected Collection<StoredProcQueryMetaData> storedProcQueries;
    protected Collection<QueryResultMetaData> queryResultMetaData;
    protected JoinMetaData[] joinMetaData;
    protected IndexMetaData[] indexMetaData;
    protected ForeignKeyMetaData[] foreignKeyMetaData;
    protected UniqueMetaData[] uniqueMetaData;
    protected List members;
    protected List<ColumnMetaData> unmappedColumns;
    protected Set<FetchGroupMetaData> fetchGroups;
    protected List<JoinMetaData> joins;
    protected List<ForeignKeyMetaData> foreignKeys;
    protected List<IndexMetaData> indexes;
    protected List<UniqueMetaData> uniqueConstraints;
    protected AbstractMemberMetaData[] managedMembers;
    protected AbstractMemberMetaData[] overriddenMembers;
    protected Map<String, Integer> memberPositionsByName;
    protected int[] allMemberPositions;
    protected int[] pkMemberPositions;
    protected int[] nonPkMemberPositions;
    protected boolean[] nonPkMemberFlags;
    protected int[] dfgMemberPositions;
    protected boolean[] dfgMemberFlags;
    protected int[] scoMutableMemberPositions;
    protected boolean[] scoMutableMemberFlags;
    protected int[] scoNonContainerMemberPositions;
    protected int[] relationPositions;
    protected int noOfInheritedManagedMembers;
    protected boolean usesSingleFieldIdentityClass;
    protected int memberCount;
    protected boolean implementationOfPersistentDefinition;
    boolean populating;
    boolean initialising;
    protected Boolean fetchGroupMetaWithPostLoad;
    protected Boolean pkIsDatastoreAttributed;
    protected Boolean hasRelations;
    protected transient boolean persistentInterfaceImplNeedingTableFromSuperclass;
    protected transient boolean persistentInterfaceImplNeedingTableFromSubclass;
    protected int[] secondClassContainerMemberPositions;
    
    protected AbstractClassMetaData(final PackageMetaData parent, final String name) {
        super(parent);
        this.mappedSuperclass = false;
        this.instantiable = true;
        this.identityType = IdentityType.DATASTORE;
        this.persistenceModifier = ClassPersistenceModifier.PERSISTENCE_CAPABLE;
        this.requiresExtent = true;
        this.detachable = false;
        this.embeddedOnly = false;
        this.cacheable = null;
        this.identitySpecified = false;
        this.listeners = null;
        this.excludeSuperClassListeners = null;
        this.excludeDefaultListeners = null;
        this.pcSuperclassMetaData = null;
        this.metaDataComplete = false;
        this.serializeRead = false;
        this.queries = null;
        this.storedProcQueries = null;
        this.queryResultMetaData = null;
        this.members = new ArrayList();
        this.unmappedColumns = null;
        this.fetchGroups = new HashSet<FetchGroupMetaData>();
        this.joins = new ArrayList<JoinMetaData>();
        this.foreignKeys = new ArrayList<ForeignKeyMetaData>();
        this.indexes = new ArrayList<IndexMetaData>();
        this.uniqueConstraints = new ArrayList<UniqueMetaData>();
        this.scoNonContainerMemberPositions = null;
        this.relationPositions = null;
        this.noOfInheritedManagedMembers = 0;
        this.implementationOfPersistentDefinition = false;
        this.populating = false;
        this.initialising = false;
        this.pkIsDatastoreAttributed = null;
        this.hasRelations = null;
        this.persistentInterfaceImplNeedingTableFromSuperclass = false;
        this.persistentInterfaceImplNeedingTableFromSubclass = false;
        this.secondClassContainerMemberPositions = null;
        if (StringUtils.isWhitespace(name)) {
            throw new InvalidMetaDataException(AbstractClassMetaData.LOCALISER, "044061", parent.name);
        }
        this.name = name;
        this.fullName = ClassUtils.createFullClassName(parent.name, name);
    }
    
    public AbstractClassMetaData(final InterfaceMetaData imd, final String implClassName, final boolean copyMembers) {
        this((PackageMetaData)imd.parent, implClassName);
        this.setMappedSuperclass(imd.mappedSuperclass);
        this.setRequiresExtent(imd.requiresExtent);
        this.setDetachable(imd.detachable);
        this.setTable(imd.table);
        this.setCatalog(imd.catalog);
        this.setSchema(imd.schema);
        this.setEntityName(imd.entityName);
        this.setObjectIdClass(imd.objectidClass);
        this.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);
        this.setEmbeddedOnly(imd.embeddedOnly);
        this.setIdentityType(imd.identityType);
        this.implementationOfPersistentDefinition = true;
        if (copyMembers) {
            this.copyMembersFromInterface(imd);
        }
        this.setVersionMetaData(imd.versionMetaData);
        this.setIdentityMetaData(imd.identityMetaData);
        this.setPrimaryKeyMetaData(imd.primaryKeyMetaData);
        if (imd.inheritanceMetaData != null) {
            if (imd.inheritanceMetaData.getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
                this.persistentInterfaceImplNeedingTableFromSuperclass = true;
            }
            else if (imd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                this.persistentInterfaceImplNeedingTableFromSubclass = true;
            }
            final InheritanceMetaData inhmd = new InheritanceMetaData();
            inhmd.setStrategy(InheritanceStrategy.NEW_TABLE);
            if (imd.inheritanceMetaData.getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
                for (AbstractClassMetaData acmd = imd.getSuperAbstractClassMetaData(); acmd != null; acmd = acmd.getSuperAbstractClassMetaData()) {
                    if (acmd.getInheritanceMetaData() != null && acmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.NEW_TABLE) {
                        if (acmd.getInheritanceMetaData().getDiscriminatorMetaData() != null) {
                            inhmd.setDiscriminatorMetaData(new DiscriminatorMetaData(acmd.getInheritanceMetaData().getDiscriminatorMetaData()));
                        }
                        inhmd.setJoinMetaData(acmd.getInheritanceMetaData().getJoinMetaData());
                        break;
                    }
                }
            }
            else if (imd.inheritanceMetaData.getStrategy() == InheritanceStrategy.NEW_TABLE) {
                if (imd.getInheritanceMetaData().getDiscriminatorMetaData() != null) {
                    inhmd.setDiscriminatorMetaData(new DiscriminatorMetaData(imd.getInheritanceMetaData().getDiscriminatorMetaData()));
                }
                inhmd.setJoinMetaData(imd.getInheritanceMetaData().getJoinMetaData());
            }
            this.setInheritanceMetaData(inhmd);
        }
        if (imd.joinMetaData != null) {
            for (int i = 0; i < imd.joinMetaData.length; ++i) {
                this.addJoin(imd.joinMetaData[i]);
            }
        }
        if (imd.foreignKeyMetaData != null) {
            for (int i = 0; i < imd.foreignKeyMetaData.length; ++i) {
                this.addForeignKey(imd.foreignKeyMetaData[i]);
            }
        }
        if (imd.indexMetaData != null) {
            for (int i = 0; i < imd.indexMetaData.length; ++i) {
                this.addIndex(imd.indexMetaData[i]);
            }
        }
        if (imd.uniqueMetaData != null) {
            for (int i = 0; i < imd.uniqueMetaData.length; ++i) {
                this.addUniqueConstraint(imd.uniqueMetaData[i]);
            }
        }
        if (imd.fetchGroups != null) {
            for (final FetchGroupMetaData fgmd : imd.fetchGroups) {
                this.addFetchGroup(fgmd);
            }
        }
        if (imd.queries != null) {
            for (final QueryMetaData query : imd.queries) {
                this.addQuery(query);
            }
        }
        if (imd.storedProcQueries != null) {
            for (final StoredProcQueryMetaData query2 : imd.storedProcQueries) {
                this.addStoredProcQuery(query2);
            }
        }
        if (imd.listeners != null) {
            if (this.listeners == null) {
                this.listeners = new ArrayList();
            }
            this.listeners.addAll(imd.listeners);
        }
    }
    
    public AbstractClassMetaData(final ClassMetaData cmd, final String implClassName) {
        this((PackageMetaData)cmd.parent, implClassName);
        this.setMappedSuperclass(cmd.mappedSuperclass);
        this.setRequiresExtent(cmd.requiresExtent);
        this.setDetachable(cmd.detachable);
        this.setCatalog(cmd.catalog);
        this.setSchema(cmd.schema);
        this.setTable(cmd.table);
        this.setEntityName(cmd.entityName);
        this.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);
        this.setEmbeddedOnly(cmd.embeddedOnly);
        this.setIdentityType(cmd.identityType);
        this.setPersistenceCapableSuperclass(cmd.getFullClassName());
        this.implementationOfPersistentDefinition = true;
        for (int i = 0; i < cmd.getMemberCount(); ++i) {
            final FieldMetaData fmd = new FieldMetaData(this, cmd.getMetaDataForManagedMemberAtAbsolutePosition(i));
            fmd.persistenceModifier = FieldPersistenceModifier.NONE;
            fmd.primaryKey = Boolean.FALSE;
            fmd.defaultFetchGroup = Boolean.FALSE;
            this.addMember(fmd);
        }
    }
    
    public boolean isInstantiable() {
        return this.instantiable;
    }
    
    protected AbstractClassMetaData getRootInstantiableClass() {
        if (this.pcSuperclassMetaData != null) {
            final AbstractClassMetaData rootCmd = this.pcSuperclassMetaData.getRootInstantiableClass();
            return (rootCmd == null && this.instantiable) ? this : rootCmd;
        }
        if (this.instantiable) {
            return this;
        }
        return null;
    }
    
    public boolean isRootInstantiableClass() {
        return this.getRootInstantiableClass() == this;
    }
    
    public boolean isImplementationOfPersistentDefinition() {
        return this.implementationOfPersistentDefinition;
    }
    
    protected void copyMembersFromInterface(final InterfaceMetaData imd) {
        for (int i = 0; i < imd.getMemberCount(); ++i) {
            final FieldMetaData fmd = new FieldMetaData(this, imd.getMetaDataForManagedMemberAtAbsolutePosition(i));
            this.addMember(fmd);
        }
    }
    
    protected void checkInitialised() {
        if (!this.isInitialised()) {
            throw new NucleusException(AbstractClassMetaData.LOCALISER.msg("044069", this.fullName)).setFatal();
        }
    }
    
    protected void checkPopulated() {
        if (!this.isPopulated() && !this.isInitialised()) {
            throw new NucleusException(AbstractClassMetaData.LOCALISER.msg("044070", this.fullName)).setFatal();
        }
    }
    
    protected void checkNotYetPopulated() {
        if (this.isPopulated() || this.isInitialised()) {
            throw new NucleusUserException("Already populated/initialised");
        }
    }
    
    protected Class loadClass(ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        if (clr == null) {
            NucleusLogger.METADATA.warn(AbstractClassMetaData.LOCALISER.msg("044067", this.fullName));
            clr = mmgr.getNucleusContext().getClassLoaderResolver(null);
        }
        Class cls;
        try {
            cls = clr.classForName(this.fullName, primary, false);
            if (cls == null) {
                NucleusLogger.METADATA.error(AbstractClassMetaData.LOCALISER.msg("044080", this.fullName));
                throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044080", this.fullName);
            }
        }
        catch (ClassNotResolvedException cnre) {
            NucleusLogger.METADATA.error(AbstractClassMetaData.LOCALISER.msg("044080", this.fullName));
            final NucleusException ne = new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044080", this.fullName);
            ne.setNestedException(cnre);
            throw ne;
        }
        return cls;
    }
    
    protected void determineIdentity() {
        if (this.identityType == null) {
            if (this.objectidClass != null) {
                this.identityType = IdentityType.APPLICATION;
            }
            else {
                int noOfPkKeys = 0;
                for (final AbstractMemberMetaData mmd : this.members) {
                    if (mmd.isPrimaryKey()) {
                        ++noOfPkKeys;
                    }
                }
                if (noOfPkKeys > 0) {
                    this.identityType = IdentityType.APPLICATION;
                }
                else {
                    this.identityType = IdentityType.DATASTORE;
                }
            }
        }
    }
    
    protected void determineSuperClassName(final ClassLoaderResolver clr, final Class cls, final MetaDataManager mmgr) {
        if (this.persistableSuperclass != null) {
            this.persistableSuperclass = ClassUtils.createFullClassName(((PackageMetaData)this.parent).name, this.persistableSuperclass);
        }
        String realPcSuperclassName = null;
        Collection<Class<?>> superclasses;
        if (cls.isInterface()) {
            superclasses = ClassUtils.getSuperinterfaces(cls);
        }
        else {
            superclasses = ClassUtils.getSuperclasses(cls);
        }
        for (final Class<?> superclass : superclasses) {
            final AbstractClassMetaData superCmd = mmgr.getMetaDataForClassInternal(superclass, clr);
            if (superCmd != null && superCmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                realPcSuperclassName = superclass.getName();
                break;
            }
        }
        if (this.persistableSuperclass == null || this.persistableSuperclass.equals(realPcSuperclassName)) {
            if (this.persistableSuperclass == null && realPcSuperclassName != null) {
                this.persistableSuperclass = realPcSuperclassName;
                if (NucleusLogger.METADATA.isDebugEnabled()) {
                    NucleusLogger.METADATA.debug(AbstractClassMetaData.LOCALISER.msg("044089", this.fullName, this.persistableSuperclass));
                }
            }
            this.validateSuperClass(clr, cls, superclasses, mmgr);
            if (this.persistableSuperclass != null && !this.isDetachable() && this.pcSuperclassMetaData.isDetachable()) {
                this.detachable = true;
            }
            return;
        }
        try {
            final AbstractClassMetaData superCmd2 = mmgr.getMetaDataForClassInternal(clr.classForName(this.persistableSuperclass), clr);
            if (superCmd2 == null || superCmd2.getPersistenceModifier() != ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044083", this.fullName, this.persistableSuperclass);
            }
        }
        catch (ClassNotResolvedException cnre) {
            throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044088", this.fullName, this.persistableSuperclass);
        }
        if (realPcSuperclassName != null) {
            throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044087", this.fullName, realPcSuperclassName, this.persistableSuperclass);
        }
        throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044082", this.fullName, this.persistableSuperclass);
    }
    
    private void validateSuperClass(final ClassLoaderResolver clr, final Class cls, final Collection<Class<?>> superclasses, final MetaDataManager mmgr) {
        if (this.persistableSuperclass != null) {
            Class pcsc = null;
            try {
                pcsc = clr.classForName(this.persistableSuperclass);
            }
            catch (ClassNotResolvedException cnre) {
                throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044081", this.fullName, this.persistableSuperclass);
            }
            if (this.persistableSuperclass.equals(this.fullName) || !pcsc.isAssignableFrom(cls)) {
                throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044082", this.fullName, this.persistableSuperclass);
            }
            if (mmgr != null) {
                this.pcSuperclassMetaData = mmgr.getMetaDataForClassInternal(pcsc, clr);
                if (this.pcSuperclassMetaData == null) {
                    throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044083", this.fullName, this.persistableSuperclass);
                }
            }
            else {
                final String superclass_pkg_name = this.persistableSuperclass.substring(0, this.persistableSuperclass.lastIndexOf(46));
                final PackageMetaData pmd = this.getPackageMetaData().getFileMetaData().getPackage(superclass_pkg_name);
                if (pmd != null) {
                    final String superclass_class_name = this.persistableSuperclass.substring(this.persistableSuperclass.lastIndexOf(46) + 1);
                    this.pcSuperclassMetaData = pmd.getClass(superclass_class_name);
                }
            }
            if (this.pcSuperclassMetaData == null) {
                throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044084", this.fullName, this.persistableSuperclass);
            }
            if (!this.pcSuperclassMetaData.isPopulated() && !this.pcSuperclassMetaData.isInitialised()) {
                this.pcSuperclassMetaData.populate(clr, cls.getClassLoader(), mmgr);
            }
        }
        else {
            for (final Class<?> superclass : superclasses) {
                final AbstractClassMetaData superCmd = mmgr.getMetaDataForClassInternal(superclass, clr);
                if (superCmd != null && superCmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                    throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044091", this.fullName, superclass.getName());
                }
            }
        }
    }
    
    protected void validateUserInputForIdentity() {
        if (this.pcSuperclassMetaData != null) {
            final AbstractClassMetaData baseCmd = this.getBaseAbstractClassMetaData();
            final IdentityMetaData baseImd = baseCmd.getIdentityMetaData();
            if (baseCmd.identitySpecified && this.identitySpecified && baseImd != null && baseImd.getValueStrategy() != null && this.identityMetaData != null && this.identityMetaData.getValueStrategy() != null && this.identityMetaData.getValueStrategy() != baseImd.getValueStrategy() && this.identityMetaData.getValueStrategy() != null && this.identityMetaData.getValueStrategy() != IdentityStrategy.NATIVE) {
                throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044094", this.fullName, this.identityMetaData.getValueStrategy(), baseImd.getValueStrategy());
            }
            if (baseCmd.identitySpecified && this.identityMetaData != null && baseImd.getValueStrategy() != this.identityMetaData.getValueStrategy()) {
                this.identityMetaData.setValueStrategy(baseImd.getValueStrategy());
            }
        }
    }
    
    public AbstractClassMetaData getBaseAbstractClassMetaData() {
        if (this.pcSuperclassMetaData != null) {
            return this.pcSuperclassMetaData.getBaseAbstractClassMetaData();
        }
        return this;
    }
    
    public boolean isDescendantOf(final AbstractClassMetaData cmd) {
        return this.pcSuperclassMetaData != null && (this.pcSuperclassMetaData == cmd || this.pcSuperclassMetaData.isDescendantOf(cmd));
    }
    
    protected String getBaseInheritanceStrategy() {
        if (this.inheritanceMetaData != null && this.inheritanceMetaData.getStrategyForTree() != null) {
            return this.inheritanceMetaData.getStrategyForTree();
        }
        if (this.pcSuperclassMetaData != null) {
            return this.pcSuperclassMetaData.getBaseInheritanceStrategy();
        }
        return null;
    }
    
    protected void inheritIdentity() {
        if (this.objectidClass != null) {
            this.objectidClass = ClassUtils.createFullClassName(((PackageMetaData)this.parent).name, this.objectidClass);
        }
        if (this.persistableSuperclass != null) {
            if (this.objectidClass != null) {
                final String superObjectIdClass = this.pcSuperclassMetaData.getObjectidClass();
                if (superObjectIdClass == null || !this.objectidClass.equals(superObjectIdClass)) {
                    throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044085", this.fullName, this.persistableSuperclass);
                }
                NucleusLogger.METADATA.warn(AbstractClassMetaData.LOCALISER.msg("044086", this.name, this.persistableSuperclass));
            }
            else {
                this.objectidClass = this.pcSuperclassMetaData.getObjectidClass();
            }
            if (this.identityType == null) {
                this.identityType = this.pcSuperclassMetaData.getIdentityType();
            }
            if (this.identityType != null && !this.identityType.equals(this.pcSuperclassMetaData.getIdentityType())) {
                throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044093", this.fullName);
            }
        }
    }
    
    protected AbstractMemberMetaData newDefaultedProperty(final String name) {
        return new PropertyMetaData(this, name);
    }
    
    protected void validateUserInputForInheritanceMetaData(final boolean isAbstract) {
        if (this.mappedSuperclass) {
            final String baseInhStrategy = this.getBaseInheritanceStrategy();
            if (baseInhStrategy != null && baseInhStrategy.equalsIgnoreCase("SINGLE_TABLE") && this.getSuperclassManagingTable() != null && this.inheritanceMetaData != null) {
                this.inheritanceMetaData.setStrategy(InheritanceStrategy.SUPERCLASS_TABLE);
            }
        }
        if (this.inheritanceMetaData != null) {
            if (this.inheritanceMetaData.getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
                final AbstractClassMetaData superCmd = this.getClassManagingTable();
                if (superCmd == null) {
                    throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044099", this.fullName);
                }
                final DiscriminatorMetaData superDismd = superCmd.getInheritanceMetaData().getDiscriminatorMetaData();
                if (superDismd == null) {
                    throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044100", this.fullName, superCmd.fullName);
                }
                final DiscriminatorMetaData dismd = this.inheritanceMetaData.getDiscriminatorMetaData();
                if (superDismd.getStrategy() == DiscriminatorStrategy.VALUE_MAP && (dismd == null || dismd.getValue() == null) && !this.mappedSuperclass && !isAbstract) {
                    throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044102", this.fullName, superCmd.fullName, superDismd.getColumnName());
                }
            }
            if (isAbstract) {
                final DiscriminatorMetaData dismd2 = this.inheritanceMetaData.getDiscriminatorMetaData();
                if (dismd2 != null && !StringUtils.isWhitespace(dismd2.getValue())) {
                    NucleusLogger.METADATA.warn(AbstractClassMetaData.LOCALISER.msg("044105", this.fullName));
                }
            }
            else {
                final DiscriminatorMetaData dismd2 = this.inheritanceMetaData.getDiscriminatorMetaData();
                if (dismd2 != null && dismd2.getColumnMetaData() != null && this.pcSuperclassMetaData != null) {
                    final ColumnMetaData superDiscrimColmd = this.pcSuperclassMetaData.getDiscriminatorColumnMetaData();
                    if (superDiscrimColmd != null) {
                        NucleusLogger.GENERAL.debug(AbstractClassMetaData.LOCALISER.msg("044126", this.fullName));
                    }
                }
            }
        }
    }
    
    protected void determineInheritanceMetaData(final MetaDataManager mmgr) {
        if (this.inheritanceMetaData == null) {
            if (this.pcSuperclassMetaData != null) {
                final AbstractClassMetaData baseCmd = this.getBaseAbstractClassMetaData();
                if (this.getBaseInheritanceStrategy() != null) {
                    final String treeStrategy = this.getBaseInheritanceStrategy();
                    if (treeStrategy.equals("JOINED")) {
                        (this.inheritanceMetaData = new InheritanceMetaData()).setStrategy(InheritanceStrategy.NEW_TABLE);
                        return;
                    }
                    if (treeStrategy.equals("SINGLE_TABLE")) {
                        (this.inheritanceMetaData = new InheritanceMetaData()).setStrategy(InheritanceStrategy.SUPERCLASS_TABLE);
                        return;
                    }
                    if (treeStrategy.equals("TABLE_PER_CLASS")) {
                        (this.inheritanceMetaData = new InheritanceMetaData()).setStrategy(InheritanceStrategy.COMPLETE_TABLE);
                        return;
                    }
                }
                if (baseCmd.getInheritanceMetaData() != null && baseCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.COMPLETE_TABLE) {
                    (this.inheritanceMetaData = new InheritanceMetaData()).setStrategy(InheritanceStrategy.COMPLETE_TABLE);
                }
                else if (this.pcSuperclassMetaData.getInheritanceMetaData() != null && this.pcSuperclassMetaData.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                    (this.inheritanceMetaData = new InheritanceMetaData()).setStrategy(InheritanceStrategy.NEW_TABLE);
                }
                else if (mmgr.getNucleusContext().getPersistenceConfiguration().getStringProperty("datanucleus.defaultInheritanceStrategy").equalsIgnoreCase("TABLE_PER_CLASS")) {
                    (this.inheritanceMetaData = new InheritanceMetaData()).setStrategy(InheritanceStrategy.NEW_TABLE);
                }
                else {
                    (this.inheritanceMetaData = new InheritanceMetaData()).setStrategy(InheritanceStrategy.SUPERCLASS_TABLE);
                }
            }
            else {
                (this.inheritanceMetaData = new InheritanceMetaData()).setStrategy(InheritanceStrategy.NEW_TABLE);
            }
            return;
        }
        if (this.inheritanceMetaData.getStrategy() == null) {
            if (this.getBaseInheritanceStrategy() != null) {
                final String treeStrategy2 = this.getBaseInheritanceStrategy();
                if (treeStrategy2.equalsIgnoreCase("SINGLE_TABLE")) {
                    if (this.pcSuperclassMetaData != null) {
                        if (this.pcSuperclassMetaData.getInheritanceMetaData() != null && this.pcSuperclassMetaData.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                            this.inheritanceMetaData.strategy = InheritanceStrategy.NEW_TABLE;
                        }
                        else {
                            this.inheritanceMetaData.strategy = InheritanceStrategy.SUPERCLASS_TABLE;
                        }
                    }
                    else {
                        this.inheritanceMetaData.strategy = InheritanceStrategy.NEW_TABLE;
                    }
                }
                else if (treeStrategy2.equalsIgnoreCase("TABLE_PER_CLASS")) {
                    this.inheritanceMetaData.strategy = InheritanceStrategy.COMPLETE_TABLE;
                }
                else if (treeStrategy2.equalsIgnoreCase("JOINED")) {
                    this.inheritanceMetaData.strategy = InheritanceStrategy.NEW_TABLE;
                }
                return;
            }
            if (this.pcSuperclassMetaData != null) {
                final String treeStrategy2 = this.getBaseInheritanceStrategy();
                InheritanceStrategy baseStrategy = null;
                if (treeStrategy2 != null && treeStrategy2.equalsIgnoreCase("SINGLE_TABLE")) {
                    baseStrategy = InheritanceStrategy.SUPERCLASS_TABLE;
                }
                else if (treeStrategy2 != null && treeStrategy2.equalsIgnoreCase("TABLE_PER_CLASS")) {
                    baseStrategy = InheritanceStrategy.COMPLETE_TABLE;
                }
                else if (treeStrategy2 != null && treeStrategy2.equalsIgnoreCase("JOINED")) {
                    baseStrategy = InheritanceStrategy.NEW_TABLE;
                }
                else {
                    final AbstractClassMetaData baseCmd2 = this.getBaseAbstractClassMetaData();
                    if (baseCmd2.getInheritanceMetaData() != null) {
                        baseStrategy = baseCmd2.getInheritanceMetaData().getStrategy();
                    }
                }
                if (baseStrategy == InheritanceStrategy.COMPLETE_TABLE) {
                    this.inheritanceMetaData.strategy = InheritanceStrategy.COMPLETE_TABLE;
                }
                else if (this.pcSuperclassMetaData.getInheritanceMetaData() != null && this.pcSuperclassMetaData.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                    this.inheritanceMetaData.strategy = InheritanceStrategy.NEW_TABLE;
                }
                else if (mmgr.getNucleusContext().getPersistenceConfiguration().getStringProperty("datanucleus.defaultInheritanceStrategy").equalsIgnoreCase("TABLE_PER_CLASS")) {
                    this.inheritanceMetaData.strategy = InheritanceStrategy.NEW_TABLE;
                }
                else {
                    this.inheritanceMetaData.strategy = InheritanceStrategy.SUPERCLASS_TABLE;
                }
            }
            else {
                this.inheritanceMetaData.strategy = InheritanceStrategy.NEW_TABLE;
            }
        }
    }
    
    protected void applyDefaultDiscriminatorValueWhenNotSpecified(final MetaDataManager mmgr) {
        if (this.inheritanceMetaData != null && this.inheritanceMetaData.getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
            final AbstractClassMetaData superCmd = this.getClassManagingTable();
            if (superCmd == null) {
                throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044064", this.getFullClassName());
            }
            if (superCmd.getInheritanceMetaData() != null) {
                final DiscriminatorMetaData superDismd = superCmd.getInheritanceMetaData().getDiscriminatorMetaData();
                DiscriminatorMetaData dismd = this.inheritanceMetaData.getDiscriminatorMetaData();
                if (superDismd != null && superDismd.getStrategy() == DiscriminatorStrategy.VALUE_MAP && (dismd == null || dismd.getValue() == null)) {
                    if (dismd == null) {
                        dismd = this.inheritanceMetaData.newDiscriminatorMetadata();
                    }
                    if (NucleusLogger.METADATA.isDebugEnabled()) {
                        NucleusLogger.METADATA.debug("No discriminator value specified for " + this.getFullClassName() + " so using fully-qualified class name");
                    }
                    dismd.setValue(this.getFullClassName());
                }
            }
        }
        if (this.inheritanceMetaData != null) {
            final DiscriminatorMetaData dismd2 = this.inheritanceMetaData.getDiscriminatorMetaData();
            if (dismd2 != null && this.getDiscriminatorStrategy() == DiscriminatorStrategy.VALUE_MAP && dismd2.getValue() != null) {
                mmgr.registerDiscriminatorValueForClass(this, dismd2.getValue());
            }
        }
    }
    
    protected void validateUnmappedColumns() {
        if (this.unmappedColumns != null && this.unmappedColumns.size() > 0) {
            for (final ColumnMetaData colmd : this.unmappedColumns) {
                if (colmd.getName() == null) {
                    throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044119", this.fullName);
                }
                if (colmd.getJdbcType() == null) {
                    throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044120", this.fullName, colmd.getName());
                }
            }
        }
    }
    
    private AbstractClassMetaData getSuperclassManagingTable() {
        if (this.pcSuperclassMetaData == null) {
            return null;
        }
        if (this.pcSuperclassMetaData.getInheritanceMetaData().getStrategy() == InheritanceStrategy.NEW_TABLE) {
            return this.pcSuperclassMetaData;
        }
        return this.pcSuperclassMetaData.getSuperclassManagingTable();
    }
    
    private AbstractClassMetaData getClassManagingTable() {
        if (this.inheritanceMetaData == null) {
            return this;
        }
        if (this.inheritanceMetaData.getStrategy() == InheritanceStrategy.NEW_TABLE) {
            return this;
        }
        if (this.inheritanceMetaData.getStrategy() != InheritanceStrategy.SUPERCLASS_TABLE) {
            return null;
        }
        if (this.pcSuperclassMetaData == null) {
            return null;
        }
        return this.pcSuperclassMetaData.getClassManagingTable();
    }
    
    public final AbstractClassMetaData getSuperAbstractClassMetaData() {
        this.checkPopulated();
        return this.pcSuperclassMetaData;
    }
    
    public boolean pkIsDatastoreAttributed(final StoreManager storeMgr) {
        if (this.pkIsDatastoreAttributed == null) {
            this.pkIsDatastoreAttributed = Boolean.FALSE;
            if (this.identityType == IdentityType.APPLICATION) {
                for (int i = 0; i < this.pkMemberPositions.length; ++i) {
                    if (storeMgr.isStrategyDatastoreAttributed(this, this.pkMemberPositions[i])) {
                        this.pkIsDatastoreAttributed = true;
                    }
                }
            }
            else if (this.identityType == IdentityType.DATASTORE) {
                this.pkIsDatastoreAttributed = storeMgr.isStrategyDatastoreAttributed(this, -1);
            }
        }
        return this.pkIsDatastoreAttributed;
    }
    
    protected void determineObjectIdClass(final MetaDataManager mmgr) {
        if (this.identityType != IdentityType.APPLICATION || this.objectidClass != null) {
            return;
        }
        int no_of_pk_fields = 0;
        AbstractMemberMetaData mmd_pk = null;
        for (final AbstractMemberMetaData mmd : this.members) {
            if (mmd.isPrimaryKey()) {
                mmd_pk = mmd;
                ++no_of_pk_fields;
            }
        }
        if (no_of_pk_fields == 0 && this.inheritanceMetaData.getStrategy() == InheritanceStrategy.SUBCLASS_TABLE && this.getSuperclassManagingTable() == null) {
            NucleusLogger.METADATA.warn("Class " + this.getFullClassName() + " has no table of its own, is using application-identity and has no primary key fields. Marking as not persistence instantiable");
            this.instantiable = false;
            return;
        }
        boolean needsObjectidClass = false;
        if (this.persistableSuperclass == null) {
            needsObjectidClass = true;
        }
        else if (this.persistableSuperclass != null && this.getSuperclassManagingTable() == null) {
            needsObjectidClass = true;
        }
        if (needsObjectidClass) {
            final ApiAdapter api = mmgr.getApiAdapter();
            if (no_of_pk_fields == 0) {
                NucleusLogger.METADATA.error(AbstractClassMetaData.LOCALISER.msg("044065", this.fullName, "" + no_of_pk_fields));
                throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044065", this.fullName, "" + no_of_pk_fields);
            }
            if (no_of_pk_fields > 1) {
                NucleusLogger.METADATA.warn(AbstractClassMetaData.LOCALISER.msg("044065", this.fullName, "" + no_of_pk_fields));
                if (!mmgr.isEnhancing()) {
                    this.objectidClass = this.fullName + "_PK";
                    NucleusLogger.METADATA.debug("Class " + this.fullName + " has " + this.getNoOfPrimaryKeyMembers() + " primary-key field(s) yet no objectidClass defined. Assumed to be " + this.objectidClass);
                }
            }
            else {
                final Class pk_type = mmd_pk.getType();
                if (Byte.class.isAssignableFrom(pk_type) || Byte.TYPE.isAssignableFrom(pk_type)) {
                    this.objectidClass = api.getSingleFieldIdentityClassNameForByte();
                }
                else if (Character.class.isAssignableFrom(pk_type) || Character.TYPE.isAssignableFrom(pk_type)) {
                    this.objectidClass = api.getSingleFieldIdentityClassNameForChar();
                }
                else if (Integer.class.isAssignableFrom(pk_type) || Integer.TYPE.isAssignableFrom(pk_type)) {
                    this.objectidClass = api.getSingleFieldIdentityClassNameForInt();
                }
                else if (Long.class.isAssignableFrom(pk_type) || Long.TYPE.isAssignableFrom(pk_type)) {
                    this.objectidClass = api.getSingleFieldIdentityClassNameForLong();
                }
                else if (Short.class.isAssignableFrom(pk_type) || Short.TYPE.isAssignableFrom(pk_type)) {
                    this.objectidClass = api.getSingleFieldIdentityClassNameForShort();
                }
                else if (String.class.isAssignableFrom(pk_type)) {
                    this.objectidClass = api.getSingleFieldIdentityClassNameForString();
                }
                else {
                    if (!Object.class.isAssignableFrom(pk_type)) {
                        NucleusLogger.METADATA.error(AbstractClassMetaData.LOCALISER.msg("044066", this.fullName, pk_type.getName()));
                        throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044066", this.fullName, pk_type.getName());
                    }
                    this.objectidClass = api.getSingleFieldIdentityClassNameForObject();
                }
            }
        }
    }
    
    protected void validateObjectIdClass(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.getPersistenceCapableSuperclass() == null && this.objectidClass != null) {
            final ApiAdapter api = mmgr.getApiAdapter();
            Class obj_cls = null;
            try {
                obj_cls = clr.classForName(this.objectidClass);
            }
            catch (ClassNotResolvedException cnre) {
                throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044079", this.fullName, this.objectidClass);
            }
            boolean validated = false;
            final Set errors = new HashSet();
            try {
                if (api.isValidPrimaryKeyClass(obj_cls, this, clr, this.getNoOfPopulatedPKMembers(), mmgr)) {
                    validated = true;
                }
            }
            catch (NucleusException ex) {
                errors.add(ex);
            }
            if (!validated) {
                throw new NucleusUserException(AbstractClassMetaData.LOCALISER_API.msg("019016", this.getFullClassName(), obj_cls.getName()), errors.toArray(new Throwable[errors.size()]));
            }
        }
    }
    
    public abstract void populate(final ClassLoaderResolver p0, final ClassLoader p1, final MetaDataManager p2);
    
    @Override
    public abstract void initialise(final ClassLoaderResolver p0, final MetaDataManager p1);
    
    protected void initialiseMemberPositionInformation(final MetaDataManager mmgr) {
        this.memberCount = this.noOfInheritedManagedMembers + this.managedMembers.length;
        this.dfgMemberFlags = new boolean[this.memberCount];
        this.scoMutableMemberFlags = new boolean[this.memberCount];
        this.nonPkMemberFlags = new boolean[this.memberCount];
        int pk_field_count = 0;
        int dfg_field_count = 0;
        int scm_field_count = 0;
        for (int i = 0; i < this.memberCount; ++i) {
            final AbstractMemberMetaData mmd = this.getMetaDataForManagedMemberAtAbsolutePositionInternal(i);
            if (mmd.isPrimaryKey()) {
                ++pk_field_count;
            }
            else {
                this.nonPkMemberFlags[i] = true;
            }
            if (mmd.isDefaultFetchGroup()) {
                this.dfgMemberFlags[i] = true;
                ++dfg_field_count;
            }
            if (mmd.calcIsSecondClassMutable(mmgr)) {
                this.scoMutableMemberFlags[i] = true;
                ++scm_field_count;
            }
        }
        if (pk_field_count > 0 && this.identityType != IdentityType.APPLICATION) {
            throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044078", this.fullName, pk_field_count, this.identityType);
        }
        if (pk_field_count > 0) {
            this.pkMemberPositions = new int[pk_field_count];
            int i = 0;
            int pk_num = 0;
            while (i < this.memberCount) {
                final AbstractMemberMetaData mmd2 = this.getMetaDataForManagedMemberAtAbsolutePositionInternal(i);
                if (mmd2.isPrimaryKey()) {
                    this.pkMemberPositions[pk_num++] = i;
                }
                ++i;
            }
        }
        else if (this.instantiable && pk_field_count == 0 && this.identityType == IdentityType.APPLICATION) {
            throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044077", this.fullName, this.objectidClass);
        }
        this.nonPkMemberPositions = new int[this.memberCount - pk_field_count];
        int i = 0;
        int npkf = 0;
        while (i < this.memberCount) {
            final AbstractMemberMetaData mmd2 = this.getMetaDataForManagedMemberAtAbsolutePositionInternal(i);
            if (!mmd2.isPrimaryKey()) {
                this.nonPkMemberPositions[npkf++] = i;
            }
            ++i;
        }
        this.dfgMemberPositions = new int[dfg_field_count];
        this.scoMutableMemberPositions = new int[scm_field_count];
        i = 0;
        int dfg_num = 0;
        int scm_num = 0;
        while (i < this.memberCount) {
            if (this.dfgMemberFlags[i]) {
                this.dfgMemberPositions[dfg_num++] = i;
            }
            if (this.scoMutableMemberFlags[i]) {
                this.scoMutableMemberPositions[scm_num++] = i;
            }
            ++i;
        }
    }
    
    void getReferencedClassMetaData(final List orderedCMDs, final Set referencedCMDs, final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        final Map viewReferences = new HashMap();
        this.getReferencedClassMetaData(orderedCMDs, referencedCMDs, viewReferences, clr, mmgr);
    }
    
    private void getReferencedClassMetaData(final List orderedCMDs, final Set referencedCMDs, final Map viewReferences, final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (!referencedCMDs.contains(this)) {
            referencedCMDs.add(this);
            for (int i = 0; i < this.managedMembers.length; ++i) {
                final AbstractMemberMetaData mmd = this.managedMembers[i];
                mmd.getReferencedClassMetaData(orderedCMDs, referencedCMDs, clr, mmgr);
            }
            if (this.persistableSuperclass != null) {
                final AbstractClassMetaData super_cmd = this.getSuperAbstractClassMetaData();
                super_cmd.getReferencedClassMetaData(orderedCMDs, referencedCMDs, clr, mmgr);
            }
            if (this.objectidClass != null) {
                final AbstractClassMetaData id_cmd = mmgr.getMetaDataForClass(this.objectidClass, clr);
                if (id_cmd != null) {
                    id_cmd.getReferencedClassMetaData(orderedCMDs, referencedCMDs, clr, mmgr);
                }
            }
            final String viewDefStr = this.getValueForExtension("view-definition");
            if (viewDefStr != null) {
                final MacroString viewDef = new MacroString(this.fullName, this.getValueForExtension("view-imports"), viewDefStr);
                viewDef.substituteMacros(new MacroString.MacroHandler() {
                    @Override
                    public void onIdentifierMacro(final MacroString.IdentifierMacro im) {
                        if (!AbstractClassMetaData.this.getFullClassName().equals(im.className)) {
                            AbstractClassMetaData.this.addViewReference(viewReferences, im.className);
                            final AbstractClassMetaData view_cmd = mmgr.getMetaDataForClass(im.className, clr);
                            view_cmd.getReferencedClassMetaData(orderedCMDs, referencedCMDs, viewReferences, clr, mmgr);
                        }
                    }
                    
                    @Override
                    public void onParameterMacro(final MacroString.ParameterMacro pm) {
                        throw new NucleusUserException("Parameter macros not allowed in view definitions: " + pm);
                    }
                }, clr);
            }
            orderedCMDs.add(this);
        }
    }
    
    private void addViewReference(final Map viewReferences, final String referenced_name) {
        if (this.fullName.equals(referenced_name)) {
            Set referencedSet = viewReferences.get(referenced_name);
            if (referencedSet == null) {
                referencedSet = new HashSet();
                viewReferences.put(this.fullName, referencedSet);
            }
            referencedSet.add(referenced_name);
            ViewUtils.checkForCircularViewReferences(viewReferences, this.fullName, referenced_name, null);
        }
    }
    
    public int getNoOfQueries() {
        return this.queries.size();
    }
    
    public QueryMetaData[] getQueries() {
        return (QueryMetaData[])((this.queries == null) ? null : ((QueryMetaData[])this.queries.toArray(new QueryMetaData[this.queries.size()])));
    }
    
    public int getNoOfStoredProcQueries() {
        return this.storedProcQueries.size();
    }
    
    public StoredProcQueryMetaData[] getStoredProcQueries() {
        return (StoredProcQueryMetaData[])((this.storedProcQueries == null) ? null : ((StoredProcQueryMetaData[])this.storedProcQueries.toArray(new StoredProcQueryMetaData[this.storedProcQueries.size()])));
    }
    
    public QueryResultMetaData[] getQueryResultMetaData() {
        if (this.queryResultMetaData == null) {
            return null;
        }
        return this.queryResultMetaData.toArray(new QueryResultMetaData[this.queryResultMetaData.size()]);
    }
    
    public final VersionMetaData getVersionMetaData() {
        return this.versionMetaData;
    }
    
    public final VersionMetaData getVersionMetaDataForClass() {
        if (this.versionMetaData != null) {
            return this.versionMetaData;
        }
        if (this.getSuperAbstractClassMetaData() != null) {
            return this.getSuperAbstractClassMetaData().getVersionMetaDataForClass();
        }
        return null;
    }
    
    public final boolean isVersioned() {
        final VersionMetaData vermd = this.getVersionMetaDataForClass();
        return vermd != null && vermd.getVersionStrategy() != null && vermd.getVersionStrategy() != VersionStrategy.NONE;
    }
    
    public final VersionMetaData getVersionMetaDataForTable() {
        if (this.pcSuperclassMetaData != null) {
            if (this.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE && this.pcSuperclassMetaData.getInheritanceMetaData().getStrategy() == InheritanceStrategy.NEW_TABLE) {
                final VersionMetaData vermd = this.pcSuperclassMetaData.getVersionMetaDataForTable();
                if (vermd != null) {
                    return vermd;
                }
            }
            if (this.getInheritanceMetaData().getStrategy() == InheritanceStrategy.NEW_TABLE && this.pcSuperclassMetaData.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                final VersionMetaData vermd = this.pcSuperclassMetaData.getVersionMetaDataForTable();
                if (vermd != null) {
                    return vermd;
                }
            }
            if (this.getInheritanceMetaData().getStrategy() == InheritanceStrategy.COMPLETE_TABLE) {
                final VersionMetaData vermd = this.pcSuperclassMetaData.getVersionMetaDataForTable();
                if (vermd != null) {
                    return vermd;
                }
            }
        }
        return this.versionMetaData;
    }
    
    public final DiscriminatorMetaData getDiscriminatorMetaDataForTable() {
        if (this.pcSuperclassMetaData != null && (this.pcSuperclassMetaData.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE || this.pcSuperclassMetaData.getInheritanceMetaData().getStrategy() == InheritanceStrategy.COMPLETE_TABLE)) {
            final DiscriminatorMetaData superDismd = this.pcSuperclassMetaData.getDiscriminatorMetaDataForTable();
            if (superDismd != null) {
                return superDismd;
            }
        }
        return (this.inheritanceMetaData != null) ? this.inheritanceMetaData.getDiscriminatorMetaData() : null;
    }
    
    public final DiscriminatorStrategy getDiscriminatorStrategyForTable() {
        if (this.inheritanceMetaData == null) {
            return null;
        }
        if (this.inheritanceMetaData.getStrategy() == InheritanceStrategy.NEW_TABLE && this.inheritanceMetaData.getDiscriminatorMetaData() != null) {
            return this.inheritanceMetaData.getDiscriminatorMetaData().getStrategy();
        }
        if (this.getSuperAbstractClassMetaData() != null) {
            return this.getSuperAbstractClassMetaData().getDiscriminatorStrategy();
        }
        return null;
    }
    
    public final DiscriminatorMetaData getDiscriminatorMetaData() {
        if (this.inheritanceMetaData != null && this.inheritanceMetaData.getDiscriminatorMetaData() != null) {
            return this.inheritanceMetaData.getDiscriminatorMetaData();
        }
        if (this.getSuperAbstractClassMetaData() != null) {
            return this.getSuperAbstractClassMetaData().getDiscriminatorMetaData();
        }
        return null;
    }
    
    public final DiscriminatorMetaData getDiscriminatorMetaDataRoot() {
        DiscriminatorMetaData dismd = null;
        if (this.pcSuperclassMetaData != null) {
            dismd = this.pcSuperclassMetaData.getDiscriminatorMetaDataRoot();
        }
        if (dismd == null) {
            dismd = ((this.inheritanceMetaData != null) ? this.inheritanceMetaData.getDiscriminatorMetaData() : null);
        }
        return dismd;
    }
    
    public final boolean hasDiscriminatorStrategy() {
        final DiscriminatorStrategy strategy = this.getDiscriminatorStrategy();
        return strategy != null && strategy != DiscriminatorStrategy.NONE;
    }
    
    public final DiscriminatorStrategy getDiscriminatorStrategy() {
        if (this.inheritanceMetaData != null && this.inheritanceMetaData.getDiscriminatorMetaData() != null && this.inheritanceMetaData.getDiscriminatorMetaData().getStrategy() != null) {
            return this.inheritanceMetaData.getDiscriminatorMetaData().getStrategy();
        }
        if (this.pcSuperclassMetaData != null) {
            return this.pcSuperclassMetaData.getDiscriminatorStrategy();
        }
        return null;
    }
    
    public String getDiscriminatorColumnName() {
        if (this.inheritanceMetaData != null && this.inheritanceMetaData.getDiscriminatorMetaData() != null && this.inheritanceMetaData.getDiscriminatorMetaData().getColumnMetaData() != null && this.inheritanceMetaData.getDiscriminatorMetaData().getColumnMetaData().getName() != null) {
            return this.inheritanceMetaData.getDiscriminatorMetaData().getColumnMetaData().getName();
        }
        if (this.pcSuperclassMetaData != null) {
            return this.pcSuperclassMetaData.getDiscriminatorColumnName();
        }
        return null;
    }
    
    public ColumnMetaData getDiscriminatorColumnMetaData() {
        if (this.inheritanceMetaData != null && this.inheritanceMetaData.getDiscriminatorMetaData() != null && this.inheritanceMetaData.getDiscriminatorMetaData().getColumnMetaData() != null) {
            return this.inheritanceMetaData.getDiscriminatorMetaData().getColumnMetaData();
        }
        if (this.pcSuperclassMetaData != null) {
            return this.pcSuperclassMetaData.getDiscriminatorColumnMetaData();
        }
        return null;
    }
    
    public Object getDiscriminatorValue() {
        if (this.hasDiscriminatorStrategy()) {
            final DiscriminatorStrategy str = this.getDiscriminatorStrategy();
            if (str == DiscriminatorStrategy.CLASS_NAME) {
                return this.getFullClassName();
            }
            if (str == DiscriminatorStrategy.VALUE_MAP) {
                final DiscriminatorMetaData dismd = this.getDiscriminatorMetaDataRoot();
                Object value = this.getInheritanceMetaData().getDiscriminatorMetaData().getValue();
                if (dismd.getColumnMetaData() != null) {
                    final ColumnMetaData colmd = dismd.getColumnMetaData();
                    if ("integer".equalsIgnoreCase(colmd.getJdbcType()) || "tinyint".equalsIgnoreCase(colmd.getJdbcType()) || "smallint".equalsIgnoreCase(colmd.getJdbcType())) {
                        value = Long.parseLong((String)value);
                    }
                }
                return value;
            }
        }
        return null;
    }
    
    public final JoinMetaData[] getJoinMetaData() {
        return this.joinMetaData;
    }
    
    public final Set<FetchGroupMetaData> getFetchGroupMetaData() {
        return this.fetchGroups;
    }
    
    public Set<FetchGroupMetaData> getFetchGroupMetaData(final Collection groupNames) {
        final Set<FetchGroupMetaData> results = new HashSet<FetchGroupMetaData>();
        for (final String groupname : groupNames) {
            final FetchGroupMetaData fgmd = this.getFetchGroupMetaData(groupname);
            if (fgmd != null) {
                results.add(fgmd);
            }
        }
        return results;
    }
    
    public FetchGroupMetaData getFetchGroupMetaData(final String groupname) {
        final FetchGroupMetaData fgmd = (this.fetchGroupMetaDataByName != null) ? this.fetchGroupMetaDataByName.get(groupname) : null;
        if (fgmd == null && this.pcSuperclassMetaData != null) {
            return this.pcSuperclassMetaData.getFetchGroupMetaData(groupname);
        }
        return fgmd;
    }
    
    public IdentityType getIdentityType() {
        return this.identityType;
    }
    
    public synchronized void setIdentityType(final IdentityType type) {
        this.checkNotYetPopulated();
        this.identityType = type;
    }
    
    public final IndexMetaData[] getIndexMetaData() {
        return this.indexMetaData;
    }
    
    public final ForeignKeyMetaData[] getForeignKeyMetaData() {
        return this.foreignKeyMetaData;
    }
    
    public final UniqueMetaData[] getUniqueMetaData() {
        return this.uniqueMetaData;
    }
    
    public final List<ColumnMetaData> getUnmappedColumns() {
        return this.unmappedColumns;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getFullClassName() {
        return this.fullName;
    }
    
    public String getObjectidClass() {
        return this.objectidClass;
    }
    
    public AbstractClassMetaData setObjectIdClass(final String objectidClass) {
        this.objectidClass = (StringUtils.isWhitespace(objectidClass) ? this.objectidClass : objectidClass);
        return this;
    }
    
    public String getEntityName() {
        return this.entityName;
    }
    
    public AbstractClassMetaData setEntityName(final String name) {
        this.entityName = (StringUtils.isWhitespace(name) ? this.entityName : name);
        return this;
    }
    
    public String getCatalog() {
        if (this.catalog == null) {
            return ((PackageMetaData)this.parent).getCatalog();
        }
        return this.catalog;
    }
    
    public AbstractClassMetaData setCatalog(final String catalog) {
        this.catalog = (StringUtils.isWhitespace(catalog) ? this.catalog : catalog);
        return this;
    }
    
    public String getSchema() {
        if (this.schema == null) {
            return ((PackageMetaData)this.parent).getSchema();
        }
        return this.schema;
    }
    
    public AbstractClassMetaData setSchema(final String schema) {
        this.schema = (StringUtils.isWhitespace(schema) ? this.schema : schema);
        return this;
    }
    
    public String getTable() {
        return this.table;
    }
    
    public AbstractClassMetaData setTable(final String table) {
        this.table = (StringUtils.isWhitespace(table) ? this.table : table);
        return this;
    }
    
    public boolean isRequiresExtent() {
        return this.requiresExtent;
    }
    
    public AbstractClassMetaData setRequiresExtent(final boolean flag) {
        this.requiresExtent = flag;
        return this;
    }
    
    public AbstractClassMetaData setRequiresExtent(final String flag) {
        if (!StringUtils.isWhitespace(flag)) {
            this.requiresExtent = Boolean.parseBoolean(flag);
        }
        return this;
    }
    
    public boolean isDetachable() {
        return this.detachable;
    }
    
    public AbstractClassMetaData setDetachable(final boolean flag) {
        this.detachable = flag;
        return this;
    }
    
    public AbstractClassMetaData setDetachable(final String flag) {
        if (!StringUtils.isWhitespace(flag)) {
            this.detachable = Boolean.parseBoolean(flag);
        }
        return this;
    }
    
    public Boolean isCacheable() {
        return this.cacheable;
    }
    
    public AbstractClassMetaData setCacheable(final boolean cache) {
        this.cacheable = cache;
        return this;
    }
    
    public AbstractClassMetaData setCacheable(final String cache) {
        if (!StringUtils.isWhitespace(cache)) {
            this.cacheable = Boolean.parseBoolean(cache);
        }
        return this;
    }
    
    public boolean isEmbeddedOnly() {
        return this.embeddedOnly;
    }
    
    public AbstractClassMetaData setEmbeddedOnly(final boolean flag) {
        this.embeddedOnly = flag;
        return this;
    }
    
    public AbstractClassMetaData setEmbeddedOnly(final String flag) {
        if (!StringUtils.isWhitespace(flag)) {
            this.embeddedOnly = Boolean.parseBoolean(flag);
        }
        return this;
    }
    
    public final IdentityMetaData getIdentityMetaData() {
        return this.identityMetaData;
    }
    
    public final IdentityMetaData getBaseIdentityMetaData() {
        if (this.pcSuperclassMetaData != null) {
            return this.pcSuperclassMetaData.getBaseIdentityMetaData();
        }
        return this.identityMetaData;
    }
    
    public final InheritanceMetaData getInheritanceMetaData() {
        return this.inheritanceMetaData;
    }
    
    public final PrimaryKeyMetaData getPrimaryKeyMetaData() {
        return this.primaryKeyMetaData;
    }
    
    public PackageMetaData getPackageMetaData() {
        if (this.parent != null) {
            return (PackageMetaData)this.parent;
        }
        return null;
    }
    
    public String getPackageName() {
        return this.getPackageMetaData().getName();
    }
    
    public int getNoOfMembers() {
        return this.members.size();
    }
    
    public AbstractMemberMetaData getMetaDataForMemberAtRelativePosition(final int index) {
        if (index < 0 || index >= this.members.size()) {
            return null;
        }
        return this.members.get(index);
    }
    
    public ClassPersistenceModifier getPersistenceModifier() {
        return this.persistenceModifier;
    }
    
    public AbstractClassMetaData setPersistenceModifier(final ClassPersistenceModifier modifier) {
        this.persistenceModifier = modifier;
        return this;
    }
    
    public String getPersistenceCapableSuperclass() {
        return this.persistableSuperclass;
    }
    
    public synchronized void setPersistenceCapableSuperclass(final String pcSuperclassName) {
        this.checkNotYetPopulated();
        this.persistableSuperclass = pcSuperclassName;
    }
    
    public boolean usesSingleFieldIdentityClass() {
        return this.usesSingleFieldIdentityClass;
    }
    
    public boolean isMetaDataComplete() {
        return this.metaDataComplete;
    }
    
    public boolean isMappedSuperclass() {
        return this.mappedSuperclass;
    }
    
    public boolean isSerializeRead() {
        return this.serializeRead;
    }
    
    public boolean isSameOrAncestorOf(final AbstractClassMetaData cmd) {
        this.checkInitialised();
        if (cmd == null) {
            return false;
        }
        if (this.fullName.equals(cmd.fullName)) {
            return true;
        }
        for (AbstractClassMetaData parent = cmd.getSuperAbstractClassMetaData(); parent != null; parent = parent.getSuperAbstractClassMetaData()) {
            if (this.fullName.equals(parent.fullName)) {
                return true;
            }
        }
        return false;
    }
    
    public String[] getPrimaryKeyMemberNames() {
        if (this.identityType != IdentityType.APPLICATION) {
            return null;
        }
        List memberNames = new ArrayList();
        for (final AbstractMemberMetaData mmd : this.members) {
            if (Boolean.TRUE.equals(mmd.primaryKey)) {
                memberNames.add(mmd.name);
            }
        }
        if (memberNames.size() > 0) {
            return memberNames.toArray(new String[memberNames.size()]);
        }
        memberNames = null;
        return this.pcSuperclassMetaData.getPrimaryKeyMemberNames();
    }
    
    public boolean hasMember(final String memberName) {
        for (final AbstractMemberMetaData mmd : this.members) {
            if (mmd.getName().equals(memberName)) {
                return true;
            }
        }
        return this.pcSuperclassMetaData != null && this.pcSuperclassMetaData.hasMember(memberName);
    }
    
    public AbstractMemberMetaData getMetaDataForMember(final String name) {
        if (name == null) {
            return null;
        }
        for (final AbstractMemberMetaData mmd : this.members) {
            if (mmd.getName().equals(name)) {
                return mmd;
            }
        }
        if (this.pcSuperclassMetaData != null) {
            return this.pcSuperclassMetaData.getMetaDataForMember(name);
        }
        return null;
    }
    
    public int getNoOfManagedMembers() {
        if (this.managedMembers == null) {
            return 0;
        }
        return this.managedMembers.length;
    }
    
    public AbstractMemberMetaData[] getManagedMembers() {
        this.checkInitialised();
        return this.managedMembers;
    }
    
    public int getNoOfOverriddenMembers() {
        if (this.overriddenMembers == null) {
            return 0;
        }
        return this.overriddenMembers.length;
    }
    
    public AbstractMemberMetaData[] getOverriddenMembers() {
        this.checkInitialised();
        return this.overriddenMembers;
    }
    
    public AbstractMemberMetaData getOverriddenMember(final int position) {
        this.checkInitialised();
        if (this.overriddenMembers == null) {
            return null;
        }
        if (position < 0 || position >= this.overriddenMembers.length) {
            return null;
        }
        return this.overriddenMembers[position];
    }
    
    public AbstractMemberMetaData getOverriddenMember(final String name) {
        this.checkInitialised();
        if (this.overriddenMembers == null) {
            return null;
        }
        for (int i = 0; i < this.overriddenMembers.length; ++i) {
            if (this.overriddenMembers[i].getName().equals(name)) {
                return this.overriddenMembers[i];
            }
        }
        return null;
    }
    
    protected AbstractMemberMetaData getMemberBeingOverridden(final String name) {
        for (final AbstractMemberMetaData apmd : this.members) {
            if (apmd.name.equals(name) && apmd.fieldBelongsToClass()) {
                return apmd;
            }
        }
        if (this.pcSuperclassMetaData != null) {
            return this.pcSuperclassMetaData.getMemberBeingOverridden(name);
        }
        return null;
    }
    
    public int getNoOfInheritedManagedMembers() {
        this.checkInitialised();
        return this.noOfInheritedManagedMembers;
    }
    
    public int getMemberCount() {
        return this.memberCount;
    }
    
    public AbstractMemberMetaData getMetaDataForManagedMemberAtRelativePosition(final int position) {
        this.checkInitialised();
        if (this.managedMembers == null) {
            return null;
        }
        if (position < 0 || position >= this.managedMembers.length) {
            return null;
        }
        return this.managedMembers[position];
    }
    
    public AbstractMemberMetaData getMetaDataForManagedMemberAtAbsolutePosition(final int abs_position) {
        this.checkInitialised();
        return this.getMetaDataForManagedMemberAtAbsolutePositionInternal(abs_position);
    }
    
    protected AbstractMemberMetaData getMetaDataForManagedMemberAtAbsolutePositionInternal(final int abs_position) {
        if (abs_position < this.noOfInheritedManagedMembers) {
            if (this.pcSuperclassMetaData == null) {
                return null;
            }
            final AbstractMemberMetaData mmd = this.pcSuperclassMetaData.getMetaDataForManagedMemberAtAbsolutePositionInternal(abs_position);
            if (mmd != null) {
                for (int i = 0; i < this.overriddenMembers.length; ++i) {
                    if (this.overriddenMembers[i].getName().equals(mmd.getName()) && this.overriddenMembers[i].getClassName().equals(mmd.getClassName())) {
                        return this.overriddenMembers[i];
                    }
                }
                return mmd;
            }
            return null;
        }
        else {
            if (abs_position - this.noOfInheritedManagedMembers >= this.managedMembers.length) {
                return null;
            }
            return this.managedMembers[abs_position - this.noOfInheritedManagedMembers];
        }
    }
    
    public int getAbsoluteMemberPositionForRelativePosition(final int relativePosition) {
        return this.noOfInheritedManagedMembers + relativePosition;
    }
    
    public int getRelativePositionOfMember(final String memberName) {
        this.checkInitialised();
        if (memberName == null) {
            return -1;
        }
        final Integer i = this.memberPositionsByName.get(memberName);
        return (i == null) ? -1 : i;
    }
    
    public int getAbsolutePositionOfMember(final String memberName) {
        this.checkInitialised();
        if (memberName == null) {
            return -1;
        }
        int i = this.getRelativePositionOfMember(memberName);
        if (i < 0) {
            if (this.pcSuperclassMetaData != null) {
                i = this.pcSuperclassMetaData.getAbsolutePositionOfMember(memberName);
            }
        }
        else {
            i += this.noOfInheritedManagedMembers;
        }
        return i;
    }
    
    public int getAbsolutePositionOfMember(final String className, final String memberName) {
        this.checkInitialised();
        if (memberName == null) {
            return -1;
        }
        int i = -1;
        if (className.equals(this.getFullClassName())) {
            i = this.getRelativePositionOfMember(memberName);
        }
        if (i < 0) {
            if (this.pcSuperclassMetaData != null) {
                i = this.pcSuperclassMetaData.getAbsolutePositionOfMember(className, memberName);
            }
        }
        else {
            i += this.noOfInheritedManagedMembers;
        }
        return i;
    }
    
    private int getNoOfPopulatedPKMembers() {
        if (this.pcSuperclassMetaData != null) {
            return this.pcSuperclassMetaData.getNoOfPopulatedPKMembers();
        }
        final Iterator fields_iter = this.members.iterator();
        int noOfPks = 0;
        while (fields_iter.hasNext()) {
            final AbstractMemberMetaData mmd = fields_iter.next();
            if (mmd.isPrimaryKey()) {
                ++noOfPks;
            }
        }
        return noOfPks;
    }
    
    public int getNoOfPrimaryKeyMembers() {
        if (this.pkMemberPositions == null) {
            return 0;
        }
        return this.pkMemberPositions.length;
    }
    
    public int[] getAllMemberPositions() {
        this.checkInitialised();
        if (this.allMemberPositions == null) {
            this.allMemberPositions = new int[this.memberCount];
            for (int i = 0; i < this.memberCount; ++i) {
                this.allMemberPositions[i] = i;
            }
        }
        return this.allMemberPositions;
    }
    
    public int[] getPKMemberPositions() {
        this.checkInitialised();
        return this.pkMemberPositions;
    }
    
    public int[] getNonPKMemberPositions() {
        this.checkInitialised();
        return this.nonPkMemberPositions;
    }
    
    public boolean[] getNonPKMemberFlags() {
        this.checkInitialised();
        return this.nonPkMemberFlags;
    }
    
    public int[] getDFGMemberPositions() {
        this.checkInitialised();
        return this.dfgMemberPositions;
    }
    
    public boolean[] getDFGMemberFlags() {
        this.checkInitialised();
        return this.dfgMemberFlags;
    }
    
    public int[] getBasicMemberPositions(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        Iterator iter = this.members.iterator();
        int numBasics = 0;
        while (iter.hasNext()) {
            final AbstractMemberMetaData mmd = iter.next();
            if (mmd.getRelationType(clr) == RelationType.NONE && !mmd.isPersistentInterface(clr, mmgr) && !Collection.class.isAssignableFrom(mmd.getType()) && !Map.class.isAssignableFrom(mmd.getType()) && !mmd.getType().isArray()) {
                ++numBasics;
            }
        }
        int[] inheritedBasicPositions = null;
        if (this.pcSuperclassMetaData != null) {
            inheritedBasicPositions = this.pcSuperclassMetaData.getBasicMemberPositions(clr, mmgr);
        }
        final int[] basicPositions = new int[numBasics + ((inheritedBasicPositions != null) ? inheritedBasicPositions.length : 0)];
        int number = 0;
        if (inheritedBasicPositions != null) {
            for (int i = 0; i < inheritedBasicPositions.length; ++i) {
                basicPositions[number++] = inheritedBasicPositions[i];
            }
        }
        iter = this.members.iterator();
        while (iter.hasNext()) {
            final AbstractMemberMetaData mmd2 = iter.next();
            if (mmd2.getRelationType(clr) == RelationType.NONE && !mmd2.isPersistentInterface(clr, mmgr) && !Collection.class.isAssignableFrom(mmd2.getType()) && !Map.class.isAssignableFrom(mmd2.getType()) && !mmd2.getType().isArray()) {
                basicPositions[number++] = mmd2.getAbsoluteFieldNumber();
            }
        }
        return basicPositions;
    }
    
    public int[] getMultivaluedMemberPositions() {
        Iterator iter = this.members.iterator();
        int numMultivalues = 0;
        while (iter.hasNext()) {
            final AbstractMemberMetaData mmd = iter.next();
            if (mmd.getType().isArray() || Collection.class.isAssignableFrom(mmd.getType()) || Map.class.isAssignableFrom(mmd.getType())) {
                ++numMultivalues;
            }
        }
        int[] inheritedMultivaluePositions = null;
        if (this.pcSuperclassMetaData != null) {
            inheritedMultivaluePositions = this.pcSuperclassMetaData.getMultivaluedMemberPositions();
        }
        final int[] multivaluePositions = new int[numMultivalues + ((inheritedMultivaluePositions != null) ? inheritedMultivaluePositions.length : 0)];
        int number = 0;
        if (inheritedMultivaluePositions != null) {
            for (int i = 0; i < inheritedMultivaluePositions.length; ++i) {
                multivaluePositions[number++] = inheritedMultivaluePositions[i];
            }
        }
        iter = this.members.iterator();
        while (iter.hasNext()) {
            final AbstractMemberMetaData mmd2 = iter.next();
            if (mmd2.getType().isArray() || Collection.class.isAssignableFrom(mmd2.getType()) || Map.class.isAssignableFrom(mmd2.getType())) {
                multivaluePositions[number++] = mmd2.getAbsoluteFieldNumber();
            }
        }
        return multivaluePositions;
    }
    
    public int[] getSCOMutableMemberPositions() {
        this.checkInitialised();
        return this.scoMutableMemberPositions;
    }
    
    public int[] getSCONonContainerMemberPositions() {
        this.checkInitialised();
        if (this.scoNonContainerMemberPositions == null) {
            int numberNonContainerSCOFields = 0;
            for (int i = 0; i < this.scoMutableMemberPositions.length; ++i) {
                final AbstractMemberMetaData mmd = this.getMetaDataForManagedMemberAtAbsolutePosition(this.scoMutableMemberPositions[i]);
                if (!Collection.class.isAssignableFrom(mmd.getType()) && !Map.class.isAssignableFrom(mmd.getType())) {
                    ++numberNonContainerSCOFields;
                }
            }
            final int[] noncontainerMemberPositions = new int[numberNonContainerSCOFields];
            int nonContNum = 0;
            for (int j = 0; j < this.scoMutableMemberPositions.length; ++j) {
                final AbstractMemberMetaData mmd2 = this.getMetaDataForManagedMemberAtAbsolutePosition(this.scoMutableMemberPositions[j]);
                if (!Collection.class.isAssignableFrom(mmd2.getType()) && !Map.class.isAssignableFrom(mmd2.getType())) {
                    noncontainerMemberPositions[nonContNum++] = this.scoMutableMemberPositions[j];
                }
            }
            this.scoNonContainerMemberPositions = noncontainerMemberPositions;
        }
        return this.scoNonContainerMemberPositions;
    }
    
    public int[] getSCOContainerMemberPositions() {
        this.checkInitialised();
        if (this.secondClassContainerMemberPositions == null) {
            int numberContainerSCOFields = 0;
            for (int i = 0; i < this.scoMutableMemberPositions.length; ++i) {
                final AbstractMemberMetaData mmd = this.getMetaDataForManagedMemberAtAbsolutePosition(this.scoMutableMemberPositions[i]);
                if (Collection.class.isAssignableFrom(mmd.getType()) || Map.class.isAssignableFrom(mmd.getType())) {
                    ++numberContainerSCOFields;
                }
            }
            final int[] containerMemberPositions = new int[numberContainerSCOFields];
            int contNum = 0;
            for (int j = 0; j < this.scoMutableMemberPositions.length; ++j) {
                final AbstractMemberMetaData mmd2 = this.getMetaDataForManagedMemberAtAbsolutePosition(this.scoMutableMemberPositions[j]);
                if (Collection.class.isAssignableFrom(mmd2.getType()) || Map.class.isAssignableFrom(mmd2.getType())) {
                    containerMemberPositions[contNum++] = this.scoMutableMemberPositions[j];
                }
            }
            this.secondClassContainerMemberPositions = containerMemberPositions;
        }
        return this.secondClassContainerMemberPositions;
    }
    
    public boolean[] getSCOMutableMemberFlags() {
        this.checkInitialised();
        return this.scoMutableMemberFlags;
    }
    
    public boolean hasRelations(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.hasRelations == null) {
            this.hasRelations = (this.getRelationMemberPositions(clr, mmgr).length > 0);
        }
        return this.hasRelations;
    }
    
    public int[] getNonRelationMemberPositions(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        final int[] relPositions = this.getRelationMemberPositions(clr, mmgr);
        if (relPositions == null || relPositions.length == 0) {
            return this.getAllMemberPositions();
        }
        final int[] allPositions = this.getAllMemberPositions();
        final int[] nonrelPositions = new int[allPositions.length - relPositions.length];
        int nonrelPos = 0;
        int nextRelPos = 0;
        for (int i = 0; i < allPositions.length; ++i) {
            if (nextRelPos == relPositions.length) {
                nonrelPositions[nonrelPos++] = i;
            }
            else if (allPositions[i] == relPositions[nextRelPos]) {
                ++nextRelPos;
            }
            else {
                nonrelPositions[nonrelPos++] = i;
            }
        }
        return nonrelPositions;
    }
    
    public int[] getRelationMemberPositions(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.relationPositions == null) {
            int[] superclassRelationPositions = null;
            if (this.pcSuperclassMetaData != null) {
                superclassRelationPositions = this.pcSuperclassMetaData.getRelationMemberPositions(clr, mmgr);
            }
            int numRelations;
            final int numRelationsSuperclass = numRelations = ((superclassRelationPositions != null) ? superclassRelationPositions.length : 0);
            for (int i = 0; i < this.managedMembers.length; ++i) {
                if (this.managedMembers[i].getRelationType(clr) != RelationType.NONE || this.managedMembers[i].isPersistentInterface(clr, mmgr)) {
                    ++numRelations;
                }
            }
            this.relationPositions = new int[numRelations];
            int num = 0;
            if (numRelationsSuperclass > 0) {
                for (int j = 0; j < superclassRelationPositions.length; ++j) {
                    this.relationPositions[num++] = superclassRelationPositions[j];
                }
            }
            if (numRelations > numRelationsSuperclass) {
                for (int j = 0; j < this.managedMembers.length; ++j) {
                    if (this.managedMembers[j].getRelationType(clr) != RelationType.NONE || this.managedMembers[j].isPersistentInterface(clr, mmgr)) {
                        this.relationPositions[num++] = this.managedMembers[j].getAbsoluteFieldNumber();
                    }
                }
            }
        }
        return this.relationPositions;
    }
    
    public int[] getBidirectionalRelationMemberPositions(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.relationPositions == null) {
            this.getRelationMemberPositions(clr, mmgr);
        }
        int numBidirs = 0;
        for (int i = 0; i < this.relationPositions.length; ++i) {
            final AbstractMemberMetaData mmd = this.getMetaDataForManagedMemberAtAbsolutePosition(this.relationPositions[i]);
            final RelationType relationType = mmd.getRelationType(clr);
            if (relationType == RelationType.ONE_TO_ONE_BI || relationType == RelationType.ONE_TO_MANY_BI || relationType == RelationType.MANY_TO_ONE_BI || relationType == RelationType.MANY_TO_MANY_BI) {
                ++numBidirs;
            }
        }
        final int[] bidirRelations = new int[numBidirs];
        numBidirs = 0;
        for (int j = 0; j < this.relationPositions.length; ++j) {
            final AbstractMemberMetaData mmd2 = this.getMetaDataForManagedMemberAtAbsolutePosition(this.relationPositions[j]);
            final RelationType relationType2 = mmd2.getRelationType(clr);
            if (relationType2 == RelationType.ONE_TO_ONE_BI || relationType2 == RelationType.ONE_TO_MANY_BI || relationType2 == RelationType.MANY_TO_ONE_BI || relationType2 == RelationType.MANY_TO_MANY_BI) {
                bidirRelations[numBidirs] = mmd2.getAbsoluteFieldNumber();
            }
        }
        return bidirRelations;
    }
    
    public void setMappedSuperclass(final boolean mapped) {
        this.mappedSuperclass = mapped;
    }
    
    public void setSerializeRead(final boolean serialise) {
        this.serializeRead = serialise;
    }
    
    public void setMetaDataComplete() {
        this.metaDataComplete = true;
    }
    
    public void addQuery(final QueryMetaData qmd) {
        if (qmd == null) {
            return;
        }
        if (this.queries == null) {
            this.queries = new HashSet<QueryMetaData>();
        }
        this.queries.add(qmd);
        qmd.parent = this;
    }
    
    public QueryMetaData newQueryMetadata(final String queryName) {
        if (StringUtils.isWhitespace(queryName)) {
            throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044154", this.fullName);
        }
        final QueryMetaData qmd = new QueryMetaData(queryName);
        this.addQuery(qmd);
        return qmd;
    }
    
    public void addStoredProcQuery(final StoredProcQueryMetaData qmd) {
        if (qmd == null) {
            return;
        }
        if (this.storedProcQueries == null) {
            this.storedProcQueries = new HashSet<StoredProcQueryMetaData>();
        }
        this.storedProcQueries.add(qmd);
        qmd.parent = this;
    }
    
    public StoredProcQueryMetaData newStoredProcQueryMetadata(final String queryName) {
        if (StringUtils.isWhitespace(queryName)) {
            throw new InvalidClassMetaDataException(AbstractClassMetaData.LOCALISER, "044154", this.fullName);
        }
        final StoredProcQueryMetaData qmd = new StoredProcQueryMetaData(queryName);
        this.addStoredProcQuery(qmd);
        return qmd;
    }
    
    public void addQueryResultMetaData(final QueryResultMetaData resultMetaData) {
        if (this.queryResultMetaData == null) {
            this.queryResultMetaData = new HashSet<QueryResultMetaData>();
        }
        if (!this.queryResultMetaData.contains(resultMetaData)) {
            this.queryResultMetaData.add(resultMetaData);
            resultMetaData.parent = this;
        }
    }
    
    public void addIndex(final IndexMetaData idxmd) {
        if (idxmd == null) {
            return;
        }
        if (this.isInitialised()) {
            throw new NucleusUserException("Already initialised");
        }
        this.indexes.add(idxmd);
        idxmd.parent = this;
    }
    
    public IndexMetaData newIndexMetadata() {
        final IndexMetaData idxmd = new IndexMetaData();
        this.addIndex(idxmd);
        return idxmd;
    }
    
    public void addForeignKey(final ForeignKeyMetaData fkmd) {
        if (fkmd == null) {
            return;
        }
        if (this.isInitialised()) {
            throw new NucleusUserException("Already initialised");
        }
        this.foreignKeys.add(fkmd);
        fkmd.parent = this;
    }
    
    public ForeignKeyMetaData newForeignKeyMetadata() {
        final ForeignKeyMetaData fkmd = new ForeignKeyMetaData();
        this.addForeignKey(fkmd);
        return fkmd;
    }
    
    public void addUniqueConstraint(final UniqueMetaData unimd) {
        if (unimd == null) {
            return;
        }
        if (this.isInitialised()) {
            throw new NucleusUserException("Already initialised");
        }
        this.uniqueConstraints.add(unimd);
        unimd.parent = this;
    }
    
    public UniqueMetaData newUniqueMetadata() {
        final UniqueMetaData unimd = new UniqueMetaData();
        this.addUniqueConstraint(unimd);
        return unimd;
    }
    
    public final void addUnmappedColumn(final ColumnMetaData colmd) {
        if (this.unmappedColumns == null) {
            this.unmappedColumns = new ArrayList<ColumnMetaData>();
        }
        this.unmappedColumns.add(colmd);
        colmd.parent = this;
    }
    
    public ColumnMetaData newUnmappedColumnMetaData() {
        final ColumnMetaData colmd = new ColumnMetaData();
        this.addUnmappedColumn(colmd);
        return colmd;
    }
    
    public FieldMetaData newFieldMetadata(final String fieldName) {
        final FieldMetaData fmd = new FieldMetaData(this, fieldName);
        this.addMember(fmd);
        return fmd;
    }
    
    public PropertyMetaData newPropertyMetadata(final String propName) {
        final PropertyMetaData pmd = new PropertyMetaData(this, propName);
        this.addMember(pmd);
        return pmd;
    }
    
    public void addMember(final AbstractMemberMetaData mmd) {
        if (mmd == null) {
            return;
        }
        if (this.isInitialised()) {
            throw new NucleusUserException("adding field/property " + mmd.getName() + " when already initialised!");
        }
        final Iterator iter = this.members.iterator();
        while (iter.hasNext()) {
            final AbstractMemberMetaData md = iter.next();
            if (mmd.getName().equals(md.getName()) && ((mmd instanceof PropertyMetaData && md instanceof PropertyMetaData) || (mmd instanceof FieldMetaData && md instanceof FieldMetaData))) {
                throw new NucleusUserException(AbstractClassMetaData.LOCALISER.msg("044090", this.fullName, mmd.getName()));
            }
            String existingName = md.getName();
            final boolean existingIsProperty = md instanceof PropertyMetaData;
            if (existingIsProperty) {
                existingName = ((PropertyMetaData)md).getFieldName();
                if (existingName == null) {
                    existingName = md.getName();
                }
            }
            String newName = mmd.getName();
            final boolean newIsProperty = mmd instanceof PropertyMetaData;
            if (newIsProperty) {
                newName = ((PropertyMetaData)mmd).getFieldName();
                if (newName == null) {
                    newName = mmd.getName();
                }
            }
            if (!existingName.equals(newName)) {
                continue;
            }
            if (existingIsProperty && newIsProperty) {
                throw new NucleusUserException(AbstractClassMetaData.LOCALISER.msg("044090", this.fullName, mmd.getName()));
            }
            if (existingIsProperty && !newIsProperty) {
                NucleusLogger.METADATA.debug("Ignoring metadata for field " + mmd.getFullFieldName() + " since we already have MetaData for the property " + md.getFullFieldName());
                return;
            }
            if (existingIsProperty || !newIsProperty) {
                continue;
            }
            NucleusLogger.METADATA.debug("Ignoring existing metadata for field " + md.getFullFieldName() + " since now we have MetaData for the property " + mmd.getFullFieldName());
            iter.remove();
        }
        mmd.parent = this;
        this.members.add(mmd);
    }
    
    public void addFetchGroup(final FetchGroupMetaData fgmd) {
        if (fgmd == null) {
            return;
        }
        if (this.isInitialised()) {
            throw new NucleusUserException("Already initialised");
        }
        this.fetchGroups.add(fgmd);
        fgmd.parent = this;
    }
    
    public FetchGroupMetaData newFetchGroupMetaData(final String name) {
        final FetchGroupMetaData fgmd = new FetchGroupMetaData(name);
        this.addFetchGroup(fgmd);
        return fgmd;
    }
    
    public void addJoin(final JoinMetaData jnmd) {
        if (jnmd == null) {
            return;
        }
        if (this.isInitialised()) {
            throw new NucleusUserException("Already initialised");
        }
        this.joins.add(jnmd);
        jnmd.parent = this;
    }
    
    public JoinMetaData newJoinMetaData() {
        final JoinMetaData joinmd = new JoinMetaData();
        this.addJoin(joinmd);
        return joinmd;
    }
    
    public void addListener(final EventListenerMetaData listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
            listener.parent = this;
        }
    }
    
    public EventListenerMetaData getListenerForClass(final String className) {
        if (this.listeners == null) {
            return null;
        }
        for (int i = 0; i < this.listeners.size(); ++i) {
            final EventListenerMetaData elmd = this.listeners.get(i);
            if (elmd.getClassName().equals(className)) {
                return elmd;
            }
        }
        return null;
    }
    
    public List getListeners() {
        return this.listeners;
    }
    
    public void excludeSuperClassListeners() {
        this.excludeSuperClassListeners = Boolean.TRUE;
    }
    
    public boolean isExcludeSuperClassListeners() {
        return this.excludeSuperClassListeners != null && Boolean.TRUE.equals(this.excludeSuperClassListeners);
    }
    
    public void excludeDefaultListeners() {
        this.excludeDefaultListeners = Boolean.TRUE;
    }
    
    public boolean isExcludeDefaultListeners() {
        return this.excludeDefaultListeners != null && Boolean.TRUE.equals(this.excludeDefaultListeners);
    }
    
    public final void setVersionMetaData(final VersionMetaData versionMetaData) {
        this.versionMetaData = versionMetaData;
        if (this.versionMetaData != null) {
            this.versionMetaData.parent = this;
        }
    }
    
    public VersionMetaData newVersionMetadata() {
        final VersionMetaData vermd = new VersionMetaData();
        this.setVersionMetaData(vermd);
        return vermd;
    }
    
    public final void setIdentityMetaData(final IdentityMetaData identityMetaData) {
        this.identityMetaData = identityMetaData;
        if (this.identityMetaData != null) {
            this.identityMetaData.parent = this;
        }
        this.identitySpecified = true;
    }
    
    public IdentityMetaData newIdentityMetadata() {
        final IdentityMetaData idmd = new IdentityMetaData();
        this.setIdentityMetaData(idmd);
        return idmd;
    }
    
    public final void setInheritanceMetaData(final InheritanceMetaData inheritanceMetaData) {
        this.inheritanceMetaData = inheritanceMetaData;
        if (this.inheritanceMetaData != null) {
            this.inheritanceMetaData.parent = this;
        }
    }
    
    public InheritanceMetaData newInheritanceMetadata() {
        final InheritanceMetaData inhmd = new InheritanceMetaData();
        this.setInheritanceMetaData(inhmd);
        return inhmd;
    }
    
    public final void setPrimaryKeyMetaData(final PrimaryKeyMetaData primaryKeyMetaData) {
        this.primaryKeyMetaData = primaryKeyMetaData;
        if (this.primaryKeyMetaData != null) {
            this.primaryKeyMetaData.parent = this;
        }
    }
    
    public PrimaryKeyMetaData newPrimaryKeyMetadata() {
        final PrimaryKeyMetaData pkmd = new PrimaryKeyMetaData();
        this.setPrimaryKeyMetaData(pkmd);
        return pkmd;
    }
    
    public final boolean hasFetchGroupWithPostLoad() {
        if (this.fetchGroupMetaWithPostLoad == null) {
            this.fetchGroupMetaWithPostLoad = Boolean.FALSE;
            if (this.fetchGroups != null) {
                for (final FetchGroupMetaData fgmd : this.fetchGroups) {
                    if (fgmd.getPostLoad()) {
                        this.fetchGroupMetaWithPostLoad = Boolean.TRUE;
                        break;
                    }
                }
            }
        }
        if (this.getSuperAbstractClassMetaData() != null) {
            return this.getSuperAbstractClassMetaData().hasFetchGroupWithPostLoad() || this.fetchGroupMetaWithPostLoad;
        }
        return this.fetchGroupMetaWithPostLoad;
    }
    
    static {
        LOCALISER_API = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
