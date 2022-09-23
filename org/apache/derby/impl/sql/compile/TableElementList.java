// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.Properties;
import org.apache.derby.impl.sql.execute.IndexConstantAction;
import org.apache.derby.impl.sql.execute.ConstraintConstantAction;
import org.apache.derby.impl.sql.execute.ConstraintInfo;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.catalog.types.DefaultInfoImpl;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import java.util.List;
import org.apache.derby.iapi.sql.depend.ProviderList;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.ProviderInfo;
import org.apache.derby.catalog.DefaultInfo;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.impl.sql.execute.ColumnInfo;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;
import org.apache.derby.iapi.sql.depend.Provider;
import java.util.Set;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;

public class TableElementList extends QueryTreeNodeVector
{
    private int numColumns;
    private TableDescriptor td;
    
    public void addTableElement(final TableElementNode tableElementNode) {
        this.addElement(tableElementNode);
        if (tableElementNode instanceof ColumnDefinitionNode || tableElementNode.getElementType() == 7) {
            ++this.numColumns;
        }
    }
    
    void setCollationTypesOnCharacterStringColumns(final SchemaDescriptor schemaDescriptor) throws StandardException {
        final int size = this.size();
        schemaDescriptor.getCollationType();
        for (int i = 0; i < size; ++i) {
            if (((TableElementNode)this.elementAt(i)) instanceof ColumnDefinitionNode) {
                this.setCollationTypeOnCharacterStringColumn(schemaDescriptor, (ColumnDefinitionNode)this.elementAt(i));
            }
        }
    }
    
    void setCollationTypeOnCharacterStringColumn(final SchemaDescriptor schemaDescriptor, final ColumnDefinitionNode columnDefinitionNode) throws StandardException {
        final int collationType = schemaDescriptor.getCollationType();
        final DataTypeDescriptor type = columnDefinitionNode.getType();
        if (type != null) {
            if (type.getTypeId().isStringTypeId()) {
                columnDefinitionNode.setCollationType(collationType);
            }
            return;
        }
        if (columnDefinitionNode.hasGenerationClause()) {
            return;
        }
        throw StandardException.newException("42XA9", columnDefinitionNode.getColumnName());
    }
    
    void validate(final DDLStatementNode ddlStatementNode, final DataDictionary dataDictionary, final TableDescriptor td) throws StandardException {
        this.td = td;
        int n = 0;
        final int size = this.size();
        final HashSet set = new HashSet(size + 2, 0.999f);
        final HashSet set2 = new HashSet(size + 2, 0.999f);
        final ArrayList list = new ArrayList<ConstraintDescriptor>();
        if (td != null) {
            final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(td);
            if (constraintDescriptors != null) {
                for (int i = 0; i < constraintDescriptors.size(); ++i) {
                    final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
                    if (element.getConstraintType() == 2 || element.getConstraintType() == 3) {
                        list.add(element);
                    }
                }
            }
        }
        int tableType = 0;
        if (ddlStatementNode instanceof CreateTableNode) {
            tableType = ((CreateTableNode)ddlStatementNode).tableType;
        }
        for (int j = 0; j < size; ++j) {
            final TableElementNode tableElementNode = (TableElementNode)this.elementAt(j);
            if (tableElementNode instanceof ColumnDefinitionNode) {
                final ColumnDefinitionNode columnDefinitionNode = (ColumnDefinitionNode)this.elementAt(j);
                if (tableType == 3 && (columnDefinitionNode.getType().getTypeId().isLongConcatableTypeId() || columnDefinitionNode.getType().getTypeId().isUserDefinedTypeId())) {
                    throw StandardException.newException("42962", columnDefinitionNode.getColumnName());
                }
                this.checkForDuplicateColumns(ddlStatementNode, set, columnDefinitionNode.getColumnName());
                columnDefinitionNode.checkUserType(td);
                columnDefinitionNode.bindAndValidateDefault(dataDictionary, td);
                columnDefinitionNode.validateAutoincrement(dataDictionary, td, tableType);
                if (tableElementNode instanceof ModifyColumnNode) {
                    final ModifyColumnNode modifyColumnNode = (ModifyColumnNode)columnDefinitionNode;
                    modifyColumnNode.checkExistingConstraints(td);
                    modifyColumnNode.useExistingCollation(td);
                }
                else if (columnDefinitionNode.isAutoincrementColumn()) {
                    ++n;
                }
            }
            else if (tableElementNode.getElementType() == 7) {
                final String name = tableElementNode.getName();
                if (td.getColumnDescriptor(name) == null) {
                    throw StandardException.newException("42X14", name, td.getQualifiedName());
                }
                break;
            }
            if (tableElementNode.hasConstraint()) {
                final ConstraintDefinitionNode e = (ConstraintDefinitionNode)tableElementNode;
                e.bind(ddlStatementNode, dataDictionary);
                if (e.getConstraintType() == 2 || e.getConstraintType() == 3) {
                    Object o = null;
                    String[] array = null;
                    for (int k = 0; k < list.size(); ++k) {
                        final ConstraintDescriptor value = list.get(k);
                        if (value instanceof ConstraintDefinitionNode) {
                            final ConstraintDefinitionNode constraintDefinitionNode = (ConstraintDefinitionNode)value;
                            o = constraintDefinitionNode.getConstraintMoniker();
                            array = constraintDefinitionNode.getColumnList().getColumnNames();
                        }
                        else if (value instanceof ConstraintDescriptor) {
                            final ConstraintDescriptor constraintDescriptor = value;
                            o = constraintDescriptor.getConstraintName();
                            array = constraintDescriptor.getColumnDescriptors().getColumnNames();
                        }
                        if (this.columnsMatch(e.getColumnList().getColumnNames(), array)) {
                            throw StandardException.newException("42Z93", e.getConstraintMoniker(), o);
                        }
                    }
                    list.add((ConstraintDescriptor)e);
                }
                this.checkForDuplicateConstraintNames(ddlStatementNode, set2, e.getConstraintMoniker());
                if (e.getConstraintType() == 5) {
                    final String constraintMoniker = e.getConstraintMoniker();
                    if (constraintMoniker != null) {
                        final String dropSchemaName = e.getDropSchemaName();
                        final SchemaDescriptor schemaDescriptor = (dropSchemaName == null) ? td.getSchemaDescriptor() : this.getSchemaDescriptor(dropSchemaName);
                        final ConstraintDescriptor constraintDescriptorByName = dataDictionary.getConstraintDescriptorByName(td, schemaDescriptor, constraintMoniker, false);
                        if (constraintDescriptorByName == null) {
                            throw StandardException.newException("42X86", schemaDescriptor.getSchemaName() + "." + constraintMoniker, td.getQualifiedName());
                        }
                        this.getCompilerContext().createDependency(constraintDescriptorByName);
                    }
                }
                if (e.hasPrimaryKeyConstraint()) {
                    this.verifyUniqueColumnList(ddlStatementNode, e);
                }
                else if (e.hasUniqueKeyConstraint()) {
                    this.verifyUniqueColumnList(ddlStatementNode, e);
                    if (!dataDictionary.checkVersion(160, null)) {
                        this.checkForNullColumns(e, td);
                    }
                }
                else if (e.hasForeignKeyConstraint()) {
                    this.verifyUniqueColumnList(ddlStatementNode, e);
                }
            }
        }
        if (n > 1) {
            throw StandardException.newException("428C1");
        }
    }
    
    public void validatePrimaryKeyNullability() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final TableElementNode tableElementNode = (TableElementNode)this.elementAt(i);
            if (tableElementNode.hasConstraint()) {
                final ConstraintDefinitionNode columnListToNotNull = (ConstraintDefinitionNode)tableElementNode;
                if (columnListToNotNull.hasPrimaryKeyConstraint()) {
                    if (this.td == null) {
                        this.setColumnListToNotNull(columnListToNotNull);
                    }
                    else {
                        this.checkForNullColumns(columnListToNotNull, this.td);
                    }
                }
            }
        }
    }
    
    public int countConstraints(final int n) {
        int n2 = 0;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final TableElementNode tableElementNode = (TableElementNode)this.elementAt(i);
            if (tableElementNode instanceof ConstraintDefinitionNode) {
                if (n == ((ConstraintDefinitionNode)tableElementNode).getConstraintType()) {
                    ++n2;
                }
            }
        }
        return n2;
    }
    
    public int countGenerationClauses() {
        int n = 0;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final TableElementNode tableElementNode = (TableElementNode)this.elementAt(i);
            if (tableElementNode instanceof ColumnDefinitionNode) {
                if (((ColumnDefinitionNode)tableElementNode).hasGenerationClause()) {
                    ++n;
                }
            }
        }
        return n;
    }
    
    public int countNumberOfColumns() {
        return this.numColumns;
    }
    
    public int genColumnInfos(final ColumnInfo[] array) throws StandardException {
        int n = 0;
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((TableElementNode)this.elementAt(i)).getElementType() == 7) {
                final String name = ((TableElementNode)this.elementAt(i)).getName();
                array[i] = new ColumnInfo(name, this.td.getColumnDescriptor(name).getType(), null, null, null, null, null, 1, 0L, 0L, 0L);
                break;
            }
            if (!(this.elementAt(i) instanceof ColumnDefinitionNode)) {
                ++n;
            }
            else {
                final ColumnDefinitionNode columnDefinitionNode = (ColumnDefinitionNode)this.elementAt(i);
                ProviderList auxiliaryProviderList = null;
                ProviderInfo[] persistentProviderInfos = null;
                if (columnDefinitionNode.hasGenerationClause()) {
                    auxiliaryProviderList = columnDefinitionNode.getGenerationClauseNode().getAuxiliaryProviderList();
                }
                if (auxiliaryProviderList != null && auxiliaryProviderList.size() > 0) {
                    persistentProviderInfos = this.getDataDictionary().getDependencyManager().getPersistentProviderInfos(auxiliaryProviderList);
                }
                array[i - n] = new ColumnInfo(columnDefinitionNode.getColumnName(), columnDefinitionNode.getType(), columnDefinitionNode.getDefaultValue(), columnDefinitionNode.getDefaultInfo(), persistentProviderInfos, null, columnDefinitionNode.getOldDefaultUUID(), columnDefinitionNode.getAction(), columnDefinitionNode.isAutoincrementColumn() ? columnDefinitionNode.getAutoincrementStart() : 0L, columnDefinitionNode.isAutoincrementColumn() ? columnDefinitionNode.getAutoincrementIncrement() : 0L, columnDefinitionNode.isAutoincrementColumn() ? columnDefinitionNode.getAutoinc_create_or_modify_Start_Increment() : -1L);
                if (columnDefinitionNode.hasConstraint()) {
                    ++n;
                }
            }
        }
        return n;
    }
    
    public void appendNewColumnsToRCL(final FromBaseTable fromBaseTable) throws StandardException {
        final int size = this.size();
        final ResultColumnList resultColumns = fromBaseTable.getResultColumns();
        final TableName tableName = fromBaseTable.getTableName();
        for (int i = 0; i < size; ++i) {
            if (this.elementAt(i) instanceof ColumnDefinitionNode) {
                final ColumnDefinitionNode columnDefinitionNode = (ColumnDefinitionNode)this.elementAt(i);
                final ResultColumn resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, columnDefinitionNode.getType(), this.getNodeFactory().getNode(94, columnDefinitionNode.getColumnName(), tableName, columnDefinitionNode.getType(), this.getContextManager()), this.getContextManager());
                resultColumn.setName(columnDefinitionNode.getColumnName());
                resultColumns.addElement(resultColumn);
            }
        }
    }
    
    void bindAndValidateCheckConstraints(final FromList list) throws StandardException {
        final FromBaseTable fromBaseTable = (FromBaseTable)list.elementAt(0);
        final int size = this.size();
        final CompilerContext compilerContext = this.getCompilerContext();
        final ArrayList list2 = new ArrayList();
        for (int i = 0; i < size; ++i) {
            final TableElementNode tableElementNode = (TableElementNode)this.elementAt(i);
            if (tableElementNode instanceof ConstraintDefinitionNode) {
                final ConstraintDefinitionNode constraintDefinitionNode = (ConstraintDefinitionNode)tableElementNode;
                if (constraintDefinitionNode.getConstraintType() == 4) {
                    final ValueNode checkCondition = constraintDefinitionNode.getCheckCondition();
                    final int reliability = compilerContext.getReliability();
                    try {
                        final ProviderList list3 = new ProviderList();
                        final ProviderList currentAuxiliaryProviderList = compilerContext.getCurrentAuxiliaryProviderList();
                        compilerContext.setCurrentAuxiliaryProviderList(list3);
                        compilerContext.setReliability(18041);
                        final ValueNode bindExpression = checkCondition.bindExpression(list, null, list2);
                        if (!list2.isEmpty()) {
                            throw StandardException.newException("42Y01", constraintDefinitionNode.getConstraintText());
                        }
                        constraintDefinitionNode.setCheckCondition(bindExpression.checkIsBoolean());
                        if (list3.size() > 0) {
                            constraintDefinitionNode.setAuxiliaryProviderList(list3);
                        }
                        compilerContext.setCurrentAuxiliaryProviderList(currentAuxiliaryProviderList);
                    }
                    finally {
                        compilerContext.setReliability(reliability);
                    }
                    final ResultColumnList resultColumns = fromBaseTable.getResultColumns();
                    final int countReferencedColumns = resultColumns.countReferencedColumns();
                    final int[] checkColumnReferences = new int[countReferencedColumns];
                    resultColumns.recordColumnReferences(checkColumnReferences, 1);
                    constraintDefinitionNode.setCheckColumnReferences(checkColumnReferences);
                    final ResultColumnList columnList = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
                    resultColumns.copyReferencedColumnsToNewList(columnList);
                    if (constraintDefinitionNode.getColumnList() != null) {
                        final String name = ((ResultColumn)constraintDefinitionNode.getColumnList().elementAt(0)).getName();
                        if (countReferencedColumns > 1 || !name.equals(((ResultColumn)columnList.elementAt(0)).getName())) {
                            throw StandardException.newException("42621", name);
                        }
                    }
                    constraintDefinitionNode.setColumnList(columnList);
                    resultColumns.clearColumnReferences();
                }
            }
        }
    }
    
    void bindAndValidateGenerationClauses(final SchemaDescriptor schemaDescriptor, final FromList list, final FormatableBitSet set, final TableDescriptor tableDescriptor) throws StandardException {
        final FromBaseTable fromBaseTable = (FromBaseTable)list.elementAt(0);
        final ResultColumnList resultColumns = fromBaseTable.getResultColumns();
        final int size = fromBaseTable.getResultColumns().size();
        final int size2 = this.size();
        this.findIllegalGenerationReferences(list, tableDescriptor);
        set.grow(size + 1);
        final CompilerContext compilerContext = this.getCompilerContext();
        final ArrayList list2 = new ArrayList();
        for (int i = 0; i < size2; ++i) {
            final TableElementNode tableElementNode = (TableElementNode)this.elementAt(i);
            if (tableElementNode instanceof ColumnDefinitionNode) {
                final ColumnDefinitionNode columnDefinitionNode = (ColumnDefinitionNode)tableElementNode;
                if (columnDefinitionNode.hasGenerationClause()) {
                    final GenerationClauseNode generationClauseNode = columnDefinitionNode.getGenerationClauseNode();
                    final int reliability = compilerContext.getReliability();
                    final ProviderList currentAuxiliaryProviderList = compilerContext.getCurrentAuxiliaryProviderList();
                    try {
                        final ProviderList list3 = new ProviderList();
                        compilerContext.setCurrentAuxiliaryProviderList(list3);
                        compilerContext.setReliability(30329);
                        final DataTypeDescriptor typeServices = generationClauseNode.bindExpression(list, null, list2).getTypeServices();
                        final DataTypeDescriptor type = columnDefinitionNode.getType();
                        if (type == null) {
                            columnDefinitionNode.setType(typeServices);
                            resultColumns.getResultColumn(columnDefinitionNode.getColumnName(), false).setType(typeServices);
                            this.setCollationTypeOnCharacterStringColumn(schemaDescriptor, columnDefinitionNode);
                            columnDefinitionNode.checkUserType(fromBaseTable.getTableDescriptor());
                        }
                        else {
                            final TypeId typeId = type.getTypeId();
                            final TypeId typeId2 = typeServices.getTypeId();
                            if (!this.getTypeCompiler(typeId2).convertible(typeId, false)) {
                                throw StandardException.newException("42XA0", columnDefinitionNode.getName(), typeId2.getSQLTypeName());
                            }
                        }
                        if (!list2.isEmpty()) {
                            throw StandardException.newException("42XA1", columnDefinitionNode.getName());
                        }
                        if (list3.size() > 0) {
                            generationClauseNode.setAuxiliaryProviderList(list3);
                        }
                    }
                    finally {
                        compilerContext.setCurrentAuxiliaryProviderList(currentAuxiliaryProviderList);
                        compilerContext.setReliability(reliability);
                    }
                    final ResultColumnList resultColumns2 = fromBaseTable.getResultColumns();
                    final int countReferencedColumns = resultColumns2.countReferencedColumns();
                    final int[] array = new int[countReferencedColumns];
                    set.set(resultColumns2.getPosition(columnDefinitionNode.getColumnName(), 1));
                    resultColumns2.recordColumnReferences(array, 1);
                    final String[] array2 = new String[countReferencedColumns];
                    for (int j = 0; j < countReferencedColumns; ++j) {
                        array2[j] = ((ResultColumn)resultColumns2.elementAt(array[j] - 1)).getName();
                    }
                    columnDefinitionNode.setDefaultInfo(new DefaultInfoImpl(generationClauseNode.getExpressionText(), array2, this.getLanguageConnectionContext().getCurrentSchemaName()));
                    resultColumns2.clearColumnReferences();
                }
            }
        }
    }
    
    void findIllegalGenerationReferences(final FromList list, final TableDescriptor tableDescriptor) throws StandardException {
        final ArrayList list2 = new ArrayList<ColumnDefinitionNode>();
        final HashSet set = new HashSet<String>();
        final int size = this.size();
        if (tableDescriptor != null) {
            final ColumnDescriptorList generatedColumns = tableDescriptor.getGeneratedColumns();
            for (int size2 = generatedColumns.size(), i = 0; i < size2; ++i) {
                set.add(generatedColumns.elementAt(i).getColumnName());
            }
        }
        for (int j = 0; j < size; ++j) {
            final TableElementNode tableElementNode = (TableElementNode)this.elementAt(j);
            if (tableElementNode instanceof ColumnDefinitionNode) {
                final ColumnDefinitionNode e = (ColumnDefinitionNode)tableElementNode;
                if (e.hasGenerationClause()) {
                    list2.add(e);
                    set.add(e.getColumnName());
                }
            }
        }
        for (int size3 = list2.size(), k = 0; k < size3; ++k) {
            final ColumnDefinitionNode columnDefinitionNode = list2.get(k);
            final List referencedColumns = columnDefinitionNode.getGenerationClauseNode().findReferencedColumns();
            for (int size4 = referencedColumns.size(), l = 0; l < size4; ++l) {
                final String columnName = referencedColumns.get(l).getColumnName();
                if (columnName != null && set.contains(columnName)) {
                    throw StandardException.newException("42XA4", columnDefinitionNode.getColumnName());
                }
            }
        }
    }
    
    void validateForeignKeysOnGenerationClauses(final FromList list, final FormatableBitSet set) throws StandardException {
        if (set.getNumBitsSet() <= 0) {
            return;
        }
        final ResultColumnList resultColumns = ((FromBaseTable)list.elementAt(0)).getResultColumns();
        for (int size = this.size(), i = 0; i < size; ++i) {
            final TableElementNode tableElementNode = (TableElementNode)this.elementAt(i);
            if (tableElementNode instanceof FKConstraintDefinitionNode) {
                final FKConstraintDefinitionNode fkConstraintDefinitionNode = (FKConstraintDefinitionNode)tableElementNode;
                final ConstraintInfo referencedConstraintInfo = fkConstraintDefinitionNode.getReferencedConstraintInfo();
                final int referentialActionDeleteRule = referencedConstraintInfo.getReferentialActionDeleteRule();
                final int referentialActionUpdateRule = referencedConstraintInfo.getReferentialActionUpdateRule();
                if (referentialActionUpdateRule != 1 && referentialActionUpdateRule != 2) {
                    throw StandardException.newException("XSCB3.S");
                }
                if (referentialActionDeleteRule == 3 || referentialActionDeleteRule == 4) {
                    final ResultColumnList columnList = fkConstraintDefinitionNode.getColumnList();
                    for (int size2 = columnList.size(), j = 0; j < size2; ++j) {
                        final String name = ((ResultColumn)columnList.elementAt(j)).getName();
                        if (set.isSet(resultColumns.getPosition(name, 1))) {
                            throw StandardException.newException("42XA6", name);
                        }
                    }
                }
            }
        }
    }
    
    void genConstraintActions(final boolean b, final ConstraintConstantAction[] array, final String s, final SchemaDescriptor schemaDescriptor, final DataDictionary dataDictionary) throws StandardException {
        final int size = this.size();
        int n = 0;
        for (int i = 0; i < size; ++i) {
            String[] array2 = null;
            final TableElementNode tableElementNode = (TableElementNode)this.elementAt(i);
            IndexConstantAction indexConstantAction = null;
            if (tableElementNode.hasConstraint()) {
                if (!(tableElementNode instanceof ColumnDefinitionNode)) {
                    final ConstraintDefinitionNode constraintDefinitionNode = (ConstraintDefinitionNode)tableElementNode;
                    if (constraintDefinitionNode.getColumnList() != null) {
                        array2 = new String[constraintDefinitionNode.getColumnList().size()];
                        constraintDefinitionNode.getColumnList().exportNames(array2);
                    }
                    final int constraintType = constraintDefinitionNode.getConstraintType();
                    final String constraintText = constraintDefinitionNode.getConstraintText();
                    final String constraintMoniker = constraintDefinitionNode.getConstraintMoniker();
                    if (constraintDefinitionNode.requiresBackingIndex()) {
                        if (constraintDefinitionNode.constraintType == 3 && dataDictionary.checkVersion(160, null)) {
                            final boolean columnsNullable = this.areColumnsNullable(constraintDefinitionNode, this.td);
                            indexConstantAction = this.genIndexAction(b, !columnsNullable, columnsNullable, null, constraintDefinitionNode, array2, true, schemaDescriptor, s, constraintType, dataDictionary);
                        }
                        else {
                            indexConstantAction = this.genIndexAction(b, constraintDefinitionNode.requiresUniqueIndex(), false, null, constraintDefinitionNode, array2, true, schemaDescriptor, s, constraintType, dataDictionary);
                        }
                    }
                    if (constraintType == 5) {
                        array[n] = this.getGenericConstantActionFactory().getDropConstraintConstantAction(constraintMoniker, constraintDefinitionNode.getDropSchemaName(), s, this.td.getUUID(), schemaDescriptor.getSchemaName(), indexConstantAction, constraintDefinitionNode.getDropBehavior(), constraintDefinitionNode.getVerifyType());
                    }
                    else {
                        final ProviderList auxiliaryProviderList = constraintDefinitionNode.getAuxiliaryProviderList();
                        ConstraintInfo referencedConstraintInfo = null;
                        if (constraintDefinitionNode instanceof FKConstraintDefinitionNode) {
                            referencedConstraintInfo = ((FKConstraintDefinitionNode)constraintDefinitionNode).getReferencedConstraintInfo();
                        }
                        ProviderInfo[] persistentProviderInfos;
                        if (auxiliaryProviderList != null && auxiliaryProviderList.size() > 0) {
                            persistentProviderInfos = dataDictionary.getDependencyManager().getPersistentProviderInfos(auxiliaryProviderList);
                        }
                        else {
                            persistentProviderInfos = new ProviderInfo[0];
                        }
                        array[n++] = this.getGenericConstantActionFactory().getCreateConstraintConstantAction(constraintMoniker, constraintType, b, s, (this.td != null) ? this.td.getUUID() : ((UUID)null), schemaDescriptor.getSchemaName(), array2, indexConstantAction, constraintText, true, referencedConstraintInfo, persistentProviderInfos);
                    }
                }
            }
        }
    }
    
    private boolean columnsMatch(final String[] array, final String[] array2) {
        if (array.length != array2.length) {
            return false;
        }
        final int length = array.length;
        final int length2 = array2.length;
        for (int i = 0; i < length; ++i) {
            boolean b = false;
            for (int j = 0; j < length2; ++j) {
                if (array[i].equals(array2[j])) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                return false;
            }
        }
        return true;
    }
    
    private IndexConstantAction genIndexAction(final boolean b, final boolean b2, final boolean b3, String backingIndexName, final ConstraintDefinitionNode constraintDefinitionNode, final String[] array, final boolean b4, final SchemaDescriptor schemaDescriptor, final String s, final int n, final DataDictionary dataDictionary) throws StandardException {
        if (backingIndexName == null) {
            backingIndexName = constraintDefinitionNode.getBackingIndexName(dataDictionary);
        }
        if (n == 5) {
            return this.getGenericConstantActionFactory().getDropIndexConstantAction(null, backingIndexName, s, schemaDescriptor.getSchemaName(), this.td.getUUID(), this.td.getHeapConglomerateId());
        }
        final boolean[] array2 = new boolean[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = true;
        }
        return this.getGenericConstantActionFactory().getCreateIndexConstantAction(b, b2, b3, "BTREE", schemaDescriptor.getSchemaName(), backingIndexName, s, (this.td != null) ? this.td.getUUID() : ((UUID)null), array, array2, b4, constraintDefinitionNode.getBackingIndexUUID(), this.checkIndexPageSizeProperty(constraintDefinitionNode));
    }
    
    private Properties checkIndexPageSizeProperty(final ConstraintDefinitionNode constraintDefinitionNode) throws StandardException {
        Properties properties = constraintDefinitionNode.getProperties();
        if (properties == null) {
            properties = new Properties();
        }
        if (properties.get("derby.storage.pageSize") != null || PropertyUtil.getServiceProperty(this.getLanguageConnectionContext().getTransactionCompile(), "derby.storage.pageSize") != null) {
            return properties;
        }
        final ResultColumnList columnList = constraintDefinitionNode.getColumnList();
        int n = 0;
        for (int i = 0; i < columnList.size(); ++i) {
            final String name = ((ResultColumn)columnList.elementAt(i)).getName();
            DataTypeDescriptor dataTypeDescriptor;
            if (this.td == null) {
                dataTypeDescriptor = this.getColumnDataTypeDescriptor(name);
            }
            else {
                dataTypeDescriptor = this.getColumnDataTypeDescriptor(name, this.td);
            }
            if (dataTypeDescriptor != null) {
                n += dataTypeDescriptor.getTypeId().getApproximateLengthInBytes(dataTypeDescriptor);
            }
        }
        if (n > 1024) {
            properties.put("derby.storage.pageSize", "32768");
        }
        return properties;
    }
    
    private void checkForDuplicateColumns(final DDLStatementNode ddlStatementNode, final Set set, final String s) throws StandardException {
        if (!set.add(s) && ddlStatementNode instanceof CreateTableNode) {
            throw StandardException.newException("42X12", s);
        }
    }
    
    private void checkForDuplicateConstraintNames(final DDLStatementNode ddlStatementNode, final Set set, final String s) throws StandardException {
        if (s == null) {
            return;
        }
        if (!set.add(s) && ddlStatementNode instanceof CreateTableNode) {
            throw StandardException.newException("42X91", s);
        }
    }
    
    private void verifyUniqueColumnList(final DDLStatementNode ddlStatementNode, final ConstraintDefinitionNode constraintDefinitionNode) throws StandardException {
        if (ddlStatementNode instanceof CreateTableNode) {
            final String verifyCreateConstraintColumnList = constraintDefinitionNode.getColumnList().verifyCreateConstraintColumnList(this);
            if (verifyCreateConstraintColumnList != null) {
                throw StandardException.newException("42X93", ddlStatementNode.getRelativeName(), verifyCreateConstraintColumnList);
            }
        }
        final String verifyUniqueNames = constraintDefinitionNode.getColumnList().verifyUniqueNames(false);
        if (verifyUniqueNames != null) {
            throw StandardException.newException("42X92", verifyUniqueNames);
        }
    }
    
    private void setColumnListToNotNull(final ConstraintDefinitionNode constraintDefinitionNode) {
        final ResultColumnList columnList = constraintDefinitionNode.getColumnList();
        for (int size = columnList.size(), i = 0; i < size; ++i) {
            this.findColumnDefinition(((ResultColumn)columnList.elementAt(i)).getName()).setNullability(false);
        }
    }
    
    private boolean areColumnsNullable(final ConstraintDefinitionNode constraintDefinitionNode, final TableDescriptor tableDescriptor) {
        final ResultColumnList columnList = constraintDefinitionNode.getColumnList();
        for (int size = columnList.size(), i = 0; i < size; ++i) {
            final String name = ((ResultColumn)columnList.elementAt(i)).getName();
            DataTypeDescriptor dataTypeDescriptor;
            if (tableDescriptor == null) {
                dataTypeDescriptor = this.getColumnDataTypeDescriptor(name);
            }
            else {
                dataTypeDescriptor = this.getColumnDataTypeDescriptor(name, tableDescriptor);
            }
            if (dataTypeDescriptor != null && dataTypeDescriptor.isNullable()) {
                return true;
            }
        }
        return false;
    }
    
    private void checkForNullColumns(final ConstraintDefinitionNode constraintDefinitionNode, final TableDescriptor tableDescriptor) throws StandardException {
        final ResultColumnList columnList = constraintDefinitionNode.getColumnList();
        for (int size = columnList.size(), i = 0; i < size; ++i) {
            final String name = ((ResultColumn)columnList.elementAt(i)).getName();
            DataTypeDescriptor dataTypeDescriptor;
            if (tableDescriptor == null) {
                dataTypeDescriptor = this.getColumnDataTypeDescriptor(name);
            }
            else {
                dataTypeDescriptor = this.getColumnDataTypeDescriptor(name, tableDescriptor);
            }
            if (dataTypeDescriptor != null && dataTypeDescriptor.isNullable()) {
                throw StandardException.newException(this.getLanguageConnectionContext().getDataDictionary().checkVersion(160, null) ? "42831.S.1" : "42831", name);
            }
        }
    }
    
    private DataTypeDescriptor getColumnDataTypeDescriptor(final String s) {
        final ColumnDefinitionNode columnDefinition = this.findColumnDefinition(s);
        if (columnDefinition != null) {
            return columnDefinition.getType();
        }
        return null;
    }
    
    private DataTypeDescriptor getColumnDataTypeDescriptor(final String s, final TableDescriptor tableDescriptor) {
        final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(s);
        if (columnDescriptor != null) {
            return columnDescriptor.getType();
        }
        return this.getColumnDataTypeDescriptor(s);
    }
    
    private ColumnDefinitionNode findColumnDefinition(final String s) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final TableElementNode tableElementNode = (TableElementNode)this.elementAt(i);
            if (tableElementNode instanceof ColumnDefinitionNode) {
                final ColumnDefinitionNode columnDefinitionNode = (ColumnDefinitionNode)tableElementNode;
                if (s.equals(columnDefinitionNode.getName())) {
                    return columnDefinitionNode;
                }
            }
        }
        return null;
    }
    
    public boolean containsColumnName(final String s) {
        return this.findColumnDefinition(s) != null;
    }
}
