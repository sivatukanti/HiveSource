// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.StatementGenerator;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import org.datanucleus.store.rdbms.query.StatementParameterMapping;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.sql.UnionStatementGenerator;
import org.datanucleus.store.rdbms.sql.DiscriminatorStatementGenerator;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import java.util.Iterator;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.ArrayTable;
import org.datanucleus.metadata.AbstractMemberMetaData;

public class JoinArrayStore extends AbstractArrayStore
{
    public JoinArrayStore(final AbstractMemberMetaData mmd, final ArrayTable arrayTable, final ClassLoaderResolver clr) {
        super(arrayTable.getStoreManager(), clr);
        this.containerTable = arrayTable;
        this.setOwner(arrayTable.getOwnerMemberMetaData());
        this.ownerMapping = arrayTable.getOwnerMapping();
        this.elementMapping = arrayTable.getElementMapping();
        this.orderMapping = arrayTable.getOrderMapping();
        this.relationDiscriminatorMapping = arrayTable.getRelationDiscriminatorMapping();
        this.relationDiscriminatorValue = arrayTable.getRelationDiscriminatorValue();
        this.elementType = arrayTable.getElementType();
        this.elementsAreEmbedded = arrayTable.isEmbeddedElement();
        this.elementsAreSerialised = arrayTable.isSerialisedElement();
        if (this.elementsAreSerialised) {
            this.elementInfo = null;
        }
        else {
            final Class element_class = clr.classForName(this.elementType);
            if (ClassUtils.isReferenceType(element_class)) {
                final String[] implNames = MetaDataUtils.getInstance().getImplementationNamesForReferenceField(this.ownerMemberMetaData, 4, clr, this.storeMgr.getMetaDataManager());
                this.elementInfo = new ElementInfo[implNames.length];
                for (int i = 0; i < implNames.length; ++i) {
                    final DatastoreClass table = this.storeMgr.getDatastoreClass(implNames[i], clr);
                    final AbstractClassMetaData cmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(implNames[i], clr);
                    this.elementInfo[i] = new ElementInfo(cmd, table);
                }
            }
            else {
                this.emd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(element_class, clr);
                if (this.emd != null) {
                    this.elementType = this.emd.getFullClassName();
                    if (!this.elementsAreEmbedded && !this.elementsAreSerialised) {
                        this.elementInfo = this.getElementInformationForClass();
                        if (this.elementInfo != null && this.elementInfo.length > 1) {
                            throw new NucleusUserException(JoinArrayStore.LOCALISER.msg("056045", this.ownerMemberMetaData.getFullFieldName()));
                        }
                    }
                    else {
                        this.elementInfo = null;
                    }
                }
                else {
                    this.elementInfo = null;
                }
            }
        }
    }
    
    @Override
    public Iterator iterator(final ObjectProvider ownerOP) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        SQLStatement sqlStmt = null;
        final ClassLoaderResolver clr = ownerOP.getExecutionContext().getClassLoaderResolver();
        final SQLExpressionFactory exprFactory = this.storeMgr.getSQLExpressionFactory();
        StatementClassMapping iteratorMappingClass = null;
        if (this.elementsAreEmbedded || this.elementsAreSerialised) {
            sqlStmt = new SQLStatement(this.storeMgr, this.containerTable, null, null);
            sqlStmt.setClassLoaderResolver(clr);
            sqlStmt.select(sqlStmt.getPrimaryTable(), this.elementMapping, null);
        }
        else if (this.elementMapping instanceof ReferenceMapping) {
            sqlStmt = new SQLStatement(this.storeMgr, this.containerTable, null, null);
            sqlStmt.setClassLoaderResolver(clr);
            sqlStmt.select(sqlStmt.getPrimaryTable(), this.elementMapping, null);
        }
        else {
            iteratorMappingClass = new StatementClassMapping();
            for (int i = 0; i < this.elementInfo.length; ++i) {
                final int elementNo = i;
                final Class elementCls = clr.classForName(this.elementInfo[elementNo].getClassName());
                SQLStatement elementStmt = null;
                if (this.elementInfo[elementNo].getDiscriminatorStrategy() != null && this.elementInfo[elementNo].getDiscriminatorStrategy() != DiscriminatorStrategy.NONE) {
                    final String elementType = this.ownerMemberMetaData.getCollection().getElementType();
                    if (ClassUtils.isReferenceType(clr.classForName(elementType))) {
                        final String[] clsNames = this.storeMgr.getNucleusContext().getMetaDataManager().getClassesImplementingInterface(elementType, clr);
                        final Class[] cls = new Class[clsNames.length];
                        for (int j = 0; j < clsNames.length; ++j) {
                            cls[j] = clr.classForName(clsNames[j]);
                        }
                        final StatementGenerator stmtGen = new DiscriminatorStatementGenerator(this.storeMgr, clr, cls, true, (DatastoreIdentifier)null, (String)null, this.containerTable, (DatastoreIdentifier)null, this.elementMapping);
                        if (this.allowNulls) {
                            stmtGen.setOption("allowNulls");
                        }
                        elementStmt = stmtGen.getStatement();
                    }
                    else {
                        final StatementGenerator stmtGen2 = new DiscriminatorStatementGenerator(this.storeMgr, clr, elementCls, true, null, null, this.containerTable, null, this.elementMapping);
                        if (this.allowNulls) {
                            stmtGen2.setOption("allowNulls");
                        }
                        elementStmt = stmtGen2.getStatement();
                    }
                    this.iterateUsingDiscriminator = true;
                }
                else {
                    final StatementGenerator stmtGen3 = new UnionStatementGenerator(this.storeMgr, clr, elementCls, true, null, null, this.containerTable, null, this.elementMapping);
                    stmtGen3.setOption("selectNucleusType");
                    if (this.allowNulls) {
                        stmtGen3.setOption("allowNulls");
                    }
                    iteratorMappingClass.setNucleusTypeColumnName("NUCLEUS_TYPE");
                    elementStmt = stmtGen3.getStatement();
                }
                if (sqlStmt == null) {
                    sqlStmt = elementStmt;
                }
                else {
                    sqlStmt.union(elementStmt);
                }
            }
            final SQLTable elementSqlTbl = sqlStmt.getTable(this.elementInfo[0].getDatastoreClass(), sqlStmt.getPrimaryTable().getGroupName());
            SQLStatementHelper.selectFetchPlanOfSourceClassInStatement(sqlStmt, iteratorMappingClass, ownerOP.getExecutionContext().getFetchPlan(), elementSqlTbl, this.emd, 0);
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
                for (int l = 0; l < paramPositions.length; ++l) {
                    paramPositions[l] = inputParamNum++;
                }
                ownerIdx.addParameterOccurrence(paramPositions);
            }
        }
        else {
            final int[] paramPositions2 = new int[this.ownerMapping.getNumberOfDatastoreMappings()];
            for (int m = 0; m < paramPositions2.length; ++m) {
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
                        if (this.elementsAreEmbedded || this.elementsAreSerialised) {
                            return new ArrayStoreIterator(ownerOP, rs, null, this);
                        }
                        if (this.elementMapping instanceof ReferenceMapping) {
                            return new ArrayStoreIterator(ownerOP, rs, null, this);
                        }
                        final ResultObjectFactory rof = this.storeMgr.newResultObjectFactory(this.emd, iteratorMappingClass, false, null, clr.classForName(this.elementType));
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
            throw new NucleusDataStoreException(JoinArrayStore.LOCALISER.msg("056006", stmt), e);
        }
        catch (MappedDatastoreException e2) {
            throw new NucleusDataStoreException(JoinArrayStore.LOCALISER.msg("056006", stmt), e2);
        }
    }
}
