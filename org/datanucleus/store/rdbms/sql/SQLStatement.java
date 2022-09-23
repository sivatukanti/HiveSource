// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

import org.datanucleus.store.StoreManager;
import org.datanucleus.store.rdbms.sql.expression.BooleanSubqueryExpression;
import java.util.Collection;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.rdbms.sql.expression.BooleanLiteral;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.AggregateTemporalExpression;
import org.datanucleus.store.rdbms.sql.expression.AggregateNumericExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.Iterator;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import java.util.List;
import java.util.HashMap;
import org.datanucleus.store.rdbms.query.QueryGenerator;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.Map;
import org.datanucleus.util.Localiser;

public class SQLStatement
{
    protected static final Localiser LOCALISER;
    protected static final Map<String, SQLTableNamer> tableNamerByName;
    protected SQLText sql;
    protected RDBMSStoreManager rdbmsMgr;
    protected ClassLoaderResolver clr;
    protected QueryGenerator queryGenerator;
    protected String candidateClassName;
    protected boolean distinct;
    protected HashMap<String, Object> extensions;
    protected SQLStatement parent;
    protected List<SQLStatement> unions;
    protected List<String> selects;
    protected SQLExpression[] updates;
    protected boolean aggregated;
    protected SQLTable primaryTable;
    protected List<SQLJoin> joins;
    protected boolean requiresJoinReorder;
    protected Map<String, SQLTable> tables;
    protected Map<String, SQLTableGroup> tableGroups;
    protected BooleanExpression where;
    protected List<SQLExpression> groupingExpressions;
    protected BooleanExpression having;
    protected SQLExpression[] orderingExpressions;
    protected boolean[] orderingDirections;
    protected String[] orderNullDirectives;
    protected long rangeOffset;
    protected long rangeCount;
    private int[] orderingColumnIndexes;
    
    public SQLStatement(final RDBMSStoreManager rdbmsMgr, final Table table, final DatastoreIdentifier alias, final String tableGroupName) {
        this(null, rdbmsMgr, table, alias, tableGroupName);
    }
    
    public SQLStatement(final SQLStatement parentStmt, final RDBMSStoreManager rdbmsMgr, final Table table, DatastoreIdentifier alias, final String tableGroupName) {
        this.sql = null;
        this.queryGenerator = null;
        this.candidateClassName = null;
        this.distinct = false;
        this.parent = null;
        this.unions = null;
        this.selects = new ArrayList<String>();
        this.updates = null;
        this.aggregated = false;
        this.requiresJoinReorder = false;
        this.tableGroups = new HashMap<String, SQLTableGroup>();
        this.groupingExpressions = null;
        this.orderingExpressions = null;
        this.orderingDirections = null;
        this.orderNullDirectives = null;
        this.rangeOffset = -1L;
        this.rangeCount = -1L;
        this.parent = parentStmt;
        this.rdbmsMgr = rdbmsMgr;
        final String namerStrategy = rdbmsMgr.getStringProperty("datanucleus.rdbms.sqlTableNamingStrategy");
        this.addExtension("datanucleus.sqlTableNamingStrategy", namerStrategy);
        final String tableGrpName = (tableGroupName != null) ? tableGroupName : "Group0";
        if (alias == null) {
            alias = rdbmsMgr.getIdentifierFactory().newTableIdentifier(this.generateTableAlias(table, tableGrpName));
        }
        this.putSQLTableInGroup(this.primaryTable = new SQLTable(this, table, alias, tableGrpName), tableGrpName, null);
        if (parentStmt != null) {
            this.queryGenerator = parentStmt.getQueryGenerator();
        }
    }
    
    public RDBMSStoreManager getRDBMSManager() {
        return this.rdbmsMgr;
    }
    
    public void setClassLoaderResolver(final ClassLoaderResolver clr) {
        this.clr = clr;
    }
    
    public ClassLoaderResolver getClassLoaderResolver() {
        if (this.clr == null) {
            this.clr = this.rdbmsMgr.getNucleusContext().getClassLoaderResolver(null);
        }
        return this.clr;
    }
    
    public void setCandidateClassName(final String name) {
        this.candidateClassName = name;
    }
    
    public String getCandidateClassName() {
        return this.candidateClassName;
    }
    
    public QueryGenerator getQueryGenerator() {
        return this.queryGenerator;
    }
    
    public void setQueryGenerator(final QueryGenerator gen) {
        this.queryGenerator = gen;
    }
    
    public SQLExpressionFactory getSQLExpressionFactory() {
        return this.rdbmsMgr.getSQLExpressionFactory();
    }
    
    public DatastoreAdapter getDatastoreAdapter() {
        return this.rdbmsMgr.getDatastoreAdapter();
    }
    
    public SQLStatement getParentStatement() {
        return this.parent;
    }
    
    public boolean isChildStatementOf(final SQLStatement stmt) {
        return stmt != null && this.parent != null && (stmt == this.parent || this.isChildStatementOf(this.parent));
    }
    
    public void addExtension(final String key, final Object value) {
        this.invalidateStatement();
        if (this.extensions == null) {
            this.extensions = new HashMap<String, Object>();
        }
        this.extensions.put(key, value);
    }
    
    public Object getValueForExtension(final String key) {
        if (this.extensions == null) {
            return this.extensions;
        }
        return this.extensions.get(key);
    }
    
    public void union(final SQLStatement stmt) {
        this.invalidateStatement();
        if (this.unions == null) {
            this.unions = new ArrayList<SQLStatement>();
        }
        this.unions.add(stmt);
    }
    
    public int getNumberOfUnions() {
        if (this.unions == null) {
            return 0;
        }
        int number = this.unions.size();
        for (final SQLStatement unioned : this.unions) {
            number += unioned.getNumberOfUnions();
        }
        return number;
    }
    
    public List<SQLStatement> getUnions() {
        return this.unions;
    }
    
    public boolean allUnionsForSamePrimaryTable() {
        if (this.unions != null) {
            for (final SQLStatement unionStmt : this.unions) {
                if (!unionStmt.getPrimaryTable().equals(this.primaryTable)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isDistinct() {
        return this.distinct;
    }
    
    public void setDistinct(final boolean distinct) {
        this.invalidateStatement();
        this.distinct = distinct;
    }
    
    public int getNumberOfSelects() {
        return this.selects.size();
    }
    
    public int[] select(final SQLExpression expr, final String alias) {
        if (expr == null) {
            throw new NucleusException("Expression to select is null");
        }
        this.invalidateStatement();
        if (expr instanceof AggregateNumericExpression || expr instanceof AggregateTemporalExpression) {
            this.aggregated = true;
        }
        final int[] selected = new int[expr.getNumberOfSubExpressions()];
        if (expr.getNumberOfSubExpressions() > 1) {
            for (int i = 0; i < expr.getNumberOfSubExpressions(); ++i) {
                String exprStr = expr.getSubExpression(i).toSQLText().toSQL();
                if (alias != null) {
                    exprStr = exprStr + " AS " + alias + i;
                }
                selected[i] = this.selectItem(exprStr);
            }
        }
        else {
            String exprStr2 = expr.toSQLText().toSQL();
            if (alias != null) {
                exprStr2 = exprStr2 + " AS " + alias;
            }
            selected[0] = this.selectItem(exprStr2);
        }
        if (this.unions != null) {
            for (final SQLStatement stmt : this.unions) {
                stmt.select(expr, alias);
            }
        }
        return selected;
    }
    
    public int[] select(SQLTable table, final JavaTypeMapping mapping, final String alias, final boolean applyToUnions) {
        if (mapping == null) {
            throw new NucleusException("Mapping to select is null");
        }
        if (table == null) {
            table = this.primaryTable;
        }
        if (mapping.getTable() != table.getTable()) {
            throw new NucleusException("Table being selected from (\"" + table.getTable() + "\") is inconsistent with the column selected (\"" + mapping.getTable() + "\")");
        }
        this.invalidateStatement();
        final DatastoreMapping[] mappings = mapping.getDatastoreMappings();
        final int[] selected = new int[mappings.length];
        for (int i = 0; i < selected.length; ++i) {
            DatastoreIdentifier colAlias = null;
            if (alias != null) {
                String name = alias;
                if (selected.length > 1) {
                    name = alias + "_" + i;
                }
                colAlias = this.rdbmsMgr.getIdentifierFactory().newColumnIdentifier(name);
            }
            final SQLColumn col = new SQLColumn(table, mappings[i].getColumn(), colAlias);
            final int position = this.selectItem(col.toString());
            selected[i] = position;
        }
        if (applyToUnions && this.unions != null) {
            for (final SQLStatement stmt : this.unions) {
                stmt.select(table, mapping, alias);
            }
        }
        return selected;
    }
    
    public int[] select(final SQLTable table, final JavaTypeMapping mapping, final String alias) {
        return this.select(table, mapping, alias, true);
    }
    
    public int select(SQLTable table, final Column column, final String alias) {
        if (column == null) {
            throw new NucleusException("Column to select is null");
        }
        if (table == null) {
            table = this.primaryTable;
        }
        if (column.getTable() != table.getTable()) {
            throw new NucleusException("Table being selected from (\"" + table.getTable() + "\") is inconsistent with the column selected (\"" + column.getTable() + "\")");
        }
        this.invalidateStatement();
        DatastoreIdentifier colAlias = null;
        if (alias != null) {
            colAlias = this.rdbmsMgr.getIdentifierFactory().newColumnIdentifier(alias);
        }
        final SQLColumn col = new SQLColumn(table, column, colAlias);
        final int position = this.selectItem(col.toString());
        if (this.unions != null) {
            for (final SQLStatement stmt : this.unions) {
                stmt.select(table, column, alias);
            }
        }
        return position;
    }
    
    private int selectItem(final String item) {
        if (this.selects.contains(item)) {
            return this.selects.indexOf(item) + 1;
        }
        for (int numberSelected = this.selects.size(), i = 0; i < numberSelected; ++i) {
            final String selectedItem = this.selects.get(i);
            if (selectedItem.startsWith(item + " ")) {
                return i + 1;
            }
            if (item.startsWith(selectedItem + " ")) {
                this.selects.set(i, item);
                return i + 1;
            }
        }
        this.selects.add(item);
        return this.selects.indexOf(item) + 1;
    }
    
    public void setUpdates(final SQLExpression[] exprs) {
        this.invalidateStatement();
        this.updates = exprs;
    }
    
    public boolean hasUpdates() {
        if (this.updates == null) {
            return false;
        }
        for (int i = 0; i < this.updates.length; ++i) {
            if (this.updates[i] != null) {
                return true;
            }
        }
        return false;
    }
    
    public SQLTable getPrimaryTable() {
        return this.primaryTable;
    }
    
    public SQLTable getTable(final String alias) {
        if (alias.equals(this.primaryTable.alias.getIdentifierName())) {
            return this.primaryTable;
        }
        if (this.tables != null) {
            return this.tables.get(alias);
        }
        return null;
    }
    
    public SQLTable getTableForDatastoreContainer(final Table table) {
        for (final SQLTableGroup grp : this.tableGroups.values()) {
            final SQLTable[] tbls = grp.getTables();
            for (int i = 0; i < tbls.length; ++i) {
                if (tbls[i].getTable() == table) {
                    return tbls[i];
                }
            }
        }
        return null;
    }
    
    public SQLTable getTable(final Table table, final String groupName) {
        if (groupName == null) {
            return null;
        }
        final SQLTableGroup tableGrp = this.tableGroups.get(groupName);
        if (tableGrp == null) {
            return null;
        }
        final SQLTable[] tables = tableGrp.getTables();
        for (int i = 0; i < tables.length; ++i) {
            if (tables[i].getTable() == table) {
                return tables[i];
            }
        }
        return null;
    }
    
    public SQLTableGroup getTableGroup(final String groupName) {
        return this.tableGroups.get(groupName);
    }
    
    public int getNumberOfTableGroups() {
        return this.tableGroups.size();
    }
    
    public int getNumberOfTables() {
        return (this.tables != null) ? this.tables.size() : -1;
    }
    
    public SQLTable innerJoin(final SQLTable sourceTable, final JavaTypeMapping sourceMapping, final Table target, final String targetAlias, final JavaTypeMapping targetMapping, final Object[] discrimValues, final String tableGrpName) {
        return this.innerJoin(sourceTable, sourceMapping, null, target, targetAlias, targetMapping, null, discrimValues, tableGrpName);
    }
    
    public SQLTable innerJoin(SQLTable sourceTable, final JavaTypeMapping sourceMapping, final JavaTypeMapping sourceParentMapping, final Table target, String targetAlias, final JavaTypeMapping targetMapping, final JavaTypeMapping targetParentMapping, final Object[] discrimValues, String tableGrpName) {
        this.invalidateStatement();
        if (this.tables == null) {
            this.tables = new HashMap<String, SQLTable>();
        }
        if (tableGrpName == null) {
            tableGrpName = "Group" + this.tableGroups.size();
        }
        if (targetAlias == null) {
            targetAlias = this.generateTableAlias(target, tableGrpName);
        }
        if (sourceTable == null) {
            sourceTable = this.primaryTable;
        }
        final DatastoreIdentifier targetId = this.rdbmsMgr.getIdentifierFactory().newTableIdentifier(targetAlias);
        final SQLTable targetTbl = new SQLTable(this, target, targetId, tableGrpName);
        this.putSQLTableInGroup(targetTbl, tableGrpName, SQLJoin.JoinType.INNER_JOIN);
        this.join(SQLJoin.JoinType.INNER_JOIN, sourceTable, sourceMapping, sourceParentMapping, targetTbl, targetMapping, targetParentMapping, discrimValues);
        if (this.unions != null) {
            for (final SQLStatement stmt : this.unions) {
                stmt.innerJoin(sourceTable, sourceMapping, sourceParentMapping, target, targetAlias, targetMapping, targetParentMapping, discrimValues, tableGrpName);
            }
        }
        return targetTbl;
    }
    
    public SQLTable leftOuterJoin(final SQLTable sourceTable, final JavaTypeMapping sourceMapping, final Table target, final String targetAlias, final JavaTypeMapping targetMapping, final Object[] discrimValues, final String tableGrpName) {
        return this.leftOuterJoin(sourceTable, sourceMapping, null, target, targetAlias, targetMapping, null, discrimValues, tableGrpName);
    }
    
    public SQLTable leftOuterJoin(SQLTable sourceTable, final JavaTypeMapping sourceMapping, final JavaTypeMapping sourceParentMapping, final Table target, String targetAlias, final JavaTypeMapping targetMapping, final JavaTypeMapping targetParentMapping, final Object[] discrimValues, String tableGrpName) {
        this.invalidateStatement();
        if (this.tables == null) {
            this.tables = new HashMap<String, SQLTable>();
        }
        if (tableGrpName == null) {
            tableGrpName = "Group" + this.tableGroups.size();
        }
        if (targetAlias == null) {
            targetAlias = this.generateTableAlias(target, tableGrpName);
        }
        if (sourceTable == null) {
            sourceTable = this.primaryTable;
        }
        final DatastoreIdentifier targetId = this.rdbmsMgr.getIdentifierFactory().newTableIdentifier(targetAlias);
        final SQLTable targetTbl = new SQLTable(this, target, targetId, tableGrpName);
        this.putSQLTableInGroup(targetTbl, tableGrpName, SQLJoin.JoinType.LEFT_OUTER_JOIN);
        this.join(SQLJoin.JoinType.LEFT_OUTER_JOIN, sourceTable, sourceMapping, sourceParentMapping, targetTbl, targetMapping, targetParentMapping, discrimValues);
        if (this.unions != null) {
            for (final SQLStatement stmt : this.unions) {
                stmt.leftOuterJoin(sourceTable, sourceMapping, sourceParentMapping, target, targetAlias, targetMapping, targetParentMapping, discrimValues, tableGrpName);
            }
        }
        return targetTbl;
    }
    
    public SQLTable rightOuterJoin(final SQLTable sourceTable, final JavaTypeMapping sourceMapping, final Table target, final String targetAlias, final JavaTypeMapping targetMapping, final Object[] discrimValues, final String tableGrpName) {
        return this.rightOuterJoin(sourceTable, sourceMapping, null, target, targetAlias, targetMapping, null, discrimValues, tableGrpName);
    }
    
    public SQLTable rightOuterJoin(SQLTable sourceTable, final JavaTypeMapping sourceMapping, final JavaTypeMapping sourceParentMapping, final Table target, String targetAlias, final JavaTypeMapping targetMapping, final JavaTypeMapping targetParentMapping, final Object[] discrimValues, String tableGrpName) {
        this.invalidateStatement();
        if (this.tables == null) {
            this.tables = new HashMap<String, SQLTable>();
        }
        if (tableGrpName == null) {
            tableGrpName = "Group" + this.tableGroups.size();
        }
        if (targetAlias == null) {
            targetAlias = this.generateTableAlias(target, tableGrpName);
        }
        if (sourceTable == null) {
            sourceTable = this.primaryTable;
        }
        final DatastoreIdentifier targetId = this.rdbmsMgr.getIdentifierFactory().newTableIdentifier(targetAlias);
        final SQLTable targetTbl = new SQLTable(this, target, targetId, tableGrpName);
        this.putSQLTableInGroup(targetTbl, tableGrpName, SQLJoin.JoinType.RIGHT_OUTER_JOIN);
        this.join(SQLJoin.JoinType.RIGHT_OUTER_JOIN, sourceTable, sourceMapping, sourceParentMapping, targetTbl, targetMapping, targetParentMapping, discrimValues);
        if (this.unions != null) {
            for (final SQLStatement stmt : this.unions) {
                stmt.rightOuterJoin(sourceTable, sourceMapping, sourceParentMapping, target, targetAlias, targetMapping, targetParentMapping, discrimValues, tableGrpName);
            }
        }
        return targetTbl;
    }
    
    public SQLTable crossJoin(final Table target, String targetAlias, String tableGrpName) {
        this.invalidateStatement();
        if (this.tables == null) {
            this.tables = new HashMap<String, SQLTable>();
        }
        if (tableGrpName == null) {
            tableGrpName = "Group" + this.tableGroups.size();
        }
        if (targetAlias == null) {
            targetAlias = this.generateTableAlias(target, tableGrpName);
        }
        final DatastoreIdentifier targetId = this.rdbmsMgr.getIdentifierFactory().newTableIdentifier(targetAlias);
        final SQLTable targetTbl = new SQLTable(this, target, targetId, tableGrpName);
        this.putSQLTableInGroup(targetTbl, tableGrpName, SQLJoin.JoinType.CROSS_JOIN);
        this.join(SQLJoin.JoinType.CROSS_JOIN, this.primaryTable, null, null, targetTbl, null, null, null);
        if (this.unions != null) {
            for (final SQLStatement stmt : this.unions) {
                stmt.crossJoin(target, targetAlias, tableGrpName);
            }
        }
        return targetTbl;
    }
    
    public SQLJoin.JoinType getJoinTypeForTable(final SQLTable sqlTbl) {
        if (this.joins == null) {
            return null;
        }
        for (final SQLJoin join : this.joins) {
            if (join.getTable().equals(sqlTbl)) {
                return join.getType();
            }
        }
        return null;
    }
    
    public SQLJoin getJoinForTable(final SQLTable sqlTbl) {
        if (this.joins == null) {
            return null;
        }
        for (final SQLJoin join : this.joins) {
            if (join.getTable().equals(sqlTbl)) {
                return join;
            }
        }
        return null;
    }
    
    public String removeCrossJoin(final SQLTable targetSqlTbl) {
        if (this.joins == null) {
            return null;
        }
        final Iterator<SQLJoin> joinIter = this.joins.iterator();
        while (joinIter.hasNext()) {
            final SQLJoin join = joinIter.next();
            if (join.getTable().equals(targetSqlTbl) && join.getType() == SQLJoin.JoinType.CROSS_JOIN) {
                joinIter.remove();
                this.requiresJoinReorder = true;
                this.tables.remove(join.getTable().alias.getIdentifierName());
                final String removedAliasName = join.getTable().alias.getIdentifierName();
                if (this.unions != null) {
                    for (final SQLStatement stmt : this.unions) {
                        stmt.removeCrossJoin(targetSqlTbl);
                    }
                }
                return removedAliasName;
            }
        }
        return null;
    }
    
    private void putSQLTableInGroup(final SQLTable sqlTbl, final String groupName, final SQLJoin.JoinType joinType) {
        SQLTableGroup tableGrp = this.tableGroups.get(groupName);
        if (tableGrp == null) {
            tableGrp = new SQLTableGroup(groupName, joinType);
        }
        tableGrp.addTable(sqlTbl);
        this.tableGroups.put(groupName, tableGrp);
    }
    
    protected void join(final SQLJoin.JoinType joinType, final SQLTable sourceTable, final JavaTypeMapping sourceMapping, final JavaTypeMapping sourceParentMapping, final SQLTable targetTable, final JavaTypeMapping targetMapping, final JavaTypeMapping targetParentMapping, final Object[] discrimValues) {
        if (this.tables == null) {
            throw new NucleusException("tables not set in statement!");
        }
        if (this.tables.containsValue(targetTable)) {
            NucleusLogger.DATASTORE.debug("Attempt to join to " + targetTable + " but join already exists");
            return;
        }
        this.tables.put(targetTable.alias.getIdentifierName(), targetTable);
        final BooleanExpression joinCondition = this.getJoinConditionForJoin(sourceTable, sourceMapping, sourceParentMapping, targetTable, targetMapping, targetParentMapping, discrimValues);
        if (this.rdbmsMgr.getDatastoreAdapter().supportsOption("ANSI_Join_Syntax")) {
            final SQLJoin join = new SQLJoin(joinType, targetTable, sourceTable, joinCondition);
            if (this.joins == null) {
                this.joins = new ArrayList<SQLJoin>();
            }
            this.joins.add(join);
        }
        else {
            final SQLJoin join = new SQLJoin(SQLJoin.JoinType.NON_ANSI_JOIN, targetTable, sourceTable, null);
            if (this.joins == null) {
                this.joins = new ArrayList<SQLJoin>();
            }
            this.joins.add(join);
            this.whereAnd(joinCondition, false);
        }
    }
    
    protected BooleanExpression getJoinConditionForJoin(final SQLTable sourceTable, final JavaTypeMapping sourceMapping, final JavaTypeMapping sourceParentMapping, final SQLTable targetTable, final JavaTypeMapping targetMapping, final JavaTypeMapping targetParentMapping, final Object[] discrimValues) {
        BooleanExpression joinCondition = null;
        if (sourceMapping != null && targetMapping != null) {
            if (sourceMapping.getNumberOfDatastoreMappings() != targetMapping.getNumberOfDatastoreMappings()) {
                throw new NucleusException("Cannot join from " + sourceMapping + " to " + targetMapping + " since they have different numbers of datastore columns!");
            }
            final SQLExpressionFactory factory = this.rdbmsMgr.getSQLExpressionFactory();
            SQLExpression sourceExpr = null;
            if (sourceParentMapping == null) {
                sourceExpr = factory.newExpression(this, (sourceTable != null) ? sourceTable : this.primaryTable, sourceMapping);
            }
            else {
                sourceExpr = factory.newExpression(this, (sourceTable != null) ? sourceTable : this.primaryTable, sourceMapping, sourceParentMapping);
            }
            SQLExpression targetExpr = null;
            if (targetParentMapping == null) {
                targetExpr = factory.newExpression(this, targetTable, targetMapping);
            }
            else {
                targetExpr = factory.newExpression(this, targetTable, targetMapping, targetParentMapping);
            }
            joinCondition = sourceExpr.eq(targetExpr);
            final JavaTypeMapping discrimMapping = targetTable.getTable().getDiscriminatorMapping(false);
            if (discrimMapping != null && discrimValues != null) {
                final SQLExpression discrimExpr = factory.newExpression(this, targetTable, discrimMapping);
                BooleanExpression discrimCondition = null;
                for (int i = 0; i < discrimValues.length; ++i) {
                    final SQLExpression discrimVal = factory.newLiteral(this, discrimMapping, discrimValues[i]);
                    final BooleanExpression condition = discrimExpr.eq(discrimVal);
                    if (discrimCondition == null) {
                        discrimCondition = condition;
                    }
                    else {
                        discrimCondition = discrimCondition.ior(condition);
                    }
                }
                discrimCondition.encloseInParentheses();
                joinCondition = joinCondition.and(discrimCondition);
            }
        }
        return joinCondition;
    }
    
    protected synchronized String generateTableAlias(final Table tbl, final String groupName) {
        String namingSchema = null;
        if (this.extensions != null) {
            namingSchema = this.extensions.get("datanucleus.sqlTableNamingStrategy");
        }
        if (namingSchema == null) {
            namingSchema = "alpha-scheme";
        }
        SQLTableNamer namer = SQLStatement.tableNamerByName.get(namingSchema);
        if (namer == null) {
            try {
                namer = (SQLTableNamer)this.rdbmsMgr.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store.rdbms.sql_tablenamer", "name", namingSchema, "class", null, null);
            }
            catch (Exception e) {
                throw new NucleusException("Attempt to find/instantiate SQL table namer " + namingSchema + " threw an exception", e);
            }
            SQLStatement.tableNamerByName.put(namingSchema, namer);
        }
        return namer.getAliasForTable(this, tbl, groupName);
    }
    
    public void whereAnd(final BooleanExpression expr, final boolean applyToUnions) {
        this.invalidateStatement();
        if (expr instanceof BooleanLiteral && !expr.isParameter() && (boolean)((BooleanLiteral)expr).getValue()) {
            return;
        }
        if (this.where == null) {
            this.where = expr;
        }
        else {
            this.where = this.where.and(expr);
        }
        if (this.unions != null && applyToUnions) {
            for (final SQLStatement stmt : this.unions) {
                stmt.whereAnd(expr, true);
            }
        }
    }
    
    public void whereOr(final BooleanExpression expr, final boolean applyToUnions) {
        this.invalidateStatement();
        if (this.where == null) {
            this.where = expr;
        }
        else {
            this.where = this.where.ior(expr);
        }
        if (this.unions != null && applyToUnions) {
            for (final SQLStatement stmt : this.unions) {
                stmt.whereOr(expr, true);
            }
        }
    }
    
    public void addGroupingExpression(final SQLExpression expr) {
        this.invalidateStatement();
        if (this.groupingExpressions == null) {
            this.groupingExpressions = new ArrayList<SQLExpression>();
        }
        this.groupingExpressions.add(expr);
        this.aggregated = true;
        if (this.unions != null) {
            final Iterator<SQLStatement> i = this.unions.iterator();
            while (i.hasNext()) {
                i.next().addGroupingExpression(expr);
            }
        }
    }
    
    public void setHaving(final BooleanExpression expr) {
        this.invalidateStatement();
        this.having = expr;
        this.aggregated = true;
        if (this.unions != null) {
            final Iterator<SQLStatement> i = this.unions.iterator();
            while (i.hasNext()) {
                i.next().setHaving(expr);
            }
        }
    }
    
    public void setOrdering(final SQLExpression[] exprs, final boolean[] descending) {
        this.setOrdering(exprs, descending, null);
    }
    
    public void setOrdering(final SQLExpression[] exprs, final boolean[] descending, final String[] nullOrders) {
        if (exprs.length != descending.length) {
            throw new NucleusException(SQLStatement.LOCALISER.msg("052503", "" + exprs.length, "" + descending.length)).setFatal();
        }
        this.invalidateStatement();
        this.orderingExpressions = exprs;
        this.orderingDirections = descending;
        this.orderNullDirectives = nullOrders;
    }
    
    public void setRange(final long offset, final long count) {
        this.invalidateStatement();
        this.rangeOffset = offset;
        this.rangeCount = count;
    }
    
    public synchronized SQLText getSelectStatement() {
        if (this.sql != null) {
            return this.sql;
        }
        final DatastoreAdapter dba = this.getDatastoreAdapter();
        boolean lock = false;
        final Boolean val = (Boolean)this.getValueForExtension("lock-for-update");
        if (val != null) {
            lock = val;
        }
        boolean addAliasToAllSelects = false;
        if ((this.rangeOffset > 0L || this.rangeCount > -1L) && dba.getRangeByRowNumberColumn2().length() > 0) {
            addAliasToAllSelects = true;
        }
        this.sql = new SQLText("SELECT ");
        if (this.distinct) {
            this.sql.append("DISTINCT ");
        }
        this.addOrderingColumnsToSelect();
        if (this.selects.isEmpty()) {
            this.sql.append("*");
        }
        else {
            int autoAliasNum = 0;
            final Iterator<String> selectIter = this.selects.iterator();
            while (selectIter.hasNext()) {
                String selected = selectIter.next();
                if (addAliasToAllSelects && selected.indexOf(" AS ") < 0) {
                    selected = selected + " AS DN_" + autoAliasNum;
                    ++autoAliasNum;
                }
                this.sql.append(selected);
                if (selectIter.hasNext()) {
                    this.sql.append(',');
                }
            }
            if ((this.rangeOffset > -1L || this.rangeCount > -1L) && dba.getRangeByRowNumberColumn().length() > 0) {
                this.sql.append(',').append(dba.getRangeByRowNumberColumn()).append(" rn");
            }
        }
        this.sql.append(" FROM ");
        this.sql.append(this.primaryTable.toString());
        if (lock && dba.supportsOption("LockOptionAfterFromClause")) {
            this.sql.append(" WITH ").append(dba.getSelectWithLockOption());
        }
        if (this.joins != null) {
            this.sql.append(this.getSqlForJoins(lock));
        }
        if (this.where != null) {
            this.sql.append(" WHERE ").append(this.where.toSQLText());
        }
        if (this.groupingExpressions != null) {
            final List groupBy = new ArrayList();
            for (final SQLExpression expr : this.groupingExpressions) {
                final String exprText = expr.toSQLText().toSQL();
                if (!groupBy.contains(exprText)) {
                    groupBy.add(exprText);
                }
            }
            if (groupBy.size() > 0 && this.aggregated) {
                this.sql.append(" GROUP BY ");
                for (int i = 0; i < groupBy.size(); ++i) {
                    if (i > 0) {
                        this.sql.append(',');
                    }
                    this.sql.append(groupBy.get(i));
                }
            }
        }
        if (this.having != null) {
            this.sql.append(" HAVING ").append(this.having.toSQLText());
        }
        if (this.unions != null) {
            if (!dba.supportsOption("Union_Syntax")) {
                throw new NucleusException(SQLStatement.LOCALISER.msg("052504", "UNION")).setFatal();
            }
            final Iterator<SQLStatement> unionIter = this.unions.iterator();
            while (unionIter.hasNext()) {
                if (dba.supportsOption("UseUnionAll")) {
                    this.sql.append(" UNION ALL ");
                }
                else {
                    this.sql.append(" UNION ");
                }
                final SQLStatement stmt = unionIter.next();
                final SQLText unionSql = stmt.getSelectStatement();
                this.sql.append(unionSql);
            }
        }
        final SQLText orderStmt = this.generateOrderingStatement();
        if (orderStmt != null) {
            this.sql.append(" ORDER BY ").append(orderStmt);
        }
        if (this.rangeOffset > -1L || this.rangeCount > -1L) {
            final String limitClause = dba.getRangeByLimitEndOfStatementClause(this.rangeOffset, this.rangeCount);
            if (limitClause.length() > 0) {
                this.sql.append(" ").append(limitClause);
            }
        }
        if (lock && dba.supportsOption("LockWithSelectForUpdate")) {
            if (this.distinct && !dba.supportsOption("DistinctWithSelectForUpdate")) {
                NucleusLogger.QUERY.warn(SQLStatement.LOCALISER.msg("052502"));
            }
            else {
                this.sql.append(" " + dba.getSelectForUpdateText());
            }
        }
        if (lock && !dba.supportsOption("LockWithSelectForUpdate") && !dba.supportsOption("LockOptionAfterFromClause") && !dba.supportsOption("LockOptionWithinJoinClause")) {
            NucleusLogger.QUERY.warn("Requested locking of query statement, but this RDBMS doesn't support a convenient mechanism");
        }
        if (this.rangeOffset > 0L || this.rangeCount > -1L) {
            if (dba.getRangeByRowNumberColumn2().length() > 0) {
                final SQLText userSql = this.sql;
                final SQLText innerSql = new SQLText("SELECT subq.*");
                innerSql.append(',').append(dba.getRangeByRowNumberColumn2()).append(" rn");
                innerSql.append(" FROM (").append(userSql).append(") subq ");
                final SQLText outerSql = new SQLText("SELECT * FROM (").append(innerSql).append(") ");
                outerSql.append("WHERE ");
                if (this.rangeOffset > 0L) {
                    outerSql.append("rn > " + this.rangeOffset);
                    if (this.rangeCount > -1L) {
                        outerSql.append(" AND rn <= " + (this.rangeCount + this.rangeOffset));
                    }
                }
                else {
                    outerSql.append(" rn <= " + this.rangeCount);
                }
                this.sql = outerSql;
            }
            else if (dba.getRangeByRowNumberColumn().length() > 0) {
                final SQLText userSql = this.sql;
                this.sql = new SQLText("SELECT ");
                final Iterator<String> selectIter2 = this.selects.iterator();
                while (selectIter2.hasNext()) {
                    final String selectExpr = selectIter2.next();
                    this.sql.append("subq.");
                    String selectedCol = selectExpr;
                    final int dotIndex = selectedCol.indexOf(" AS ");
                    if (dotIndex > 0) {
                        selectedCol = selectedCol.substring(dotIndex + 4);
                    }
                    this.sql.append(selectedCol);
                    if (selectIter2.hasNext()) {
                        this.sql.append(',');
                    }
                }
                this.sql.append(" FROM (").append(userSql).append(") subq WHERE ");
                if (this.rangeOffset > 0L) {
                    this.sql.append("subq.rn").append(">").append("" + this.rangeOffset);
                }
                if (this.rangeCount > 0L) {
                    if (this.rangeOffset > 0L) {
                        this.sql.append(" AND ");
                    }
                    this.sql.append("subq.rn").append("<=").append("" + (this.rangeCount + this.rangeOffset));
                }
            }
        }
        return this.sql;
    }
    
    private void reorderJoins(final List knownJoins, final List joinsToAdd) {
        if (joinsToAdd == null) {
            this.requiresJoinReorder = false;
            return;
        }
        while (joinsToAdd.size() > 0) {
            final Iterator<SQLJoin> joinIter = joinsToAdd.iterator();
            final int origSize = joinsToAdd.size();
            while (joinIter.hasNext()) {
                final SQLJoin join = joinIter.next();
                if (join.getType() == SQLJoin.JoinType.CROSS_JOIN) {
                    knownJoins.add(join);
                    joinIter.remove();
                }
                else if (join.getType() == SQLJoin.JoinType.NON_ANSI_JOIN) {
                    knownJoins.add(join);
                    joinIter.remove();
                }
                else if (join.getJoinedTable().equals(this.primaryTable)) {
                    knownJoins.add(join);
                    joinIter.remove();
                }
                else {
                    final Iterator<SQLJoin> knownJoinIter = knownJoins.iterator();
                    boolean valid = false;
                    while (knownJoinIter.hasNext()) {
                        final SQLJoin currentJoin = knownJoinIter.next();
                        if (join.getJoinedTable().equals(currentJoin.getTable())) {
                            valid = true;
                            break;
                        }
                    }
                    if (!valid) {
                        continue;
                    }
                    knownJoins.add(join);
                    joinIter.remove();
                }
            }
            if (joinsToAdd.size() == origSize) {
                throw new NucleusException("Unable to reorder joins for SQL statement since circular! Consider reordering the components in the WHERE clause : affected joins - " + StringUtils.collectionToString(joinsToAdd));
            }
        }
        this.requiresJoinReorder = false;
    }
    
    private String getSqlForJoins(final boolean lock) {
        final StringBuffer sql = new StringBuffer();
        final DatastoreAdapter dba = this.getDatastoreAdapter();
        if (this.requiresJoinReorder) {
            final List<SQLJoin> theJoins = new ArrayList<SQLJoin>(this.joins.size());
            this.reorderJoins(theJoins, this.joins);
            this.joins = theJoins;
        }
        for (final SQLJoin join : this.joins) {
            if (join.getType() == SQLJoin.JoinType.CROSS_JOIN) {
                if (dba.supportsOption("ANSI_CrossJoin_Syntax")) {
                    sql.append(" ");
                    sql.append(join.toFromClause(dba, lock));
                }
                else if (dba.supportsOption("ANSI_CrossJoinAsInner11_Syntax")) {
                    sql.append(" INNER JOIN " + join.getTable() + " ON 1=1");
                }
                else {
                    sql.append(",");
                    sql.append(join.getTable().toString());
                }
            }
            else if (dba.supportsOption("ANSI_Join_Syntax")) {
                sql.append(" ");
                sql.append(join.toFromClause(dba, lock));
            }
            else {
                sql.append(",");
                sql.append(join.toFromClause(dba, lock));
            }
        }
        return sql.toString();
    }
    
    public synchronized SQLText getUpdateStatement() {
        if (this.sql != null) {
            return this.sql;
        }
        final SQLText setSQL = new SQLText("SET ");
        if (this.updates != null && this.updates.length > 0) {
            for (int i = 0; i < this.updates.length; ++i) {
                if (this.updates[i] != null) {
                    if (i != 0) {
                        setSQL.append(",");
                    }
                    setSQL.append(this.updates[i].toSQLText());
                }
            }
        }
        this.sql = this.rdbmsMgr.getDatastoreAdapter().getUpdateTableStatement(this.primaryTable, setSQL);
        if (this.joins != null) {
            final Iterator<SQLJoin> joinIter = this.joins.iterator();
            final SQLJoin subJoin = joinIter.next();
            final SQLStatement subStmt = new SQLStatement(this, this.rdbmsMgr, subJoin.getTable().getTable(), subJoin.getTable().getAlias(), subJoin.getTable().getGroupName());
            subStmt.whereAnd(subJoin.getCondition(), false);
            if (this.where != null) {
                subStmt.whereAnd(this.where, false);
            }
            while (joinIter.hasNext()) {
                final SQLJoin join = joinIter.next();
                subStmt.joins.add(join);
            }
            final BooleanExpression existsExpr = new BooleanSubqueryExpression(this, "EXISTS", subStmt);
            this.where = existsExpr;
        }
        if (this.where != null) {
            this.sql.append(" WHERE ").append(this.where.toSQLText());
        }
        return this.sql;
    }
    
    public synchronized SQLText getDeleteStatement() {
        if (this.sql != null) {
            return this.sql;
        }
        this.sql = new SQLText(this.rdbmsMgr.getDatastoreAdapter().getDeleteTableStatement(this.primaryTable));
        if (this.joins != null) {
            final Iterator<SQLJoin> joinIter = this.joins.iterator();
            final SQLJoin subJoin = joinIter.next();
            final SQLStatement subStmt = new SQLStatement(this, this.rdbmsMgr, subJoin.getTable().getTable(), subJoin.getTable().getAlias(), subJoin.getTable().getGroupName());
            subStmt.whereAnd(subJoin.getCondition(), false);
            if (this.where != null) {
                subStmt.whereAnd(this.where, false);
            }
            while (joinIter.hasNext()) {
                final SQLJoin join = joinIter.next();
                subStmt.joins.add(join);
            }
            final BooleanExpression existsExpr = new BooleanSubqueryExpression(this, "EXISTS", subStmt);
            this.where = existsExpr;
        }
        if (this.where != null) {
            this.sql.append(" WHERE ").append(this.where.toSQLText());
        }
        return this.sql;
    }
    
    protected SQLText generateOrderingStatement() {
        SQLText orderStmt = null;
        if (this.orderingExpressions != null && this.orderingExpressions.length > 0) {
            final DatastoreAdapter dba = this.getDatastoreAdapter();
            if (dba.supportsOption("OrderByUsingSelectColumnIndex")) {
                orderStmt = new SQLText();
                for (int i = 0; i < this.orderingExpressions.length; ++i) {
                    if (i > 0) {
                        orderStmt.append(',');
                    }
                    orderStmt.append(Integer.toString(this.orderingColumnIndexes[i]));
                    if (this.orderingDirections[i]) {
                        orderStmt.append(" DESC");
                    }
                    if (this.orderNullDirectives != null && this.orderNullDirectives[i] != null) {
                        orderStmt.append(" " + this.orderNullDirectives[i]);
                    }
                }
            }
            else {
                orderStmt = new SQLText();
                final boolean needsSelect = dba.supportsOption("IncludeOrderByColumnsInSelect");
                for (int j = 0; j < this.orderingExpressions.length; ++j) {
                    if (j > 0) {
                        orderStmt.append(',');
                    }
                    if (needsSelect && !this.aggregated) {
                        final String orderString = "NUCORDER" + j;
                        if (this.orderingExpressions[j].getNumberOfSubExpressions() == 1) {
                            orderStmt.append(dba.getOrderString(this.rdbmsMgr, orderString, this.orderingExpressions[j]));
                        }
                        else {
                            final JavaTypeMapping m = this.orderingExpressions[j].getJavaTypeMapping();
                            final DatastoreMapping[] mappings = m.getDatastoreMappings();
                            for (int k = 0; k < mappings.length; ++k) {
                                final String alias = orderString + "_" + k;
                                orderStmt.append(dba.getOrderString(this.rdbmsMgr, alias, this.orderingExpressions[j]));
                                if (k < mappings.length - 1) {
                                    orderStmt.append(',');
                                }
                            }
                        }
                    }
                    else {
                        final String orderString = this.orderingExpressions[j].toSQLText().toSQL();
                        orderStmt.append(dba.getOrderString(this.rdbmsMgr, orderString, this.orderingExpressions[j]));
                    }
                    if (this.orderingDirections[j]) {
                        orderStmt.append(" DESC");
                    }
                    if (this.orderNullDirectives != null && this.orderNullDirectives[j] != null) {
                        orderStmt.append(" " + this.orderNullDirectives[j]);
                    }
                }
            }
        }
        return orderStmt;
    }
    
    protected void addOrderingColumnsToSelect() {
        if (this.orderingExpressions != null) {
            final DatastoreAdapter dba = this.getDatastoreAdapter();
            if (dba.supportsOption("OrderByUsingSelectColumnIndex")) {
                this.orderingColumnIndexes = new int[this.orderingExpressions.length];
                for (int i = 0; i < this.orderingExpressions.length; ++i) {
                    this.selects.add(this.orderingExpressions[i].toSQLText().toString());
                    this.orderingColumnIndexes[i] = this.selects.size();
                    if (this.unions != null) {
                        final Iterator<SQLStatement> iterator = this.unions.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().selectSQLExpressionInternal(this.orderingExpressions[i], null);
                        }
                    }
                }
            }
            else if (dba.supportsOption("IncludeOrderByColumnsInSelect")) {
                for (int i = 0; i < this.orderingExpressions.length; ++i) {
                    final String orderExpr = "NUCORDER" + i;
                    if (this.orderingExpressions[i].getNumberOfSubExpressions() == 1 || this.aggregated) {
                        if (this.unions != null) {
                            for (final SQLStatement stmt : this.unions) {
                                if (this.aggregated) {
                                    stmt.selectSQLExpressionInternal(this.orderingExpressions[i], null);
                                }
                                else {
                                    stmt.selectSQLExpressionInternal(this.orderingExpressions[i], orderExpr);
                                }
                            }
                        }
                        if (this.aggregated) {
                            this.selectSQLExpressionInternal(this.orderingExpressions[i], null);
                        }
                        else {
                            this.selectSQLExpressionInternal(this.orderingExpressions[i], orderExpr);
                        }
                    }
                    else {
                        final JavaTypeMapping m = this.orderingExpressions[i].getJavaTypeMapping();
                        final DatastoreMapping[] mappings = m.getDatastoreMappings();
                        for (int j = 0; j < mappings.length; ++j) {
                            final String alias = orderExpr + "_" + j;
                            final DatastoreIdentifier aliasId = this.rdbmsMgr.getIdentifierFactory().newColumnIdentifier(alias);
                            final SQLColumn col = new SQLColumn(this.orderingExpressions[i].getSQLTable(), mappings[j].getColumn(), aliasId);
                            final String selectedName = col.toString();
                            this.selectItem(selectedName);
                            if (this.unions != null) {
                                for (final SQLStatement stmt2 : this.unions) {
                                    stmt2.selectItem(selectedName);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected int selectSQLExpressionInternal(final SQLExpression expr, final String alias) {
        String exprStr = expr.toSQLText().toSQL();
        if (alias != null) {
            exprStr = exprStr + " AS " + alias;
        }
        return this.selectItem(exprStr);
    }
    
    protected void invalidateStatement() {
        this.sql = null;
    }
    
    public void log(final NucleusLogger logger) {
        logger.debug("SQLStatement : " + this.getSelectStatement().toSQL());
        for (final String grpName : this.tableGroups.keySet()) {
            logger.debug("SQLStatement : TableGroup=" + this.tableGroups.get(grpName));
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
        tableNamerByName = new HashMap<String, SQLTableNamer>();
    }
}
