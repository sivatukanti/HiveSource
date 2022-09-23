// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.metadata.UniqueMetaData;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.key.CandidateKey;
import java.util.Set;
import org.datanucleus.metadata.ForeignKeyMetaData;
import org.datanucleus.store.rdbms.exceptions.NoTableManagedException;
import org.datanucleus.store.rdbms.key.ForeignKey;
import java.util.ArrayList;
import java.util.List;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class PersistableJoinTable extends JoinTable
{
    protected JavaTypeMapping relatedMapping;
    
    public PersistableJoinTable(final DatastoreIdentifier tableName, final AbstractMemberMetaData mmd, final RDBMSStoreManager storeMgr) {
        super(tableName, mmd, storeMgr);
    }
    
    @Override
    public JavaTypeMapping getMemberMapping(final AbstractMemberMetaData mmd) {
        return null;
    }
    
    @Override
    public void initialize(final ClassLoaderResolver clr) {
        final boolean pkRequired = this.requiresPrimaryKey();
        ColumnMetaData[] ownerColmd = null;
        if (this.mmd.getColumnMetaData() != null && this.mmd.getColumnMetaData().length > 0) {
            ownerColmd = this.mmd.getColumnMetaData();
        }
        this.ownerMapping = ColumnCreator.createColumnsForJoinTables(clr.classForName(this.mmd.getClassName(true)), this.mmd, ownerColmd, this.storeMgr, this, pkRequired, false, 1, clr);
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            this.debugMapping(this.ownerMapping);
        }
        ColumnMetaData[] relatedColmd = null;
        if (this.mmd.getJoinMetaData().getColumnMetaData() != null && this.mmd.getJoinMetaData().getColumnMetaData().length > 0) {
            relatedColmd = this.mmd.getJoinMetaData().getColumnMetaData();
        }
        this.relatedMapping = ColumnCreator.createColumnsForJoinTables(this.mmd.getType(), this.mmd, relatedColmd, this.storeMgr, this, pkRequired, false, 8, clr);
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            this.debugMapping(this.relatedMapping);
        }
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(PersistableJoinTable.LOCALISER.msg("057023", this));
        }
        this.storeMgr.registerTableInitialized(this);
        this.state = 2;
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
            DatastoreClass referencedTable = this.storeMgr.getDatastoreClass(this.mmd.getClassName(true), clr);
            if (referencedTable != null) {
                ForeignKey fk = null;
                ForeignKeyMetaData fkmd = null;
                if (this.mmd.getJoinMetaData() != null) {
                    fkmd = this.mmd.getJoinMetaData().getForeignKeyMetaData();
                }
                if (fkmd != null || autoMode) {
                    fk = new ForeignKey(this.ownerMapping, this.dba, referencedTable, true);
                    fk.setForMetaData(fkmd);
                }
                if (fk != null) {
                    foreignKeys.add(fk);
                }
            }
            referencedTable = this.storeMgr.getDatastoreClass(this.mmd.getTypeName(), clr);
            if (referencedTable != null) {
                ForeignKey fk = null;
                final ForeignKeyMetaData fkmd = this.mmd.getForeignKeyMetaData();
                if (fkmd != null || autoMode) {
                    fk = new ForeignKey(this.relatedMapping, this.dba, referencedTable, true);
                    fk.setForMetaData(fkmd);
                }
                if (fk != null) {
                    foreignKeys.add(fk);
                }
            }
        }
        catch (NoTableManagedException ex) {}
        return foreignKeys;
    }
    
    @Override
    protected Set getExpectedIndices(final ClassLoaderResolver clr) {
        final Set indices = super.getExpectedIndices(clr);
        return indices;
    }
    
    @Override
    protected List getExpectedCandidateKeys() {
        final List candidateKeys = super.getExpectedCandidateKeys();
        if (this.mmd.getJoinMetaData() != null && this.mmd.getJoinMetaData().getUniqueMetaData() != null) {
            final UniqueMetaData unimd = this.mmd.getJoinMetaData().getUniqueMetaData();
            final ColumnMetaData[] colmds = unimd.getColumnMetaData();
            if (colmds != null) {
                final CandidateKey uniKey = new CandidateKey(this);
                final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
                for (int i = 0; i < colmds.length; ++i) {
                    final Column col = this.getColumn(idFactory.newColumnIdentifier(colmds[i].getName()));
                    if (col == null) {
                        throw new NucleusUserException("Unique key on join-table " + this + " has column " + colmds[i].getName() + " that is not found");
                    }
                    uniKey.addColumn(col);
                }
                candidateKeys.add(uniKey);
            }
        }
        return candidateKeys;
    }
    
    public JavaTypeMapping getRelatedMapping() {
        this.assertIsInitialized();
        return this.relatedMapping;
    }
}
