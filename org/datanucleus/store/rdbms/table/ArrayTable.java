// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;

public class ArrayTable extends ElementContainerTable implements DatastoreElementContainer
{
    public ArrayTable(final DatastoreIdentifier tableName, final AbstractMemberMetaData mmd, final RDBMSStoreManager storeMgr) {
        super(tableName, mmd, storeMgr);
    }
    
    @Override
    public void initialize(final ClassLoaderResolver clr) {
        super.initialize(clr);
        final PrimaryKeyMetaData pkmd = (this.mmd.getJoinMetaData() != null) ? this.mmd.getJoinMetaData().getPrimaryKeyMetaData() : null;
        final boolean pkColsSpecified = pkmd != null && pkmd.getColumnMetaData() != null;
        final boolean pkRequired = this.requiresPrimaryKey();
        final boolean elementPC = this.mmd.hasArray() && this.mmd.getArray().elementIsPersistent();
        if (this.isSerialisedElementPC() || this.isEmbeddedElementPC() || (this.isEmbeddedElement() && !elementPC) || ClassUtils.isReferenceType(this.mmd.getType().getComponentType())) {
            this.elementMapping = this.storeMgr.getMappingManager().getMapping(this, this.mmd, clr, 4);
            if (Boolean.TRUE.equals(this.mmd.getContainer().allowNulls())) {
                for (int i = 0; i < this.elementMapping.getNumberOfDatastoreMappings(); ++i) {
                    final Column elementCol = this.elementMapping.getDatastoreMapping(i).getColumn();
                    elementCol.setNullable();
                }
            }
            if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                this.debugMapping(this.elementMapping);
            }
        }
        else {
            ColumnMetaData[] elemColmd = null;
            final ElementMetaData elemmd = this.mmd.getElementMetaData();
            if (elemmd != null && elemmd.getColumnMetaData() != null && elemmd.getColumnMetaData().length > 0) {
                elemColmd = elemmd.getColumnMetaData();
            }
            this.elementMapping = ColumnCreator.createColumnsForJoinTables(this.mmd.getType().getComponentType(), this.mmd, elemColmd, this.storeMgr, this, false, true, 4, clr);
            if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                this.debugMapping(this.elementMapping);
            }
        }
        ColumnMetaData colmd = null;
        if (this.mmd.getOrderMetaData() != null && this.mmd.getOrderMetaData().getColumnMetaData() != null && this.mmd.getOrderMetaData().getColumnMetaData().length > 0) {
            colmd = this.mmd.getOrderMetaData().getColumnMetaData()[0];
        }
        else {
            final DatastoreIdentifier id = this.storeMgr.getIdentifierFactory().newIndexFieldIdentifier(this.mmd);
            colmd = new ColumnMetaData();
            colmd.setName(id.getIdentifierName());
        }
        ColumnCreator.createIndexColumn(this.orderMapping = this.storeMgr.getMappingManager().getMapping(Integer.TYPE), this.storeMgr, clr, this, colmd, pkRequired && !pkColsSpecified);
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            this.debugMapping(this.orderMapping);
        }
        if (pkRequired && pkColsSpecified) {
            this.applyUserPrimaryKeySpecification(pkmd);
        }
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(ArrayTable.LOCALISER.msg("057023", this));
        }
        this.storeMgr.registerTableInitialized(this);
        this.state = 2;
    }
    
    @Override
    public String getElementType() {
        return this.mmd.getType().getComponentType().getName();
    }
    
    public boolean isSerialisedElement() {
        return this.mmd.getArray() != null && this.mmd.getArray().isSerializedElement();
    }
    
    public boolean isEmbeddedElement() {
        return (this.mmd.getArray() == null || !this.mmd.getArray().isSerializedElement()) && (this.mmd.getArray() != null && this.mmd.getArray().isEmbeddedElement());
    }
    
    public boolean isSerialisedElementPC() {
        return this.mmd.getArray() != null && this.mmd.getArray().isSerializedElement();
    }
    
    public boolean isEmbeddedElementPC() {
        return (this.mmd.getArray() == null || !this.mmd.getArray().isSerializedElement()) && (this.mmd.getElementMetaData() != null && this.mmd.getElementMetaData().getEmbeddedMetaData() != null);
    }
}
