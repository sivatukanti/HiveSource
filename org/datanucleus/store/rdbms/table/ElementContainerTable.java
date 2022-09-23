// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.metadata.UniqueMetaData;
import org.datanucleus.store.rdbms.key.CandidateKey;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.IndexMetaData;
import org.datanucleus.store.rdbms.key.Index;
import java.util.Set;
import java.util.Collection;
import org.datanucleus.store.rdbms.exceptions.NoTableManagedException;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedElementPCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedPCMapping;
import java.util.ArrayList;
import java.util.List;
import org.datanucleus.metadata.ForeignKeyMetaData;
import org.datanucleus.store.rdbms.key.ForeignKey;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public abstract class ElementContainerTable extends JoinTable
{
    protected JavaTypeMapping elementMapping;
    protected JavaTypeMapping orderMapping;
    protected JavaTypeMapping relationDiscriminatorMapping;
    protected String relationDiscriminatorValue;
    
    public ElementContainerTable(final DatastoreIdentifier tableName, final AbstractMemberMetaData mmd, final RDBMSStoreManager storeMgr) {
        super(tableName, mmd, storeMgr);
    }
    
    @Override
    public void initialize(final ClassLoaderResolver clr) {
        this.assertIsUninitialized();
        final boolean pkRequired = this.requiresPrimaryKey();
        final AbstractMemberMetaData[] relatedMmds = this.mmd.getRelatedMemberMetaData(clr);
        ColumnMetaData[] columnMetaData = null;
        if (this.mmd.getJoinMetaData() != null && this.mmd.getJoinMetaData().getColumnMetaData() != null && this.mmd.getJoinMetaData().getColumnMetaData().length > 0) {
            columnMetaData = this.mmd.getJoinMetaData().getColumnMetaData();
        }
        else if (relatedMmds != null && relatedMmds[0].getElementMetaData() != null && relatedMmds[0].getElementMetaData().getColumnMetaData() != null && relatedMmds[0].getElementMetaData().getColumnMetaData().length > 0) {
            columnMetaData = relatedMmds[0].getElementMetaData().getColumnMetaData();
        }
        this.ownerMapping = ColumnCreator.createColumnsForJoinTables(clr.classForName(this.ownerType), this.mmd, columnMetaData, this.storeMgr, this, pkRequired, false, 1, clr);
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            this.debugMapping(this.ownerMapping);
        }
        if (this.mmd.hasExtension("relation-discriminator-column") || this.mmd.hasExtension("relation-discriminator-value")) {
            String colName = this.mmd.getValueForExtension("relation-discriminator-column");
            if (colName == null) {
                colName = "RELATION_DISCRIM";
            }
            final ColumnMetaData colmd = new ColumnMetaData();
            colmd.setName(colName);
            boolean relationDiscriminatorPk = false;
            if (this.mmd.hasExtension("relation-discriminator-pk") && this.mmd.getValueForExtension("relation-discriminator-pk").equalsIgnoreCase("true")) {
                relationDiscriminatorPk = true;
            }
            if (!relationDiscriminatorPk) {
                colmd.setAllowsNull(Boolean.TRUE);
            }
            ColumnCreator.createIndexColumn(this.relationDiscriminatorMapping = this.storeMgr.getMappingManager().getMapping(String.class), this.storeMgr, clr, this, colmd, relationDiscriminatorPk);
            this.relationDiscriminatorValue = this.mmd.getValueForExtension("relation-discriminator-value");
            if (this.relationDiscriminatorValue == null) {
                this.relationDiscriminatorValue = this.mmd.getFullFieldName();
            }
        }
    }
    
    public abstract String getElementType();
    
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
                for (int j = 0; j < this.elementMapping.getNumberOfDatastoreMappings(); ++j) {
                    if (this.elementMapping.getDatastoreMapping(j).getColumn().getIdentifier().getIdentifierName().equals(colName)) {
                        this.elementMapping.getDatastoreMapping(j).getColumn().setAsPrimaryKey();
                        found = true;
                    }
                }
            }
            if (!found) {
                throw new NucleusUserException(ElementContainerTable.LOCALISER.msg("057040", this.toString(), colName));
            }
        }
    }
    
    @Override
    public JavaTypeMapping getMemberMapping(final AbstractMemberMetaData mmd) {
        return null;
    }
    
    public JavaTypeMapping getElementMapping() {
        this.assertIsInitialized();
        return this.elementMapping;
    }
    
    public JavaTypeMapping getOrderMapping() {
        this.assertIsInitialized();
        return this.orderMapping;
    }
    
    public JavaTypeMapping getRelationDiscriminatorMapping() {
        this.assertIsInitialized();
        return this.relationDiscriminatorMapping;
    }
    
    public String getRelationDiscriminatorValue() {
        this.assertIsInitialized();
        return this.relationDiscriminatorValue;
    }
    
    protected ForeignKey getForeignKeyToOwner(final DatastoreClass ownerTable, final boolean autoMode) {
        ForeignKey fk = null;
        if (ownerTable != null) {
            ForeignKeyMetaData fkmd = null;
            if (this.mmd.getJoinMetaData() != null) {
                fkmd = this.mmd.getJoinMetaData().getForeignKeyMetaData();
            }
            if (fkmd != null || autoMode) {
                fk = new ForeignKey(this.ownerMapping, this.dba, ownerTable, true);
                fk.setForMetaData(fkmd);
            }
        }
        return fk;
    }
    
    protected ForeignKey getForeignKeyToElement(final DatastoreClass elementTable, final boolean autoMode, final JavaTypeMapping m) {
        ForeignKey fk = null;
        if (elementTable != null) {
            ForeignKeyMetaData fkmd = this.mmd.getForeignKeyMetaData();
            if (fkmd == null && this.mmd.getElementMetaData() != null) {
                fkmd = this.mmd.getElementMetaData().getForeignKeyMetaData();
            }
            if (fkmd != null || autoMode) {
                fk = new ForeignKey(m, this.dba, elementTable, true);
                fk.setForMetaData(fkmd);
            }
        }
        return fk;
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
                final ForeignKey fk = this.getForeignKeyToOwner(referencedTable, autoMode);
                if (fk != null) {
                    foreignKeys.add(fk);
                }
            }
            if (!(this.elementMapping instanceof SerialisedPCMapping)) {
                if (this.elementMapping instanceof EmbeddedElementPCMapping) {
                    final EmbeddedElementPCMapping embMapping = (EmbeddedElementPCMapping)this.elementMapping;
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
                else if (this.elementMapping instanceof ReferenceMapping) {
                    final JavaTypeMapping[] implJavaTypeMappings = ((ReferenceMapping)this.elementMapping).getJavaTypeMapping();
                    for (int i = 0; i < implJavaTypeMappings.length; ++i) {
                        final JavaTypeMapping implMapping = implJavaTypeMappings[i];
                        if (this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(implMapping.getType(), clr) != null && implMapping.getNumberOfDatastoreMappings() > 0) {
                            referencedTable = this.storeMgr.getDatastoreClass(implMapping.getType(), clr);
                            if (referencedTable != null) {
                                final ForeignKey fk3 = this.getForeignKeyToElement(referencedTable, autoMode, implMapping);
                                if (fk3 != null) {
                                    foreignKeys.add(fk3);
                                }
                            }
                        }
                    }
                }
                else {
                    referencedTable = this.storeMgr.getDatastoreClass(this.getElementType(), clr);
                    if (referencedTable != null) {
                        final ForeignKey fk = this.getForeignKeyToElement(referencedTable, autoMode, this.elementMapping);
                        if (fk != null) {
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
        if (this.mmd.getIndexMetaData() != null) {
            final Index index = TableUtils.getIndexForField(this, this.mmd.getIndexMetaData(), this.ownerMapping);
            if (index != null) {
                indices.add(index);
            }
        }
        else if (this.mmd.getJoinMetaData() != null && this.mmd.getJoinMetaData().getIndexMetaData() != null) {
            final Index index = TableUtils.getIndexForField(this, this.mmd.getJoinMetaData().getIndexMetaData(), this.ownerMapping);
            if (index != null) {
                indices.add(index);
            }
        }
        if (this.elementMapping instanceof EmbeddedElementPCMapping) {
            final EmbeddedElementPCMapping embMapping = (EmbeddedElementPCMapping)this.elementMapping;
            for (int i = 0; i < embMapping.getNumberOfJavaTypeMappings(); ++i) {
                final JavaTypeMapping embFieldMapping = embMapping.getJavaTypeMapping(i);
                final IndexMetaData imd = embFieldMapping.getMemberMetaData().getIndexMetaData();
                if (imd != null) {
                    final Index index2 = TableUtils.getIndexForField(this, imd, embFieldMapping);
                    if (index2 != null) {
                        indices.add(index2);
                    }
                }
            }
        }
        else {
            final ElementMetaData elemmd = this.mmd.getElementMetaData();
            if (elemmd != null && elemmd.getIndexMetaData() != null) {
                final Index index3 = TableUtils.getIndexForField(this, elemmd.getIndexMetaData(), this.elementMapping);
                if (index3 != null) {
                    indices.add(index3);
                }
            }
        }
        if (this.orderMapping != null && this.mmd.getOrderMetaData() != null && this.mmd.getOrderMetaData().getIndexMetaData() != null) {
            final Index index = TableUtils.getIndexForField(this, this.mmd.getOrderMetaData().getIndexMetaData(), this.orderMapping);
            if (index != null) {
                indices.add(index);
            }
        }
        return indices;
    }
    
    @Override
    protected List getExpectedCandidateKeys() {
        final List candidateKeys = super.getExpectedCandidateKeys();
        if (this.elementMapping instanceof EmbeddedElementPCMapping) {
            final EmbeddedElementPCMapping embMapping = (EmbeddedElementPCMapping)this.elementMapping;
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
        if (this.mmd.getJoinMetaData() != null && this.mmd.getJoinMetaData().getUniqueMetaData() != null) {
            final UniqueMetaData unimd = this.mmd.getJoinMetaData().getUniqueMetaData();
            final ColumnMetaData[] colmds = unimd.getColumnMetaData();
            if (colmds != null) {
                final CandidateKey uniKey = new CandidateKey(this);
                final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
                for (int j = 0; j < colmds.length; ++j) {
                    final Column col = this.getColumn(idFactory.newColumnIdentifier(colmds[j].getName()));
                    if (col == null) {
                        throw new NucleusUserException("Unique key on join-table " + this + " has column " + colmds[j].getName() + " that is not found");
                    }
                    uniKey.addColumn(col);
                }
                candidateKeys.add(uniKey);
            }
        }
        return candidateKeys;
    }
}
