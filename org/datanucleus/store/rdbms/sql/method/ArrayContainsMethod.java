// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.sql.expression.BooleanSubqueryExpression;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.table.ArrayTable;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.lang.reflect.Array;
import org.datanucleus.store.rdbms.sql.expression.UnboundExpression;
import org.datanucleus.store.rdbms.sql.expression.ArrayLiteral;
import org.datanucleus.store.rdbms.sql.expression.ArrayExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class ArrayContainsMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() != 1) {
            throw new NucleusException("Incorrect arguments for Array.contains(SQLExpression)");
        }
        final ArrayExpression arrExpr = (ArrayExpression)expr;
        SQLExpression elemExpr = args.get(0);
        if (elemExpr.isParameter()) {
            final AbstractMemberMetaData mmd = arrExpr.getJavaTypeMapping().getMemberMetaData();
            if (mmd != null) {
                this.stmt.getQueryGenerator().bindParameter(elemExpr.getParameterName(), mmd.getType().getComponentType());
            }
        }
        if (expr instanceof ArrayLiteral) {
            if (elemExpr instanceof UnboundExpression) {
                final Class elemCls = this.clr.classForName(arrExpr.getJavaTypeMapping().getType()).getComponentType();
                elemExpr = this.stmt.getQueryGenerator().bindVariable((UnboundExpression)elemExpr, elemCls);
            }
            final ArrayLiteral lit = (ArrayLiteral)expr;
            final Object array = lit.getValue();
            final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, true);
            if (array == null || Array.getLength(array) == 0) {
                return this.exprFactory.newLiteral(this.stmt, m, true).eq(this.exprFactory.newLiteral(this.stmt, m, false));
            }
            BooleanExpression bExpr = null;
            final List<SQLExpression> elementExprs = lit.getElementExpressions();
            for (int i = 0; i < elementExprs.size(); ++i) {
                final SQLExpression arrElemExpr = elementExprs.get(i);
                if (bExpr == null) {
                    bExpr = arrElemExpr.eq(elemExpr);
                }
                else {
                    bExpr = bExpr.ior(arrElemExpr.eq(elemExpr));
                }
            }
            bExpr.encloseInParentheses();
            return bExpr;
        }
        else {
            if (arrExpr.getElementExpressions() != null) {
                if (elemExpr instanceof UnboundExpression) {
                    final Class elemCls = this.clr.classForName(arrExpr.getJavaTypeMapping().getType()).getComponentType();
                    elemExpr = this.stmt.getQueryGenerator().bindVariable((UnboundExpression)elemExpr, elemCls);
                }
                BooleanExpression bExpr2 = null;
                final List<SQLExpression> elementExprs2 = arrExpr.getElementExpressions();
                for (int j = 0; j < elementExprs2.size(); ++j) {
                    final SQLExpression arrElemExpr2 = elementExprs2.get(j);
                    if (bExpr2 == null) {
                        bExpr2 = arrElemExpr2.eq(elemExpr);
                    }
                    else {
                        bExpr2 = bExpr2.ior(arrElemExpr2.eq(elemExpr));
                    }
                }
                bExpr2.encloseInParentheses();
                return bExpr2;
            }
            return this.containsAsSubquery(arrExpr, elemExpr);
        }
    }
    
    protected SQLExpression containsAsSubquery(final ArrayExpression arrExpr, final SQLExpression elemExpr) {
        final boolean elemIsUnbound = elemExpr instanceof UnboundExpression;
        String varName = null;
        if (elemIsUnbound) {
            varName = ((UnboundExpression)elemExpr).getVariableName();
            NucleusLogger.QUERY.debug(">> Array.contains binding unbound variable " + varName + " using SUBQUERY");
        }
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final AbstractMemberMetaData mmd = arrExpr.getJavaTypeMapping().getMemberMetaData();
        final AbstractClassMetaData elemCmd = mmd.getArray().getElementClassMetaData(this.clr, storeMgr.getMetaDataManager());
        final ArrayTable joinTbl = (ArrayTable)storeMgr.getTable(mmd);
        SQLStatement subStmt = null;
        if (joinTbl != null) {
            if (elemCmd == null) {
                subStmt = new SQLStatement(this.stmt, storeMgr, joinTbl, null, null);
                subStmt.setClassLoaderResolver(this.clr);
                final JavaTypeMapping oneMapping = storeMgr.getMappingManager().getMapping(Integer.class);
                subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping, 1), null);
                final JavaTypeMapping ownerMapping = joinTbl.getOwnerMapping();
                final SQLExpression ownerExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping);
                final SQLExpression ownerIdExpr = this.exprFactory.newExpression(this.stmt, arrExpr.getSQLTable(), arrExpr.getSQLTable().getTable().getIdMapping());
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
                final DatastoreClass elemTbl = storeMgr.getDatastoreClass(mmd.getArray().getElementType(), this.clr);
                subStmt = new SQLStatement(this.stmt, storeMgr, elemTbl, null, null);
                subStmt.setClassLoaderResolver(this.clr);
                final JavaTypeMapping oneMapping2 = storeMgr.getMappingManager().getMapping(Integer.class);
                subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping2, 1), null);
                final SQLTable joinSqlTbl = subStmt.innerJoin(subStmt.getPrimaryTable(), elemTbl.getIdMapping(), joinTbl, null, joinTbl.getElementMapping(), null, null);
                final JavaTypeMapping ownerMapping2 = joinTbl.getOwnerMapping();
                final SQLExpression ownerExpr2 = this.exprFactory.newExpression(subStmt, joinSqlTbl, ownerMapping2);
                final SQLExpression ownerIdExpr2 = this.exprFactory.newExpression(this.stmt, arrExpr.getSQLTable(), arrExpr.getSQLTable().getTable().getIdMapping());
                subStmt.whereAnd(ownerExpr2.eq(ownerIdExpr2), true);
                final SQLExpression elemIdExpr2 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), elemTbl.getIdMapping());
                if (elemIsUnbound) {
                    this.stmt.getQueryGenerator().bindVariable(varName, elemCmd, elemIdExpr2.getSQLTable(), elemIdExpr2.getJavaTypeMapping());
                }
                else {
                    subStmt.whereAnd(elemIdExpr2.eq(elemExpr), true);
                }
            }
            return new BooleanSubqueryExpression(this.stmt, "EXISTS", subStmt);
        }
        throw new NucleusException("Dont support evaluation of ARRAY.contains when no join table is used");
    }
}
