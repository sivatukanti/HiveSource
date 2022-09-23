// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import java.util.Iterator;
import java.util.Collection;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.store.rdbms.sql.expression.NullLiteral;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import java.lang.reflect.Modifier;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;

public class DiscriminatorStatementGenerator extends AbstractStatementGenerator
{
    Class[] candidates;
    
    public DiscriminatorStatementGenerator(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr, final Class candidateType, final boolean includeSubclasses, final DatastoreIdentifier candidateTableAlias, final String candidateTableGroupName) {
        super(storeMgr, clr, candidateType, includeSubclasses, candidateTableAlias, candidateTableGroupName);
        this.candidates = null;
        this.setOption("restrictDiscriminator");
    }
    
    public DiscriminatorStatementGenerator(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr, final Class[] candidateTypes, final boolean includeSubclasses, final DatastoreIdentifier candidateTableAlias, final String candidateTableGroupName) {
        this(storeMgr, clr, candidateTypes[0], includeSubclasses, candidateTableAlias, candidateTableGroupName);
        this.candidates = candidateTypes;
    }
    
    public DiscriminatorStatementGenerator(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr, final Class candidateType, final boolean includeSubclasses, final DatastoreIdentifier candidateTableAlias, final String candidateTableGroupName, final Table joinTable, final DatastoreIdentifier joinTableAlias, final JavaTypeMapping joinElementMapping) {
        super(storeMgr, clr, candidateType, includeSubclasses, candidateTableAlias, candidateTableGroupName, joinTable, joinTableAlias, joinElementMapping);
        this.candidates = null;
        this.setOption("restrictDiscriminator");
    }
    
    public DiscriminatorStatementGenerator(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr, final Class[] candidateTypes, final boolean includeSubclasses, final DatastoreIdentifier candidateTableAlias, final String candidateTableGroupName, final Table joinTable, final DatastoreIdentifier joinTableAlias, final JavaTypeMapping joinElementMapping) {
        this(storeMgr, clr, candidateTypes[0], includeSubclasses, candidateTableAlias, candidateTableGroupName, joinTable, joinTableAlias, joinElementMapping);
        this.candidates = candidateTypes;
    }
    
    @Override
    public void setParentStatement(final SQLStatement stmt) {
        this.parentStmt = stmt;
    }
    
    @Override
    public SQLStatement getStatement() {
        SQLStatement stmt = null;
        SQLTable discrimSqlTbl = null;
        if (this.joinTable == null) {
            stmt = new SQLStatement(this.parentStmt, this.storeMgr, this.candidateTable, this.candidateTableAlias, this.candidateTableGroupName);
            stmt.setClassLoaderResolver(this.clr);
            discrimSqlTbl = stmt.getPrimaryTable();
        }
        else {
            stmt = new SQLStatement(this.parentStmt, this.storeMgr, this.joinTable, this.joinTableAlias, this.candidateTableGroupName);
            stmt.setClassLoaderResolver(this.clr);
            final JavaTypeMapping candidateIdMapping = this.candidateTable.getIdMapping();
            if (this.hasOption("allowNulls")) {
                discrimSqlTbl = stmt.leftOuterJoin(null, this.joinElementMapping, this.candidateTable, null, candidateIdMapping, null, stmt.getPrimaryTable().getGroupName());
            }
            else {
                discrimSqlTbl = stmt.innerJoin(null, this.joinElementMapping, this.candidateTable, null, candidateIdMapping, null, stmt.getPrimaryTable().getGroupName());
            }
        }
        final JavaTypeMapping discMapping = this.candidateTable.getDiscriminatorMapping(true);
        if (discMapping != null) {
            discrimSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(stmt, discrimSqlTbl, discMapping);
        }
        final DiscriminatorMetaData dismd = discrimSqlTbl.getTable().getDiscriminatorMetaData();
        final boolean hasDiscriminator = discMapping != null && dismd != null && dismd.getStrategy() != DiscriminatorStrategy.NONE;
        final boolean restrictDiscriminator = this.hasOption("restrictDiscriminator");
        if (hasDiscriminator && restrictDiscriminator) {
            boolean multipleCandidates = false;
            BooleanExpression discExpr = null;
            if (this.candidates != null) {
                if (this.candidates.length > 1) {
                    multipleCandidates = true;
                }
                for (int i = 0; i < this.candidates.length; ++i) {
                    if (!Modifier.isAbstract(this.candidates[i].getModifiers())) {
                        final BooleanExpression discExprCandidate = SQLStatementHelper.getExpressionForDiscriminatorForClass(stmt, this.candidates[i].getName(), dismd, discMapping, discrimSqlTbl, this.clr);
                        if (discExpr != null) {
                            discExpr = discExpr.ior(discExprCandidate);
                        }
                        else {
                            discExpr = discExprCandidate;
                        }
                        if (this.includeSubclasses) {
                            final Collection<String> subclassNames = this.storeMgr.getSubClassesForClass(this.candidateType.getName(), true, this.clr);
                            final Iterator<String> subclassIter = subclassNames.iterator();
                            if (!multipleCandidates) {
                                multipleCandidates = (subclassNames.size() > 0);
                            }
                            while (subclassIter.hasNext()) {
                                final String subclassName = subclassIter.next();
                                final BooleanExpression discExprSub = SQLStatementHelper.getExpressionForDiscriminatorForClass(stmt, subclassName, dismd, discMapping, discrimSqlTbl, this.clr);
                                discExpr = discExpr.ior(discExprSub);
                            }
                        }
                    }
                }
            }
            else {
                if (!Modifier.isAbstract(this.candidateType.getModifiers())) {
                    discExpr = SQLStatementHelper.getExpressionForDiscriminatorForClass(stmt, this.candidateType.getName(), dismd, discMapping, discrimSqlTbl, this.clr);
                }
                if (this.includeSubclasses) {
                    final Collection<String> subclassNames2 = this.storeMgr.getSubClassesForClass(this.candidateType.getName(), true, this.clr);
                    final Iterator<String> subclassIter2 = subclassNames2.iterator();
                    multipleCandidates = (subclassNames2.size() > 0);
                    while (subclassIter2.hasNext()) {
                        final String subclassName2 = subclassIter2.next();
                        final Class subclass = this.clr.classForName(subclassName2);
                        if (Modifier.isAbstract(subclass.getModifiers())) {
                            continue;
                        }
                        final BooleanExpression discExprCandidate2 = SQLStatementHelper.getExpressionForDiscriminatorForClass(stmt, subclassName2, dismd, discMapping, discrimSqlTbl, this.clr);
                        if (discExpr == null) {
                            discExpr = discExprCandidate2;
                        }
                        else {
                            discExpr = discExpr.ior(discExprCandidate2);
                        }
                    }
                }
                if (discExpr == null) {
                    final SQLExpressionFactory exprFactory = stmt.getSQLExpressionFactory();
                    final JavaTypeMapping m = exprFactory.getMappingForType(Boolean.TYPE, true);
                    discExpr = exprFactory.newLiteral(stmt, m, true).eq(exprFactory.newLiteral(stmt, m, false));
                }
            }
            if (this.hasOption("allowNulls")) {
                final SQLExpression expr = stmt.getSQLExpressionFactory().newExpression(stmt, discrimSqlTbl, discMapping);
                final SQLExpression val = new NullLiteral(stmt, null, null, null);
                final BooleanExpression nullDiscExpr = expr.eq(val);
                discExpr = discExpr.ior(nullDiscExpr);
                if (!multipleCandidates) {
                    multipleCandidates = true;
                }
            }
            if (multipleCandidates) {
                discExpr.encloseInParentheses();
            }
            stmt.whereAnd(discExpr, true);
        }
        if (this.candidateTable.getMultitenancyMapping() != null) {
            final JavaTypeMapping tenantMapping = this.candidateTable.getMultitenancyMapping();
            final SQLTable tenantSqlTbl = stmt.getTable(tenantMapping.getTable(), null);
            final SQLExpression tenantExpr = stmt.getSQLExpressionFactory().newExpression(stmt, tenantSqlTbl, tenantMapping);
            final SQLExpression tenantVal = stmt.getSQLExpressionFactory().newLiteral(stmt, tenantMapping, this.storeMgr.getStringProperty("datanucleus.TenantID"));
            stmt.whereAnd(tenantExpr.eq(tenantVal), true);
        }
        return stmt;
    }
}
