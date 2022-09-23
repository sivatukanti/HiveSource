// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import java.util.Collections;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.compile.Visitable;
import java.util.Arrays;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import java.util.HashSet;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import java.util.Comparator;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import java.util.List;

public class CreateTriggerNode extends DDLStatementNode
{
    private TableName triggerName;
    private TableName tableName;
    private int triggerEventMask;
    private ResultColumnList triggerCols;
    private boolean isBefore;
    private boolean isRow;
    private boolean isEnabled;
    private List refClause;
    private ValueNode whenClause;
    private String whenText;
    private int whenOffset;
    private StatementNode actionNode;
    private String actionText;
    private String originalActionText;
    private int actionOffset;
    private SchemaDescriptor triggerSchemaDescriptor;
    private SchemaDescriptor compSchemaDescriptor;
    private int[] referencedColInts;
    private int[] referencedColsInTriggerAction;
    private TableDescriptor triggerTableDescriptor;
    private UUID actionCompSchemaId;
    private String oldTableName;
    private String newTableName;
    private boolean oldTableInReferencingClause;
    private boolean newTableInReferencingClause;
    private static final Comparator OFFSET_COMPARATOR;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10, final Object o11, final Object o12, final Object o13, final Object o14) throws StandardException {
        this.initAndCheck(o);
        this.triggerName = (TableName)o;
        this.tableName = (TableName)o2;
        this.triggerEventMask = (int)o3;
        this.triggerCols = (ResultColumnList)o4;
        this.isBefore = (boolean)o5;
        this.isRow = (boolean)o6;
        this.isEnabled = (boolean)o7;
        this.refClause = (List)o8;
        this.whenClause = (ValueNode)o9;
        this.whenText = ((o10 == null) ? null : ((String)o10).trim());
        this.whenOffset = (int)o11;
        this.actionNode = (StatementNode)o12;
        this.originalActionText = (String)o13;
        this.actionText = ((o13 == null) ? null : ((String)o13).trim());
        this.actionOffset = (int)o14;
        this.implicitCreateSchema = true;
    }
    
    public String statementToString() {
        return "CREATE TRIGGER";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public void bindStatement() throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        final DataDictionary dataDictionary = this.getDataDictionary();
        final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
        this.compSchemaDescriptor = languageConnectionContext.getDefaultSchema();
        this.triggerSchemaDescriptor = this.getSchemaDescriptor();
        this.triggerTableDescriptor = this.getTableDescriptor(this.tableName);
        if (this.isSessionSchema(this.triggerTableDescriptor.getSchemaDescriptor())) {
            throw StandardException.newException("XCL51.S");
        }
        if (this.isPrivilegeCollectionRequired()) {
            compilerContext.pushCurrentPrivType(5);
            compilerContext.addRequiredTablePriv(this.triggerTableDescriptor);
            compilerContext.popCurrentPrivType();
        }
        final boolean bindReferencesClause = this.bindReferencesClause(dataDictionary);
        languageConnectionContext.pushTriggerTable(this.triggerTableDescriptor);
        try {
            if (bindReferencesClause) {
                compilerContext.setReliability(0);
            }
            if (this.isBefore) {
                compilerContext.setReliability(2048);
            }
            this.actionNode.bindStatement();
        }
        finally {
            languageConnectionContext.popTriggerTable(this.triggerTableDescriptor);
        }
        compilerContext.createDependency(this.triggerTableDescriptor);
        if (this.triggerCols != null && this.triggerCols.size() != 0) {
            final HashSet<String> set = new HashSet<String>();
            for (int size = this.triggerCols.size(), i = 0; i < size; ++i) {
                final ResultColumn resultColumn = (ResultColumn)this.triggerCols.elementAt(i);
                if (!set.add(resultColumn.getName())) {
                    throw StandardException.newException("42Y40", resultColumn.getName(), this.triggerName);
                }
                if (this.triggerTableDescriptor.getColumnDescriptor(resultColumn.getName()) == null) {
                    throw StandardException.newException("42X14", resultColumn.getName(), this.tableName);
                }
            }
        }
        if (this.actionNode.referencesSessionSchema()) {
            throw StandardException.newException("XCL51.S");
        }
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.isSessionSchema(this.triggerTableDescriptor.getSchemaName()) || this.actionNode.referencesSessionSchema();
    }
    
    private boolean bindReferencesClause(final DataDictionary dataDictionary) throws StandardException {
        this.validateReferencesClause(dataDictionary);
        if (this.isBefore) {
            this.forbidActionsOnGenCols();
        }
        int n = 0;
        if (this.triggerCols != null && this.triggerCols.size() != 0) {
            this.referencedColInts = new int[this.triggerCols.size()];
            for (int i = 0; i < this.triggerCols.size(); ++i) {
                final ResultColumn resultColumn = (ResultColumn)this.triggerCols.elementAt(i);
                final ColumnDescriptor columnDescriptor = this.triggerTableDescriptor.getColumnDescriptor(resultColumn.getName());
                if (columnDescriptor == null) {
                    throw StandardException.newException("42X14", resultColumn.getName(), this.tableName);
                }
                this.referencedColInts[i] = columnDescriptor.getPosition();
            }
            Arrays.sort(this.referencedColInts);
        }
        String actionText;
        if (this.isRow) {
            Arrays.fill(this.referencedColsInTriggerAction = new int[this.triggerTableDescriptor.getNumberOfColumns()], -1);
            actionText = this.getDataDictionary().getTriggerActionString(this.actionNode, this.oldTableName, this.newTableName, this.originalActionText, this.referencedColInts, this.referencedColsInTriggerAction, this.actionOffset, this.triggerTableDescriptor, this.triggerEventMask, true);
            this.referencedColsInTriggerAction = this.justTheRequiredColumns(this.referencedColsInTriggerAction);
        }
        else {
            final StringBuffer sb = new StringBuffer();
            final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(FromBaseTable.class);
            this.actionNode.accept(collectNodesVisitor);
            final List list = collectNodesVisitor.getList();
            Collections.sort((List<Object>)list, CreateTriggerNode.OFFSET_COMPARATOR);
            for (int j = 0; j < list.size(); ++j) {
                final FromBaseTable fromBaseTable = list.get(j);
                final String baseTableName = fromBaseTable.getBaseTableName();
                if (baseTableName != null) {
                    if (this.oldTableName == null || !this.oldTableName.equals(baseTableName)) {
                        if (this.newTableName == null) {
                            continue;
                        }
                        if (!this.newTableName.equals(baseTableName)) {
                            continue;
                        }
                    }
                    final int beginOffset = fromBaseTable.getTableNameField().getBeginOffset();
                    final int endOffset = fromBaseTable.getTableNameField().getEndOffset();
                    if (beginOffset != -1) {
                        this.checkInvalidTriggerReference(baseTableName);
                        sb.append(this.originalActionText.substring(n, beginOffset - this.actionOffset));
                        sb.append(baseTableName.equals(this.oldTableName) ? "new org.apache.derby.catalog.TriggerOldTransitionRows() " : "new org.apache.derby.catalog.TriggerNewTransitionRows() ");
                        if (fromBaseTable.getCorrelationName() == null) {
                            sb.append(baseTableName).append(" ");
                        }
                        n = endOffset - this.actionOffset + 1;
                    }
                }
            }
            if (n < this.originalActionText.length()) {
                sb.append(this.originalActionText.substring(n));
            }
            actionText = sb.toString();
        }
        if (this.referencedColsInTriggerAction != null) {
            Arrays.sort(this.referencedColsInTriggerAction);
        }
        boolean b = false;
        if (!actionText.equals(this.actionText)) {
            b = true;
            this.actionText = actionText;
            this.actionNode = this.parseStatement(this.actionText, true);
        }
        return b;
    }
    
    private int[] justTheRequiredColumns(final int[] array) {
        int n = 0;
        final int numberOfColumns = this.triggerTableDescriptor.getNumberOfColumns();
        for (int i = 0; i < numberOfColumns; ++i) {
            if (array[i] != -1) {
                ++n;
            }
        }
        if (n > 0) {
            final int[] array2 = new int[n];
            int n2 = 0;
            for (int j = 0; j < numberOfColumns; ++j) {
                if (array[j] != -1) {
                    array2[n2++] = array[j];
                }
            }
            return array2;
        }
        return null;
    }
    
    private void forbidActionsOnGenCols() throws StandardException {
        final ColumnDescriptorList generatedColumns = this.triggerTableDescriptor.getGeneratedColumns();
        final int size = generatedColumns.size();
        if (size == 0) {
            return;
        }
        final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(ColumnReference.class);
        this.actionNode.accept(collectNodesVisitor);
        final List list = collectNodesVisitor.getList();
        for (int size2 = list.size(), i = 0; i < size2; ++i) {
            final ColumnReference columnReference = list.get(i);
            final String columnName = columnReference.getColumnName();
            final String tableName = columnReference.getTableName();
            for (int j = 0; j < size; ++j) {
                final String columnName2 = generatedColumns.elementAt(j).getColumnName();
                if (columnName2.equals(columnName) && this.equals(this.newTableName, tableName)) {
                    throw StandardException.newException("42XAA", columnName2);
                }
            }
        }
    }
    
    private boolean equals(final String s, final String anObject) {
        if (s == null) {
            return anObject == null;
        }
        return s.equals(anObject);
    }
    
    private void checkInvalidTriggerReference(final String s) throws StandardException {
        if (s.equals(this.oldTableName) && (this.triggerEventMask & 0x4) == 0x4) {
            throw StandardException.newException("42Y92", "INSERT", "new");
        }
        if (s.equals(this.newTableName) && (this.triggerEventMask & 0x2) == 0x2) {
            throw StandardException.newException("42Y92", "DELETE", "old");
        }
    }
    
    private void validateReferencesClause(final DataDictionary dataDictionary) throws StandardException {
        if (this.refClause == null || this.refClause.isEmpty()) {
            return;
        }
        for (final TriggerReferencingStruct triggerReferencingStruct : this.refClause) {
            if (this.isRow && !triggerReferencingStruct.isRow) {
                throw StandardException.newException("42Y92", "ROW", "row");
            }
            if (!this.isRow && triggerReferencingStruct.isRow) {
                throw StandardException.newException("42Y92", "STATEMENT", "table");
            }
            if (triggerReferencingStruct.isNew) {
                if (this.newTableInReferencingClause) {
                    throw StandardException.newException("42Y93");
                }
                if ((this.triggerEventMask & 0x2) == 0x2) {
                    throw StandardException.newException("42Y92", "DELETE", "old");
                }
                this.newTableName = triggerReferencingStruct.identifier;
                this.newTableInReferencingClause = true;
            }
            else {
                if (this.oldTableInReferencingClause) {
                    throw StandardException.newException("42Y93");
                }
                if ((this.triggerEventMask & 0x4) == 0x4) {
                    throw StandardException.newException("42Y92", "INSERT", "new");
                }
                this.oldTableName = triggerReferencingStruct.identifier;
                this.oldTableInReferencingClause = true;
            }
            if (this.isBefore && !triggerReferencingStruct.isRow) {
                throw StandardException.newException("42Y92", "BEFORE", "row");
            }
        }
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getCreateTriggerConstantAction(this.triggerSchemaDescriptor.getSchemaName(), this.getRelativeName(), this.triggerEventMask, this.isBefore, this.isRow, this.isEnabled, this.triggerTableDescriptor, null, this.whenText, null, this.actionText, (this.actionCompSchemaId == null) ? this.compSchemaDescriptor.getUUID() : this.actionCompSchemaId, this.referencedColInts, this.referencedColsInTriggerAction, this.originalActionText, this.oldTableInReferencingClause, this.newTableInReferencingClause, this.oldTableInReferencingClause ? this.oldTableName : null, this.newTableInReferencingClause ? this.newTableName : null);
    }
    
    public String toString() {
        return "";
    }
    
    static {
        OFFSET_COMPARATOR = new Comparator() {
            public int compare(final Object o, final Object o2) {
                return ((FromBaseTable)o).getTableNameField().getBeginOffset() - ((FromBaseTable)o2).getTableNameField().getBeginOffset();
            }
        };
    }
}
