// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.metadata.DiscriminatorStrategy;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.JDBCUtils;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import org.datanucleus.metadata.CollectionMetaData;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.FieldValues;
import org.datanucleus.ExecutionContext;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractClassMetaData;

public abstract class ElementContainerStore extends BaseContainerStore
{
    protected boolean iterateUsingDiscriminator;
    protected String sizeStmt;
    protected String clearStmt;
    protected String addStmt;
    protected String removeStmt;
    protected boolean usingDiscriminatorInSizeStmt;
    protected ElementInfo[] elementInfo;
    protected AbstractClassMetaData emd;
    protected Table containerTable;
    protected JavaTypeMapping elementMapping;
    protected String elementType;
    protected boolean elementsAreEmbedded;
    protected boolean elementsAreSerialised;
    protected boolean elementIsPersistentInterface;
    protected JavaTypeMapping orderMapping;
    protected JavaTypeMapping relationDiscriminatorMapping;
    protected String relationDiscriminatorValue;
    
    protected ElementContainerStore(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        super(storeMgr, clr);
        this.iterateUsingDiscriminator = false;
        this.usingDiscriminatorInSizeStmt = false;
        this.elementIsPersistentInterface = false;
    }
    
    public ElementInfo[] getElementInfo() {
        return this.elementInfo;
    }
    
    public JavaTypeMapping getElementMapping() {
        return this.elementMapping;
    }
    
    public JavaTypeMapping getOrderMapping() {
        return this.orderMapping;
    }
    
    public JavaTypeMapping getRelationDiscriminatorMapping() {
        return this.relationDiscriminatorMapping;
    }
    
    public String getRelationDiscriminatorValue() {
        return this.relationDiscriminatorValue;
    }
    
    public Table getContainerTable() {
        return this.containerTable;
    }
    
    public AbstractClassMetaData getEmd() {
        return this.emd;
    }
    
    public boolean isElementsAreSerialised() {
        return this.elementsAreSerialised;
    }
    
    public boolean isElementsAreEmbedded() {
        return this.elementsAreEmbedded;
    }
    
    protected ElementInfo[] getElementInformationForClass() {
        ElementInfo[] info = null;
        DatastoreClass tbl;
        String[] clsNames;
        if (!this.clr.classForName(this.elementType).isInterface()) {
            tbl = this.storeMgr.getDatastoreClass(this.elementType, this.clr);
            clsNames = new String[] { this.elementType };
        }
        else {
            clsNames = this.storeMgr.getNucleusContext().getMetaDataManager().getClassesImplementingInterface(this.elementType, this.clr);
            tbl = this.storeMgr.getDatastoreClass(clsNames[0], this.clr);
        }
        if (tbl == null) {
            final AbstractClassMetaData[] subclassCmds = this.storeMgr.getClassesManagingTableForClass(this.emd, this.clr);
            info = new ElementInfo[subclassCmds.length];
            for (int i = 0; i < subclassCmds.length; ++i) {
                final DatastoreClass table = this.storeMgr.getDatastoreClass(subclassCmds[i].getFullClassName(), this.clr);
                info[i] = new ElementInfo(subclassCmds[i], table);
            }
        }
        else {
            info = new ElementInfo[clsNames.length];
            for (int j = 0; j < clsNames.length; ++j) {
                final AbstractClassMetaData cmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(clsNames[j], this.clr);
                final DatastoreClass table = this.storeMgr.getDatastoreClass(cmd.getFullClassName(), this.clr);
                info[j] = new ElementInfo(cmd, table);
            }
        }
        return info;
    }
    
    public boolean hasOrderMapping() {
        return this.orderMapping != null;
    }
    
    protected boolean validateElementType(final ClassLoaderResolver clr, final Object element) {
        if (element == null) {
            return true;
        }
        final Class primitiveElementClass = ClassUtils.getPrimitiveTypeForType(element.getClass());
        if (primitiveElementClass != null) {
            String elementTypeWrapper = this.elementType;
            final Class elementTypeClass = clr.classForName(this.elementType);
            if (elementTypeClass.isPrimitive()) {
                elementTypeWrapper = ClassUtils.getWrapperTypeForPrimitiveType(elementTypeClass).getName();
            }
            return clr.isAssignableFrom(elementTypeWrapper, element.getClass());
        }
        return clr.isAssignableFrom(this.elementType, element.getClass());
    }
    
    protected boolean validateElementForReading(final ObjectProvider op, final Object element) {
        if (!this.validateElementType(op.getExecutionContext().getClassLoaderResolver(), element)) {
            return false;
        }
        if (element != null && !this.elementsAreEmbedded && !this.elementsAreSerialised) {
            final ExecutionContext ec = op.getExecutionContext();
            if ((!ec.getApiAdapter().isPersistent(element) || ec != ec.getApiAdapter().getExecutionContext(element)) && !ec.getApiAdapter().isDetached(element)) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean validateElementForWriting(final ExecutionContext ec, final Object element, final FieldValues fieldValues) {
        if (!this.elementIsPersistentInterface && !this.validateElementType(ec.getClassLoaderResolver(), element)) {
            throw new ClassCastException(ElementContainerStore.LOCALISER.msg("056033", element.getClass().getName(), this.ownerMemberMetaData.getFullFieldName(), this.elementType));
        }
        boolean persisted = false;
        if (!this.elementsAreEmbedded) {
            if (!this.elementsAreSerialised) {
                final ObjectProvider elementSM = ec.findObjectProvider(element);
                if (elementSM != null && elementSM.isEmbedded()) {
                    throw new NucleusUserException(ElementContainerStore.LOCALISER.msg("056028", this.ownerMemberMetaData.getFullFieldName(), element));
                }
                persisted = SCOUtils.validateObjectForWriting(ec, element, fieldValues);
            }
        }
        return persisted;
    }
    
    public abstract Iterator iterator(final ObjectProvider p0);
    
    public void clear(final ObjectProvider ownerOP) {
        Collection dependentElements = null;
        final CollectionMetaData collmd = this.ownerMemberMetaData.getCollection();
        boolean dependent = collmd.isDependentElement();
        if (this.ownerMemberMetaData.isCascadeRemoveOrphans()) {
            dependent = true;
        }
        if (dependent && !collmd.isEmbeddedElement() && !collmd.isSerializedElement()) {
            dependentElements = new HashSet();
            final Iterator iter = this.iterator(ownerOP);
            while (iter.hasNext()) {
                dependentElements.add(iter.next());
            }
        }
        this.executeClear(ownerOP);
        if (dependentElements != null && dependentElements.size() > 0) {
            for (final Object obj : dependentElements) {
                if (ownerOP.getExecutionContext().getApiAdapter().isDeleted(obj)) {
                    continue;
                }
                ownerOP.getExecutionContext().deleteObjectInternal(obj);
            }
        }
    }
    
    public int size(final ObjectProvider op) {
        return this.getSize(op);
    }
    
    protected void invalidateAddStmt() {
        this.addStmt = null;
    }
    
    protected String getAddStmt() {
        if (this.addStmt == null) {
            synchronized (this) {
                final StringBuffer stmt = new StringBuffer("INSERT INTO ");
                stmt.append(this.getContainerTable().toString());
                stmt.append(" (");
                for (int i = 0; i < this.getOwnerMapping().getNumberOfDatastoreMappings(); ++i) {
                    if (i > 0) {
                        stmt.append(",");
                    }
                    stmt.append(this.getOwnerMapping().getDatastoreMapping(i).getColumn().getIdentifier().toString());
                }
                for (int i = 0; i < this.getElementMapping().getNumberOfDatastoreMappings(); ++i) {
                    stmt.append(",");
                    stmt.append(this.getElementMapping().getDatastoreMapping(i).getColumn().getIdentifier().toString());
                }
                if (this.getOrderMapping() != null) {
                    for (int i = 0; i < this.getOrderMapping().getNumberOfDatastoreMappings(); ++i) {
                        stmt.append(",");
                        stmt.append(this.getOrderMapping().getDatastoreMapping(i).getColumn().getIdentifier().toString());
                    }
                }
                if (this.getRelationDiscriminatorMapping() != null) {
                    for (int i = 0; i < this.getRelationDiscriminatorMapping().getNumberOfDatastoreMappings(); ++i) {
                        stmt.append(",");
                        stmt.append(this.getRelationDiscriminatorMapping().getDatastoreMapping(i).getColumn().getIdentifier().toString());
                    }
                }
                stmt.append(") VALUES (");
                for (int i = 0; i < this.getOwnerMapping().getNumberOfDatastoreMappings(); ++i) {
                    if (i > 0) {
                        stmt.append(",");
                    }
                    stmt.append(((AbstractDatastoreMapping)this.getOwnerMapping().getDatastoreMapping(i)).getInsertionInputParameter());
                }
                for (int i = 0; i < this.getElementMapping().getNumberOfDatastoreMappings(); ++i) {
                    stmt.append(",");
                    stmt.append(((AbstractDatastoreMapping)this.getElementMapping().getDatastoreMapping(0)).getInsertionInputParameter());
                }
                if (this.getOrderMapping() != null) {
                    for (int i = 0; i < this.getOrderMapping().getNumberOfDatastoreMappings(); ++i) {
                        stmt.append(",");
                        stmt.append(((AbstractDatastoreMapping)this.getOrderMapping().getDatastoreMapping(0)).getInsertionInputParameter());
                    }
                }
                if (this.getRelationDiscriminatorMapping() != null) {
                    for (int i = 0; i < this.getRelationDiscriminatorMapping().getNumberOfDatastoreMappings(); ++i) {
                        stmt.append(",");
                        stmt.append(((AbstractDatastoreMapping)this.getRelationDiscriminatorMapping().getDatastoreMapping(0)).getInsertionInputParameter());
                    }
                }
                stmt.append(") ");
                this.addStmt = stmt.toString();
            }
        }
        return this.addStmt;
    }
    
    public void executeClear(final ObjectProvider ownerOP) {
        final String clearStmt = this.getClearStmt();
        try {
            final ExecutionContext ec = ownerOP.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
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
            throw new NucleusDataStoreException(ElementContainerStore.LOCALISER.msg("056013", clearStmt), e);
        }
    }
    
    protected String getClearStmt() {
        if (this.clearStmt == null) {
            synchronized (this) {
                final StringBuffer stmt = new StringBuffer("DELETE FROM ");
                stmt.append(this.getContainerTable().toString());
                stmt.append(" WHERE ");
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
                if (this.getRelationDiscriminatorMapping() != null) {
                    BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
                }
                this.clearStmt = stmt.toString();
            }
        }
        return this.clearStmt;
    }
    
    public int getSize(final ObjectProvider ownerOP) {
        final String sizeStmt = this.getSizeStmt();
        int numRows;
        try {
            final ExecutionContext ec = ownerOP.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, sizeStmt);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(ownerOP, ec, ps, jdbcPosition, this);
                    if (this.getElementInfo() != null && this.getElementInfo().length == 1) {
                        for (int i = 0; i < this.getElementInfo().length; ++i) {
                            if (this.getElementInfo()[i].getDiscriminatorMapping() != null) {
                                jdbcPosition = BackingStoreHelper.populateElementDiscriminatorInStatement(ec, ps, jdbcPosition, true, this.getElementInfo()[i], this.clr);
                            }
                        }
                    }
                    if (this.getRelationDiscriminatorMapping() != null) {
                        jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, sizeStmt, ps);
                    try {
                        if (!rs.next()) {
                            throw new NucleusDataStoreException(ElementContainerStore.LOCALISER.msg("056007", sizeStmt));
                        }
                        numRows = rs.getInt(1);
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
            throw new NucleusDataStoreException(ElementContainerStore.LOCALISER.msg("056007", sizeStmt), e);
        }
        return numRows;
    }
    
    protected String getSizeStmt() {
        if (this.sizeStmt != null && !this.usingDiscriminatorInSizeStmt) {
            return this.sizeStmt;
        }
        synchronized (this) {
            final String containerAlias = "THIS";
            final String joinedElementAlias = "ELEM";
            final StringBuffer stmt = new StringBuffer("SELECT COUNT(*) FROM ");
            stmt.append(this.getContainerTable().toString()).append(" ").append(containerAlias);
            boolean joinedDiscrim = false;
            if (this.getElementInfo() != null && this.getElementInfo().length == 1 && this.getElementInfo()[0].getDatastoreClass() != this.getContainerTable() && this.getElementInfo()[0].getDiscriminatorMapping() != null) {
                joinedDiscrim = true;
                final JavaTypeMapping elemIdMapping = this.getElementInfo()[0].getDatastoreClass().getIdMapping();
                if (this.allowNulls) {
                    stmt.append(" LEFT OUTER JOIN ");
                }
                else {
                    stmt.append(" INNER JOIN ");
                }
                stmt.append(this.getElementInfo()[0].getDatastoreClass().toString()).append(" ").append(joinedElementAlias).append(" ON ");
                for (int i = 0; i < this.getElementMapping().getNumberOfDatastoreMappings(); ++i) {
                    if (i > 0) {
                        stmt.append(" AND ");
                    }
                    stmt.append(containerAlias).append(".").append(this.getElementMapping().getDatastoreMapping(i).getColumn().getIdentifier());
                    stmt.append("=");
                    stmt.append(joinedElementAlias).append(".").append(elemIdMapping.getDatastoreMapping(i).getColumn().getIdentifier());
                }
            }
            stmt.append(" WHERE ");
            BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, containerAlias, true);
            if (this.orderMapping != null) {
                for (int j = 0; j < this.orderMapping.getNumberOfDatastoreMappings(); ++j) {
                    stmt.append(" AND ");
                    stmt.append(containerAlias).append(".").append(this.orderMapping.getDatastoreMapping(j).getColumn().getIdentifier().toString());
                    stmt.append(">=0");
                }
            }
            if (this.getElementInfo() != null && this.getElementInfo().length == 1) {
                final StringBuffer discrStmt = new StringBuffer();
                for (int i = 0; i < this.getElementInfo().length; ++i) {
                    if (this.getElementInfo()[i].getDiscriminatorMapping() != null) {
                        this.usingDiscriminatorInSizeStmt = true;
                        if (discrStmt.length() > 0) {
                            discrStmt.append(" OR ");
                        }
                        final JavaTypeMapping discrimMapping = this.getElementInfo()[i].getDiscriminatorMapping();
                        for (int k = 0; k < discrimMapping.getNumberOfDatastoreMappings(); ++k) {
                            if (joinedDiscrim) {
                                discrStmt.append(joinedElementAlias);
                            }
                            else {
                                discrStmt.append(containerAlias);
                            }
                            discrStmt.append(".");
                            discrStmt.append(discrimMapping.getDatastoreMapping(k).getColumn().getIdentifier().toString());
                            discrStmt.append("=");
                            discrStmt.append(((AbstractDatastoreMapping)discrimMapping.getDatastoreMapping(k)).getUpdateInputParameter());
                        }
                        final Collection<String> subclasses = this.storeMgr.getSubClassesForClass(this.getElementInfo()[i].getClassName(), true, this.clr);
                        if (subclasses != null && subclasses.size() > 0) {
                            for (int l = 0; l < subclasses.size(); ++l) {
                                for (int m = 0; m < discrimMapping.getNumberOfDatastoreMappings(); ++m) {
                                    discrStmt.append(" OR ");
                                    if (joinedDiscrim) {
                                        discrStmt.append(joinedElementAlias);
                                    }
                                    else {
                                        discrStmt.append(containerAlias);
                                    }
                                    discrStmt.append(".");
                                    discrStmt.append(discrimMapping.getDatastoreMapping(m).getColumn().getIdentifier().toString());
                                    discrStmt.append("=");
                                    discrStmt.append(((AbstractDatastoreMapping)discrimMapping.getDatastoreMapping(m)).getUpdateInputParameter());
                                }
                            }
                        }
                    }
                }
                if (discrStmt.length() > 0) {
                    stmt.append(" AND (");
                    stmt.append(discrStmt);
                    if (this.allowNulls) {
                        stmt.append(" OR ");
                        stmt.append(this.getElementInfo()[0].getDiscriminatorMapping().getDatastoreMapping(0).getColumn().getIdentifier().toString());
                        stmt.append(" IS NULL");
                    }
                    stmt.append(")");
                }
            }
            if (this.relationDiscriminatorMapping != null) {
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, containerAlias, false);
            }
            return this.sizeStmt = stmt.toString();
        }
    }
    
    public static class ElementInfo
    {
        AbstractClassMetaData cmd;
        DatastoreClass table;
        
        public ElementInfo(final AbstractClassMetaData cmd, final DatastoreClass table) {
            this.cmd = cmd;
            this.table = table;
        }
        
        public String getClassName() {
            return this.cmd.getFullClassName();
        }
        
        public AbstractClassMetaData getAbstractClassMetaData() {
            return this.cmd;
        }
        
        public DatastoreClass getDatastoreClass() {
            return this.table;
        }
        
        public DiscriminatorStrategy getDiscriminatorStrategy() {
            return this.cmd.getDiscriminatorStrategyForTable();
        }
        
        public JavaTypeMapping getDiscriminatorMapping() {
            return this.table.getDiscriminatorMapping(false);
        }
        
        @Override
        public String toString() {
            return "ElementInfo : [class=" + this.cmd.getFullClassName() + " table=" + this.table + "]";
        }
    }
}
