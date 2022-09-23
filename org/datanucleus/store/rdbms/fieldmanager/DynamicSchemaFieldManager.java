// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.fieldmanager;

import org.datanucleus.ClassConstants;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.table.TableImpl;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.table.CollectionTable;
import java.util.Collection;
import org.datanucleus.store.rdbms.mapping.java.InterfaceMapping;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

public class DynamicSchemaFieldManager extends AbstractFieldManager
{
    protected static final Localiser LOCALISER;
    RDBMSStoreManager rdbmsMgr;
    ObjectProvider op;
    boolean schemaUpdatesPerformed;
    
    public DynamicSchemaFieldManager(final RDBMSStoreManager rdbmsMgr, final ObjectProvider op) {
        this.schemaUpdatesPerformed = false;
        this.rdbmsMgr = rdbmsMgr;
        this.op = op;
    }
    
    public boolean hasPerformedSchemaUpdates() {
        return this.schemaUpdatesPerformed;
    }
    
    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        if (value == null) {
            return;
        }
        final ExecutionContext ec = this.op.getExecutionContext();
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        final DatastoreClass table = this.rdbmsMgr.getDatastoreClass(this.op.getObject().getClass().getName(), clr);
        final JavaTypeMapping fieldMapping = table.getMemberMapping(mmd);
        if (fieldMapping != null) {
            if (fieldMapping instanceof InterfaceMapping) {
                final InterfaceMapping intfMapping = (InterfaceMapping)fieldMapping;
                if (mmd != null && (mmd.getFieldTypes() != null || mmd.hasExtension("implementation-classes"))) {
                    return;
                }
                this.processInterfaceMappingForValue(intfMapping, value, mmd, ec);
            }
            else if (mmd.hasCollection()) {
                boolean hasJoin = false;
                if (mmd.getJoinMetaData() != null) {
                    hasJoin = true;
                }
                else {
                    final AbstractMemberMetaData[] relMmds = mmd.getRelatedMemberMetaData(clr);
                    if (relMmds != null && relMmds[0].getJoinMetaData() != null) {
                        hasJoin = true;
                    }
                }
                if (!hasJoin) {
                    return;
                }
                final Collection coll = (Collection)value;
                if (coll.isEmpty()) {
                    return;
                }
                final Table joinTbl = fieldMapping.getStoreManager().getTable(mmd);
                final CollectionTable collTbl = (CollectionTable)joinTbl;
                final JavaTypeMapping elemMapping = collTbl.getElementMapping();
                if (elemMapping instanceof InterfaceMapping) {
                    final InterfaceMapping intfMapping2 = (InterfaceMapping)elemMapping;
                    this.processInterfaceMappingForValue(intfMapping2, coll.iterator().next(), mmd, ec);
                }
            }
            else if (mmd.hasMap()) {
                NucleusLogger.DATASTORE_SCHEMA.debug("TODO : Support dynamic schema updates for Map field " + mmd.getFullFieldName());
            }
        }
    }
    
    @Override
    public void storeBooleanField(final int fieldNumber, final boolean value) {
    }
    
    @Override
    public void storeByteField(final int fieldNumber, final byte value) {
    }
    
    @Override
    public void storeCharField(final int fieldNumber, final char value) {
    }
    
    @Override
    public void storeDoubleField(final int fieldNumber, final double value) {
    }
    
    @Override
    public void storeFloatField(final int fieldNumber, final float value) {
    }
    
    @Override
    public void storeIntField(final int fieldNumber, final int value) {
    }
    
    @Override
    public void storeLongField(final int fieldNumber, final long value) {
    }
    
    @Override
    public void storeShortField(final int fieldNumber, final short value) {
    }
    
    @Override
    public void storeStringField(final int fieldNumber, final String value) {
    }
    
    protected void processInterfaceMappingForValue(final InterfaceMapping intfMapping, final Object value, final AbstractMemberMetaData mmd, final ExecutionContext ec) {
        if (intfMapping.getMappingStrategy() == 0) {
            final int intfImplMappingNumber = intfMapping.getMappingNumberForValue(ec, value);
            if (intfImplMappingNumber == -1) {
                if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                    NucleusLogger.DATASTORE_SCHEMA.debug("Dynamic schema updates : field=" + mmd.getFullFieldName() + " has an interface mapping yet " + StringUtils.toJVMIDString(value) + " is not a known implementation - trying to update the schema ...");
                }
                final MetaDataManager mmgr = ec.getNucleusContext().getMetaDataManager();
                final ClassLoaderResolver clr = ec.getClassLoaderResolver();
                mmgr.getMetaDataForClass(value.getClass(), clr);
                final String[] impls = ec.getMetaDataManager().getClassesImplementingInterface(intfMapping.getType(), clr);
                if (ClassUtils.stringArrayContainsValue(impls, value.getClass().getName())) {
                    try {
                        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                            NucleusLogger.DATASTORE_SCHEMA.debug("Dynamic schema updates : field=" + mmd.getFullFieldName() + " has a new implementation available so reinitialising its mapping");
                        }
                        intfMapping.initialize(mmd, intfMapping.getTable(), clr);
                        intfMapping.getStoreManager().validateTable((TableImpl)intfMapping.getTable(), clr);
                    }
                    catch (Exception e) {
                        NucleusLogger.DATASTORE_SCHEMA.debug("Exception thrown trying to create missing columns for implementation", e);
                        throw new NucleusException("Exception thrown performing dynamic update of schema", e);
                    }
                    this.schemaUpdatesPerformed = true;
                }
            }
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
