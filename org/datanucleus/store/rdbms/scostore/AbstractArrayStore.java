// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.exceptions.NotYetFlushedException;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import java.sql.SQLException;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.FieldValues;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.scostore.ArrayStore;

public abstract class AbstractArrayStore extends ElementContainerStore implements ArrayStore
{
    protected AbstractArrayStore(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        super(storeMgr, clr);
    }
    
    @Override
    public List getArray(final ObjectProvider op) {
        final Iterator iter = this.iterator(op);
        final List elements = new ArrayList();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            elements.add(obj);
        }
        return elements;
    }
    
    @Override
    public void clear(final ObjectProvider op) {
        Collection dependentElements = null;
        if (this.ownerMemberMetaData.getArray().isDependentElement()) {
            dependentElements = new HashSet();
            final Iterator iter = this.iterator(op);
            while (iter.hasNext()) {
                dependentElements.add(iter.next());
            }
        }
        this.clearInternal(op);
        if (dependentElements != null && dependentElements.size() > 0) {
            op.getExecutionContext().deleteObjects(dependentElements.toArray());
        }
    }
    
    @Override
    public boolean set(final ObjectProvider op, final Object array) {
        if (array == null || Array.getLength(array) == 0) {
            return true;
        }
        final ExecutionContext ec = op.getExecutionContext();
        final int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            final Object obj = Array.get(array, i);
            this.validateElementForWriting(ec, obj, null);
        }
        boolean modified = false;
        final List exceptions = new ArrayList();
        final boolean batched = this.allowsBatching() && length > 1;
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            try {
                this.processBatchedWrites(mconn);
                Object element = null;
                for (int j = 0; j < length; ++j) {
                    element = Array.get(array, j);
                    try {
                        final int[] rc = this.internalAdd(op, element, mconn, batched, j, j == length - 1);
                        if (rc != null) {
                            for (int k = 0; k < rc.length; ++k) {
                                if (rc[k] > 0) {
                                    modified = true;
                                }
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
        catch (MappedDatastoreException e) {
            e.printStackTrace();
            exceptions.add(e);
            NucleusLogger.DATASTORE.error(e);
        }
        if (!exceptions.isEmpty()) {
            final String msg = AbstractArrayStore.LOCALISER.msg("056009", exceptions.get(0).getMessage());
            NucleusLogger.DATASTORE.error(msg);
            throw new NucleusDataStoreException(msg, exceptions.toArray(new Throwable[exceptions.size()]), op.getObject());
        }
        return modified;
    }
    
    public boolean add(final ObjectProvider op, final Object element, final int position) {
        final ExecutionContext ec = op.getExecutionContext();
        this.validateElementForWriting(ec, element, null);
        boolean modified = false;
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            try {
                final int[] returnCode = this.internalAdd(op, element, mconn, false, position, true);
                if (returnCode[0] > 0) {
                    modified = true;
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (MappedDatastoreException e) {
            throw new NucleusDataStoreException(AbstractArrayStore.LOCALISER.msg("056009", e.getMessage()), e.getCause());
        }
        return modified;
    }
    
    @Override
    public abstract Iterator iterator(final ObjectProvider p0);
    
    public void clearInternal(final ObjectProvider ownerOP) {
        final String clearStmt = this.getClearStmt();
        try {
            final ExecutionContext ec = ownerOP.getExecutionContext();
            final ManagedConnection mconn = this.getStoreManager().getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, clearStmt, false);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(ownerOP, ec, ps, jdbcPosition, this);
                    if (this.getRelationDiscriminatorMapping() != null) {
                        BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
                    sqlControl.executeStatementUpdate(ec, mconn, clearStmt, ps, true);
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
            throw new NucleusDataStoreException(AbstractArrayStore.LOCALISER.msg("056013", clearStmt), e);
        }
    }
    
    public int[] internalAdd(final ObjectProvider op, final Object element, final ManagedConnection conn, final boolean batched, final int orderId, final boolean executeNow) throws MappedDatastoreException {
        final ExecutionContext ec = op.getExecutionContext();
        final SQLController sqlControl = this.storeMgr.getSQLController();
        final String addStmt = this.getAddStmt();
        try {
            final PreparedStatement ps = sqlControl.getStatementForUpdate(conn, addStmt, false);
            boolean notYetFlushedError = false;
            try {
                int jdbcPosition = 1;
                jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                jdbcPosition = BackingStoreHelper.populateElementInStatement(ec, ps, element, jdbcPosition, this.getElementMapping());
                jdbcPosition = BackingStoreHelper.populateOrderInStatement(ec, ps, orderId, jdbcPosition, this.getOrderMapping());
                if (this.getRelationDiscriminatorMapping() != null) {
                    jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                }
                return sqlControl.executeStatementUpdate(ec, conn, addStmt, ps, executeNow);
            }
            catch (NotYetFlushedException nfe) {
                notYetFlushedError = true;
                throw nfe;
            }
            finally {
                if (notYetFlushedError) {
                    sqlControl.abortStatementForConnection(conn, ps);
                }
                else {
                    sqlControl.closeStatement(conn, ps);
                }
            }
        }
        catch (SQLException e) {
            throw new MappedDatastoreException(addStmt, e);
        }
    }
    
    public void processBatchedWrites(final ManagedConnection mconn) throws MappedDatastoreException {
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            sqlControl.processStatementsForConnection(mconn);
        }
        catch (SQLException e) {
            throw new MappedDatastoreException("SQLException", e);
        }
    }
}
