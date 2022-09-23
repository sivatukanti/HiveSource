// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.types.SCOUtils;
import java.util.Collection;
import java.sql.Statement;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.ExecutionContext;
import java.sql.SQLException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedElementPCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedPCMapping;
import org.datanucleus.store.rdbms.scostore.ElementContainerStore;
import java.util.HashMap;
import org.datanucleus.store.rdbms.scostore.IteratorStatement;
import org.datanucleus.store.query.Query;
import java.util.Map;
import java.sql.ResultSet;
import org.datanucleus.store.query.AbstractQueryResult;

public abstract class AbstractRDBMSQueryResult extends AbstractQueryResult
{
    protected ResultSet rs;
    protected ResultObjectFactory rof;
    protected Map<Object, Map<Integer, Object>> bulkLoadedValueByMemberNumber;
    
    public AbstractRDBMSQueryResult(final Query query, final ResultObjectFactory rof, final ResultSet rs) {
        super(query);
        this.rof = rof;
        this.rs = rs;
    }
    
    public void registerMemberBulkResultSet(final IteratorStatement iterStmt, ResultSet rs) {
        if (this.bulkLoadedValueByMemberNumber == null) {
            this.bulkLoadedValueByMemberNumber = new HashMap<Object, Map<Integer, Object>>();
        }
        try {
            final ExecutionContext ec = this.query.getExecutionContext();
            final AbstractMemberMetaData mmd = iterStmt.getBackingStore().getOwnerMemberMetaData();
            if (mmd.hasCollection()) {
                final ElementContainerStore backingStore = (ElementContainerStore)iterStmt.getBackingStore();
                if (backingStore.isElementsAreEmbedded() || backingStore.isElementsAreSerialised()) {
                    final int[] param = new int[backingStore.getElementMapping().getNumberOfDatastoreMappings()];
                    for (int i = 0; i < param.length; ++i) {
                        param[i] = i + 1;
                    }
                    if (backingStore.getElementMapping() instanceof SerialisedPCMapping || backingStore.getElementMapping() instanceof SerialisedReferenceMapping || backingStore.getElementMapping() instanceof EmbeddedElementPCMapping) {
                        while (rs.next()) {
                            final Object owner = iterStmt.getOwnerMapIndex().getMapping().getObject(ec, rs, iterStmt.getOwnerMapIndex().getColumnPositions());
                            final Object element = backingStore.getElementMapping().getObject(ec, rs, param, ec.findObjectProvider(owner), backingStore.getOwnerMemberMetaData().getAbsoluteFieldNumber());
                            this.addOwnerMemberValue(mmd, owner, element);
                        }
                    }
                    else {
                        while (rs.next()) {
                            final Object owner = iterStmt.getOwnerMapIndex().getMapping().getObject(ec, rs, iterStmt.getOwnerMapIndex().getColumnPositions());
                            final Object element = backingStore.getElementMapping().getObject(ec, rs, param);
                            this.addOwnerMemberValue(mmd, owner, element);
                        }
                    }
                }
                else if (backingStore.getElementMapping() instanceof ReferenceMapping) {
                    final int[] param = new int[backingStore.getElementMapping().getNumberOfDatastoreMappings()];
                    for (int i = 0; i < param.length; ++i) {
                        param[i] = i + 1;
                    }
                    while (rs.next()) {
                        final Object owner = iterStmt.getOwnerMapIndex().getMapping().getObject(ec, rs, iterStmt.getOwnerMapIndex().getColumnPositions());
                        final Object element = backingStore.getElementMapping().getObject(ec, rs, param);
                        this.addOwnerMemberValue(mmd, owner, element);
                    }
                }
                else {
                    final ResultObjectFactory scoROF = ((RDBMSStoreManager)this.query.getStoreManager()).newResultObjectFactory(backingStore.getEmd(), iterStmt.getStatementClassMapping(), false, null, ec.getClassLoaderResolver().classForName(backingStore.getOwnerMemberMetaData().getCollection().getElementType()));
                    while (rs.next()) {
                        final Object owner = iterStmt.getOwnerMapIndex().getMapping().getObject(ec, rs, iterStmt.getOwnerMapIndex().getColumnPositions());
                        final Object element = scoROF.getObject(ec, rs);
                        this.addOwnerMemberValue(mmd, owner, element);
                    }
                }
            }
        }
        catch (SQLException sqle) {
            NucleusLogger.DATASTORE.error("Exception thrown processing bulk loaded field " + iterStmt.getBackingStore().getOwnerMemberMetaData().getFullFieldName(), sqle);
            try {
                Statement stmt = null;
                try {
                    stmt = rs.getStatement();
                    rs.close();
                }
                catch (SQLException e) {
                    NucleusLogger.DATASTORE.error(AbstractRDBMSQueryResult.LOCALISER.msg("052605", e));
                }
                finally {
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    }
                    catch (SQLException ex) {}
                }
            }
            finally {
                rs = null;
            }
        }
        finally {
            try {
                Statement stmt2 = null;
                try {
                    stmt2 = rs.getStatement();
                    rs.close();
                }
                catch (SQLException e2) {
                    NucleusLogger.DATASTORE.error(AbstractRDBMSQueryResult.LOCALISER.msg("052605", e2));
                    try {
                        if (stmt2 != null) {
                            stmt2.close();
                        }
                    }
                    catch (SQLException e2) {}
                }
                finally {
                    try {
                        if (stmt2 != null) {
                            stmt2.close();
                        }
                    }
                    catch (SQLException ex2) {}
                }
            }
            finally {
                rs = null;
            }
        }
    }
    
    public abstract void initialise() throws SQLException;
    
    private void addOwnerMemberValue(final AbstractMemberMetaData mmd, final Object owner, final Object element) {
        final Object ownerId = this.query.getExecutionContext().getApiAdapter().getIdForObject(owner);
        Map<Integer, Object> fieldValuesForOwner = this.bulkLoadedValueByMemberNumber.get(ownerId);
        if (fieldValuesForOwner == null) {
            fieldValuesForOwner = new HashMap<Integer, Object>();
            this.bulkLoadedValueByMemberNumber.put(ownerId, fieldValuesForOwner);
        }
        Collection coll = fieldValuesForOwner.get(mmd.getAbsoluteFieldNumber());
        if (coll == null) {
            try {
                final Class instanceType = SCOUtils.getContainerInstanceType(mmd.getType(), mmd.getOrderMetaData() != null);
                coll = instanceType.newInstance();
                fieldValuesForOwner.put(mmd.getAbsoluteFieldNumber(), coll);
            }
            catch (Exception e) {
                throw new NucleusDataStoreException(e.getMessage(), e);
            }
        }
        coll.add(element);
    }
    
    @Override
    public void disconnect() {
        if (this.query == null) {
            return;
        }
        super.disconnect();
        this.rof = null;
        this.rs = null;
    }
    
    @Override
    public synchronized void close() {
        super.close();
        this.rof = null;
        this.rs = null;
    }
    
    @Override
    protected void closeResults() {
        if (this.rs != null) {
            try {
                Statement stmt = null;
                try {
                    stmt = this.rs.getStatement();
                    this.rs.close();
                }
                catch (SQLException e) {
                    NucleusLogger.DATASTORE.error(AbstractRDBMSQueryResult.LOCALISER.msg("052605", e));
                }
                finally {
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    }
                    catch (SQLException ex) {}
                }
            }
            finally {
                this.rs = null;
            }
        }
    }
    
    @Override
    public int hashCode() {
        if (this.rs != null) {
            return this.rs.hashCode();
        }
        if (this.query != null) {
            return this.query.hashCode();
        }
        return StringUtils.toJVMIDString(this).hashCode();
    }
}
