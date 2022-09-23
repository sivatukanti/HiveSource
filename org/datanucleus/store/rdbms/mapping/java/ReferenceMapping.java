// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.ClassConstants;
import org.datanucleus.identity.OIDFactory;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.identity.OID;
import org.datanucleus.metadata.IdentityType;
import java.sql.ResultSet;
import org.datanucleus.state.ObjectProvider;
import java.sql.PreparedStatement;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import java.util.Iterator;
import java.util.Collection;
import org.datanucleus.store.rdbms.table.ColumnCreator;
import org.datanucleus.store.rdbms.exceptions.NoTableManagedException;
import java.util.ArrayList;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.ValueMetaData;
import org.datanucleus.metadata.KeyMetaData;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;

public abstract class ReferenceMapping extends MultiPersistableMapping implements MappingCallbacks
{
    protected static final Localiser LOCALISER_MAPPED;
    public static final int PER_IMPLEMENTATION_MAPPING = 0;
    public static final int ID_MAPPING = 1;
    public static final int XCALIA_MAPPING = 2;
    protected int mappingStrategy;
    
    public ReferenceMapping() {
        this.mappingStrategy = 0;
    }
    
    @Override
    public void initialize(final AbstractMemberMetaData mmd, final Table table, final ClassLoaderResolver clr) {
        if (mmd.hasExtension("mapping-strategy")) {
            final String strategy = mmd.getValueForExtension("mapping-strategy");
            if (strategy.equalsIgnoreCase("identity")) {
                this.mappingStrategy = 1;
            }
            else if (strategy.equalsIgnoreCase("xcalia")) {
                this.mappingStrategy = 2;
            }
        }
        this.numberOfDatastoreMappings = 0;
        super.initialize(mmd, table, clr);
        this.prepareDatastoreMapping(clr);
    }
    
    public int getMappingStrategy() {
        return this.mappingStrategy;
    }
    
    protected void prepareDatastoreMapping(final ClassLoaderResolver clr) {
        if (this.mappingStrategy == 0) {
            if (this.roleForMember == 4) {
                ColumnMetaData[] colmds = null;
                final ElementMetaData elemmd = this.mmd.getElementMetaData();
                if (elemmd != null && elemmd.getColumnMetaData() != null && elemmd.getColumnMetaData().length > 0) {
                    colmds = elemmd.getColumnMetaData();
                }
                this.createPerImplementationColumnsForReferenceField(false, false, false, false, this.roleForMember, colmds, clr);
            }
            else if (this.roleForMember == 3) {
                ColumnMetaData[] colmds = null;
                final AbstractMemberMetaData[] relatedMmds = this.mmd.getRelatedMemberMetaData(clr);
                final ElementMetaData elemmd2 = this.mmd.getElementMetaData();
                if (elemmd2 != null && elemmd2.getColumnMetaData() != null && elemmd2.getColumnMetaData().length > 0) {
                    colmds = elemmd2.getColumnMetaData();
                }
                else if (relatedMmds != null && relatedMmds[0].getJoinMetaData() != null && relatedMmds[0].getJoinMetaData().getColumnMetaData() != null && relatedMmds[0].getJoinMetaData().getColumnMetaData().length > 0) {
                    colmds = relatedMmds[0].getJoinMetaData().getColumnMetaData();
                }
                this.createPerImplementationColumnsForReferenceField(false, false, false, false, this.roleForMember, colmds, clr);
            }
            else if (this.roleForMember == 5) {
                ColumnMetaData[] colmds = null;
                final KeyMetaData keymd = this.mmd.getKeyMetaData();
                if (keymd != null && keymd.getColumnMetaData() != null && keymd.getColumnMetaData().length > 0) {
                    colmds = keymd.getColumnMetaData();
                }
                this.createPerImplementationColumnsForReferenceField(false, false, false, false, this.roleForMember, colmds, clr);
            }
            else if (this.roleForMember == 6) {
                ColumnMetaData[] colmds = null;
                final ValueMetaData valuemd = this.mmd.getValueMetaData();
                if (valuemd != null && valuemd.getColumnMetaData() != null && valuemd.getColumnMetaData().length > 0) {
                    colmds = valuemd.getColumnMetaData();
                }
                this.createPerImplementationColumnsForReferenceField(false, false, false, false, this.roleForMember, colmds, clr);
            }
            else if (this.mmd.getMappedBy() == null) {
                this.createPerImplementationColumnsForReferenceField(false, true, false, this.mmd.isEmbedded() || this.mmd.getElementMetaData() != null, this.roleForMember, this.mmd.getColumnMetaData(), clr);
            }
            else {
                final AbstractClassMetaData refCmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForInterface(this.mmd.getType(), clr);
                if (refCmd != null && refCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                    final AbstractClassMetaData[] cmds = this.storeMgr.getClassesManagingTableForClass(refCmd, clr);
                    if (cmds == null || cmds.length <= 0) {
                        return;
                    }
                    if (cmds.length > 1) {
                        NucleusLogger.PERSISTENCE.warn("Field " + this.mmd.getFullFieldName() + " represents either a 1-1 relation, or a N-1 relation where the other end uses" + " \"subclass-table\" inheritance strategy and more than 1 subclasses with a table. " + "This is not fully supported currently");
                    }
                    this.storeMgr.getDatastoreClass(cmds[0].getFullClassName(), clr).getIdMapping();
                }
                else {
                    final String[] implTypes = MetaDataUtils.getInstance().getImplementationNamesForReferenceField(this.mmd, 2, clr, this.storeMgr.getMetaDataManager());
                    for (int j = 0; j < implTypes.length; ++j) {
                        final JavaTypeMapping refMapping = this.storeMgr.getDatastoreClass(implTypes[j], clr).getIdMapping();
                        final JavaTypeMapping mapping = this.storeMgr.getMappingManager().getMapping(clr.classForName(implTypes[j]));
                        mapping.setReferenceMapping(refMapping);
                        this.addJavaTypeMapping(mapping);
                    }
                }
            }
        }
        else if (this.mappingStrategy == 1 || this.mappingStrategy == 2) {
            final MappingManager mapMgr = this.storeMgr.getMappingManager();
            final JavaTypeMapping mapping2 = mapMgr.getMapping(String.class);
            mapping2.setMemberMetaData(this.mmd);
            mapping2.setTable(this.table);
            mapping2.setRoleForMember(this.roleForMember);
            final Column col = mapMgr.createColumn(mapping2, String.class.getName(), 0);
            mapMgr.createDatastoreMapping(mapping2, this.mmd, 0, col);
            this.addJavaTypeMapping(mapping2);
        }
    }
    
    private String getReferenceFieldType(final int fieldRole) {
        String fieldTypeName = this.mmd.getTypeName();
        if (this.mmd.getFieldTypes() != null && this.mmd.getFieldTypes().length == 1) {
            fieldTypeName = this.mmd.getFieldTypes()[0];
        }
        if (this.mmd.hasCollection()) {
            fieldTypeName = this.mmd.getCollection().getElementType();
        }
        else if (this.mmd.hasArray()) {
            fieldTypeName = this.mmd.getArray().getElementType();
        }
        else if (this.mmd.hasMap()) {
            if (fieldRole == 5) {
                fieldTypeName = this.mmd.getMap().getKeyType();
            }
            else if (fieldRole == 6) {
                fieldTypeName = this.mmd.getMap().getValueType();
            }
        }
        return fieldTypeName;
    }
    
    void createPerImplementationColumnsForReferenceField(boolean pk, boolean nullable, final boolean serialised, final boolean embedded, final int fieldRole, final ColumnMetaData[] columnMetaData, final ClassLoaderResolver clr) {
        if (this instanceof InterfaceMapping && this.mmd != null && this.mmd.hasExtension("implementation-classes")) {
            ((InterfaceMapping)this).setImplementationClasses(this.mmd.getValueForExtension("implementation-classes"));
        }
        String[] implTypes = null;
        try {
            implTypes = MetaDataUtils.getInstance().getImplementationNamesForReferenceField(this.mmd, fieldRole, clr, this.storeMgr.getMetaDataManager());
        }
        catch (NucleusUserException nue) {
            if (this.storeMgr.getBooleanProperty("datanucleus.store.allowReferencesWithNoImplementations", false)) {
                NucleusLogger.DATASTORE_SCHEMA.warn("Possible problem encountered while adding columns for field " + this.mmd.getFullFieldName() + " : " + nue.getMessage());
                return;
            }
            throw nue;
        }
        if (implTypes.length > 1) {
            pk = false;
        }
        if (implTypes.length > 1 && !pk) {
            nullable = true;
        }
        final Collection implClasses = new ArrayList();
        for (int i = 0; i < implTypes.length; ++i) {
            final Class type = clr.classForName(implTypes[i]);
            if (type == null) {
                throw new NucleusUserException(ReferenceMapping.LOCALISER_MAPPED.msg("020189", this.mmd.getTypeName(), implTypes[i]));
            }
            if (type.isInterface()) {
                throw new NucleusUserException(ReferenceMapping.LOCALISER_MAPPED.msg("020190", this.mmd.getFullFieldName(), this.mmd.getTypeName(), implTypes[i]));
            }
            final Iterator iter = implClasses.iterator();
            boolean toBeAdded = true;
            Class clsToSwap = null;
            while (iter.hasNext()) {
                final Class cls = iter.next();
                if (cls == type) {
                    toBeAdded = false;
                    break;
                }
                if (type.isAssignableFrom(cls)) {
                    clsToSwap = cls;
                    toBeAdded = false;
                    break;
                }
                if (cls.isAssignableFrom(type)) {
                    toBeAdded = false;
                    break;
                }
            }
            if (toBeAdded) {
                implClasses.add(type);
            }
            else if (clsToSwap != null) {
                implClasses.remove(clsToSwap);
                implClasses.add(type);
            }
        }
        int colPos = 0;
        for (final Class implClass : implClasses) {
            boolean present = false;
            for (int numJavaTypeMappings = this.getJavaTypeMapping().length, j = 0; j < numJavaTypeMappings; ++j) {
                final JavaTypeMapping implMapping = this.getJavaTypeMapping()[j];
                if (implClass.getName().equals(implMapping.getType())) {
                    present = true;
                }
            }
            if (present) {
                continue;
            }
            final String fieldTypeName = this.getReferenceFieldType(fieldRole);
            final boolean isPersistentInterfaceField = this.storeMgr.getNucleusContext().getMetaDataManager().isPersistentInterface(fieldTypeName);
            boolean columnsNeeded = true;
            if (isPersistentInterfaceField && !this.storeMgr.getNucleusContext().getMetaDataManager().isPersistentInterfaceImplementation(fieldTypeName, implClass.getName())) {
                columnsNeeded = false;
            }
            if (!columnsNeeded) {
                continue;
            }
            JavaTypeMapping m;
            if (this.storeMgr.getMappedTypeManager().isSupportedMappedType(implClass.getName())) {
                m = this.storeMgr.getMappingManager().getMapping(implClass, serialised, embedded, this.mmd.getFullFieldName());
            }
            else {
                try {
                    final DatastoreClass dc = this.storeMgr.getDatastoreClass(implClass.getName(), clr);
                    m = dc.getIdMapping();
                }
                catch (NoTableManagedException ex) {
                    throw new NucleusUserException("Cannot define columns for " + this.mmd.getFullFieldName() + " due to " + ex.getMessage(), ex);
                }
            }
            ColumnMetaData[] columnMetaDataForType = null;
            if (columnMetaData != null && columnMetaData.length > 0) {
                if (columnMetaData.length < colPos + m.getNumberOfDatastoreMappings()) {
                    throw new NucleusUserException(ReferenceMapping.LOCALISER_MAPPED.msg("020186", this.mmd.getFullFieldName(), "" + columnMetaData.length, "" + (colPos + m.getNumberOfDatastoreMappings())));
                }
                columnMetaDataForType = new ColumnMetaData[m.getNumberOfDatastoreMappings()];
                System.arraycopy(columnMetaData, colPos, columnMetaDataForType, 0, columnMetaDataForType.length);
                colPos += columnMetaDataForType.length;
            }
            ColumnCreator.createColumnsForField(implClass, this, this.table, this.storeMgr, this.mmd, pk, nullable, serialised, embedded, fieldRole, columnMetaDataForType, clr, true);
            if (!NucleusLogger.DATASTORE.isInfoEnabled()) {
                continue;
            }
            NucleusLogger.DATASTORE.info(ReferenceMapping.LOCALISER_MAPPED.msg("020188", implClass, this.mmd.getName()));
        }
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        if ((this.mappingStrategy == 1 || this.mappingStrategy == 2) && index == 0) {
            return String.class.getName();
        }
        return super.getJavaTypeForDatastoreMapping(index);
    }
    
    public int getMappingNumberForValue(final ExecutionContext ec, final Object value) {
        if (this.mappingStrategy == 0) {
            return super.getMappingNumberForValue(ec, value);
        }
        if (this.mappingStrategy == 1 || this.mappingStrategy == 2) {
            return -2;
        }
        throw new NucleusException("Mapping strategy of interface/Object fields not yet supported");
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] pos, final Object value, final ObjectProvider ownerOP, final int ownerFieldNumber) {
        if (this.mappingStrategy == 0) {
            super.setObject(ec, ps, pos, value, ownerOP, ownerFieldNumber);
        }
        else if (this.mappingStrategy == 1 || this.mappingStrategy == 2) {
            if (value == null) {
                this.getJavaTypeMapping()[0].setString(ec, ps, pos, null);
            }
            else {
                final String refString = this.getReferenceStringForObject(ec, value);
                this.getJavaTypeMapping()[0].setString(ec, ps, pos, refString);
            }
        }
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet rs, final int[] pos) {
        if (this.mappingStrategy == 0) {
            return super.getObject(ec, rs, pos);
        }
        if (this.mappingStrategy != 1 && this.mappingStrategy != 2) {
            throw new NucleusException("Mapping strategy of interface/Object fields not yet supported");
        }
        final String refString = this.getJavaTypeMapping()[0].getString(ec, rs, pos);
        if (refString == null) {
            return null;
        }
        return this.getObjectForReferenceString(ec, refString);
    }
    
    @Override
    public Class getJavaType() {
        return null;
    }
    
    protected String getReferenceStringForObject(final ExecutionContext ec, final Object value) {
        if (ec.getApiAdapter().isPersistable(value)) {
            ObjectProvider op = ec.findObjectProvider(value);
            if (op == null) {
                ec.persistObjectInternal(value, null, -1, 0);
                op = ec.findObjectProvider(value);
                op.flush();
            }
            String refString = null;
            if (this.mappingStrategy == 1) {
                refString = value.getClass().getName() + ":" + op.getInternalObjectId();
            }
            else if (this.mappingStrategy == 2) {
                final AbstractClassMetaData cmd = op.getClassMetaData();
                final DiscriminatorMetaData dismd = cmd.getDiscriminatorMetaData();
                String definer = null;
                if (dismd != null && dismd.getValue() != null) {
                    definer = dismd.getValue();
                }
                else {
                    definer = cmd.getFullClassName();
                }
                if (cmd.getIdentityType() == IdentityType.DATASTORE) {
                    refString = definer + ":" + ((OID)op.getInternalObjectId()).getKeyValue();
                }
                else {
                    refString = definer + ":" + op.getInternalObjectId().toString();
                }
            }
            return refString;
        }
        throw new NucleusException("Identity mapping of non-persistable interface/Object fields not supported");
    }
    
    protected Object getObjectForReferenceString(final ExecutionContext ec, final String refString) {
        final int sepPos = refString.indexOf(58);
        final String refDefiner = refString.substring(0, sepPos);
        String refClassName = null;
        final String refId = refString.substring(sepPos + 1);
        AbstractClassMetaData refCmd = null;
        if (this.mappingStrategy == 1) {
            refCmd = ec.getMetaDataManager().getMetaDataForClass(refDefiner, ec.getClassLoaderResolver());
        }
        else {
            refCmd = ec.getMetaDataManager().getMetaDataForClass(refDefiner, ec.getClassLoaderResolver());
            if (refCmd == null) {
                refCmd = ec.getMetaDataManager().getMetaDataForDiscriminator(refDefiner);
            }
        }
        if (refCmd == null) {
            throw new NucleusException("Reference field contains reference to class of type " + refDefiner + " but no metadata found for this class");
        }
        refClassName = refCmd.getFullClassName();
        Object id = null;
        if (refCmd.getIdentityType() == IdentityType.DATASTORE) {
            if (this.mappingStrategy == 1) {
                id = OIDFactory.getInstance(ec.getNucleusContext(), refId);
            }
            else if (this.mappingStrategy == 2) {
                id = OIDFactory.getInstance(ec.getNucleusContext(), refCmd.getFullClassName(), refId);
            }
        }
        else if (refCmd.getIdentityType() == IdentityType.APPLICATION) {
            id = ec.getApiAdapter().getNewApplicationIdentityObjectId(ec.getClassLoaderResolver(), refCmd, refId);
        }
        return ec.findObject(id, true, false, refClassName);
    }
    
    @Override
    public void postFetch(final ObjectProvider op) {
    }
    
    @Override
    public void insertPostProcessing(final ObjectProvider op) {
    }
    
    @Override
    public void postInsert(final ObjectProvider op) {
    }
    
    @Override
    public void postUpdate(final ObjectProvider op) {
    }
    
    @Override
    public void preDelete(final ObjectProvider op) {
        final boolean isDependentElement = this.mmd.isDependent();
        if (!isDependentElement) {
            return;
        }
        for (int i = 0; i < this.javaTypeMappings.length; ++i) {
            final JavaTypeMapping mapping = this.javaTypeMappings[i];
            if (mapping instanceof PersistableMapping) {
                final int fieldNumber = this.getMemberMetaData().getAbsoluteFieldNumber();
                op.isLoaded(fieldNumber);
                final Object pc = op.provideField(fieldNumber);
                if (pc != null) {
                    op.replaceFieldMakeDirty(fieldNumber, null);
                    this.storeMgr.getPersistenceHandler().updateObject(op, new int[] { fieldNumber });
                    op.getExecutionContext().deleteObjectInternal(pc);
                }
            }
        }
    }
    
    static {
        LOCALISER_MAPPED = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
