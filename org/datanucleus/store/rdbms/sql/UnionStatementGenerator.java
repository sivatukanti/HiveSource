// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.rdbms.sql.expression.NullLiteral;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.util.NucleusLogger;
import java.util.Iterator;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.exceptions.NucleusException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;

public class UnionStatementGenerator extends AbstractStatementGenerator
{
    public static final String NUC_TYPE_COLUMN = "NUCLEUS_TYPE";
    private int maxClassNameLength;
    
    public UnionStatementGenerator(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr, final Class candidateType, final boolean includeSubclasses, final DatastoreIdentifier candidateTableAlias, final String candidateTableGroupName) {
        super(storeMgr, clr, candidateType, includeSubclasses, candidateTableAlias, candidateTableGroupName);
        this.maxClassNameLength = -1;
    }
    
    public UnionStatementGenerator(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr, final Class candidateType, final boolean includeSubclasses, final DatastoreIdentifier candidateTableAlias, final String candidateTableGroupName, final Table joinTable, final DatastoreIdentifier joinTableAlias, final JavaTypeMapping joinElementMapping) {
        super(storeMgr, clr, candidateType, includeSubclasses, candidateTableAlias, candidateTableGroupName, joinTable, joinTableAlias, joinElementMapping);
        this.maxClassNameLength = -1;
    }
    
    @Override
    public void setParentStatement(final SQLStatement stmt) {
        this.parentStmt = stmt;
    }
    
    @Override
    public SQLStatement getStatement() {
        final Collection<String> candidateClassNames = new ArrayList<String>();
        final AbstractClassMetaData acmd = this.storeMgr.getMetaDataManager().getMetaDataForClass(this.candidateType, this.clr);
        candidateClassNames.add(acmd.getFullClassName());
        if (this.includeSubclasses) {
            final Collection<String> subclasses = this.storeMgr.getSubClassesForClass(this.candidateType.getName(), true, this.clr);
            candidateClassNames.addAll(subclasses);
        }
        Iterator<String> iter = candidateClassNames.iterator();
        while (iter.hasNext()) {
            final String className = iter.next();
            try {
                final Class cls = this.clr.classForName(className);
                if (!Modifier.isAbstract(cls.getModifiers())) {
                    continue;
                }
                iter.remove();
            }
            catch (Exception e) {
                iter.remove();
            }
        }
        if (this.hasOption("selectNucleusType")) {
            iter = candidateClassNames.iterator();
            while (iter.hasNext()) {
                final String className = iter.next();
                if (className.length() > this.maxClassNameLength) {
                    this.maxClassNameLength = className.length();
                }
            }
        }
        if (candidateClassNames.isEmpty()) {
            throw new NucleusException("Attempt to generate SQL statement using UNIONs for " + this.candidateType.getName() + " yet there are no concrete classes with their own table available");
        }
        SQLStatement stmt = null;
        iter = candidateClassNames.iterator();
        while (iter.hasNext()) {
            final String candidateClassName = iter.next();
            SQLStatement candidateStmt = null;
            if (this.joinTable == null) {
                candidateStmt = this.getSQLStatementForCandidate(candidateClassName);
            }
            else {
                candidateStmt = this.getSQLStatementForCandidateViaJoin(candidateClassName);
            }
            if (candidateStmt != null) {
                if (stmt == null) {
                    stmt = candidateStmt;
                }
                else {
                    stmt.union(candidateStmt);
                }
            }
        }
        return stmt;
    }
    
    protected SQLStatement getSQLStatementForCandidate(final String className) {
        final DatastoreClass table = this.storeMgr.getDatastoreClass(className, this.clr);
        if (table == null) {
            NucleusLogger.GENERAL.info("Generation of statement to retrieve objects of type " + this.candidateType.getName() + (this.includeSubclasses ? " including subclasses " : "") + " attempted to include " + className + " but this has no table of its own; ignored");
            return null;
        }
        final SQLStatement stmt = new SQLStatement(this.parentStmt, this.storeMgr, this.candidateTable, this.candidateTableAlias, this.candidateTableGroupName);
        stmt.setClassLoaderResolver(this.clr);
        stmt.setCandidateClassName(className);
        String tblGroupName = stmt.getPrimaryTable().getGroupName();
        if (table != this.candidateTable) {
            final JavaTypeMapping candidateIdMapping = this.candidateTable.getIdMapping();
            final JavaTypeMapping tableIdMapping = table.getIdMapping();
            final SQLTable tableSqlTbl = stmt.innerJoin(null, candidateIdMapping, table, null, tableIdMapping, null, stmt.getPrimaryTable().getGroupName());
            tblGroupName = tableSqlTbl.getGroupName();
        }
        final SQLExpressionFactory factory = this.storeMgr.getSQLExpressionFactory();
        final JavaTypeMapping discriminatorMapping = table.getDiscriminatorMapping(false);
        final DiscriminatorMetaData discriminatorMetaData = table.getDiscriminatorMetaData();
        if (discriminatorMapping != null && discriminatorMetaData.getStrategy() != DiscriminatorStrategy.NONE) {
            String discriminatorValue = className;
            if (discriminatorMetaData.getStrategy() == DiscriminatorStrategy.VALUE_MAP) {
                final AbstractClassMetaData targetCmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(className, this.clr);
                discriminatorValue = targetCmd.getInheritanceMetaData().getDiscriminatorMetaData().getValue();
            }
            final SQLExpression discExpr = factory.newExpression(stmt, stmt.getPrimaryTable(), discriminatorMapping);
            final SQLExpression discVal = factory.newLiteral(stmt, discriminatorMapping, discriminatorValue);
            stmt.whereAnd(discExpr.eq(discVal), false);
        }
        if (table.getMultitenancyMapping() != null) {
            final JavaTypeMapping tenantMapping = table.getMultitenancyMapping();
            final SQLTable tenantSqlTbl = stmt.getTable(tenantMapping.getTable(), tblGroupName);
            final SQLExpression tenantExpr = stmt.getSQLExpressionFactory().newExpression(stmt, tenantSqlTbl, tenantMapping);
            final SQLExpression tenantVal = stmt.getSQLExpressionFactory().newLiteral(stmt, tenantMapping, this.storeMgr.getStringProperty("datanucleus.TenantID"));
            stmt.whereAnd(tenantExpr.eq(tenantVal), true);
        }
        for (final String subclassName : this.storeMgr.getSubClassesForClass(className, false, this.clr)) {
            DatastoreClass[] subclassTables = null;
            final DatastoreClass subclassTable = this.storeMgr.getDatastoreClass(subclassName, this.clr);
            if (subclassTable == null) {
                final AbstractClassMetaData targetSubCmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(subclassName, this.clr);
                final AbstractClassMetaData[] targetSubCmds = this.storeMgr.getClassesManagingTableForClass(targetSubCmd, this.clr);
                subclassTables = new DatastoreClass[targetSubCmds.length];
                for (int i = 0; i < targetSubCmds.length; ++i) {
                    subclassTables[i] = this.storeMgr.getDatastoreClass(targetSubCmds[i].getFullClassName(), this.clr);
                }
            }
            else {
                subclassTables = new DatastoreClass[] { subclassTable };
            }
            for (int j = 0; j < subclassTables.length; ++j) {
                if (subclassTables[j] != table) {
                    final JavaTypeMapping tableIdMapping2 = table.getIdMapping();
                    final JavaTypeMapping subclassIdMapping = subclassTables[j].getIdMapping();
                    final SQLTable sqlTableSubclass = stmt.leftOuterJoin(null, tableIdMapping2, subclassTables[j], null, subclassIdMapping, null, stmt.getPrimaryTable().getGroupName());
                    final SQLExpression subclassIdExpr = factory.newExpression(stmt, sqlTableSubclass, subclassIdMapping);
                    final SQLExpression nullExpr = new NullLiteral(stmt, null, null, null);
                    stmt.whereAnd(subclassIdExpr.eq(nullExpr), false);
                }
            }
        }
        if (this.hasOption("selectNucleusType")) {
            this.addTypeSelectForClass(stmt, className);
        }
        return stmt;
    }
    
    protected SQLStatement getSQLStatementForCandidateViaJoin(final String className) {
        final DatastoreClass table = this.storeMgr.getDatastoreClass(className, this.clr);
        if (table == null) {}
        final SQLStatement stmt = new SQLStatement(this.parentStmt, this.storeMgr, this.joinTable, this.joinTableAlias, this.candidateTableGroupName);
        stmt.setClassLoaderResolver(this.clr);
        stmt.setCandidateClassName(className);
        final JavaTypeMapping candidateIdMapping = this.candidateTable.getIdMapping();
        SQLTable candidateSQLTable = null;
        if (this.hasOption("allowNulls")) {
            candidateSQLTable = stmt.leftOuterJoin(null, this.joinElementMapping, this.candidateTable, null, candidateIdMapping, null, stmt.getPrimaryTable().getGroupName());
        }
        else {
            candidateSQLTable = stmt.innerJoin(null, this.joinElementMapping, this.candidateTable, null, candidateIdMapping, null, stmt.getPrimaryTable().getGroupName());
        }
        if (table != this.candidateTable) {
            final JavaTypeMapping tableIdMapping = table.getIdMapping();
            stmt.innerJoin(candidateSQLTable, candidateIdMapping, table, null, tableIdMapping, null, stmt.getPrimaryTable().getGroupName());
        }
        final SQLExpressionFactory factory = this.storeMgr.getSQLExpressionFactory();
        final JavaTypeMapping discriminatorMapping = table.getDiscriminatorMapping(false);
        final DiscriminatorMetaData discriminatorMetaData = table.getDiscriminatorMetaData();
        if (discriminatorMapping != null && discriminatorMetaData.getStrategy() != DiscriminatorStrategy.NONE) {
            final BooleanExpression discExpr = SQLStatementHelper.getExpressionForDiscriminatorForClass(stmt, className, discriminatorMetaData, discriminatorMapping, stmt.getPrimaryTable(), this.clr);
            stmt.whereAnd(discExpr, false);
        }
        for (final String subclassName : this.storeMgr.getSubClassesForClass(className, false, this.clr)) {
            DatastoreClass[] subclassTables = null;
            final DatastoreClass subclassTable = this.storeMgr.getDatastoreClass(subclassName, this.clr);
            if (subclassTable == null) {
                final AbstractClassMetaData targetSubCmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(subclassName, this.clr);
                final AbstractClassMetaData[] targetSubCmds = this.storeMgr.getClassesManagingTableForClass(targetSubCmd, this.clr);
                subclassTables = new DatastoreClass[targetSubCmds.length];
                for (int i = 0; i < targetSubCmds.length; ++i) {
                    subclassTables[i] = this.storeMgr.getDatastoreClass(targetSubCmds[i].getFullClassName(), this.clr);
                }
            }
            else {
                subclassTables = new DatastoreClass[] { subclassTable };
            }
            for (int j = 0; j < subclassTables.length; ++j) {
                if (subclassTables[j] != table) {
                    final JavaTypeMapping subclassIdMapping = subclassTables[j].getIdMapping();
                    final SQLTable sqlTableSubclass = stmt.leftOuterJoin(null, this.joinElementMapping, subclassTables[j], null, subclassIdMapping, null, stmt.getPrimaryTable().getGroupName());
                    final SQLExpression subclassIdExpr = factory.newExpression(stmt, sqlTableSubclass, subclassIdMapping);
                    final SQLExpression nullExpr = new NullLiteral(stmt, null, null, null);
                    stmt.whereAnd(subclassIdExpr.eq(nullExpr), false);
                }
            }
        }
        if (this.hasOption("selectNucleusType")) {
            this.addTypeSelectForClass(stmt, className);
        }
        return stmt;
    }
    
    private void addTypeSelectForClass(final SQLStatement stmt, final String className) {
        if (this.hasOption("selectNucleusType")) {
            final JavaTypeMapping m = this.storeMgr.getMappingManager().getMapping(String.class);
            String nuctypeName = className;
            if (this.maxClassNameLength > nuctypeName.length()) {
                nuctypeName = StringUtils.leftAlignedPaddedString(nuctypeName, this.maxClassNameLength);
            }
            final StringLiteral lit = new StringLiteral(stmt, m, nuctypeName, (String)null);
            stmt.select(lit, "NUCLEUS_TYPE");
        }
    }
}
