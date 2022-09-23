// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.util.JBitSet;
import java.util.Enumeration;
import org.apache.derby.iapi.util.StringUtil;
import java.util.List;
import org.apache.derby.iapi.util.ReuseFactory;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.iapi.sql.compile.OptimizableList;

public class FromList extends QueryTreeNodeVector implements OptimizableList
{
    Properties properties;
    boolean fixedJoinOrder;
    boolean useStatistics;
    private boolean referencesSessionSchema;
    private boolean isTransparent;
    private WindowList windows;
    
    public FromList() {
        this.fixedJoinOrder = true;
        this.useStatistics = true;
    }
    
    public void init(final Object o) {
        this.fixedJoinOrder = !(boolean)o;
        this.isTransparent = false;
    }
    
    public void init(final Object o, final Object o2) throws StandardException {
        this.init(o);
        this.addFromTable((FromTable)o2);
    }
    
    public Optimizable getOptimizable(final int n) {
        return (Optimizable)this.elementAt(n);
    }
    
    public void setOptimizable(final int n, final Optimizable optimizable) {
        this.setElementAt((QueryTreeNode)optimizable, n);
    }
    
    public void verifyProperties(final DataDictionary dataDictionary) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((Optimizable)this.elementAt(i)).verifyProperties(dataDictionary);
        }
    }
    
    public void addFromTable(final FromTable fromTable) throws StandardException {
        if (!(fromTable instanceof TableOperatorNode)) {
            for (int size = this.size(), i = 0; i < size; ++i) {
                final TableName tableName = fromTable.getTableName();
                if (!(((FromTable)this.elementAt(i)) instanceof TableOperatorNode)) {
                    if (tableName.equals(((FromTable)this.elementAt(i)).getTableName())) {
                        throw StandardException.newException("42X09", fromTable.getExposedName());
                    }
                }
            }
        }
        this.addElement(fromTable);
    }
    
    public boolean referencesTarget(final String s, final boolean b) throws StandardException {
        boolean b2 = false;
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((FromTable)this.elementAt(i)).referencesTarget(s, b)) {
                b2 = true;
                break;
            }
        }
        return b2;
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        boolean b = false;
        if (this.referencesSessionSchema) {
            return true;
        }
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((FromTable)this.elementAt(i)).referencesSessionSchema()) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    protected FromTable getFromTableByName(final String s, final String s2, final boolean b) throws StandardException {
        FromTable fromTableByName = null;
        for (int size = this.size(), i = 0; i < size; ++i) {
            fromTableByName = ((FromTable)this.elementAt(i)).getFromTableByName(s, s2, b);
            if (fromTableByName != null) {
                return fromTableByName;
            }
        }
        return fromTableByName;
    }
    
    public void isJoinColumnForRightOuterJoin(final ResultColumn resultColumn) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((FromTable)this.elementAt(i)).isJoinColumnForRightOuterJoin(resultColumn);
        }
    }
    
    public void bindTables(final DataDictionary dataDictionary, final FromList list) throws StandardException {
        final int size = this.size();
        for (int i = 0; i < size; ++i) {
            final FromTable fromTable = (FromTable)this.elementAt(i);
            final ResultSetNode bindNonVTITables = fromTable.bindNonVTITables(dataDictionary, list);
            if (fromTable.referencesSessionSchema()) {
                this.referencesSessionSchema = true;
            }
            this.setElementAt(bindNonVTITables, i);
        }
        for (int j = 0; j < size; ++j) {
            final FromTable fromTable2 = (FromTable)this.elementAt(j);
            final ResultSetNode bindVTITables = fromTable2.bindVTITables(list);
            if (fromTable2.referencesSessionSchema()) {
                this.referencesSessionSchema = true;
            }
            this.setElementAt(bindVTITables, j);
        }
        final CompilerContext compilerContext = this.getCompilerContext();
        compilerContext.pushCurrentPrivType(8);
        for (int k = 0; k < size; ++k) {
            final FromTable fromTable3 = (FromTable)this.elementAt(k);
            if (fromTable3.isPrivilegeCollectionRequired() && fromTable3.isBaseTable() && !fromTable3.forUpdate()) {
                compilerContext.addRequiredColumnPriv(fromTable3.getTableDescriptor().getColumnDescriptor(1));
            }
        }
        compilerContext.popCurrentPrivType();
    }
    
    public void bindExpressions(final FromList list) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((FromTable)this.elementAt(i)).bindExpressions(this.isTransparent ? list : this);
        }
    }
    
    public void bindResultColumns(final FromList list) throws StandardException {
        final int size = list.size();
        for (int size2 = this.size(), i = 0; i < size2; ++i) {
            final FromTable fromTable = (FromTable)this.elementAt(i);
            if (fromTable.needsSpecialRCLBinding()) {
                fromTable.bindResultColumns(list);
            }
            list.insertElementAt(fromTable, 0);
        }
        while (list.size() > size) {
            list.removeElementAt(0);
        }
    }
    
    public ResultColumnList expandAll(final TableName tableName) throws StandardException {
        ResultColumnList list = null;
        final int level = ((FromTable)this.elementAt(0)).getLevel();
        for (int size = this.size(), i = 0; i < size; ++i) {
            final FromTable fromTable = (FromTable)this.elementAt(i);
            if (level != fromTable.getLevel()) {
                break;
            }
            final ResultColumnList allResultColumns = fromTable.getAllResultColumns(tableName);
            if (allResultColumns != null) {
                if (list == null) {
                    list = allResultColumns;
                }
                else {
                    list.nondestructiveAppend(allResultColumns);
                }
                if (tableName != null) {}
            }
        }
        if (list == null) {
            throw StandardException.newException("42X10", tableName);
        }
        return list;
    }
    
    public ResultColumn bindColumnReference(final ColumnReference columnReference) throws StandardException {
        int n = 0;
        boolean b = false;
        int n2 = -1;
        ResultColumn resultColumn = null;
        final String tableName = columnReference.getTableName();
        for (int size = this.size(), i = 0; i < size; ++i) {
            final FromTable fromTable = (FromTable)this.elementAt(i);
            final int level = fromTable.getLevel();
            if (n2 != level) {
                if (n != 0) {
                    break;
                }
                if (b) {
                    break;
                }
            }
            n2 = level;
            final ResultColumn matchingColumn = fromTable.getMatchingColumn(columnReference);
            if (matchingColumn != null) {
                if (n != 0) {
                    throw StandardException.newException("42X03", columnReference.getSQLColumnName());
                }
                resultColumn = matchingColumn;
                columnReference.setSource(matchingColumn);
                columnReference.setNestingLevel(((FromTable)this.elementAt(0)).getLevel());
                columnReference.setSourceLevel(level);
                n = 1;
                if (fromTable.isPrivilegeCollectionRequired()) {
                    this.getCompilerContext().addRequiredColumnPriv(matchingColumn.getTableColumnDescriptor());
                }
            }
            b = (b || (tableName != null && tableName.equals(fromTable.getExposedName())));
        }
        return resultColumn;
    }
    
    public void rejectParameters() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((FromTable)this.elementAt(i)).rejectParameters();
        }
    }
    
    public boolean LOJ_reorderable(final int n) throws StandardException {
        final boolean b = false;
        if (this.size() > 1) {
            return b;
        }
        return ((FromTable)this.elementAt(0)).LOJ_reorderable(n);
    }
    
    public void preprocess(final int n, final GroupByList list, final ValueNode valueNode) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            this.setElementAt(((FromTable)this.elementAt(i)).transformOuterJoins(valueNode, n).preprocess(n, list, this), i);
        }
    }
    
    public void flattenFromTables(final ResultColumnList list, final PredicateList list2, final SubqueryList list3, final GroupByList list4, final ValueNode valueNode) throws StandardException {
        int i = 1;
        final ArrayList list5 = new ArrayList<Integer>();
        while (i != 0) {
            i = 0;
            for (int n = 0; n < this.size() && i == 0; ++n) {
                final FromTable fromTable = (FromTable)this.elementAt(n);
                if (fromTable instanceof FromSubquery || fromTable.isFlattenableJoinNode()) {
                    list5.add(ReuseFactory.getInteger(fromTable.getTableNumber()));
                    final FromList flatten = fromTable.flatten(list, list2, list3, list4, valueNode);
                    if (flatten != null) {
                        this.setElementAt(flatten.elementAt(0), n);
                        for (int size = flatten.size(), j = 1; j < size; ++j) {
                            this.insertElementAt(flatten.elementAt(j), n + j);
                        }
                    }
                    else {
                        this.removeElementAt(n);
                    }
                    i = 1;
                }
            }
        }
        if (!list5.isEmpty()) {
            for (int k = 0; k < this.size(); ++k) {
                final FromTable fromTable2 = (FromTable)this.elementAt(k);
                if (fromTable2 instanceof ProjectRestrictNode) {
                    final ResultSetNode childResult = ((ProjectRestrictNode)fromTable2).getChildResult();
                    if (childResult instanceof FromBaseTable) {
                        ((FromBaseTable)childResult).clearDependency(list5);
                    }
                }
            }
        }
    }
    
    void pushPredicates(final PredicateList list) throws StandardException {
        list.categorize();
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((FromTable)this.elementAt(i)).pushExpressions(list);
        }
    }
    
    public void setLevel(final int level) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((FromTable)this.elementAt(i)).setLevel(level);
        }
    }
    
    public FromTable getFromTableByResultColumn(final ResultColumn resultColumn) {
        FromTable fromTable = null;
        for (int size = this.size(), i = 0; i < size; ++i) {
            fromTable = (FromTable)this.elementAt(i);
            if (fromTable.getResultColumns().indexOf(resultColumn) != -1) {
                break;
            }
        }
        return fromTable;
    }
    
    public void setProperties(final Properties properties) throws StandardException {
        this.properties = properties;
        final Enumeration<Object> keys = this.properties.keys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            final String s = (String)this.properties.get(key);
            if (key.equals("joinOrder")) {
                if (StringUtil.SQLEqualsIgnoreCase(s, "fixed")) {
                    this.fixedJoinOrder = true;
                }
                else {
                    if (!StringUtil.SQLEqualsIgnoreCase(s, "unfixed")) {
                        throw StandardException.newException("42X17", s);
                    }
                    this.fixedJoinOrder = false;
                }
            }
            else {
                if (!key.equals("useStatistics")) {
                    throw StandardException.newException("42X41", key, s);
                }
                if (StringUtil.SQLEqualsIgnoreCase(s, "true")) {
                    this.useStatistics = true;
                }
                else {
                    if (!StringUtil.SQLEqualsIgnoreCase(s, "false")) {
                        throw StandardException.newException("42X64", s);
                    }
                    this.useStatistics = false;
                }
            }
        }
    }
    
    public void reOrder(final int[] array) {
        final FromTable[] array2 = new FromTable[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (FromTable)this.elementAt(array[i]);
        }
        for (int j = 0; j < array.length; ++j) {
            this.setElementAt(array2[j], j);
        }
    }
    
    public boolean useStatistics() {
        return this.useStatistics;
    }
    
    public boolean optimizeJoinOrder() {
        return !this.fixedJoinOrder;
    }
    
    public boolean legalJoinOrder(final int n) {
        final JBitSet set = new JBitSet(n);
        for (int size = this.size(), i = 0; i < size; ++i) {
            final FromTable fromTable = (FromTable)this.elementAt(i);
            set.or(fromTable.getReferencedTableMap());
            if (!fromTable.legalJoinOrder(set)) {
                return false;
            }
        }
        return true;
    }
    
    public void initAccessPaths(final Optimizer optimizer) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((FromTable)this.elementAt(i)).initAccessPaths(optimizer);
        }
    }
    
    public void bindUntypedNullsToResultColumns(final ResultColumnList list) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((FromTable)this.elementAt(i)).bindUntypedNullsToResultColumns(list);
        }
    }
    
    void decrementLevel(final int n) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final FromTable fromTable = (FromTable)this.elementAt(i);
            fromTable.decrementLevel(n);
            final PredicateList restrictionList = ((ProjectRestrictNode)fromTable).getRestrictionList();
            if (restrictionList != null) {
                restrictionList.decrementLevel(this, n);
            }
        }
    }
    
    boolean returnsAtMostSingleRow(final ResultColumnList list, final ValueNode valueNode, final PredicateList list2, final DataDictionary dataDictionary) throws StandardException {
        boolean b = false;
        ColumnReference columnReference = null;
        final PredicateList list3 = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        for (int size = list2.size(), i = 0; i < size; ++i) {
            list3.addPredicate((Predicate)list2.elementAt(i));
        }
        if (list != null) {
            final ResultColumn resultColumn = (ResultColumn)list.elementAt(0);
            if (resultColumn.getExpression() instanceof ColumnReference) {
                columnReference = (ColumnReference)resultColumn.getExpression();
            }
        }
        final int size2 = this.size();
        for (int j = 0; j < size2; ++j) {
            final FromTable fromTable = (FromTable)this.elementAt(j);
            if (!(fromTable instanceof ProjectRestrictNode)) {
                return false;
            }
            final ProjectRestrictNode projectRestrictNode = (ProjectRestrictNode)fromTable;
            if (!(projectRestrictNode.getChildResult() instanceof FromBaseTable)) {
                return false;
            }
            final FromBaseTable fromBaseTable = (FromBaseTable)projectRestrictNode.getChildResult();
            if (fromBaseTable.getExistsBaseTable()) {
                final int tableNumber = fromBaseTable.getTableNumber();
                for (int k = list3.size() - 1; k >= 0; --k) {
                    for (ValueNode valueNode2 = ((Predicate)list3.elementAt(k)).getAndNode(); valueNode2 instanceof AndNode; valueNode2 = ((AndNode)valueNode2).getRightOperand()) {
                        final AndNode andNode = (AndNode)valueNode2;
                        if (andNode.getLeftOperand().isRelationalOperator()) {
                            if (((RelationalOperator)andNode.getLeftOperand()).getOperator() == 1) {
                                if (andNode.getLeftOperand().getTablesReferenced().get(tableNumber)) {
                                    list3.removeElementAt(k);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        final int[] tableNumbers = this.getTableNumbers();
        final JBitSet[][] array = new JBitSet[size2][size2];
        final boolean[] array2 = new boolean[size2];
        for (int l = 0; l < size2; ++l) {
            final ProjectRestrictNode projectRestrictNode2 = (ProjectRestrictNode)this.elementAt(l);
            final FromBaseTable fromBaseTable2 = (FromBaseTable)projectRestrictNode2.getChildResult();
            if (fromBaseTable2.getExistsBaseTable()) {
                array2[l] = true;
            }
            else {
                final int numberOfColumns = fromBaseTable2.getTableDescriptor().getNumberOfColumns();
                final boolean[] array3 = new boolean[numberOfColumns + 1];
                final int tableNumber2 = fromBaseTable2.getTableNumber();
                boolean b2 = false;
                for (int n = 0; n < size2; ++n) {
                    array[l][n] = new JBitSet(numberOfColumns + 1);
                }
                if (columnReference != null && columnReference.getTableNumber() == tableNumber2) {
                    list.recordColumnReferences(array3, array[l], l);
                    b2 = true;
                }
                if (valueNode != null) {
                    valueNode.checkTopPredicatesForEqualsConditions(tableNumber2, array3, tableNumbers, array[l], b2);
                }
                list3.checkTopPredicatesForEqualsConditions(tableNumber2, array3, tableNumbers, array[l], b2);
                if (projectRestrictNode2.getRestrictionList() != null) {
                    projectRestrictNode2.getRestrictionList().checkTopPredicatesForEqualsConditions(tableNumber2, array3, tableNumbers, array[l], b2);
                }
                if (!fromBaseTable2.supersetOfUniqueIndex(array[l])) {
                    return false;
                }
                if (fromBaseTable2.supersetOfUniqueIndex(array3)) {
                    array2[l] = true;
                    b = true;
                }
            }
        }
        if (b) {
            int n2 = 1;
            while (n2 != 0) {
                n2 = 0;
                for (int n3 = 0; n3 < size2; ++n3) {
                    if (array2[n3]) {
                        for (int n4 = 0; n4 < size2; ++n4) {
                            if (!array2[n4] && array[n4][n3].get(0)) {
                                array2[n4] = true;
                                n2 = 1;
                            }
                        }
                    }
                }
            }
            for (int n5 = 0; n5 < size2; ++n5) {
                if (!array2[n5]) {
                    b = false;
                    break;
                }
            }
        }
        return b;
    }
    
    int[] getTableNumbers() {
        final int size = this.size();
        final int[] array = new int[size];
        for (int i = 0; i < size; ++i) {
            final ProjectRestrictNode projectRestrictNode = (ProjectRestrictNode)this.elementAt(i);
            if (projectRestrictNode.getChildResult() instanceof FromTable) {
                array[i] = ((FromTable)projectRestrictNode.getChildResult()).getTableNumber();
            }
        }
        return array;
    }
    
    void genExistsBaseTables(final JBitSet set, final FromList list, final boolean b) throws StandardException {
        final JBitSet set2 = (JBitSet)set.clone();
        final int size = this.size();
        for (int i = 0; i < size; ++i) {
            final ResultSetNode childResult = ((ProjectRestrictNode)this.elementAt(i)).getChildResult();
            if (childResult instanceof FromTable) {
                set2.clear(((FromTable)childResult).getTableNumber());
            }
        }
        if (set2.getFirstSetBit() == -1) {
            for (int size2 = list.size(), j = 0; j < size2; ++j) {
                set2.or(((FromTable)list.elementAt(j)).getReferencedTableMap());
            }
        }
        for (int k = 0; k < size; ++k) {
            final FromTable fromTable = (FromTable)this.elementAt(k);
            if (fromTable instanceof ProjectRestrictNode) {
                final ProjectRestrictNode projectRestrictNode = (ProjectRestrictNode)fromTable;
                if (projectRestrictNode.getChildResult() instanceof FromBaseTable) {
                    ((FromBaseTable)projectRestrictNode.getChildResult()).setExistsBaseTable(true, (JBitSet)set2.clone(), b);
                }
            }
        }
    }
    
    boolean tableNumberIsNotExists(final int n) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ProjectRestrictNode projectRestrictNode = (ProjectRestrictNode)this.elementAt(i);
            if (projectRestrictNode.getChildResult() instanceof FromTable) {
                final FromTable fromTable = (FromTable)projectRestrictNode.getChildResult();
                if (fromTable.getTableNumber() == n) {
                    return fromTable.isNotExists();
                }
            }
        }
        return false;
    }
    
    public int updateTargetLockMode() {
        return ((ResultSetNode)this.elementAt(0)).updateTargetLockMode();
    }
    
    boolean hashJoinSpecified() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final String userSpecifiedJoinStrategy = ((FromTable)this.elementAt(i)).getUserSpecifiedJoinStrategy();
            if (userSpecifiedJoinStrategy != null && StringUtil.SQLToUpperCase(userSpecifiedJoinStrategy).equals("HASH")) {
                return true;
            }
        }
        return false;
    }
    
    void markAsTransparent() {
        this.isTransparent = true;
    }
    
    public void setWindows(final WindowList windows) {
        this.windows = windows;
    }
    
    public WindowList getWindows() {
        return this.windows;
    }
}
