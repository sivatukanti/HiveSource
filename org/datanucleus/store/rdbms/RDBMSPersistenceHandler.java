// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import org.datanucleus.ClassConstants;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.store.rdbms.fieldmanager.DynamicSchemaFieldManager;
import java.util.Set;
import java.util.HashSet;
import org.datanucleus.store.rdbms.request.LocateRequest;
import org.datanucleus.store.rdbms.request.LocateBulkRequest;
import java.util.HashMap;
import org.datanucleus.store.rdbms.request.DeleteRequest;
import org.datanucleus.store.rdbms.request.UpdateRequest;
import org.datanucleus.store.rdbms.request.FetchRequest;
import java.util.List;
import org.datanucleus.util.NucleusLogger;
import java.util.ArrayList;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.rdbms.request.InsertRequest;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.request.RequestType;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.Iterator;
import java.util.Collection;
import org.datanucleus.store.rdbms.table.SecondaryDatastoreClass;
import org.datanucleus.store.rdbms.table.ClassView;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.ClassLoaderResolver;
import java.util.Collections;
import org.datanucleus.util.SoftValueMap;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.rdbms.request.Request;
import org.datanucleus.store.rdbms.request.RequestIdentifier;
import java.util.Map;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.AbstractPersistenceHandler;

public class RDBMSPersistenceHandler extends AbstractPersistenceHandler
{
    protected static final Localiser LOCALISER_RDBMS;
    private Map<RequestIdentifier, Request> requestsByID;
    
    public RDBMSPersistenceHandler(final StoreManager storeMgr) {
        super(storeMgr);
        this.requestsByID = Collections.synchronizedMap((Map<RequestIdentifier, Request>)new SoftValueMap());
    }
    
    @Override
    public void close() {
        this.requestsByID.clear();
        this.requestsByID = null;
    }
    
    private DatastoreClass getDatastoreClass(final String className, final ClassLoaderResolver clr) {
        return ((RDBMSStoreManager)this.storeMgr).getDatastoreClass(className, clr);
    }
    
    @Override
    public void insertObjects(final ObjectProvider... ops) {
        super.insertObjects(ops);
    }
    
    @Override
    public void insertObject(final ObjectProvider op) {
        this.assertReadOnlyForUpdateOfObject(op);
        this.checkForSchemaUpdatesForFieldsOfObject(op, op.getLoadedFieldNumbers());
        final ExecutionContext ec = op.getExecutionContext();
        final ClassLoaderResolver clr = op.getExecutionContext().getClassLoaderResolver();
        final String className = op.getClassMetaData().getFullClassName();
        final DatastoreClass dc = this.getDatastoreClass(className, clr);
        if (dc != null) {
            if (ec.getStatistics() != null) {
                ec.getStatistics().incrementInsertCount();
            }
            this.insertTable(dc, op, clr);
            return;
        }
        if (op.getClassMetaData().getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
            throw new NucleusUserException(RDBMSPersistenceHandler.LOCALISER_RDBMS.msg("032013", className));
        }
        throw new NucleusException(RDBMSPersistenceHandler.LOCALISER_RDBMS.msg("032014", className, op.getClassMetaData().getInheritanceMetaData().getStrategy())).setFatal();
    }
    
    private void insertTable(final DatastoreClass table, final ObjectProvider op, final ClassLoaderResolver clr) {
        if (table instanceof ClassView) {
            throw new NucleusUserException("Cannot perform InsertRequest on RDBMS view " + table);
        }
        final DatastoreClass supertable = table.getSuperDatastoreClass();
        if (supertable != null) {
            this.insertTable(supertable, op, clr);
        }
        this.getInsertRequest(table, op.getClassMetaData(), clr).execute(op);
        final Collection<SecondaryDatastoreClass> secondaryTables = table.getSecondaryDatastoreClasses();
        if (secondaryTables != null) {
            final Iterator<SecondaryDatastoreClass> tablesIter = secondaryTables.iterator();
            while (tablesIter.hasNext()) {
                this.insertTable(tablesIter.next(), op, clr);
            }
        }
    }
    
    private Request getInsertRequest(final DatastoreClass table, final AbstractClassMetaData cmd, final ClassLoaderResolver clr) {
        final RequestIdentifier reqID = new RequestIdentifier(table, null, RequestType.INSERT, cmd.getFullClassName());
        Request req = this.requestsByID.get(reqID);
        if (req == null) {
            req = new InsertRequest(table, cmd, clr);
            this.requestsByID.put(reqID, req);
        }
        return req;
    }
    
    @Override
    public void fetchObject(final ObjectProvider op, final int[] memberNumbers) {
        final ExecutionContext ec = op.getExecutionContext();
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        AbstractMemberMetaData[] mmds = null;
        if (memberNumbers != null && memberNumbers.length > 0) {
            int[] memberNumbersToProcess = memberNumbers;
            final AbstractClassMetaData cmd = op.getClassMetaData();
            if (this.storeMgr.getBooleanProperty("datanucleus.rdbms.fetchUnloadedAutomatically") && !op.getLifecycleState().isDeleted()) {
                boolean fetchPerformsSelect = false;
                for (int i = 0; i < memberNumbers.length; ++i) {
                    final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(memberNumbers[i]);
                    final RelationType relationType = mmd.getRelationType(clr);
                    if (relationType != RelationType.ONE_TO_MANY_UNI && relationType != RelationType.ONE_TO_MANY_BI && relationType != RelationType.MANY_TO_MANY_BI) {
                        fetchPerformsSelect = true;
                        break;
                    }
                }
                if (fetchPerformsSelect) {
                    final List<Integer> memberNumberList = new ArrayList<Integer>();
                    for (int j = 0; j < memberNumbers.length; ++j) {
                        memberNumberList.add(memberNumbers[j]);
                    }
                    final boolean[] loadedFlags = op.getLoadedFields();
                    for (int k = 0; k < loadedFlags.length; ++k) {
                        boolean requested = false;
                        for (int l = 0; l < memberNumbers.length; ++l) {
                            if (memberNumbers[l] == k) {
                                requested = true;
                                break;
                            }
                        }
                        if (!requested && !loadedFlags[k]) {
                            final AbstractMemberMetaData mmd2 = cmd.getMetaDataForManagedMemberAtAbsolutePosition(k);
                            final RelationType relType = mmd2.getRelationType(clr);
                            if (relType == RelationType.NONE || relType == RelationType.ONE_TO_ONE_BI || relType == RelationType.ONE_TO_ONE_UNI) {
                                memberNumberList.add(k);
                            }
                        }
                    }
                    memberNumbersToProcess = new int[memberNumberList.size()];
                    int k = 0;
                    final Iterator<Integer> fieldNumberIter = memberNumberList.iterator();
                    while (fieldNumberIter.hasNext()) {
                        memberNumbersToProcess[k++] = fieldNumberIter.next();
                    }
                }
            }
            mmds = new AbstractMemberMetaData[memberNumbersToProcess.length];
            for (int m = 0; m < mmds.length; ++m) {
                mmds[m] = cmd.getMetaDataForManagedMemberAtAbsolutePosition(memberNumbersToProcess[m]);
            }
        }
        if (op.isEmbedded()) {
            final StringBuffer str = new StringBuffer();
            if (mmds != null) {
                for (int i2 = 0; i2 < mmds.length; ++i2) {
                    if (i2 > 0) {
                        str.append(',');
                    }
                    str.append(mmds[i2].getName());
                }
            }
            NucleusLogger.PERSISTENCE.info("Request to load fields \"" + str.toString() + "\" of class " + op.getClassMetaData().getFullClassName() + " but object is embedded, so ignored");
        }
        else {
            if (ec.getStatistics() != null) {
                ec.getStatistics().incrementFetchCount();
            }
            final DatastoreClass table = this.getDatastoreClass(op.getClassMetaData().getFullClassName(), clr);
            final Request req = this.getFetchRequest(table, mmds, op.getClassMetaData(), clr);
            req.execute(op);
        }
    }
    
    private Request getFetchRequest(final DatastoreClass table, final AbstractMemberMetaData[] mmds, final AbstractClassMetaData cmd, final ClassLoaderResolver clr) {
        final RequestIdentifier reqID = new RequestIdentifier(table, mmds, RequestType.FETCH, cmd.getFullClassName());
        Request req = this.requestsByID.get(reqID);
        if (req == null) {
            req = new FetchRequest(table, mmds, cmd, clr);
            this.requestsByID.put(reqID, req);
        }
        return req;
    }
    
    @Override
    public void updateObject(final ObjectProvider op, final int[] fieldNumbers) {
        this.assertReadOnlyForUpdateOfObject(op);
        this.checkForSchemaUpdatesForFieldsOfObject(op, fieldNumbers);
        AbstractMemberMetaData[] mmds = null;
        if (fieldNumbers != null && fieldNumbers.length > 0) {
            final ExecutionContext ec = op.getExecutionContext();
            mmds = new AbstractMemberMetaData[fieldNumbers.length];
            for (int i = 0; i < mmds.length; ++i) {
                mmds[i] = op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumbers[i]);
            }
            if (ec.getStatistics() != null) {
                ec.getStatistics().incrementUpdateCount();
            }
            final ClassLoaderResolver clr = ec.getClassLoaderResolver();
            final DatastoreClass dc = this.getDatastoreClass(op.getObject().getClass().getName(), clr);
            this.updateTable(dc, op, clr, mmds);
        }
    }
    
    private void updateTable(final DatastoreClass table, final ObjectProvider op, final ClassLoaderResolver clr, final AbstractMemberMetaData[] mmds) {
        if (table instanceof ClassView) {
            throw new NucleusUserException("Cannot perform UpdateRequest on RDBMS view " + table);
        }
        final DatastoreClass supertable = table.getSuperDatastoreClass();
        if (supertable != null) {
            this.updateTable(supertable, op, clr, mmds);
        }
        this.getUpdateRequest(table, mmds, op.getClassMetaData(), clr).execute(op);
        final Collection<SecondaryDatastoreClass> secondaryTables = table.getSecondaryDatastoreClasses();
        if (secondaryTables != null) {
            final Iterator<SecondaryDatastoreClass> tablesIter = secondaryTables.iterator();
            while (tablesIter.hasNext()) {
                this.updateTable(tablesIter.next(), op, clr, mmds);
            }
        }
    }
    
    private Request getUpdateRequest(final DatastoreClass table, final AbstractMemberMetaData[] mmds, final AbstractClassMetaData cmd, final ClassLoaderResolver clr) {
        final RequestIdentifier reqID = new RequestIdentifier(table, mmds, RequestType.UPDATE, cmd.getFullClassName());
        Request req = this.requestsByID.get(reqID);
        if (req == null) {
            req = new UpdateRequest(table, mmds, cmd, clr);
            this.requestsByID.put(reqID, req);
        }
        return req;
    }
    
    @Override
    public void deleteObject(final ObjectProvider op) {
        this.assertReadOnlyForUpdateOfObject(op);
        final ExecutionContext ec = op.getExecutionContext();
        if (ec.getStatistics() != null) {
            ec.getStatistics().incrementDeleteCount();
        }
        final ClassLoaderResolver clr = op.getExecutionContext().getClassLoaderResolver();
        final DatastoreClass dc = this.getDatastoreClass(op.getClassMetaData().getFullClassName(), clr);
        this.deleteTable(dc, op, clr);
    }
    
    private void deleteTable(final DatastoreClass table, final ObjectProvider sm, final ClassLoaderResolver clr) {
        if (table instanceof ClassView) {
            throw new NucleusUserException("Cannot perform DeleteRequest on RDBMS view " + table);
        }
        final Collection<SecondaryDatastoreClass> secondaryTables = table.getSecondaryDatastoreClasses();
        if (secondaryTables != null) {
            final Iterator<SecondaryDatastoreClass> tablesIter = secondaryTables.iterator();
            while (tablesIter.hasNext()) {
                this.deleteTable(tablesIter.next(), sm, clr);
            }
        }
        this.getDeleteRequest(table, sm.getClassMetaData(), clr).execute(sm);
        final DatastoreClass supertable = table.getSuperDatastoreClass();
        if (supertable != null) {
            this.deleteTable(supertable, sm, clr);
        }
    }
    
    private Request getDeleteRequest(final DatastoreClass table, final AbstractClassMetaData acmd, final ClassLoaderResolver clr) {
        final RequestIdentifier reqID = new RequestIdentifier(table, null, RequestType.DELETE, acmd.getFullClassName());
        Request req = this.requestsByID.get(reqID);
        if (req == null) {
            req = new DeleteRequest(table, acmd, clr);
            this.requestsByID.put(reqID, req);
        }
        return req;
    }
    
    @Override
    public void locateObjects(final ObjectProvider[] ops) {
        if (ops == null || ops.length == 0) {
            return;
        }
        final ClassLoaderResolver clr = ops[0].getExecutionContext().getClassLoaderResolver();
        final Map<DatastoreClass, List<ObjectProvider>> opsByTable = new HashMap<DatastoreClass, List<ObjectProvider>>();
        for (int i = 0; i < ops.length; ++i) {
            final AbstractClassMetaData cmd = ops[i].getClassMetaData();
            DatastoreClass table = this.getDatastoreClass(cmd.getFullClassName(), clr);
            table = table.getBaseDatastoreClass();
            List<ObjectProvider> opList = opsByTable.get(table);
            if (opList == null) {
                opList = new ArrayList<ObjectProvider>();
            }
            opList.add(ops[i]);
            opsByTable.put(table, opList);
        }
        for (final Map.Entry<DatastoreClass, List<ObjectProvider>> entry : opsByTable.entrySet()) {
            final DatastoreClass table = entry.getKey();
            final List<ObjectProvider> tableOps = entry.getValue();
            final LocateBulkRequest req = new LocateBulkRequest(table);
            req.execute(tableOps.toArray(new ObjectProvider[tableOps.size()]));
        }
    }
    
    @Override
    public void locateObject(final ObjectProvider op) {
        final ClassLoaderResolver clr = op.getExecutionContext().getClassLoaderResolver();
        final DatastoreClass table = this.getDatastoreClass(op.getObject().getClass().getName(), clr);
        this.getLocateRequest(table, op.getObject().getClass().getName()).execute(op);
    }
    
    private Request getLocateRequest(final DatastoreClass table, final String className) {
        final RequestIdentifier reqID = new RequestIdentifier(table, null, RequestType.LOCATE, className);
        Request req = this.requestsByID.get(reqID);
        if (req == null) {
            req = new LocateRequest(table);
            this.requestsByID.put(reqID, req);
        }
        return req;
    }
    
    @Override
    public Object findObject(final ExecutionContext ec, final Object id) {
        return null;
    }
    
    public void removeAllRequests() {
        synchronized (this.requestsByID) {
            this.requestsByID.clear();
        }
    }
    
    public void removeRequestsForTable(final DatastoreClass table) {
        synchronized (this.requestsByID) {
            final Set keySet = new HashSet(this.requestsByID.keySet());
            for (final RequestIdentifier reqId : keySet) {
                if (reqId.getTable() == table) {
                    this.requestsByID.remove(reqId);
                }
            }
        }
    }
    
    private void checkForSchemaUpdatesForFieldsOfObject(final ObjectProvider sm, final int[] fieldNumbers) {
        if (this.storeMgr.getBooleanObjectProperty("datanucleus.rdbms.dynamicSchemaUpdates")) {
            final DynamicSchemaFieldManager dynamicSchemaFM = new DynamicSchemaFieldManager((RDBMSStoreManager)this.storeMgr, sm);
            sm.provideFields(fieldNumbers, dynamicSchemaFM);
            if (dynamicSchemaFM.hasPerformedSchemaUpdates()) {
                this.requestsByID.clear();
            }
        }
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
