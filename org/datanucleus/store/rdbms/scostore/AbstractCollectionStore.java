// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.JDBCUtils;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.ExecutionContext;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import java.util.Collection;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedElementPCMapping;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.scostore.CollectionStore;

public abstract class AbstractCollectionStore extends ElementContainerStore implements CollectionStore
{
    protected String containsStmt;
    
    protected AbstractCollectionStore(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        super(storeMgr, clr);
    }
    
    @Override
    public boolean updateEmbeddedElement(final ObjectProvider op, final Object element, final int fieldNumber, final Object value) {
        boolean modified = false;
        if (this.elementMapping != null && this.elementMapping instanceof EmbeddedElementPCMapping) {
            final String fieldName = this.emd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber).getName();
            if (fieldName == null) {
                return false;
            }
            final JavaTypeMapping fieldMapping = ((EmbeddedElementPCMapping)this.elementMapping).getJavaTypeMapping(fieldName);
            if (fieldMapping == null) {
                return false;
            }
            modified = this.updateEmbeddedElement(op, element, fieldNumber, value, fieldMapping);
        }
        return modified;
    }
    
    @Override
    public void update(final ObjectProvider op, final Collection coll) {
        this.clear(op);
        this.addAll(op, coll, 0);
    }
    
    @Override
    public boolean contains(final ObjectProvider op, final Object element) {
        return this.validateElementForReading(op, element) && this.containsInternal(op, element);
    }
    
    protected String getUpdateEmbeddedElementStmt(final JavaTypeMapping fieldMapping) {
        final JavaTypeMapping ownerMapping = this.getOwnerMapping();
        final Table containerTable = this.getContainerTable();
        final JavaTypeMapping elementMapping = this.getElementMapping();
        final StringBuffer stmt = new StringBuffer("UPDATE ");
        stmt.append(containerTable.toString());
        stmt.append(" SET ");
        for (int i = 0; i < fieldMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(fieldMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
            stmt.append(" = ");
            stmt.append(((AbstractDatastoreMapping)fieldMapping.getDatastoreMapping(i)).getUpdateInputParameter());
        }
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, ownerMapping, null, true);
        final EmbeddedElementPCMapping embeddedMapping = (EmbeddedElementPCMapping)elementMapping;
        for (int j = 0; j < embeddedMapping.getNumberOfJavaTypeMappings(); ++j) {
            final JavaTypeMapping m = embeddedMapping.getJavaTypeMapping(j);
            if (m != null) {
                for (int k = 0; k < m.getNumberOfDatastoreMappings(); ++k) {
                    stmt.append(" AND ");
                    stmt.append(m.getDatastoreMapping(k).getColumn().getIdentifier().toString());
                    stmt.append(" = ");
                    stmt.append(((AbstractDatastoreMapping)m.getDatastoreMapping(k)).getUpdateInputParameter());
                }
            }
        }
        return stmt.toString();
    }
    
    public boolean updateEmbeddedElement(final ObjectProvider op, final Object element, final int fieldNumber, final Object value, final JavaTypeMapping fieldMapping) {
        boolean modified = false;
        final String stmt = this.getUpdateEmbeddedElementStmt(fieldMapping);
        try {
            final ExecutionContext ec = op.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, stmt, false);
                try {
                    int jdbcPosition = 1;
                    fieldMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, fieldMapping), value);
                    jdbcPosition += fieldMapping.getNumberOfDatastoreMappings();
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    jdbcPosition = BackingStoreHelper.populateEmbeddedElementFieldsInStatement(op, element, ps, jdbcPosition, ((JoinTable)this.getContainerTable()).getOwnerMemberMetaData(), this.getElementMapping(), this.getEmd(), this);
                    sqlControl.executeStatementUpdate(ec, mconn, stmt, ps, true);
                    modified = true;
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
            e.printStackTrace();
            throw new NucleusDataStoreException(AbstractCollectionStore.LOCALISER.msg("056009", stmt), e);
        }
        return modified;
    }
    
    private String getContainsStmt(final Object element) {
        if (this.elementMapping instanceof ReferenceMapping && this.elementMapping.getNumberOfDatastoreMappings() > 1) {
            return this.getContainsStatementString(element);
        }
        if (this.containsStmt == null) {
            synchronized (this) {
                this.containsStmt = this.getContainsStatementString(element);
            }
        }
        return this.containsStmt;
    }
    
    private String getContainsStatementString(final Object element) {
        final JavaTypeMapping ownerMapping = this.getOwnerMapping();
        final Table containerTable = this.getContainerTable();
        final boolean elementsAreSerialised = this.isElementsAreSerialised();
        final JavaTypeMapping elementMapping = this.getElementMapping();
        final ElementInfo[] elementInfo = this.getElementInfo();
        final StringBuffer stmt = new StringBuffer("SELECT ");
        final String containerAlias = "THIS";
        final String joinedElementAlias = "ELEM";
        for (int i = 0; i < ownerMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(ownerMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
        }
        stmt.append(" FROM ");
        stmt.append(this.getContainerTable().toString()).append(" ").append(containerAlias);
        final boolean joinedDiscrim = false;
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, ownerMapping, containerAlias, true);
        BackingStoreHelper.appendWhereClauseForElement(stmt, elementMapping, element, elementsAreSerialised, containerAlias, false);
        if (elementInfo != null && containerTable == elementInfo[0].getDatastoreClass() && elementInfo[0].getDiscriminatorMapping() != null) {
            stmt.append(" AND (");
            final Collection<String> subclasses = this.storeMgr.getSubClassesForClass(elementInfo[0].getClassName(), true, this.clr);
            for (int j = 0; j < subclasses.size() + 1; ++j) {
                final JavaTypeMapping discrimMapping = elementInfo[0].getDiscriminatorMapping();
                for (int k = 0; k < discrimMapping.getNumberOfDatastoreMappings(); ++k) {
                    if (joinedDiscrim) {
                        stmt.append(joinedElementAlias);
                    }
                    else {
                        stmt.append(containerAlias);
                    }
                    stmt.append(".").append(discrimMapping.getDatastoreMapping(k).getColumn().getIdentifier().toString());
                    stmt.append(" = ");
                    stmt.append(((AbstractDatastoreMapping)discrimMapping.getDatastoreMapping(k)).getUpdateInputParameter());
                    if (k != discrimMapping.getNumberOfDatastoreMappings() - 1 || j != subclasses.size()) {
                        stmt.append(" OR ");
                    }
                }
            }
            stmt.append(")");
        }
        if (this.relationDiscriminatorMapping != null) {
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, containerAlias, false);
        }
        return stmt.toString();
    }
    
    protected boolean containsInternal(final ObjectProvider op, final Object element) {
        final Table containerTable = this.getContainerTable();
        final JavaTypeMapping elementMapping = this.getElementMapping();
        final ElementInfo[] elementInfo = this.getElementInfo();
        final String stmt = this.getContainsStmt(element);
        boolean retval;
        try {
            final ExecutionContext ec = op.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, stmt);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    jdbcPosition = BackingStoreHelper.populateElementForWhereClauseInStatement(ec, ps, element, jdbcPosition, elementMapping);
                    if (elementInfo != null && elementInfo[0].getDiscriminatorMapping() != null && elementInfo[0].getDatastoreClass() == containerTable) {
                        jdbcPosition = BackingStoreHelper.populateElementDiscriminatorInStatement(ec, ps, jdbcPosition, true, elementInfo[0], this.clr);
                    }
                    if (this.relationDiscriminatorMapping != null) {
                        jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        retval = rs.next();
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
            throw new NucleusDataStoreException(AbstractCollectionStore.LOCALISER.msg("056008", stmt), e);
        }
        return retval;
    }
    
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
        catch (SQLException sqle) {
            throw new MappedDatastoreException("SQLException", sqle);
        }
    }
    
    protected String getRemoveStmt(final Object element) {
        if (this.elementMapping instanceof ReferenceMapping && this.elementMapping.getNumberOfDatastoreMappings() > 1) {
            return this.getRemoveStatementString(element);
        }
        if (this.removeStmt == null) {
            synchronized (this) {
                this.removeStmt = this.getRemoveStatementString(element);
            }
        }
        return this.removeStmt;
    }
    
    private String getRemoveStatementString(final Object element) {
        final StringBuffer stmt = new StringBuffer();
        stmt.append("DELETE FROM ");
        stmt.append(this.containerTable.toString());
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, this.containerTable.toString(), true);
        BackingStoreHelper.appendWhereClauseForElement(stmt, this.elementMapping, element, this.elementsAreSerialised, this.containerTable.toString(), false);
        if (this.relationDiscriminatorMapping != null) {
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, this.containerTable.toString(), false);
        }
        return stmt.toString();
    }
}
