// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.AbstractMemberMetaData;

public abstract class JoinTable extends TableImpl
{
    protected final AbstractMemberMetaData mmd;
    protected JavaTypeMapping ownerMapping;
    protected final String ownerType;
    
    protected JoinTable(final DatastoreIdentifier tableName, final AbstractMemberMetaData mmd, final RDBMSStoreManager storeMgr) {
        super(tableName, storeMgr);
        this.mmd = mmd;
        this.ownerType = mmd.getClassName(true);
        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.NONE) {
            throw new NucleusException(JoinTable.LOCALISER.msg("057006", mmd.getName())).setFatal();
        }
    }
    
    @Override
    public PrimaryKey getPrimaryKey() {
        final PrimaryKey pk = super.getPrimaryKey();
        if (this.mmd.getJoinMetaData() != null) {
            final PrimaryKeyMetaData pkmd = this.mmd.getJoinMetaData().getPrimaryKeyMetaData();
            if (pkmd != null && pkmd.getName() != null) {
                pk.setName(pkmd.getName());
            }
        }
        return pk;
    }
    
    protected boolean requiresPrimaryKey() {
        boolean pkRequired = true;
        if (this.mmd.getJoinMetaData() != null && this.mmd.getJoinMetaData().hasExtension("primary-key") && this.mmd.getJoinMetaData().getValueForExtension("primary-key").equalsIgnoreCase("false")) {
            pkRequired = false;
        }
        return pkRequired;
    }
    
    public JavaTypeMapping getOwnerMapping() {
        this.assertIsInitialized();
        return this.ownerMapping;
    }
    
    public AbstractMemberMetaData getOwnerMemberMetaData() {
        return this.mmd;
    }
    
    @Override
    public JavaTypeMapping getIdMapping() {
        throw new NucleusException("Unsupported ID mapping in join table").setFatal();
    }
    
    protected void debugMapping(final JavaTypeMapping mapping) {
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            final StringBuffer columnsStr = new StringBuffer();
            for (int i = 0; i < mapping.getNumberOfDatastoreMappings(); ++i) {
                if (i > 0) {
                    columnsStr.append(",");
                }
                columnsStr.append(mapping.getDatastoreMapping(i).getColumn());
            }
            if (mapping.getNumberOfDatastoreMappings() == 0) {
                columnsStr.append("[none]");
            }
            final StringBuffer datastoreMappingTypes = new StringBuffer();
            for (int j = 0; j < mapping.getNumberOfDatastoreMappings(); ++j) {
                if (j > 0) {
                    datastoreMappingTypes.append(',');
                }
                datastoreMappingTypes.append(mapping.getDatastoreMapping(j).getClass().getName());
            }
            NucleusLogger.DATASTORE.debug(JoinTable.LOCALISER.msg("057010", this.mmd.getFullFieldName(), columnsStr.toString(), mapping.getClass().getName(), datastoreMappingTypes.toString()));
        }
    }
}
