// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import java.sql.SQLException;
import org.datanucleus.store.rdbms.JDBCUtils;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.ArrayList;
import java.util.List;
import org.datanucleus.metadata.CollectionMetaData;
import org.datanucleus.ExecutionContext;
import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Iterator;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.scostore.ListStore;

public abstract class AbstractListStore extends AbstractCollectionStore implements ListStore
{
    protected boolean indexedList;
    protected String indexOfStmt;
    protected String lastIndexOfStmt;
    protected String removeAtStmt;
    protected String shiftStmt;
    
    protected AbstractListStore(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        super(storeMgr, clr);
        this.indexedList = true;
    }
    
    @Override
    public Iterator iterator(final ObjectProvider op) {
        return this.listIterator(op);
    }
    
    @Override
    public ListIterator listIterator(final ObjectProvider op) {
        return this.listIterator(op, -1, -1);
    }
    
    protected abstract ListIterator listIterator(final ObjectProvider p0, final int p1, final int p2);
    
    @Override
    public boolean add(final ObjectProvider op, final Object element, final int size) {
        return this.internalAdd(op, 0, true, Collections.singleton(element), size);
    }
    
    @Override
    public void add(final ObjectProvider op, final Object element, final int index, final int size) {
        this.internalAdd(op, index, false, Collections.singleton(element), size);
    }
    
    @Override
    public boolean addAll(final ObjectProvider op, final Collection elements, final int size) {
        return this.internalAdd(op, 0, true, elements, size);
    }
    
    @Override
    public boolean addAll(final ObjectProvider op, final Collection elements, final int index, final int size) {
        return this.internalAdd(op, index, false, elements, size);
    }
    
    protected abstract boolean internalAdd(final ObjectProvider p0, final int p1, final boolean p2, final Collection p3, final int p4);
    
    @Override
    public Object get(final ObjectProvider op, final int index) {
        final ListIterator iter = this.listIterator(op, index, index);
        if (iter == null || !iter.hasNext()) {
            return null;
        }
        if (!this.indexedList) {
            Object obj = null;
            int position = 0;
            while (iter.hasNext()) {
                obj = iter.next();
                if (position == index) {
                    return obj;
                }
                ++position;
            }
        }
        return iter.next();
    }
    
    @Override
    public int indexOf(final ObjectProvider op, final Object element) {
        this.validateElementForReading(op, element);
        return this.internalIndexOf(op, element, this.getIndexOfStmt(element));
    }
    
    @Override
    public int lastIndexOf(final ObjectProvider op, final Object element) {
        this.validateElementForReading(op, element);
        return this.internalIndexOf(op, element, this.getLastIndexOfStmt(element));
    }
    
    @Override
    public boolean removeAll(final ObjectProvider op, final Collection elements, final int size) {
        if (elements == null || elements.size() == 0) {
            return false;
        }
        boolean modified = false;
        if (this.indexedList) {
            final int[] indices = this.getIndicesOf(op, elements);
            for (int i = 0; i < indices.length; ++i) {
                this.internalRemoveAt(op, indices[i], -1);
                modified = true;
            }
            boolean dependent = this.ownerMemberMetaData.getCollection().isDependentElement();
            if (this.ownerMemberMetaData.isCascadeRemoveOrphans()) {
                dependent = true;
            }
            if (dependent) {
                op.getExecutionContext().deleteObjects(elements.toArray());
            }
        }
        else {
            for (final Object element : elements) {
                this.remove(op, element, size, true);
            }
        }
        return modified;
    }
    
    @Override
    public boolean remove(final ObjectProvider op, final Object element, final int size, final boolean allowDependentField) {
        if (!this.validateElementForReading(op, element)) {
            return false;
        }
        Object elementToRemove = element;
        final ExecutionContext ec = op.getExecutionContext();
        if (ec.getApiAdapter().isDetached(element)) {
            elementToRemove = ec.findObject(ec.getApiAdapter().getIdForObject(element), true, false, element.getClass().getName());
        }
        final boolean modified = this.internalRemove(op, elementToRemove, size);
        if (allowDependentField) {
            final CollectionMetaData collmd = this.ownerMemberMetaData.getCollection();
            boolean dependent = collmd.isDependentElement();
            if (this.ownerMemberMetaData.isCascadeRemoveOrphans()) {
                dependent = true;
            }
            if (dependent && !collmd.isEmbeddedElement()) {
                op.getExecutionContext().deleteObjectInternal(elementToRemove);
            }
        }
        return modified;
    }
    
    protected abstract boolean internalRemove(final ObjectProvider p0, final Object p1, final int p2);
    
    @Override
    public Object remove(final ObjectProvider op, final int index, final int size) {
        final Object element = this.get(op, index);
        if (this.indexedList) {
            this.internalRemoveAt(op, index, size);
        }
        else {
            this.internalRemove(op, element, size);
        }
        final CollectionMetaData collmd = this.ownerMemberMetaData.getCollection();
        boolean dependent = collmd.isDependentElement();
        if (this.ownerMemberMetaData.isCascadeRemoveOrphans()) {
            dependent = true;
        }
        if (dependent && !collmd.isEmbeddedElement() && !this.contains(op, element)) {
            op.getExecutionContext().deleteObjectInternal(element);
        }
        return element;
    }
    
    protected abstract void internalRemoveAt(final ObjectProvider p0, final int p1, final int p2);
    
    @Override
    public List subList(final ObjectProvider op, final int startIdx, final int endIdx) {
        final ListIterator iter = this.listIterator(op, startIdx, endIdx);
        final List list = new ArrayList();
        while (iter.hasNext()) {
            list.add(iter.next());
        }
        if (!this.indexedList && list.size() > endIdx - startIdx) {
            return list.subList(startIdx, endIdx);
        }
        return list;
    }
    
    protected int[] getIndicesOf(final ObjectProvider op, final Collection elements) {
        if (elements == null || elements.size() == 0) {
            return null;
        }
        final Iterator iter = elements.iterator();
        while (iter.hasNext()) {
            this.validateElementForReading(op, iter.next());
        }
        final String stmt = this.getIndicesOfStmt(elements);
        final int[] indices = new int[elements.size()];
        try {
            final ExecutionContext ec = op.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, stmt, false);
                try {
                    if (!elements.isEmpty()) {
                        final Iterator elemIter = elements.iterator();
                        int jdbcPosition = 1;
                        while (elemIter.hasNext()) {
                            final Object element = elemIter.next();
                            jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                            jdbcPosition = BackingStoreHelper.populateElementForWhereClauseInStatement(ec, ps, element, jdbcPosition, this.elementMapping);
                            if (this.relationDiscriminatorMapping != null) {
                                jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                            }
                        }
                    }
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        int i = 0;
                        while (rs.next()) {
                            indices[i++] = rs.getInt(1);
                        }
                        if (i < elements.size()) {
                            throw new NucleusDataStoreException(AbstractListStore.LOCALISER.msg("056023", stmt));
                        }
                        JDBCUtils.logWarnings(rs);
                    }
                    finally {
                        rs.close();
                    }
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
            throw new NucleusDataStoreException(AbstractListStore.LOCALISER.msg("056017", stmt), e);
        }
        return indices;
    }
    
    protected int internalIndexOf(final ObjectProvider op, final Object element, final String stmt) {
        try {
            final ExecutionContext ec = op.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, stmt, false);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    jdbcPosition = BackingStoreHelper.populateElementForWhereClauseInStatement(ec, ps, element, jdbcPosition, this.elementMapping);
                    if (this.relationDiscriminatorMapping != null) {
                        jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        final boolean found = rs.next();
                        if (!found) {
                            JDBCUtils.logWarnings(rs);
                            return -1;
                        }
                        final int index = rs.getInt(1);
                        JDBCUtils.logWarnings(rs);
                        return index;
                    }
                    finally {
                        rs.close();
                    }
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
            throw new NucleusDataStoreException(AbstractListStore.LOCALISER.msg("056017", stmt), e);
        }
    }
    
    protected void internalRemoveAt(final ObjectProvider op, final int index, final String stmt, final int size) {
        int currentListSize = 0;
        if (size < 0) {
            currentListSize = this.size(op);
        }
        else {
            currentListSize = size;
        }
        final ExecutionContext ec = op.getExecutionContext();
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, stmt, false);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    jdbcPosition = BackingStoreHelper.populateOrderInStatement(ec, ps, index, jdbcPosition, this.getOrderMapping());
                    if (this.relationDiscriminatorMapping != null) {
                        jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
                    final int[] rowsDeleted = sqlControl.executeStatementUpdate(ec, mconn, stmt, ps, true);
                    if (rowsDeleted[0] == 0) {}
                }
                finally {
                    sqlControl.closeStatement(mconn, ps);
                }
                if (index != currentListSize - 1) {
                    for (int i = index + 1; i < currentListSize; ++i) {
                        this.internalShift(op, mconn, false, i, -1, true);
                    }
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(AbstractListStore.LOCALISER.msg("056012", stmt), e);
        }
        catch (MappedDatastoreException e2) {
            throw new NucleusDataStoreException(AbstractListStore.LOCALISER.msg("056012", stmt), e2);
        }
    }
    
    protected int[] internalShift(final ObjectProvider op, final ManagedConnection conn, final boolean batched, final int oldIndex, final int amount, final boolean executeNow) throws MappedDatastoreException {
        final ExecutionContext ec = op.getExecutionContext();
        final SQLController sqlControl = this.storeMgr.getSQLController();
        final String shiftStmt = this.getShiftStmt();
        try {
            final PreparedStatement ps = sqlControl.getStatementForUpdate(conn, shiftStmt, batched);
            try {
                int jdbcPosition = 1;
                jdbcPosition = BackingStoreHelper.populateOrderInStatement(ec, ps, amount, jdbcPosition, this.orderMapping);
                jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                jdbcPosition = BackingStoreHelper.populateOrderInStatement(ec, ps, oldIndex, jdbcPosition, this.orderMapping);
                if (this.relationDiscriminatorMapping != null) {
                    jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                }
                return sqlControl.executeStatementUpdate(ec, conn, shiftStmt, ps, executeNow);
            }
            finally {
                sqlControl.closeStatement(conn, ps);
            }
        }
        catch (SQLException sqle) {
            throw new MappedDatastoreException(shiftStmt, sqle);
        }
    }
    
    protected String getIndexOfStmt(final Object element) {
        if (this.elementMapping instanceof ReferenceMapping && this.elementMapping.getNumberOfDatastoreMappings() > 1) {
            return this.getIndexOfStatementString(element);
        }
        if (this.indexOfStmt == null) {
            synchronized (this) {
                this.indexOfStmt = this.getIndexOfStatementString(element);
            }
        }
        return this.indexOfStmt;
    }
    
    private String getIndexOfStatementString(final Object element) {
        final StringBuffer stmt = new StringBuffer("SELECT ");
        for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
        }
        stmt.append(" FROM ");
        stmt.append(this.containerTable.toString());
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
        BackingStoreHelper.appendWhereClauseForElement(stmt, this.elementMapping, element, this.isElementsAreSerialised(), null, false);
        if (this.relationDiscriminatorMapping != null) {
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
        }
        stmt.append(" ORDER BY ");
        for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
        }
        return stmt.toString();
    }
    
    protected String getLastIndexOfStmt(final Object element) {
        if (this.elementMapping instanceof ReferenceMapping && this.elementMapping.getNumberOfDatastoreMappings() > 1) {
            return this.getLastIndexOfStatementString(element);
        }
        if (this.lastIndexOfStmt == null) {
            synchronized (this) {
                this.lastIndexOfStmt = this.getLastIndexOfStatementString(element);
            }
        }
        return this.lastIndexOfStmt;
    }
    
    private String getLastIndexOfStatementString(final Object element) {
        final StringBuffer stmt = new StringBuffer("SELECT ");
        for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
        }
        stmt.append(" FROM ");
        stmt.append(this.containerTable.toString());
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
        BackingStoreHelper.appendWhereClauseForElement(stmt, this.elementMapping, element, this.isElementsAreSerialised(), null, false);
        if (this.relationDiscriminatorMapping != null) {
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
        }
        stmt.append(" ORDER BY ");
        for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
            stmt.append(" DESC ");
        }
        return stmt.toString();
    }
    
    protected String getIndicesOfStmt(final Collection elements) {
        final StringBuffer stmt = new StringBuffer("SELECT ");
        for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
        }
        stmt.append(" FROM ");
        stmt.append(this.containerTable.toString());
        stmt.append(" WHERE ");
        final Iterator iter = elements.iterator();
        boolean first_element = true;
        while (iter.hasNext()) {
            final Object element = iter.next();
            if (!first_element) {
                stmt.append(" OR (");
            }
            else {
                stmt.append("(");
            }
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
            BackingStoreHelper.appendWhereClauseForElement(stmt, this.elementMapping, element, this.isElementsAreSerialised(), null, false);
            if (this.relationDiscriminatorMapping != null) {
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
            }
            stmt.append(")");
            first_element = false;
        }
        stmt.append(" ORDER BY ");
        for (int j = 0; j < this.orderMapping.getNumberOfDatastoreMappings(); ++j) {
            if (j > 0) {
                stmt.append(",");
            }
            stmt.append(this.orderMapping.getDatastoreMapping(j).getColumn().getIdentifier().toString());
            stmt.append(" DESC");
        }
        return stmt.toString();
    }
    
    protected String getRemoveAtStmt() {
        if (this.removeAtStmt == null) {
            synchronized (this) {
                final StringBuffer stmt = new StringBuffer("DELETE FROM ");
                stmt.append(this.containerTable.toString());
                stmt.append(" WHERE ");
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
                if (this.orderMapping != null) {
                    BackingStoreHelper.appendWhereClauseForMapping(stmt, this.orderMapping, null, false);
                }
                if (this.relationDiscriminatorMapping != null) {
                    BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
                }
                this.removeAtStmt = stmt.toString();
            }
        }
        return this.removeAtStmt;
    }
    
    protected String getShiftStmt() {
        if (this.shiftStmt == null) {
            synchronized (this) {
                final StringBuffer stmt = new StringBuffer("UPDATE ");
                stmt.append(this.containerTable.toString());
                stmt.append(" SET ");
                for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
                    if (i > 0) {
                        stmt.append(",");
                    }
                    stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                    stmt.append(" = ");
                    stmt.append(((AbstractDatastoreMapping)this.orderMapping.getDatastoreMapping(i)).getUpdateInputParameter());
                    stmt.append(" + ");
                    stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                }
                stmt.append(" WHERE ");
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.orderMapping, null, false);
                if (this.relationDiscriminatorMapping != null) {
                    BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
                }
                this.shiftStmt = stmt.toString();
            }
        }
        return this.shiftStmt;
    }
}
