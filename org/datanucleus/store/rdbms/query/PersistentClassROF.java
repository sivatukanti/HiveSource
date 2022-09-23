// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.store.rdbms.fieldmanager.ResultSetGetter;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.store.FieldValues;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.identity.OIDFactory;
import org.datanucleus.identity.OID;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.metadata.InterfaceMetaData;
import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.NucleusUserException;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import org.datanucleus.util.NucleusLogger;
import java.sql.ResultSet;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.SoftValueMap;
import java.util.Map;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;

public final class PersistentClassROF implements ResultObjectFactory
{
    protected static final Localiser LOCALISER;
    protected final RDBMSStoreManager storeMgr;
    protected final AbstractClassMetaData acmd;
    private Class persistentClass;
    protected StatementClassMapping stmtMapping;
    protected final FetchPlan fetchPlan;
    private final boolean ignoreCache;
    private Map resolvedClasses;
    
    public PersistentClassROF(final RDBMSStoreManager storeMgr, final AbstractClassMetaData acmd, final StatementClassMapping mappingDefinition, final boolean ignoreCache, final FetchPlan fetchPlan, final Class persistentClass) {
        this.stmtMapping = null;
        this.resolvedClasses = new SoftValueMap();
        if (mappingDefinition == null) {
            throw new NucleusException("Attempt to create PersistentIDROF with null mappingDefinition");
        }
        this.storeMgr = storeMgr;
        this.stmtMapping = mappingDefinition;
        this.acmd = acmd;
        this.ignoreCache = ignoreCache;
        this.fetchPlan = fetchPlan;
        this.persistentClass = persistentClass;
    }
    
    public void setPersistentClass(final Class cls) {
        this.persistentClass = cls;
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet rs) {
        String className = null;
        boolean requiresInheritanceCheck = true;
        final StatementMappingIndex discrimMapIdx = this.stmtMapping.getMappingForMemberPosition(-3);
        if (discrimMapIdx != null) {
            try {
                final String discrimValue = rs.getString(discrimMapIdx.getColumnPositions()[0]);
                if (discrimValue == null) {
                    NucleusLogger.DATASTORE_RETRIEVE.debug("Value of discriminator is null so assuming object is null");
                    return null;
                }
                final JavaTypeMapping discrimMapping = discrimMapIdx.getMapping();
                final DiscriminatorMetaData dismd = (discrimMapping != null) ? discrimMapping.getTable().getDiscriminatorMetaData() : null;
                className = ec.getMetaDataManager().getClassNameFromDiscriminatorValue(discrimValue, dismd);
                requiresInheritanceCheck = false;
            }
            catch (SQLException sqle) {
                NucleusLogger.DATASTORE_RETRIEVE.debug("Exception obtaining value of discriminator : " + sqle.getMessage());
            }
        }
        else if (this.stmtMapping.getNucleusTypeColumnName() != null) {
            try {
                className = rs.getString(this.stmtMapping.getNucleusTypeColumnName()).trim();
                if (className == null) {
                    NucleusLogger.DATASTORE_RETRIEVE.debug("Value of determiner column is null so assuming object is null");
                    return null;
                }
                requiresInheritanceCheck = false;
            }
            catch (SQLException ex) {}
        }
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        Class pcClassForObject = this.persistentClass;
        if (className != null) {
            final Class cls = this.resolvedClasses.get(className);
            if (cls != null) {
                pcClassForObject = cls;
            }
            else {
                if (this.persistentClass.getName().equals(className)) {
                    pcClassForObject = this.persistentClass;
                }
                else {
                    pcClassForObject = clr.classForName(className, this.persistentClass.getClassLoader());
                }
                this.resolvedClasses.put(className, pcClassForObject);
            }
        }
        if (requiresInheritanceCheck) {
            final String[] subclasses = ec.getMetaDataManager().getSubclassesForClass(pcClassForObject.getName(), false);
            if (subclasses == null || subclasses.length == 0) {
                requiresInheritanceCheck = false;
            }
        }
        String warnMsg = null;
        if (Modifier.isAbstract(pcClassForObject.getModifiers())) {
            final String[] subclasses2 = ec.getMetaDataManager().getSubclassesForClass(pcClassForObject.getName(), false);
            if (subclasses2 != null) {
                Class concreteSubclass = null;
                int numConcreteSubclasses = 0;
                for (int i = 0; i < subclasses2.length; ++i) {
                    final Class subcls = clr.classForName(subclasses2[i]);
                    if (!Modifier.isAbstract(subcls.getModifiers())) {
                        ++numConcreteSubclasses;
                        concreteSubclass = subcls;
                    }
                }
                if (numConcreteSubclasses == 1) {
                    pcClassForObject = concreteSubclass;
                    NucleusLogger.DATASTORE_RETRIEVE.warn(PersistentClassROF.LOCALISER.msg("052300", pcClassForObject.getName(), concreteSubclass.getName()));
                }
                else {
                    if (numConcreteSubclasses == 0) {
                        throw new NucleusUserException(PersistentClassROF.LOCALISER.msg("052301", pcClassForObject.getName()));
                    }
                    warnMsg = "Found type=" + pcClassForObject + " but abstract and more than 1 concrete subclass (" + StringUtils.objectArrayToString(subclasses2) + "). Really you need a discriminator " + " to help identifying the type. Choosing " + concreteSubclass;
                    pcClassForObject = concreteSubclass;
                    requiresInheritanceCheck = true;
                }
            }
        }
        AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(pcClassForObject, clr);
        if (cmd == null) {
            return null;
        }
        final int[] fieldNumbers = this.stmtMapping.getMemberNumbers();
        StatementClassMapping mappingDefinition;
        int[] mappedFieldNumbers;
        if (this.acmd instanceof InterfaceMetaData) {
            mappingDefinition = new StatementClassMapping();
            mappingDefinition.setNucleusTypeColumnName(this.stmtMapping.getNucleusTypeColumnName());
            mappedFieldNumbers = new int[fieldNumbers.length];
            for (int j = 0; j < fieldNumbers.length; ++j) {
                final AbstractMemberMetaData mmd = this.acmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumbers[j]);
                mappingDefinition.addMappingForMember(mappedFieldNumbers[j] = cmd.getAbsolutePositionOfMember(mmd.getName()), this.stmtMapping.getMappingForMemberPosition(fieldNumbers[j]));
            }
        }
        else {
            mappingDefinition = this.stmtMapping;
            mappedFieldNumbers = fieldNumbers;
        }
        final VersionMetaData vermd = cmd.getVersionMetaDataForClass();
        Object surrogateVersion = null;
        StatementMappingIndex versionMapping = null;
        if (vermd != null) {
            if (vermd.getFieldName() == null) {
                versionMapping = this.stmtMapping.getMappingForMemberPosition(-2);
            }
            else {
                final AbstractMemberMetaData vermmd = cmd.getMetaDataForMember(vermd.getFieldName());
                versionMapping = this.stmtMapping.getMappingForMemberPosition(vermmd.getAbsoluteFieldNumber());
            }
        }
        if (versionMapping != null) {
            final JavaTypeMapping mapping = versionMapping.getMapping();
            surrogateVersion = mapping.getObject(ec, rs, versionMapping.getColumnPositions());
        }
        Object obj = null;
        boolean needToSetVersion = false;
        if (this.persistentClass.isInterface() && !cmd.isImplementationOfPersistentDefinition()) {
            cmd = ec.getMetaDataManager().getMetaDataForInterface(this.persistentClass, clr);
            if (cmd == null) {
                cmd = ec.getMetaDataManager().getMetaDataForClass(pcClassForObject, clr);
            }
        }
        if (cmd.getIdentityType() == IdentityType.APPLICATION) {
            final int[] pkNumbers = cmd.getPKMemberPositions();
            boolean nullObject = true;
            for (int k = 0; k < pkNumbers.length; ++k) {
                final StatementMappingIndex pkIdx = mappingDefinition.getMappingForMemberPosition(pkNumbers[k]);
                if (pkIdx == null) {
                    throw new NucleusException("You have just executed an SQL statement yet the information for the primary key column(s) is not available! Please generate a testcase and report this issue");
                }
                final int[] colPositions = pkIdx.getColumnPositions();
                for (int l = 0; l < colPositions.length; ++l) {
                    try {
                        final Object pkObj = rs.getObject(colPositions[l]);
                        if (pkObj != null) {
                            nullObject = false;
                            break;
                        }
                    }
                    catch (SQLException sqle2) {
                        NucleusLogger.DATASTORE_RETRIEVE.warn("Exception thrown while retrieving results ", sqle2);
                    }
                    if (!nullObject) {
                        break;
                    }
                }
            }
            if (!nullObject) {
                if (warnMsg != null) {
                    NucleusLogger.DATASTORE_RETRIEVE.warn(warnMsg);
                }
                obj = this.getObjectForApplicationId(ec, rs, mappingDefinition, mappedFieldNumbers, pcClassForObject, cmd, requiresInheritanceCheck, surrogateVersion);
            }
        }
        else if (cmd.getIdentityType() == IdentityType.DATASTORE) {
            final StatementMappingIndex datastoreIdMapping = this.stmtMapping.getMappingForMemberPosition(-1);
            final JavaTypeMapping mapping2 = datastoreIdMapping.getMapping();
            final OID oid = (OID)mapping2.getObject(ec, rs, datastoreIdMapping.getColumnPositions());
            if (oid != null) {
                Object id = oid;
                if (!pcClassForObject.getName().equals(oid.getPcClass())) {
                    id = OIDFactory.getInstance(ec.getNucleusContext(), pcClassForObject.getName(), oid.getKeyValue());
                }
                if (warnMsg != null) {
                    NucleusLogger.DATASTORE_RETRIEVE.warn(warnMsg);
                }
                if (mappedFieldNumbers == null) {
                    obj = ec.findObject(id, false, requiresInheritanceCheck, null);
                    needToSetVersion = true;
                }
                else {
                    obj = this.getObjectForDatastoreId(ec, rs, mappingDefinition, mappedFieldNumbers, id, requiresInheritanceCheck ? null : pcClassForObject, cmd, surrogateVersion);
                }
            }
        }
        else if (cmd.getIdentityType() == IdentityType.NONDURABLE) {
            final Object id2 = ec.newObjectId(className, null);
            if (mappedFieldNumbers == null) {
                obj = ec.findObject(id2, false, requiresInheritanceCheck, null);
                needToSetVersion = true;
            }
            else {
                obj = this.getObjectForDatastoreId(ec, rs, mappingDefinition, mappedFieldNumbers, id2, pcClassForObject, cmd, surrogateVersion);
            }
        }
        if (obj != null && needToSetVersion) {
            if (surrogateVersion != null) {
                final ObjectProvider objSM = ec.findObjectProvider(obj);
                objSM.setVersion(surrogateVersion);
            }
            else if (vermd != null && vermd.getFieldName() != null) {
                final int versionFieldNumber = this.acmd.getMetaDataForMember(vermd.getFieldName()).getAbsoluteFieldNumber();
                if (this.stmtMapping.getMappingForMemberPosition(versionFieldNumber) != null) {
                    final ObjectProvider objSM2 = ec.findObjectProvider(obj);
                    final Object verFieldValue = objSM2.provideField(versionFieldNumber);
                    if (verFieldValue != null) {
                        objSM2.setVersion(verFieldValue);
                    }
                }
            }
        }
        return obj;
    }
    
    private Object getObjectForApplicationId(final ExecutionContext ec, final ResultSet resultSet, final StatementClassMapping mappingDefinition, final int[] fieldNumbers, Class pcClass, final AbstractClassMetaData cmd, final boolean requiresInheritanceCheck, final Object surrogateVersion) {
        final Object id = getIdentityForResultSetRow(this.storeMgr, resultSet, mappingDefinition, ec, cmd, pcClass, requiresInheritanceCheck);
        if (ec.getApiAdapter().isSingleFieldIdentity(id)) {
            pcClass = ec.getApiAdapter().getTargetClassForSingleFieldIdentity(id);
        }
        return ec.findObject(id, new FieldValues() {
            @Override
            public void fetchFields(final ObjectProvider sm) {
                final FieldManager fm = PersistentClassROF.this.storeMgr.getFieldManagerForResultProcessing(sm, resultSet, mappingDefinition);
                sm.replaceFields(fieldNumbers, fm, false);
                if (surrogateVersion != null) {
                    sm.setVersion(surrogateVersion);
                }
                else if (cmd.getVersionMetaData() != null && cmd.getVersionMetaData().getFieldName() != null) {
                    final VersionMetaData vermd = cmd.getVersionMetaData();
                    final int versionFieldNumber = PersistentClassROF.this.acmd.getMetaDataForMember(vermd.getFieldName()).getAbsoluteFieldNumber();
                    if (PersistentClassROF.this.stmtMapping.getMappingForMemberPosition(versionFieldNumber) != null) {
                        final Object verFieldValue = sm.provideField(versionFieldNumber);
                        if (verFieldValue != null) {
                            sm.setVersion(verFieldValue);
                        }
                    }
                }
            }
            
            @Override
            public void fetchNonLoadedFields(final ObjectProvider sm) {
                final FieldManager fm = PersistentClassROF.this.storeMgr.getFieldManagerForResultProcessing(sm, resultSet, mappingDefinition);
                sm.replaceNonLoadedFields(fieldNumbers, fm);
            }
            
            @Override
            public FetchPlan getFetchPlanForLoading() {
                return PersistentClassROF.this.fetchPlan;
            }
        }, pcClass, this.ignoreCache, false);
    }
    
    public static Object getIdentityForResultSetRow(final RDBMSStoreManager storeMgr, final ResultSet resultSet, final StatementClassMapping mappingDefinition, final ExecutionContext ec, final AbstractClassMetaData cmd, final Class pcClass, final boolean inheritanceCheck) {
        if (cmd.getIdentityType() == IdentityType.DATASTORE) {
            return getDatastoreIdentityForResultSetRow(ec, cmd, pcClass, inheritanceCheck, resultSet, mappingDefinition);
        }
        if (cmd.getIdentityType() == IdentityType.APPLICATION) {
            final FieldManager resultsFM = new ResultSetGetter(storeMgr, ec, resultSet, mappingDefinition, cmd);
            return IdentityUtils.getApplicationIdentityForResultSetRow(ec, cmd, pcClass, inheritanceCheck, resultsFM);
        }
        return null;
    }
    
    public static Object getDatastoreIdentityForResultSetRow(final ExecutionContext ec, final AbstractClassMetaData cmd, Class pcClass, final boolean inheritanceCheck, final ResultSet resultSet, final StatementClassMapping mappingDefinition) {
        if (cmd.getIdentityType() != IdentityType.DATASTORE) {
            return null;
        }
        if (pcClass == null) {
            pcClass = ec.getClassLoaderResolver().classForName(cmd.getFullClassName());
        }
        final StatementMappingIndex datastoreIdMapping = mappingDefinition.getMappingForMemberPosition(-1);
        final JavaTypeMapping mapping = datastoreIdMapping.getMapping();
        OID oid = (OID)mapping.getObject(ec, resultSet, datastoreIdMapping.getColumnPositions());
        if (oid != null && !pcClass.getName().equals(oid.getPcClass())) {
            oid = OIDFactory.getInstance(ec.getNucleusContext(), pcClass.getName(), oid.getKeyValue());
        }
        if (!inheritanceCheck) {
            return oid;
        }
        if (ec.hasIdentityInCache(oid)) {
            return oid;
        }
        final String[] subclasses = ec.getMetaDataManager().getSubclassesForClass(pcClass.getName(), true);
        if (subclasses != null) {
            for (int i = 0; i < subclasses.length; ++i) {
                oid = OIDFactory.getInstance(ec.getNucleusContext(), subclasses[i], oid.getKeyValue());
                if (ec.hasIdentityInCache(oid)) {
                    return oid;
                }
            }
        }
        final String className = ec.getStoreManager().getClassNameForObjectID(oid, ec.getClassLoaderResolver(), ec);
        return OIDFactory.getInstance(ec.getNucleusContext(), className, oid.getKeyValue());
    }
    
    private Object getObjectForDatastoreId(final ExecutionContext ec, final ResultSet resultSet, final StatementClassMapping mappingDefinition, final int[] fieldNumbers, final Object oid, final Class pcClass, final AbstractClassMetaData cmd, final Object surrogateVersion) {
        if (oid == null) {
            return null;
        }
        return ec.findObject(oid, new FieldValues() {
            @Override
            public void fetchFields(final ObjectProvider sm) {
                final FieldManager fm = PersistentClassROF.this.storeMgr.getFieldManagerForResultProcessing(sm, resultSet, mappingDefinition);
                sm.replaceFields(fieldNumbers, fm, false);
                if (surrogateVersion != null) {
                    sm.setVersion(surrogateVersion);
                }
                else if (cmd.getVersionMetaData() != null && cmd.getVersionMetaData().getFieldName() != null) {
                    final VersionMetaData vermd = cmd.getVersionMetaData();
                    final int versionFieldNumber = PersistentClassROF.this.acmd.getMetaDataForMember(vermd.getFieldName()).getAbsoluteFieldNumber();
                    if (PersistentClassROF.this.stmtMapping.getMappingForMemberPosition(versionFieldNumber) != null) {
                        final Object verFieldValue = sm.provideField(versionFieldNumber);
                        if (verFieldValue != null) {
                            sm.setVersion(verFieldValue);
                        }
                    }
                }
            }
            
            @Override
            public void fetchNonLoadedFields(final ObjectProvider sm) {
                final FieldManager fm = PersistentClassROF.this.storeMgr.getFieldManagerForResultProcessing(sm, resultSet, mappingDefinition);
                sm.replaceNonLoadedFields(fieldNumbers, fm);
            }
            
            @Override
            public FetchPlan getFetchPlanForLoading() {
                return PersistentClassROF.this.fetchPlan;
            }
        }, pcClass, this.ignoreCache, false);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
