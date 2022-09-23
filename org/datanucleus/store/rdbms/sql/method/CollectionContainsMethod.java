// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.BooleanSubqueryExpression;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.table.CollectionTable;
import org.datanucleus.store.rdbms.sql.SQLJoin;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import java.util.Iterator;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.sql.expression.UnboundExpression;
import org.datanucleus.query.compiler.CompilationComponent;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.sql.expression.InExpression;
import org.datanucleus.store.rdbms.sql.expression.EnumExpression;
import org.datanucleus.store.rdbms.sql.expression.ByteExpression;
import org.datanucleus.store.rdbms.sql.expression.CharacterExpression;
import org.datanucleus.store.rdbms.sql.expression.TemporalExpression;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLLiteral;
import java.util.Collection;
import org.datanucleus.store.rdbms.sql.expression.CollectionLiteral;
import org.datanucleus.store.rdbms.sql.expression.CollectionExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class CollectionContainsMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() == 0 || args.size() > 1) {
            throw new NucleusException(CollectionContainsMethod.LOCALISER.msg("060016", "contains", "CollectionExpression", 1));
        }
        final CollectionExpression collExpr = (CollectionExpression)expr;
        final AbstractMemberMetaData mmd = collExpr.getJavaTypeMapping().getMemberMetaData();
        final SQLExpression elemExpr = args.get(0);
        if (elemExpr.isParameter() && mmd != null && mmd.getCollection() != null) {
            final Class elementCls = this.stmt.getQueryGenerator().getClassLoaderResolver().classForName(mmd.getCollection().getElementType());
            this.stmt.getQueryGenerator().bindParameter(elemExpr.getParameterName(), elementCls);
        }
        if (collExpr instanceof CollectionLiteral) {
            final CollectionLiteral lit = (CollectionLiteral)collExpr;
            final Collection coll = (Collection)lit.getValue();
            final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, true);
            if (coll == null || coll.isEmpty()) {
                return this.exprFactory.newLiteral(this.stmt, m, true).eq(this.exprFactory.newLiteral(this.stmt, m, false));
            }
            if (collExpr.isParameter()) {
                this.stmt.getQueryGenerator().useParameterExpressionAsLiteral((SQLLiteral)collExpr);
            }
            boolean useInExpression = false;
            final List<SQLExpression> collElementExprs = lit.getElementExpressions();
            if (collElementExprs != null && !collElementExprs.isEmpty()) {
                boolean incompatible = true;
                final Class elemtype = this.clr.classForName(elemExpr.getJavaTypeMapping().getType());
                for (final SQLExpression collElementExpr : collElementExprs) {
                    final Class collElemType = this.clr.classForName(collElementExpr.getJavaTypeMapping().getType());
                    if (this.elementTypeCompatible(elemtype, collElemType)) {
                        incompatible = false;
                        break;
                    }
                }
                if (incompatible) {
                    return this.exprFactory.newLiteral(this.stmt, m, true).eq(this.exprFactory.newLiteral(this.stmt, m, false));
                }
                SQLExpression collElementExpr = collElementExprs.get(0);
                if (collElementExpr instanceof StringExpression || collElementExpr instanceof NumericExpression || collElementExpr instanceof TemporalExpression || collElementExpr instanceof CharacterExpression || collElementExpr instanceof ByteExpression || collElementExpr instanceof EnumExpression) {
                    useInExpression = true;
                }
            }
            if (useInExpression) {
                final SQLExpression[] exprs = (SQLExpression[])((collElementExprs != null) ? ((SQLExpression[])collElementExprs.toArray(new SQLExpression[collElementExprs.size()])) : null);
                return new InExpression(elemExpr, exprs);
            }
            BooleanExpression bExpr = null;
            for (int i = 0; i < collElementExprs.size(); ++i) {
                if (bExpr == null) {
                    bExpr = collElementExprs.get(i).eq(elemExpr);
                }
                else {
                    bExpr = bExpr.ior(collElementExprs.get(i).eq(elemExpr));
                }
            }
            bExpr.encloseInParentheses();
            return bExpr;
        }
        else {
            if (mmd.isSerialized()) {
                throw new NucleusUserException("Cannot perform Collection.contains when the collection is being serialised");
            }
            final ApiAdapter api = this.stmt.getRDBMSManager().getApiAdapter();
            final Class elementType = this.clr.classForName(mmd.getCollection().getElementType());
            if (!api.isPersistable(elementType) && mmd.getJoinMetaData() == null) {
                throw new NucleusUserException("Cannot perform Collection.contains when the collection<Non-Persistable> is not in a join table");
            }
            if (this.stmt.getQueryGenerator().getCompilationComponent() != CompilationComponent.FILTER) {
                return this.containsAsSubquery(collExpr, elemExpr);
            }
            boolean useSubquery = this.getNeedsSubquery(collExpr, elemExpr);
            if (elemExpr instanceof UnboundExpression) {
                final String varName = ((UnboundExpression)elemExpr).getVariableName();
                final String extensionName = "datanucleus.query.jdoql." + varName + ".join";
                final String extensionValue = (String)this.stmt.getQueryGenerator().getValueForExtension(extensionName);
                if (extensionValue != null && extensionValue.equalsIgnoreCase("SUBQUERY")) {
                    useSubquery = true;
                }
                else if (extensionValue != null && extensionValue.equalsIgnoreCase("INNERJOIN")) {
                    useSubquery = false;
                }
            }
            if (useSubquery) {
                return this.containsAsSubquery(collExpr, elemExpr);
            }
            return this.containsAsInnerJoin(collExpr, elemExpr);
        }
    }
    
    protected boolean getNeedsSubquery(final SQLExpression collExpr, final SQLExpression elemExpr) {
        if (elemExpr instanceof UnboundExpression) {
            NucleusLogger.QUERY.debug(">> collection.contains collExpr=" + collExpr + " elemExpr=" + elemExpr + " elem.variable=" + ((UnboundExpression)elemExpr).getVariableName() + " need to implement check on whether there is a !coll or an OR using just this variable");
        }
        boolean needsSubquery = false;
        final Boolean hasOR = (Boolean)this.stmt.getQueryGenerator().getProperty("Filter.OR");
        if (hasOR != null && hasOR) {
            needsSubquery = true;
        }
        final Boolean hasNOT = (Boolean)this.stmt.getQueryGenerator().getProperty("Filter.NOT");
        if (hasNOT != null && hasNOT) {
            needsSubquery = true;
        }
        return needsSubquery;
    }
    
    protected SQLExpression containsAsInnerJoin(final CollectionExpression collExpr, final SQLExpression elemExpr) {
        boolean elemIsUnbound = elemExpr instanceof UnboundExpression;
        String varName = null;
        String elemAlias = null;
        String elemType = null;
        if (elemIsUnbound) {
            varName = ((UnboundExpression)elemExpr).getVariableName();
            NucleusLogger.QUERY.debug("collection.contains(" + elemExpr + ") binding unbound variable " + varName + " using INNER JOIN");
        }
        else if (!this.stmt.getQueryGenerator().hasExplicitJoins()) {
            final SQLJoin.JoinType joinType = this.stmt.getJoinTypeForTable(elemExpr.getSQLTable());
            if (joinType == SQLJoin.JoinType.CROSS_JOIN) {
                elemAlias = this.stmt.removeCrossJoin(elemExpr.getSQLTable());
                elemIsUnbound = true;
                elemType = elemExpr.getJavaTypeMapping().getType();
                NucleusLogger.QUERY.debug("collection.contains(" + elemExpr + ") was previously bound as CROSS JOIN but changing to INNER JOIN");
            }
        }
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final AbstractMemberMetaData mmd = collExpr.getJavaTypeMapping().getMemberMetaData();
        AbstractClassMetaData elemCmd = mmd.getCollection().getElementClassMetaData(this.clr, storeMgr.getMetaDataManager());
        final CollectionTable joinTbl = (CollectionTable)storeMgr.getTable(mmd);
        if (elemIsUnbound) {
            final Class varType = this.stmt.getQueryGenerator().getTypeOfVariable(varName);
            if (varType != null) {
                elemType = varType.getName();
                elemCmd = storeMgr.getMetaDataManager().getMetaDataForClass(elemType, this.clr);
            }
        }
        if (elemType == null) {
            elemType = mmd.getCollection().getElementType();
        }
        if (joinTbl != null) {
            if (elemCmd == null) {
                final SQLTable joinSqlTbl = this.stmt.innerJoin(collExpr.getSQLTable(), collExpr.getSQLTable().getTable().getIdMapping(), joinTbl, elemAlias, joinTbl.getOwnerMapping(), null, null);
                final SQLExpression elemIdExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, joinTbl.getElementMapping());
                if (elemIsUnbound) {
                    this.stmt.getQueryGenerator().bindVariable(varName, null, elemIdExpr.getSQLTable(), elemIdExpr.getJavaTypeMapping());
                }
                else {
                    this.stmt.whereAnd(elemIdExpr.eq(elemExpr), true);
                }
            }
            else {
                final SQLTable joinSqlTbl = this.stmt.innerJoin(collExpr.getSQLTable(), collExpr.getSQLTable().getTable().getIdMapping(), joinTbl, null, joinTbl.getOwnerMapping(), null, null);
                if (!mmd.getCollection().isEmbeddedElement()) {
                    final DatastoreClass elemTbl = storeMgr.getDatastoreClass(elemType, this.clr);
                    SQLTable elemSqlTbl = null;
                    if (joinTbl.getElementMapping() instanceof ReferenceMapping && ((ReferenceMapping)joinTbl.getElementMapping()).getMappingStrategy() == 0) {
                        JavaTypeMapping elemMapping = null;
                        final JavaTypeMapping[] elemImplMappings = ((ReferenceMapping)joinTbl.getElementMapping()).getJavaTypeMapping();
                        for (int i = 0; i < elemImplMappings.length; ++i) {
                            if (elemImplMappings[i].getType().equals(elemCmd.getFullClassName())) {
                                elemMapping = elemImplMappings[i];
                                break;
                            }
                        }
                        elemSqlTbl = this.stmt.innerJoin(joinSqlTbl, elemMapping, joinTbl.getElementMapping(), elemTbl, elemAlias, elemTbl.getIdMapping(), null, null, null);
                    }
                    else {
                        elemSqlTbl = this.stmt.innerJoin(joinSqlTbl, joinTbl.getElementMapping(), elemTbl, elemAlias, elemTbl.getIdMapping(), null, null);
                    }
                    final SQLExpression elemIdExpr2 = this.exprFactory.newExpression(this.stmt, elemSqlTbl, elemTbl.getIdMapping());
                    if (elemIsUnbound) {
                        this.stmt.getQueryGenerator().bindVariable(varName, elemCmd, elemIdExpr2.getSQLTable(), elemIdExpr2.getJavaTypeMapping());
                    }
                    else {
                        this.stmt.whereAnd(elemIdExpr2.eq(elemExpr), true);
                    }
                }
                else {
                    final SQLExpression elemIdExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, joinTbl.getElementMapping());
                    if (elemIsUnbound) {
                        this.stmt.getQueryGenerator().bindVariable(varName, elemCmd, elemIdExpr.getSQLTable(), elemIdExpr.getJavaTypeMapping());
                    }
                    else {
                        this.stmt.whereAnd(elemIdExpr.eq(elemExpr), true);
                    }
                }
            }
        }
        else {
            final DatastoreClass elemTbl2 = storeMgr.getDatastoreClass(mmd.getCollection().getElementType(), this.clr);
            JavaTypeMapping ownerMapping = null;
            if (mmd.getMappedBy() != null) {
                ownerMapping = elemTbl2.getMemberMapping(mmd.getRelatedMemberMetaData(this.clr)[0]);
            }
            else {
                ownerMapping = elemTbl2.getExternalMapping(mmd, 5);
            }
            final SQLTable elemSqlTbl = this.stmt.innerJoin(collExpr.getSQLTable(), collExpr.getSQLTable().getTable().getIdMapping(), elemTbl2, elemAlias, ownerMapping, null, null);
            if (elemIsUnbound) {
                SQLExpression elemIdExpr2 = null;
                if (!elemType.equals(mmd.getCollection().getElementType())) {
                    final DatastoreClass varTbl = storeMgr.getDatastoreClass(elemType, this.clr);
                    final SQLTable varSqlTbl = this.stmt.innerJoin(elemSqlTbl, elemTbl2.getIdMapping(), varTbl, null, varTbl.getIdMapping(), null, null);
                    elemIdExpr2 = this.exprFactory.newExpression(this.stmt, varSqlTbl, varTbl.getIdMapping());
                }
                else {
                    elemIdExpr2 = this.exprFactory.newExpression(this.stmt, elemSqlTbl, elemTbl2.getIdMapping());
                }
                this.stmt.getQueryGenerator().bindVariable(varName, elemCmd, elemIdExpr2.getSQLTable(), elemIdExpr2.getJavaTypeMapping());
            }
            else {
                final SQLExpression elemIdExpr2 = this.exprFactory.newExpression(this.stmt, elemSqlTbl, elemTbl2.getIdMapping());
                this.stmt.whereAnd(elemIdExpr2.eq(elemExpr), true);
            }
        }
        final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, true);
        return this.exprFactory.newLiteral(this.stmt, m, true).eq(this.exprFactory.newLiteral(this.stmt, m, true));
    }
    
    protected SQLExpression containsAsSubquery(final CollectionExpression collExpr, final SQLExpression elemExpr) {
        final boolean elemIsUnbound = elemExpr instanceof UnboundExpression;
        String varName = null;
        if (elemIsUnbound) {
            varName = ((UnboundExpression)elemExpr).getVariableName();
            NucleusLogger.QUERY.debug("collection.contains(" + elemExpr + ") binding unbound variable " + varName + " using SUBQUERY");
        }
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final AbstractMemberMetaData mmd = collExpr.getJavaTypeMapping().getMemberMetaData();
        AbstractClassMetaData elemCmd = mmd.getCollection().getElementClassMetaData(this.clr, storeMgr.getMetaDataManager());
        final CollectionTable joinTbl = (CollectionTable)storeMgr.getTable(mmd);
        String elemType = mmd.getCollection().getElementType();
        if (elemIsUnbound) {
            final Class varType = this.stmt.getQueryGenerator().getTypeOfVariable(varName);
            if (varType != null) {
                elemType = varType.getName();
                elemCmd = storeMgr.getMetaDataManager().getMetaDataForClass(elemType, this.clr);
            }
        }
        SQLStatement subStmt = null;
        if (joinTbl != null) {
            if (elemCmd == null) {
                subStmt = new SQLStatement(this.stmt, storeMgr, joinTbl, null, null);
                subStmt.setClassLoaderResolver(this.clr);
                final JavaTypeMapping oneMapping = storeMgr.getMappingManager().getMapping(Integer.class);
                subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping, 1), null);
                final JavaTypeMapping ownerMapping = joinTbl.getOwnerMapping();
                final SQLExpression ownerExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping);
                final SQLExpression ownerIdExpr = this.exprFactory.newExpression(this.stmt, collExpr.getSQLTable(), collExpr.getSQLTable().getTable().getIdMapping());
                subStmt.whereAnd(ownerExpr.eq(ownerIdExpr), true);
                final SQLExpression elemIdExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), joinTbl.getElementMapping());
                if (elemIsUnbound) {
                    this.stmt.getQueryGenerator().bindVariable(varName, null, elemIdExpr.getSQLTable(), elemIdExpr.getJavaTypeMapping());
                }
                else {
                    subStmt.whereAnd(elemIdExpr.eq(elemExpr), true);
                }
            }
            else {
                final DatastoreClass elemTbl = storeMgr.getDatastoreClass(elemType, this.clr);
                subStmt = new SQLStatement(this.stmt, storeMgr, elemTbl, null, null);
                subStmt.setClassLoaderResolver(this.clr);
                final JavaTypeMapping oneMapping2 = storeMgr.getMappingManager().getMapping(Integer.class);
                subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping2, 1), null);
                final SQLTable joinSqlTbl = subStmt.innerJoin(subStmt.getPrimaryTable(), elemTbl.getIdMapping(), joinTbl, null, joinTbl.getElementMapping(), null, null);
                final JavaTypeMapping ownerMapping2 = joinTbl.getOwnerMapping();
                final SQLExpression ownerExpr2 = this.exprFactory.newExpression(subStmt, joinSqlTbl, ownerMapping2);
                final SQLExpression ownerIdExpr2 = this.exprFactory.newExpression(this.stmt, collExpr.getSQLTable(), collExpr.getSQLTable().getTable().getIdMapping());
                subStmt.whereAnd(ownerExpr2.eq(ownerIdExpr2), true);
                final SQLExpression elemIdExpr2 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), elemTbl.getIdMapping());
                if (elemIsUnbound) {
                    this.stmt.getQueryGenerator().bindVariable(varName, elemCmd, elemIdExpr2.getSQLTable(), elemIdExpr2.getJavaTypeMapping());
                }
                else {
                    subStmt.whereAnd(elemIdExpr2.eq(elemExpr), true);
                }
            }
        }
        else {
            final DatastoreClass elemTbl = storeMgr.getDatastoreClass(mmd.getCollection().getElementType(), this.clr);
            subStmt = new SQLStatement(this.stmt, storeMgr, elemTbl, null, null);
            subStmt.setClassLoaderResolver(this.clr);
            final JavaTypeMapping oneMapping2 = storeMgr.getMappingManager().getMapping(Integer.class);
            subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping2, 1), null);
            JavaTypeMapping ownerMapping3 = null;
            if (mmd.getMappedBy() != null) {
                ownerMapping3 = elemTbl.getMemberMapping(mmd.getRelatedMemberMetaData(this.clr)[0]);
            }
            else {
                ownerMapping3 = elemTbl.getExternalMapping(mmd, 5);
            }
            final SQLExpression ownerExpr3 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping3);
            final SQLExpression ownerIdExpr3 = this.exprFactory.newExpression(this.stmt, collExpr.getSQLTable(), collExpr.getSQLTable().getTable().getIdMapping());
            subStmt.whereAnd(ownerExpr3.eq(ownerIdExpr3), true);
            if (elemIsUnbound) {
                SQLExpression elemIdExpr3 = null;
                if (!elemType.equals(mmd.getCollection().getElementType())) {
                    final DatastoreClass varTbl = storeMgr.getDatastoreClass(elemType, this.clr);
                    final SQLTable varSqlTbl = subStmt.innerJoin(subStmt.getPrimaryTable(), elemTbl.getIdMapping(), varTbl, null, varTbl.getIdMapping(), null, null);
                    elemIdExpr3 = this.exprFactory.newExpression(subStmt, varSqlTbl, varTbl.getIdMapping());
                }
                else {
                    elemIdExpr3 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), elemTbl.getIdMapping());
                }
                this.stmt.getQueryGenerator().bindVariable(varName, elemCmd, elemIdExpr3.getSQLTable(), elemIdExpr3.getJavaTypeMapping());
            }
            else {
                final SQLExpression elemIdExpr3 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), elemTbl.getIdMapping());
                subStmt.whereAnd(elemIdExpr3.eq(elemExpr), true);
            }
        }
        return new BooleanSubqueryExpression(this.stmt, "EXISTS", subStmt);
    }
    
    protected boolean elementTypeCompatible(final Class elementType, final Class collectionElementType) {
        return (elementType.isPrimitive() || !collectionElementType.isPrimitive() || collectionElementType.isAssignableFrom(elementType) || elementType.isAssignableFrom(collectionElementType)) && (!elementType.isPrimitive() || (elementType == Boolean.TYPE && collectionElementType == Boolean.class) || (elementType == Byte.TYPE && collectionElementType == Byte.class) || (elementType == Character.TYPE && collectionElementType == Character.class) || (elementType == Double.TYPE && collectionElementType == Double.class) || (elementType == Float.TYPE && collectionElementType == Float.class) || (elementType == Integer.TYPE && collectionElementType == Integer.class) || (elementType == Long.TYPE && collectionElementType == Long.class) || (elementType == Short.TYPE && collectionElementType == Short.class));
    }
}
