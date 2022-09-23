// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import java.sql.SQLException;
import org.datanucleus.metadata.CollectionMetaData;
import org.datanucleus.util.StringUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.FieldValues;
import java.util.Iterator;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.scostore.SetStore;

public abstract class AbstractSetStore extends AbstractCollectionStore implements SetStore
{
    protected AbstractSetStore(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        super(storeMgr, clr);
    }
    
    @Override
    public abstract Iterator iterator(final ObjectProvider p0);
    
    @Override
    public boolean add(final ObjectProvider op, final Object element, final int size) {
        final ExecutionContext ec = op.getExecutionContext();
        this.validateElementForWriting(ec, element, null);
        boolean modified = false;
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            try {
                final int[] num = this.internalAdd(op, mconn, false, element, true);
                if (num[0] > 0) {
                    modified = true;
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (MappedDatastoreException e) {
            NucleusLogger.DATASTORE.error(e);
            final String msg = AbstractSetStore.LOCALISER.msg("056009", e.getMessage());
            NucleusLogger.DATASTORE.error(msg);
            throw new NucleusDataStoreException(msg, e);
        }
        return modified;
    }
    
    @Override
    public boolean addAll(final ObjectProvider op, final Collection elements, final int size) {
        if (elements == null || elements.size() == 0) {
            return false;
        }
        boolean modified = false;
        final List exceptions = new ArrayList();
        final boolean batched = elements.size() > 1;
        final ExecutionContext ec = op.getExecutionContext();
        Iterator iter = elements.iterator();
        while (iter.hasNext()) {
            this.validateElementForWriting(ec, iter.next(), null);
        }
        final ManagedConnection mconn = this.storeMgr.getConnection(ec);
        try {
            iter = elements.iterator();
            Object element = null;
            int[] returnCode = null;
            while (iter.hasNext()) {
                element = iter.next();
                try {
                    returnCode = this.internalAdd(op, mconn, batched, element, !batched || (batched && !iter.hasNext()));
                }
                catch (MappedDatastoreException mde) {
                    exceptions.add(mde);
                    NucleusLogger.DATASTORE.error(mde);
                }
            }
            if (exceptions.size() == 0) {
                if (returnCode == null) {
                    modified = false;
                }
                else {
                    for (int i = 0; i < returnCode.length; ++i) {
                        if (returnCode[i] > 0) {
                            modified = true;
                        }
                    }
                }
            }
        }
        finally {
            mconn.release();
        }
        if (!exceptions.isEmpty()) {
            final String msg = AbstractSetStore.LOCALISER.msg("056009", exceptions.get(0).getMessage());
            NucleusLogger.DATASTORE.error(msg);
            throw new NucleusDataStoreException(msg, exceptions.toArray(new Throwable[exceptions.size()]), op.getObject());
        }
        return modified;
    }
    
    @Override
    public boolean remove(final ObjectProvider op, final Object element, final int size, final boolean allowDependentField) {
        if (!this.validateElementForReading(op, element)) {
            NucleusLogger.DATASTORE.debug("Attempt to remove element=" + StringUtils.toJVMIDString(element) + " but doesn't exist in this Set.");
            return false;
        }
        Object elementToRemove = element;
        final ExecutionContext ec = op.getExecutionContext();
        if (ec.getApiAdapter().isDetached(element)) {
            elementToRemove = ec.findObject(ec.getApiAdapter().getIdForObject(element), true, false, element.getClass().getName());
        }
        final boolean modified = this.remove(op, elementToRemove, size);
        final CollectionMetaData collmd = this.ownerMemberMetaData.getCollection();
        boolean dependent = collmd.isDependentElement();
        if (this.ownerMemberMetaData.isCascadeRemoveOrphans()) {
            dependent = true;
        }
        if (allowDependentField && dependent && !collmd.isEmbeddedElement()) {
            op.getExecutionContext().deleteObjectInternal(elementToRemove);
        }
        return modified;
    }
    
    @Override
    public boolean removeAll(final ObjectProvider op, final Collection elements, final int size) {
        if (elements == null || elements.size() == 0) {
            return false;
        }
        boolean modified = false;
        final List exceptions = new ArrayList();
        final boolean batched = elements.size() > 1;
        for (final Object element : elements) {
            if (!this.validateElementForReading(op, element)) {
                NucleusLogger.DATASTORE.debug("AbstractSetStore::removeAll element=" + element + " doesn't exist in this Set.");
                return false;
            }
        }
        try {
            final ExecutionContext ec = op.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            try {
                final SQLController sqlControl = this.storeMgr.getSQLController();
                try {
                    sqlControl.processStatementsForConnection(mconn);
                }
                catch (SQLException e) {
                    throw new MappedDatastoreException("SQLException", e);
                }
                final Iterator iter = elements.iterator();
                while (iter.hasNext()) {
                    final Object element2 = iter.next();
                    try {
                        final int[] rc = this.internalRemove(op, mconn, batched, element2, !batched || (batched && !iter.hasNext()));
                        if (rc == null) {
                            continue;
                        }
                        for (int i = 0; i < rc.length; ++i) {
                            if (rc[i] > 0) {
                                modified = true;
                            }
                        }
                    }
                    catch (MappedDatastoreException mde) {
                        mde.printStackTrace();
                        exceptions.add(mde);
                        NucleusLogger.DATASTORE.error(mde);
                    }
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (MappedDatastoreException e2) {
            e2.printStackTrace();
            exceptions.add(e2);
            NucleusLogger.DATASTORE.error(e2);
        }
        if (!exceptions.isEmpty()) {
            final String msg = AbstractSetStore.LOCALISER.msg("056012", exceptions.get(0).getMessage());
            NucleusLogger.DATASTORE.error(msg);
            throw new NucleusDataStoreException(msg, exceptions.toArray(new Throwable[exceptions.size()]), op.getObject());
        }
        return modified;
    }
    
    public int[] internalAdd(final ObjectProvider op, final ManagedConnection conn, final boolean batched, final Object element, final boolean processNow) throws MappedDatastoreException {
        final ExecutionContext ec = op.getExecutionContext();
        final SQLController sqlControl = this.storeMgr.getSQLController();
        final String addStmt = this.getAddStmt();
        try {
            final PreparedStatement ps = sqlControl.getStatementForUpdate(conn, addStmt, batched);
            try {
                int jdbcPosition = 1;
                jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                jdbcPosition = BackingStoreHelper.populateElementInStatement(ec, ps, element, jdbcPosition, this.elementMapping);
                if (this.relationDiscriminatorMapping != null) {
                    jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                }
                return sqlControl.executeStatementUpdate(ec, conn, addStmt, ps, processNow);
            }
            finally {
                sqlControl.closeStatement(conn, ps);
            }
        }
        catch (SQLException e) {
            throw new MappedDatastoreException(this.getAddStmt(), e);
        }
    }
    
    public boolean remove(final ObjectProvider op, final Object element, final int size) {
        boolean modified = false;
        final ExecutionContext ec = op.getExecutionContext();
        final String removeStmt = this.getRemoveStmt(element);
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, removeStmt, false);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    jdbcPosition = BackingStoreHelper.populateElementForWhereClauseInStatement(ec, ps, element, jdbcPosition, this.elementMapping);
                    if (this.relationDiscriminatorMapping != null) {
                        jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
                    final int[] rowsDeleted = sqlControl.executeStatementUpdate(ec, mconn, removeStmt, ps, true);
                    modified = (rowsDeleted[0] == 1);
                }
                finally {
                    sqlControl.closeStatement(mconn, ps);
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (SQLException e) {
            NucleusLogger.DATASTORE.error(e);
            final String msg = AbstractSetStore.LOCALISER.msg("056012", removeStmt);
            NucleusLogger.DATASTORE.error(msg);
            throw new NucleusDataStoreException(msg, e);
        }
        return modified;
    }
    
    @Override
    public int[] internalRemove(final ObjectProvider op, final ManagedConnection conn, final boolean batched, final Object element, final boolean executeNow) throws MappedDatastoreException {
        final ExecutionContext ec = op.getExecutionContext();
        final SQLController sqlControl = this.storeMgr.getSQLController();
        final String removeStmt = this.getRemoveStmt(element);
        try {
            final PreparedStatement ps = sqlControl.getStatementForUpdate(conn, removeStmt, batched);
            try {
                int jdbcPosition = 1;
                jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                jdbcPosition = BackingStoreHelper.populateElementForWhereClauseInStatement(ec, ps, element, jdbcPosition, this.elementMapping);
                if (this.relationDiscriminatorMapping != null) {
                    jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                }
                return sqlControl.executeStatementUpdate(ec, conn, removeStmt, ps, executeNow);
            }
            finally {
                sqlControl.closeStatement(conn, ps);
            }
        }
        catch (SQLException e) {
            throw new MappedDatastoreException("SQLException", e);
        }
    }
}
