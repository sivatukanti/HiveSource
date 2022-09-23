// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.ClassConstants;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.exceptions.DatastoreReadOnlyException;
import org.datanucleus.util.StringUtils;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ExecutionContext;
import org.datanucleus.util.Localiser;

public abstract class AbstractPersistenceHandler implements StorePersistenceHandler
{
    protected static final Localiser LOCALISER;
    protected StoreManager storeMgr;
    
    public AbstractPersistenceHandler(final StoreManager storeMgr) {
        this.storeMgr = storeMgr;
    }
    
    @Override
    public void batchStart(final ExecutionContext ec, final PersistenceBatchType batchType) {
    }
    
    @Override
    public void batchEnd(final ExecutionContext ec, final PersistenceBatchType type) {
    }
    
    @Override
    public void insertObjects(final ObjectProvider... ops) {
        if (ops.length == 1) {
            this.insertObject(ops[0]);
            return;
        }
        for (int i = 0; i < ops.length; ++i) {
            this.insertObject(ops[i]);
        }
    }
    
    @Override
    public void deleteObjects(final ObjectProvider... ops) {
        if (ops.length == 1) {
            this.deleteObject(ops[0]);
            return;
        }
        for (int i = 0; i < ops.length; ++i) {
            this.deleteObject(ops[i]);
        }
    }
    
    @Override
    public void locateObjects(final ObjectProvider[] ops) {
        if (ops.length == 1) {
            this.locateObject(ops[0]);
            return;
        }
        for (int i = 0; i < ops.length; ++i) {
            this.locateObject(ops[i]);
        }
    }
    
    @Override
    public Object[] findObjects(final ExecutionContext ec, final Object[] ids) {
        final Object[] objects = new Object[ids.length];
        for (int i = 0; i < ids.length; ++i) {
            objects[i] = this.findObject(ec, ids[i]);
        }
        return objects;
    }
    
    public void assertReadOnlyForUpdateOfObject(final ObjectProvider op) {
        if (!this.storeMgr.getBooleanProperty("datanucleus.readOnlyDatastore")) {
            final AbstractClassMetaData cmd = op.getClassMetaData();
            if (cmd.hasExtension("read-only")) {
                final String value = cmd.getValueForExtension("read-only");
                if (!StringUtils.isWhitespace(value)) {
                    final boolean readonly = Boolean.valueOf(value);
                    if (readonly) {
                        if (this.storeMgr.getStringProperty("datanucleus.readOnlyDatastoreAction").equalsIgnoreCase("EXCEPTION")) {
                            throw new DatastoreReadOnlyException(AbstractPersistenceHandler.LOCALISER.msg("032006", op.getObjectAsPrintable()), op.getExecutionContext().getClassLoaderResolver());
                        }
                        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                            NucleusLogger.PERSISTENCE.debug(AbstractPersistenceHandler.LOCALISER.msg("032007", op.getObjectAsPrintable()));
                        }
                    }
                }
            }
            return;
        }
        if (this.storeMgr.getStringProperty("datanucleus.readOnlyDatastoreAction").equalsIgnoreCase("EXCEPTION")) {
            throw new DatastoreReadOnlyException(AbstractPersistenceHandler.LOCALISER.msg("032004", op.getObjectAsPrintable()), op.getExecutionContext().getClassLoaderResolver());
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(AbstractPersistenceHandler.LOCALISER.msg("032005", op.getObjectAsPrintable()));
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
