// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import org.datanucleus.FetchPlan;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.query.StatementParameterMapping;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.UnionStatementGenerator;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.DiscriminatorStatementGenerator;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import java.util.Iterator;
import org.datanucleus.store.FieldValues;
import java.lang.reflect.Array;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.ExecutionContext;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.ArrayMetaData;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;

public class FKArrayStore extends AbstractArrayStore
{
    private String clearNullifyStmt;
    private String updateFkStmt;
    
    public FKArrayStore(final AbstractMemberMetaData mmd, final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        super(storeMgr, clr);
        this.setOwner(mmd);
        final ArrayMetaData arrmd = mmd.getArray();
        if (arrmd == null) {
            throw new NucleusUserException(FKArrayStore.LOCALISER.msg("056000", mmd.getFullFieldName()));
        }
        this.elementType = mmd.getType().getComponentType().getName();
        final Class element_class = clr.classForName(this.elementType);
        if (ClassUtils.isReferenceType(element_class)) {
            this.emd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForImplementationOfReference(element_class, null, clr);
            if (this.emd != null) {
                this.elementType = this.emd.getFullClassName();
            }
        }
        else {
            this.emd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(element_class, clr);
        }
        if (this.emd == null) {
            throw new NucleusUserException(FKArrayStore.LOCALISER.msg("056003", element_class.getName(), mmd.getFullFieldName()));
        }
        this.elementInfo = this.getElementInformationForClass();
        if (this.elementInfo != null && this.elementInfo.length > 1) {
            throw new NucleusUserException(FKArrayStore.LOCALISER.msg("056045", this.ownerMemberMetaData.getFullFieldName()));
        }
        this.elementMapping = this.elementInfo[0].getDatastoreClass().getIdMapping();
        this.elementsAreEmbedded = false;
        this.elementsAreSerialised = false;
        final String mappedByFieldName = mmd.getMappedBy();
        if (mappedByFieldName != null) {
            final AbstractMemberMetaData eofmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForMember(element_class, clr, mappedByFieldName);
            if (eofmd == null) {
                throw new NucleusUserException(FKArrayStore.LOCALISER.msg("056024", mmd.getFullFieldName(), mappedByFieldName, element_class.getName()));
            }
            if (!clr.isAssignableFrom(eofmd.getType(), mmd.getAbstractClassMetaData().getFullClassName())) {
                throw new NucleusUserException(FKArrayStore.LOCALISER.msg("056025", mmd.getFullFieldName(), eofmd.getFullFieldName(), eofmd.getTypeName(), mmd.getAbstractClassMetaData().getFullClassName()));
            }
            final String ownerFieldName = eofmd.getName();
            this.ownerMapping = this.elementInfo[0].getDatastoreClass().getMemberMapping(eofmd);
            if (this.ownerMapping == null) {
                throw new NucleusUserException(FKArrayStore.LOCALISER.msg("056046", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.elementType, ownerFieldName));
            }
            if (this.isEmbeddedMapping(this.ownerMapping)) {
                throw new NucleusUserException(FKArrayStore.LOCALISER.msg("056026", ownerFieldName, this.elementType, eofmd.getTypeName(), mmd.getClassName()));
            }
        }
        else {
            this.ownerMapping = this.elementInfo[0].getDatastoreClass().getExternalMapping(mmd, 5);
            if (this.ownerMapping == null) {
                throw new NucleusUserException(FKArrayStore.LOCALISER.msg("056047", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.elementType));
            }
        }
        this.orderMapping = this.elementInfo[0].getDatastoreClass().getExternalMapping(mmd, 4);
        if (this.orderMapping == null) {
            throw new NucleusUserException(FKArrayStore.LOCALISER.msg("056048", mmd.getAbstractClassMetaData().getFullClassName(), mmd.getName(), this.elementType));
        }
        this.relationDiscriminatorMapping = this.elementInfo[0].getDatastoreClass().getExternalMapping(mmd, 6);
        if (this.relationDiscriminatorMapping != null) {
            this.relationDiscriminatorValue = mmd.getValueForExtension("relation-discriminator-value");
            if (this.relationDiscriminatorValue == null) {
                this.relationDiscriminatorValue = mmd.getFullFieldName();
            }
        }
        this.containerTable = this.elementInfo[0].getDatastoreClass();
        if (mmd.getMappedBy() != null && this.ownerMapping.getTable() != this.containerTable) {
            this.containerTable = this.ownerMapping.getTable();
        }
    }
    
    private boolean updateElementFk(final ObjectProvider ownerOP, final Object element, final Object owner, final int index) {
        if (element == null) {
            return false;
        }
        final String updateFkStmt = this.getUpdateFkStmt();
        final ExecutionContext ec = ownerOP.getExecutionContext();
        boolean retval;
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, updateFkStmt, false);
                try {
                    int jdbcPosition = 1;
                    if (this.elementInfo.length > 1) {
                        final DatastoreClass table = this.storeMgr.getDatastoreClass(element.getClass().getName(), this.clr);
                        if (table != null) {
                            ps.setString(jdbcPosition++, table.toString());
                        }
                        else {
                            NucleusLogger.PERSISTENCE.info(">> FKArrayStore.updateElementFK : need to set table in statement but dont know table where to store " + element);
                        }
                    }
                    if (owner == null) {
                        this.ownerMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, this.ownerMapping), null);
                        jdbcPosition += this.ownerMapping.getNumberOfDatastoreMappings();
                    }
                    else {
                        jdbcPosition = BackingStoreHelper.populateOwnerInStatement(ownerOP, ec, ps, jdbcPosition, this);
                    }
                    jdbcPosition = BackingStoreHelper.populateOrderInStatement(ec, ps, index, jdbcPosition, this.orderMapping);
                    if (this.relationDiscriminatorMapping != null) {
                        jdbcPosition = BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                    }
                    jdbcPosition = BackingStoreHelper.populateElementInStatement(ec, ps, element, jdbcPosition, this.elementMapping);
                    sqlControl.executeStatementUpdate(ec, mconn, updateFkStmt, ps, true);
                    retval = true;
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
            throw new NucleusDataStoreException(FKArrayStore.LOCALISER.msg("056027", updateFkStmt), e);
        }
        return retval;
    }
    
    private String getUpdateFkStmt() {
        if (this.updateFkStmt == null) {
            synchronized (this) {
                final StringBuffer stmt = new StringBuffer("UPDATE ");
                if (this.elementInfo.length > 1) {
                    stmt.append("?");
                }
                else {
                    stmt.append(this.elementInfo[0].getDatastoreClass().toString());
                }
                stmt.append(" SET ");
                for (int i = 0; i < this.ownerMapping.getNumberOfDatastoreMappings(); ++i) {
                    if (i > 0) {
                        stmt.append(",");
                    }
                    stmt.append(this.ownerMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                    stmt.append(" = ");
                    stmt.append(((AbstractDatastoreMapping)this.ownerMapping.getDatastoreMapping(i)).getUpdateInputParameter());
                }
                for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
                    stmt.append(",");
                    stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                    stmt.append(" = ");
                    stmt.append(((AbstractDatastoreMapping)this.orderMapping.getDatastoreMapping(i)).getUpdateInputParameter());
                }
                if (this.relationDiscriminatorMapping != null) {
                    for (int i = 0; i < this.relationDiscriminatorMapping.getNumberOfDatastoreMappings(); ++i) {
                        stmt.append(",");
                        stmt.append(this.relationDiscriminatorMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                        stmt.append(" = ");
                        stmt.append(((AbstractDatastoreMapping)this.relationDiscriminatorMapping.getDatastoreMapping(i)).getUpdateInputParameter());
                    }
                }
                stmt.append(" WHERE ");
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.elementMapping, null, true);
                this.updateFkStmt = stmt.toString();
            }
        }
        return this.updateFkStmt;
    }
    
    @Override
    public void clear(final ObjectProvider ownerOP) {
        boolean deleteElements = false;
        if (this.ownerMemberMetaData.getArray().isDependentElement()) {
            NucleusLogger.DATASTORE.debug(FKArrayStore.LOCALISER.msg("056034"));
            deleteElements = true;
        }
        else if (this.ownerMapping.isNullable() && this.orderMapping.isNullable()) {
            NucleusLogger.DATASTORE.debug(FKArrayStore.LOCALISER.msg("056036"));
            deleteElements = false;
        }
        else {
            NucleusLogger.DATASTORE.debug(FKArrayStore.LOCALISER.msg("056035"));
            deleteElements = true;
        }
        if (deleteElements) {
            ownerOP.isLoaded(this.ownerMemberMetaData.getAbsoluteFieldNumber());
            final Object[] value = (Object[])ownerOP.provideField(this.ownerMemberMetaData.getAbsoluteFieldNumber());
            if (value != null && value.length > 0) {
                ownerOP.getExecutionContext().deleteObjects(value);
            }
        }
        else {
            final String clearNullifyStmt = this.getClearNullifyStmt();
            try {
                final ExecutionContext ec = ownerOP.getExecutionContext();
                final ManagedConnection mconn = this.storeMgr.getConnection(ec);
                final SQLController sqlControl = this.storeMgr.getSQLController();
                try {
                    final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, clearNullifyStmt, false);
                    try {
                        int jdbcPosition = 1;
                        jdbcPosition = BackingStoreHelper.populateOwnerInStatement(ownerOP, ec, ps, jdbcPosition, this);
                        if (this.relationDiscriminatorMapping != null) {
                            BackingStoreHelper.populateRelationDiscriminatorInStatement(ec, ps, jdbcPosition, this);
                        }
                        sqlControl.executeStatementUpdate(ec, mconn, clearNullifyStmt, ps, true);
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
                throw new NucleusDataStoreException(FKArrayStore.LOCALISER.msg("056013", clearNullifyStmt), e);
            }
        }
    }
    
    protected String getClearNullifyStmt() {
        if (this.clearNullifyStmt == null) {
            synchronized (this) {
                final StringBuffer stmt = new StringBuffer("UPDATE ");
                if (this.elementInfo.length > 1) {
                    stmt.append("?");
                }
                else {
                    stmt.append(this.elementInfo[0].getDatastoreClass().toString());
                }
                stmt.append(" SET ");
                for (int i = 0; i < this.ownerMapping.getNumberOfDatastoreMappings(); ++i) {
                    if (i > 0) {
                        stmt.append(", ");
                    }
                    stmt.append(this.ownerMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString() + " = NULL");
                }
                for (int i = 0; i < this.orderMapping.getNumberOfDatastoreMappings(); ++i) {
                    stmt.append(", ");
                    stmt.append(this.orderMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString() + " = NULL");
                }
                if (this.relationDiscriminatorMapping != null) {
                    for (int i = 0; i < this.relationDiscriminatorMapping.getNumberOfDatastoreMappings(); ++i) {
                        stmt.append(", ");
                        stmt.append(this.relationDiscriminatorMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString() + " = NULL");
                    }
                }
                stmt.append(" WHERE ");
                BackingStoreHelper.appendWhereClauseForMapping(stmt, this.ownerMapping, null, true);
                if (this.relationDiscriminatorMapping != null) {
                    BackingStoreHelper.appendWhereClauseForMapping(stmt, this.relationDiscriminatorMapping, null, false);
                }
                this.clearNullifyStmt = stmt.toString();
            }
        }
        return this.clearNullifyStmt;
    }
    
    @Override
    public boolean set(final ObjectProvider ownerOP, final Object array) {
        if (array == null) {
            return true;
        }
        for (int i = 0; i < Array.getLength(array); ++i) {
            this.validateElementForWriting(ownerOP.getExecutionContext(), Array.get(array, i), null);
        }
        for (int length = Array.getLength(array), j = 0; j < length; ++j) {
            final Object obj = Array.get(array, j);
            this.updateElementFk(ownerOP, obj, ownerOP.getObject(), j);
        }
        return true;
    }
    
    @Override
    public Iterator iterator(final ObjectProvider ownerOP) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        if (this.elementInfo == null || this.elementInfo.length == 0) {
            return null;
        }
        SQLStatement sqlStmt = null;
        final SQLExpressionFactory exprFactory = this.storeMgr.getSQLExpressionFactory();
        final ClassLoaderResolver clr = ownerOP.getExecutionContext().getClassLoaderResolver();
        final StatementClassMapping iteratorMappingDef = new StatementClassMapping();
        if (this.elementInfo[0].getDatastoreClass().getDiscriminatorMetaData() != null && this.elementInfo[0].getDatastoreClass().getDiscriminatorMetaData().getStrategy() != DiscriminatorStrategy.NONE) {
            final String elementType = this.ownerMemberMetaData.getArray().getElementType();
            if (ClassUtils.isReferenceType(clr.classForName(elementType))) {
                final String[] clsNames = this.storeMgr.getNucleusContext().getMetaDataManager().getClassesImplementingInterface(elementType, clr);
                final Class[] cls = new Class[clsNames.length];
                for (int i = 0; i < clsNames.length; ++i) {
                    cls[i] = clr.classForName(clsNames[i]);
                }
                sqlStmt = new DiscriminatorStatementGenerator(this.storeMgr, clr, cls, true, (DatastoreIdentifier)null, (String)null).getStatement();
            }
            else {
                sqlStmt = new DiscriminatorStatementGenerator(this.storeMgr, clr, clr.classForName(this.elementInfo[0].getClassName()), true, null, null).getStatement();
            }
            this.iterateUsingDiscriminator = true;
            SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, iteratorMappingDef, ownerOP.getExecutionContext().getFetchPlan(), sqlStmt.getPrimaryTable(), this.emd, 0);
        }
        else {
            for (int j = 0; j < this.elementInfo.length; ++j) {
                final Class elementCls = clr.classForName(this.elementInfo[j].getClassName());
                final UnionStatementGenerator stmtGen = new UnionStatementGenerator(this.storeMgr, clr, elementCls, true, null, null);
                stmtGen.setOption("selectNucleusType");
                iteratorMappingDef.setNucleusTypeColumnName("NUCLEUS_TYPE");
                final SQLStatement subStmt = stmtGen.getStatement();
                if (sqlStmt == null) {
                    SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(subStmt, iteratorMappingDef, ownerOP.getExecutionContext().getFetchPlan(), subStmt.getPrimaryTable(), this.emd, 0);
                }
                else {
                    SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(subStmt, null, ownerOP.getExecutionContext().getFetchPlan(), subStmt.getPrimaryTable(), this.emd, 0);
                }
                if (sqlStmt == null) {
                    sqlStmt = subStmt;
                }
                else {
                    sqlStmt.union(subStmt);
                }
            }
        }
        final SQLTable ownerSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), this.ownerMapping);
        final SQLExpression ownerExpr = exprFactory.newExpression(sqlStmt, ownerSqlTbl, this.ownerMapping);
        final SQLExpression ownerVal = exprFactory.newLiteralParameter(sqlStmt, this.ownerMapping, null, "OWNER");
        sqlStmt.whereAnd(ownerExpr.eq(ownerVal), true);
        if (this.relationDiscriminatorMapping != null) {
            final SQLTable distSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), this.relationDiscriminatorMapping);
            final SQLExpression distExpr = exprFactory.newExpression(sqlStmt, distSqlTbl, this.relationDiscriminatorMapping);
            final SQLExpression distVal = exprFactory.newLiteral(sqlStmt, this.relationDiscriminatorMapping, this.relationDiscriminatorValue);
            sqlStmt.whereAnd(distExpr.eq(distVal), true);
        }
        if (this.orderMapping != null) {
            final SQLTable orderSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), this.orderMapping);
            final SQLExpression[] orderExprs = new SQLExpression[this.orderMapping.getNumberOfDatastoreMappings()];
            final boolean[] descendingOrder = new boolean[this.orderMapping.getNumberOfDatastoreMappings()];
            orderExprs[0] = exprFactory.newExpression(sqlStmt, orderSqlTbl, this.orderMapping);
            sqlStmt.setOrdering(orderExprs, descendingOrder);
        }
        int inputParamNum = 1;
        final StatementMappingIndex ownerIdx = new StatementMappingIndex(this.ownerMapping);
        if (sqlStmt.getNumberOfUnions() > 0) {
            for (int k = 0; k < sqlStmt.getNumberOfUnions() + 1; ++k) {
                final int[] paramPositions = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
                for (int l = 0; l < this.ownerMapping.getNumberOfDatastoreMappings(); ++l) {
                    paramPositions[l] = inputParamNum++;
                }
                ownerIdx.addParameterOccurrence(paramPositions);
            }
        }
        else {
            final int[] paramPositions2 = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
            for (int m = 0; m < this.ownerMapping.getNumberOfDatastoreMappings(); ++m) {
                paramPositions2[m] = inputParamNum++;
            }
            ownerIdx.addParameterOccurrence(paramPositions2);
        }
        final StatementParameterMapping iteratorMappingParams = new StatementParameterMapping();
        iteratorMappingParams.addMappingForParameter("owner", ownerIdx);
        if (ec.getTransaction().getSerializeRead() != null && ec.getTransaction().getSerializeRead()) {
            sqlStmt.addExtension("lock-for-update", true);
        }
        final String stmt = sqlStmt.getSelectStatement().toSQL();
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, stmt);
                for (int numParams = ownerIdx.getNumberOfParameterOccurrences(), paramInstance = 0; paramInstance < numParams; ++paramInstance) {
                    ownerIdx.getMapping().setObject(ec, ps, ownerIdx.getParameterPositionsForOccurrence(paramInstance), ownerOP.getObject());
                }
                try {
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, stmt, ps);
                    try {
                        ResultObjectFactory rof = null;
                        if (this.elementsAreEmbedded || this.elementsAreSerialised) {
                            throw new NucleusException("Cannot have FK array with non-persistent objects");
                        }
                        rof = this.storeMgr.newResultObjectFactory(this.emd, iteratorMappingDef, false, null, clr.classForName(this.elementType));
                        return new ArrayStoreIterator(ownerOP, rs, rof, this);
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
            throw new NucleusDataStoreException(FKArrayStore.LOCALISER.msg("056006", stmt), e);
        }
        catch (MappedDatastoreException e2) {
            throw new NucleusDataStoreException(FKArrayStore.LOCALISER.msg("056006", stmt), e2);
        }
    }
}
