// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.metadata.IdentityType;
import java.util.List;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;

public class CollectionTable extends ElementContainerTable implements DatastoreElementContainer
{
    public CollectionTable(final DatastoreIdentifier tableName, final AbstractMemberMetaData mmd, final RDBMSStoreManager storeMgr) {
        super(tableName, mmd, storeMgr);
    }
    
    @Override
    public void initialize(final ClassLoaderResolver clr) {
        super.initialize(clr);
        final PrimaryKeyMetaData pkmd = (this.mmd.getJoinMetaData() != null) ? this.mmd.getJoinMetaData().getPrimaryKeyMetaData() : null;
        final boolean pkColsSpecified = pkmd != null && pkmd.getColumnMetaData() != null;
        final boolean pkRequired = this.requiresPrimaryKey();
        final boolean elementPC = this.mmd.hasCollection() && this.mmd.getCollection().elementIsPersistent();
        final Class elementClass = clr.classForName(this.getElementType());
        if (this.isSerialisedElement() || this.isEmbeddedElementPC() || (this.isEmbeddedElement() && !elementPC) || ClassUtils.isReferenceType(elementClass)) {
            this.elementMapping = this.storeMgr.getMappingManager().getMapping(this, this.mmd, clr, 3);
            if (NucleusLogger.DATASTORE.isDebugEnabled()) {
                this.debugMapping(this.elementMapping);
            }
        }
        else {
            ColumnMetaData[] elemColmd = null;
            final AbstractMemberMetaData[] relatedMmds = this.mmd.getRelatedMemberMetaData(clr);
            final ElementMetaData elemmd = this.mmd.getElementMetaData();
            if (elemmd != null && elemmd.getColumnMetaData() != null && elemmd.getColumnMetaData().length > 0) {
                elemColmd = elemmd.getColumnMetaData();
            }
            else if (relatedMmds != null && relatedMmds[0].getJoinMetaData() != null && relatedMmds[0].getJoinMetaData().getColumnMetaData() != null && relatedMmds[0].getJoinMetaData().getColumnMetaData().length > 0) {
                elemColmd = relatedMmds[0].getJoinMetaData().getColumnMetaData();
            }
            this.elementMapping = ColumnCreator.createColumnsForJoinTables(elementClass, this.mmd, elemColmd, this.storeMgr, this, false, false, 3, clr);
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
        boolean orderRequired = false;
        if (this.mmd.getOrderMetaData() != null) {
            if (this.mmd.getOrderMetaData().isIndexedList()) {
                orderRequired = true;
            }
        }
        else if (List.class.isAssignableFrom(this.mmd.getType())) {
            orderRequired = true;
        }
        else if (this.requiresPrimaryKey() && !pkColsSpecified) {
            if (this.isEmbeddedElementPC()) {
                if (this.mmd.getCollection().getElementClassMetaData(clr, this.storeMgr.getMetaDataManager()).getIdentityType() != IdentityType.APPLICATION) {
                    orderRequired = true;
                }
            }
            else if (this.isSerialisedElement()) {
                orderRequired = true;
            }
            else if (this.elementMapping instanceof ReferenceMapping) {
                final ReferenceMapping refMapping = (ReferenceMapping)this.elementMapping;
                if (refMapping.getJavaTypeMapping().length > 1) {
                    orderRequired = true;
                }
            }
            else if (!(this.elementMapping instanceof PersistableMapping)) {
                final Column elementCol2 = this.elementMapping.getDatastoreMapping(0).getColumn();
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
                for (int j = 0; j < this.elementMapping.getNumberOfDatastoreMappings(); ++j) {
                    this.elementMapping.getDatastoreMapping(j).getColumn().setAsPrimaryKey();
                }
            }
        }
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(CollectionTable.LOCALISER.msg("057023", this));
        }
        this.storeMgr.registerTableInitialized(this);
        this.state = 2;
    }
    
    @Override
    public String getElementType() {
        return this.mmd.getCollection().getElementType();
    }
    
    public boolean isSerialisedElement() {
        return this.mmd.getCollection() != null && this.mmd.getCollection().isSerializedElement();
    }
    
    public boolean isEmbeddedElement() {
        return (this.mmd.getCollection() == null || !this.mmd.getCollection().isSerializedElement()) && (this.mmd.getCollection() != null && this.mmd.getCollection().isEmbeddedElement());
    }
    
    public boolean isSerialisedElementPC() {
        return this.mmd.getCollection() != null && this.mmd.getCollection().isSerializedElement() && this.mmd.getCollection().elementIsPersistent();
    }
    
    public boolean isEmbeddedElementPC() {
        return (this.mmd.getCollection() == null || !this.mmd.getCollection().isSerializedElement()) && (this.mmd.getElementMetaData() != null && this.mmd.getElementMetaData().getEmbeddedMetaData() != null);
    }
    
    @Override
    protected boolean requiresPrimaryKey() {
        return (this.mmd.getOrderMetaData() == null || this.mmd.getOrderMetaData().isIndexedList()) && super.requiresPrimaryKey();
    }
}
