// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.store.rdbms.key.CandidateKey;
import org.datanucleus.metadata.UniqueMetaData;
import org.datanucleus.store.rdbms.key.Index;
import org.datanucleus.metadata.IndexMetaData;
import java.util.Set;
import java.util.Collection;
import org.datanucleus.metadata.ForeignKeyMetaData;
import org.datanucleus.store.rdbms.exceptions.NoTableManagedException;
import org.datanucleus.store.rdbms.key.ForeignKey;
import java.util.ArrayList;
import java.util.List;
import org.datanucleus.metadata.ValueMetaData;
import org.datanucleus.metadata.KeyMetaData;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedValuePCMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedKeyPCMapping;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import java.util.Map;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class MapTable extends JoinTable implements DatastoreMap
{
    private JavaTypeMapping keyMapping;
    private JavaTypeMapping valueMapping;
    private JavaTypeMapping orderMapping;
    protected Map embeddedKeyMappingsMap;
    protected Map embeddedValueMappingsMap;
    
    public MapTable(final DatastoreIdentifier tableName, final AbstractMemberMetaData mmd, final RDBMSStoreManager storeMgr) {
        super(tableName, mmd, storeMgr);
    }
    
    @Override
    public void initialize(final ClassLoaderResolver clr) {
        this.assertIsUninitialized();
        final MapMetaData mapmd = this.mmd.getMap();
        if (mapmd == null) {
            throw new NucleusUserException(MapTable.LOCALISER.msg("057017", this.mmd));
        }
        final PrimaryKeyMetaData pkmd = (this.mmd.getJoinMetaData() != null) ? this.mmd.getJoinMetaData().getPrimaryKeyMetaData() : null;
        final boolean pkColsSpecified = pkmd != null && pkmd.getColumnMetaData() != null;
        final boolean pkRequired = this.requiresPrimaryKey();
        ColumnMetaData[] ownerColmd = null;
        if (this.mmd.getJoinMetaData() != null && this.mmd.getJoinMetaData().getColumnMetaData() != null && this.mmd.getJoinMetaData().getColumnMetaData().length > 0) {
            ownerColmd = this.mmd.getJoinMetaData().getColumnMetaData();
        }
        this.ownerMapping = ColumnCreator.createColumnsForJoinTables(clr.classForName(this.ownerType), this.mmd, ownerColmd, this.storeMgr, this, pkRequired, false, 1, clr);
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            this.debugMapping(this.ownerMapping);
        }
        final String keyValueFieldName = (this.mmd.getKeyMetaData() != null) ? this.mmd.getKeyMetaData().getMappedBy() : null;
        final String valueKeyFieldName = (this.mmd.getValueMetaData() != null) ? this.mmd.getValueMetaData().getMappedBy() : null;
        final boolean keyPC = this.mmd.hasMap() && this.mmd.getMap().keyIsPersistent();
        final Class keyCls = clr.classForName(mapmd.getKeyType());
        if (keyValueFieldName == null || !this.isEmbeddedValuePC()) {
            if (this.isSerialisedKey() || this.isEmbeddedKeyPC() || (this.isEmbeddedKey() && !keyPC) || ClassUtils.isReferenceType(keyCls)) {
                this.keyMapping = this.storeMgr.getMappingManager().getMapping(this, this.mmd, clr, 5);
                if (Boolean.TRUE.equals(this.mmd.getContainer().allowNulls())) {
                    for (int i = 0; i < this.keyMapping.getNumberOfDatastoreMappings(); ++i) {
                        final Column elementCol = this.keyMapping.getDatastoreMapping(i).getColumn();
                        elementCol.setNullable();
                    }
                }
                if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                    this.debugMapping(this.keyMapping);
                }
                if (valueKeyFieldName != null && this.isEmbeddedKeyPC()) {
                    final EmbeddedKeyPCMapping embMapping = (EmbeddedKeyPCMapping)this.keyMapping;
                    this.valueMapping = embMapping.getJavaTypeMapping(valueKeyFieldName);
                }
            }
            else {
                ColumnMetaData[] keyColmd = null;
                final KeyMetaData keymd = this.mmd.getKeyMetaData();
                if (keymd != null && keymd.getColumnMetaData() != null && keymd.getColumnMetaData().length > 0) {
                    keyColmd = keymd.getColumnMetaData();
                }
                this.keyMapping = ColumnCreator.createColumnsForJoinTables(keyCls, this.mmd, keyColmd, this.storeMgr, this, false, false, 5, clr);
                if (this.mmd.getContainer().allowNulls() == Boolean.TRUE) {
                    for (int j = 0; j < this.keyMapping.getNumberOfDatastoreMappings(); ++j) {
                        final Column elementCol2 = this.keyMapping.getDatastoreMapping(j).getColumn();
                        elementCol2.setNullable();
                    }
                }
                if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                    this.debugMapping(this.keyMapping);
                }
            }
        }
        final boolean valuePC = this.mmd.hasMap() && this.mmd.getMap().valueIsPersistent();
        final Class valueCls = clr.classForName(mapmd.getValueType());
        if (valueKeyFieldName == null || !this.isEmbeddedKeyPC()) {
            if (this.isSerialisedValue() || this.isEmbeddedValuePC() || (this.isEmbeddedValue() && !valuePC) || ClassUtils.isReferenceType(valueCls)) {
                this.valueMapping = this.storeMgr.getMappingManager().getMapping(this, this.mmd, clr, 6);
                if (this.mmd.getContainer().allowNulls() == Boolean.TRUE) {
                    for (int j = 0; j < this.valueMapping.getNumberOfDatastoreMappings(); ++j) {
                        final Column elementCol2 = this.valueMapping.getDatastoreMapping(j).getColumn();
                        elementCol2.setNullable();
                    }
                }
                if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                    this.debugMapping(this.valueMapping);
                }
                if (keyValueFieldName != null && this.isEmbeddedValuePC()) {
                    final EmbeddedValuePCMapping embMapping2 = (EmbeddedValuePCMapping)this.valueMapping;
                    this.keyMapping = embMapping2.getJavaTypeMapping(keyValueFieldName);
                }
            }
            else {
                ColumnMetaData[] valueColmd = null;
                final ValueMetaData valuemd = this.mmd.getValueMetaData();
                if (valuemd != null && valuemd.getColumnMetaData() != null && valuemd.getColumnMetaData().length > 0) {
                    valueColmd = valuemd.getColumnMetaData();
                }
                this.valueMapping = ColumnCreator.createColumnsForJoinTables(clr.classForName(mapmd.getValueType()), this.mmd, valueColmd, this.storeMgr, this, false, true, 6, clr);
                if (this.mmd.getContainer().allowNulls() == Boolean.TRUE) {
                    for (int k = 0; k < this.valueMapping.getNumberOfDatastoreMappings(); ++k) {
                        final Column elementCol3 = this.valueMapping.getDatastoreMapping(k).getColumn();
                        elementCol3.setNullable();
                    }
                }
                if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                    this.debugMapping(this.valueMapping);
                }
            }
        }
        boolean orderRequired = false;
        if (this.mmd.getOrderMetaData() != null) {
            orderRequired = true;
        }
        else if (this.requiresPrimaryKey() && !pkColsSpecified) {
            if (this.isEmbeddedKeyPC()) {
                if (this.mmd.getMap().getKeyClassMetaData(clr, this.storeMgr.getMetaDataManager()).getIdentityType() != IdentityType.APPLICATION) {
                    orderRequired = true;
                }
            }
            else if (this.isSerialisedKey()) {
                orderRequired = true;
            }
            else if (this.keyMapping instanceof ReferenceMapping) {
                final ReferenceMapping refMapping = (ReferenceMapping)this.keyMapping;
                if (refMapping.getJavaTypeMapping().length > 1) {
                    orderRequired = true;
                }
            }
            else if (!(this.keyMapping instanceof PersistableMapping)) {
                final Column elementCol2 = this.keyMapping.getDatastoreMapping(0).getColumn();
                if (!this.storeMgr.getDatastoreAdapter().isValidPrimaryKeyType(elementCol2.getJdbcType())) {
                    orderRequired = true;
                }
            }
        }
        if (orderRequired) {
            ColumnMetaData orderColmd = null;
            if (this.mmd.getOrderMetaData() != null && this.mmd.getOrderMetaData().getColumnMetaData() != null && this.mmd.getOrderMetaData().getColumnMetaData().length > 0) {
                orderColmd = this.mmd.getOrderMetaData().getColumnMetaData()[0];
                if (orderColmd.getName() == null) {
                    orderColmd = new ColumnMetaData(orderColmd);
                    if (this.mmd.hasExtension("adapter-column-name")) {
                        orderColmd.setName(this.mmd.getValueForExtension("adapter-column-name"));
                    }
                    else {
                        final DatastoreIdentifier id = this.storeMgr.getIdentifierFactory().newIndexFieldIdentifier(this.mmd);
                        orderColmd.setName(id.getIdentifierName());
                    }
                }
            }
            else if (this.mmd.hasExtension("adapter-column-name")) {
                orderColmd = new ColumnMetaData();
                orderColmd.setName(this.mmd.getValueForExtension("adapter-column-name"));
            }
            else {
                final DatastoreIdentifier id = this.storeMgr.getIdentifierFactory().newIndexFieldIdentifier(this.mmd);
                orderColmd = new ColumnMetaData();
                orderColmd.setName(id.getIdentifierName());
            }
            ColumnCreator.createIndexColumn(this.orderMapping = this.storeMgr.getMappingManager().getMapping(Integer.TYPE), this.storeMgr, clr, this, orderColmd, pkRequired && !pkColsSpecified);
            if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                this.debugMapping(this.orderMapping);
            }
        }
        if (pkRequired) {
            if (pkColsSpecified) {
                this.applyUserPrimaryKeySpecification(pkmd);
            }
            else if (orderRequired) {
                this.orderMapping.getDatastoreMapping(0).getColumn().setAsPrimaryKey();
            }
            else {
                for (int l = 0; l < this.keyMapping.getNumberOfDatastoreMappings(); ++l) {
                    this.keyMapping.getDatastoreMapping(l).getColumn().setAsPrimaryKey();
                }
            }
        }
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(MapTable.LOCALISER.msg("057023", this));
        }
        this.storeMgr.registerTableInitialized(this);
        this.state = 2;
    }
    
    protected void applyUserPrimaryKeySpecification(final PrimaryKeyMetaData pkmd) {
        final ColumnMetaData[] pkCols = pkmd.getColumnMetaData();
        for (int i = 0; i < pkCols.length; ++i) {
            final String colName = pkCols[i].getName();
            boolean found = false;
            for (int j = 0; j < this.ownerMapping.getNumberOfDatastoreMappings(); ++j) {
                if (this.ownerMapping.getDatastoreMapping(j).getColumn().getIdentifier().getIdentifierName().equals(colName)) {
                    this.ownerMapping.getDatastoreMapping(j).getColumn().setAsPrimaryKey();
                    found = true;
                }
            }
            if (!found) {
                for (int j = 0; j < this.keyMapping.getNumberOfDatastoreMappings(); ++j) {
                    if (this.keyMapping.getDatastoreMapping(j).getColumn().getIdentifier().getIdentifierName().equals(colName)) {
                        this.keyMapping.getDatastoreMapping(j).getColumn().setAsPrimaryKey();
                        found = true;
                    }
                }
            }
            if (!found) {
                for (int j = 0; j < this.valueMapping.getNumberOfDatastoreMappings(); ++j) {
                    if (this.valueMapping.getDatastoreMapping(j).getColumn().getIdentifier().getIdentifierName().equals(colName)) {
                        this.valueMapping.getDatastoreMapping(j).getColumn().setAsPrimaryKey();
                        found = true;
                    }
                }
            }
            if (!found) {
                throw new NucleusUserException(MapTable.LOCALISER.msg("057040", this.toString(), colName));
            }
        }
    }
    
    public boolean isEmbeddedKey() {
        return (this.mmd.getMap() == null || !this.mmd.getMap().isSerializedKey()) && (this.mmd.getMap() != null && this.mmd.getMap().isEmbeddedKey());
    }
    
    public boolean isSerialisedKey() {
        return this.mmd.getMap() != null && this.mmd.getMap().isSerializedKey();
    }
    
    public boolean isSerialisedKeyPC() {
        return this.mmd.getMap() != null && this.mmd.getMap().isSerializedKey() && this.mmd.getMap().keyIsPersistent();
    }
    
    public boolean isEmbeddedKeyPC() {
        return (this.mmd.getMap() == null || !this.mmd.getMap().isSerializedKey()) && (this.mmd.getKeyMetaData() != null && this.mmd.getKeyMetaData().getEmbeddedMetaData() != null);
    }
    
    public boolean isEmbeddedValue() {
        return (this.mmd.getMap() == null || !this.mmd.getMap().isSerializedValue()) && (this.mmd.getMap() != null && this.mmd.getMap().isEmbeddedValue());
    }
    
    public boolean isSerialisedValue() {
        return this.mmd.getMap() != null && this.mmd.getMap().isSerializedValue();
    }
    
    public boolean isSerialisedValuePC() {
        return this.mmd.getMap() != null && this.mmd.getMap().isSerializedValue() && this.mmd.getMap().valueIsPersistent();
    }
    
    public boolean isEmbeddedValuePC() {
        return (this.mmd.getMap() == null || !this.mmd.getMap().isSerializedValue()) && (this.mmd.getValueMetaData() != null && this.mmd.getValueMetaData().getEmbeddedMetaData() != null);
    }
    
    @Override
    public JavaTypeMapping getKeyMapping() {
        this.assertIsInitialized();
        return this.keyMapping;
    }
    
    @Override
    public JavaTypeMapping getValueMapping() {
        this.assertIsInitialized();
        return this.valueMapping;
    }
    
    public String getKeyType() {
        return this.mmd.getMap().getKeyType();
    }
    
    public String getValueType() {
        return this.mmd.getMap().getValueType();
    }
    
    public JavaTypeMapping getOrderMapping() {
        this.assertIsInitialized();
        return this.orderMapping;
    }
    
    @Override
    public List getExpectedForeignKeys(final ClassLoaderResolver clr) {
        this.assertIsInitialized();
        boolean autoMode = false;
        if (this.storeMgr.getStringProperty("datanucleus.rdbms.constraintCreateMode").equals("DataNucleus")) {
            autoMode = true;
        }
        final ArrayList foreignKeys = new ArrayList();
        try {
            DatastoreClass referencedTable = this.storeMgr.getDatastoreClass(this.ownerType, clr);
            if (referencedTable != null) {
                ForeignKeyMetaData fkmd = null;
                if (this.mmd.getJoinMetaData() != null) {
                    fkmd = this.mmd.getJoinMetaData().getForeignKeyMetaData();
                }
                if (fkmd != null || autoMode) {
                    final ForeignKey fk = new ForeignKey(this.ownerMapping, this.dba, referencedTable, true);
                    fk.setForMetaData(fkmd);
                    foreignKeys.add(fk);
                }
            }
            if (!this.isSerialisedValuePC()) {
                if (this.isEmbeddedValuePC()) {
                    final EmbeddedValuePCMapping embMapping = (EmbeddedValuePCMapping)this.valueMapping;
                    for (int i = 0; i < embMapping.getNumberOfJavaTypeMappings(); ++i) {
                        final JavaTypeMapping embFieldMapping = embMapping.getJavaTypeMapping(i);
                        final AbstractMemberMetaData embFmd = embFieldMapping.getMemberMetaData();
                        if (ClassUtils.isReferenceType(embFmd.getType()) && embFieldMapping instanceof ReferenceMapping) {
                            final Collection fks = TableUtils.getForeignKeysForReferenceField(embFieldMapping, embFmd, autoMode, this.storeMgr, clr);
                            foreignKeys.addAll(fks);
                        }
                        else if (this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(embFmd.getType(), clr) != null && embFieldMapping.getNumberOfDatastoreMappings() > 0 && embFieldMapping instanceof PersistableMapping) {
                            final ForeignKey fk2 = TableUtils.getForeignKeyForPCField(embFieldMapping, embFmd, autoMode, this.storeMgr, clr);
                            if (fk2 != null) {
                                foreignKeys.add(fk2);
                            }
                        }
                    }
                }
                else if (this.mmd.getMap().valueIsPersistent()) {
                    referencedTable = this.storeMgr.getDatastoreClass(this.mmd.getMap().getValueType(), clr);
                    if (referencedTable != null) {
                        ForeignKeyMetaData fkmd = null;
                        if (this.mmd.getValueMetaData() != null) {
                            fkmd = this.mmd.getValueMetaData().getForeignKeyMetaData();
                        }
                        if (fkmd != null || autoMode) {
                            final ForeignKey fk = new ForeignKey(this.valueMapping, this.dba, referencedTable, true);
                            fk.setForMetaData(fkmd);
                            foreignKeys.add(fk);
                        }
                    }
                }
            }
            if (!this.isSerialisedKeyPC()) {
                if (this.isEmbeddedKeyPC()) {
                    final EmbeddedKeyPCMapping embMapping2 = (EmbeddedKeyPCMapping)this.keyMapping;
                    for (int i = 0; i < embMapping2.getNumberOfJavaTypeMappings(); ++i) {
                        final JavaTypeMapping embFieldMapping = embMapping2.getJavaTypeMapping(i);
                        final AbstractMemberMetaData embFmd = embFieldMapping.getMemberMetaData();
                        if (ClassUtils.isReferenceType(embFmd.getType()) && embFieldMapping instanceof ReferenceMapping) {
                            final Collection fks = TableUtils.getForeignKeysForReferenceField(embFieldMapping, embFmd, autoMode, this.storeMgr, clr);
                            foreignKeys.addAll(fks);
                        }
                        else if (this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(embFmd.getType(), clr) != null && embFieldMapping.getNumberOfDatastoreMappings() > 0 && embFieldMapping instanceof PersistableMapping) {
                            final ForeignKey fk2 = TableUtils.getForeignKeyForPCField(embFieldMapping, embFmd, autoMode, this.storeMgr, clr);
                            if (fk2 != null) {
                                foreignKeys.add(fk2);
                            }
                        }
                    }
                }
                else if (this.mmd.getMap().keyIsPersistent()) {
                    referencedTable = this.storeMgr.getDatastoreClass(this.mmd.getMap().getKeyType(), clr);
                    if (referencedTable != null) {
                        ForeignKeyMetaData fkmd = null;
                        if (this.mmd.getKeyMetaData() != null) {
                            fkmd = this.mmd.getKeyMetaData().getForeignKeyMetaData();
                        }
                        if (fkmd != null || autoMode) {
                            final ForeignKey fk = new ForeignKey(this.keyMapping, this.dba, referencedTable, true);
                            fk.setForMetaData(fkmd);
                            foreignKeys.add(fk);
                        }
                    }
                }
            }
        }
        catch (NoTableManagedException ex) {}
        return foreignKeys;
    }
    
    @Override
    protected Set getExpectedIndices(final ClassLoaderResolver clr) {
        final Set indices = super.getExpectedIndices(clr);
        if (this.keyMapping instanceof EmbeddedKeyPCMapping) {
            final EmbeddedKeyPCMapping embMapping = (EmbeddedKeyPCMapping)this.keyMapping;
            for (int i = 0; i < embMapping.getNumberOfJavaTypeMappings(); ++i) {
                final JavaTypeMapping embFieldMapping = embMapping.getJavaTypeMapping(i);
                final IndexMetaData imd = embFieldMapping.getMemberMetaData().getIndexMetaData();
                if (imd != null) {
                    final Index index = TableUtils.getIndexForField(this, imd, embFieldMapping);
                    if (index != null) {
                        indices.add(index);
                    }
                }
            }
        }
        else if (this.mmd.getKeyMetaData() != null) {
            final IndexMetaData idxmd = this.mmd.getKeyMetaData().getIndexMetaData();
            if (idxmd != null) {
                final Index index2 = TableUtils.getIndexForField(this, idxmd, this.keyMapping);
                if (index2 != null) {
                    indices.add(index2);
                }
            }
        }
        if (this.valueMapping instanceof EmbeddedValuePCMapping) {
            final EmbeddedValuePCMapping embMapping2 = (EmbeddedValuePCMapping)this.valueMapping;
            for (int i = 0; i < embMapping2.getNumberOfJavaTypeMappings(); ++i) {
                final JavaTypeMapping embFieldMapping = embMapping2.getJavaTypeMapping(i);
                final IndexMetaData imd = embFieldMapping.getMemberMetaData().getIndexMetaData();
                if (imd != null) {
                    final Index index = TableUtils.getIndexForField(this, imd, embFieldMapping);
                    if (index != null) {
                        indices.add(index);
                    }
                }
            }
        }
        else if (this.mmd.getValueMetaData() != null) {
            final IndexMetaData idxmd = this.mmd.getValueMetaData().getIndexMetaData();
            if (idxmd != null) {
                final Index index2 = TableUtils.getIndexForField(this, idxmd, this.valueMapping);
                if (index2 != null) {
                    indices.add(index2);
                }
            }
        }
        return indices;
    }
    
    @Override
    protected List getExpectedCandidateKeys() {
        final List candidateKeys = super.getExpectedCandidateKeys();
        if (this.keyMapping instanceof EmbeddedKeyPCMapping) {
            final EmbeddedKeyPCMapping embMapping = (EmbeddedKeyPCMapping)this.keyMapping;
            for (int i = 0; i < embMapping.getNumberOfJavaTypeMappings(); ++i) {
                final JavaTypeMapping embFieldMapping = embMapping.getJavaTypeMapping(i);
                final UniqueMetaData umd = embFieldMapping.getMemberMetaData().getUniqueMetaData();
                if (umd != null) {
                    final CandidateKey ck = TableUtils.getCandidateKeyForField(this, umd, embFieldMapping);
                    if (ck != null) {
                        candidateKeys.add(ck);
                    }
                }
            }
        }
        else if (this.mmd.getKeyMetaData() != null) {
            final UniqueMetaData unimd = this.mmd.getKeyMetaData().getUniqueMetaData();
            if (unimd != null) {
                final CandidateKey ck2 = TableUtils.getCandidateKeyForField(this, unimd, this.keyMapping);
                if (ck2 != null) {
                    candidateKeys.add(ck2);
                }
            }
        }
        if (this.valueMapping instanceof EmbeddedValuePCMapping) {
            final EmbeddedValuePCMapping embMapping2 = (EmbeddedValuePCMapping)this.valueMapping;
            for (int i = 0; i < embMapping2.getNumberOfJavaTypeMappings(); ++i) {
                final JavaTypeMapping embFieldMapping = embMapping2.getJavaTypeMapping(i);
                final UniqueMetaData umd = embFieldMapping.getMemberMetaData().getUniqueMetaData();
                if (umd != null) {
                    final CandidateKey ck = TableUtils.getCandidateKeyForField(this, umd, embFieldMapping);
                    if (ck != null) {
                        candidateKeys.add(ck);
                    }
                }
            }
        }
        else if (this.mmd.getValueMetaData() != null) {
            final UniqueMetaData unimd = this.mmd.getValueMetaData().getUniqueMetaData();
            if (unimd != null) {
                final CandidateKey ck2 = TableUtils.getCandidateKeyForField(this, unimd, this.valueMapping);
                if (ck2 != null) {
                    candidateKeys.add(ck2);
                }
            }
        }
        return candidateKeys;
    }
    
    @Override
    public JavaTypeMapping getMemberMapping(final AbstractMemberMetaData mmd) {
        return null;
    }
}
