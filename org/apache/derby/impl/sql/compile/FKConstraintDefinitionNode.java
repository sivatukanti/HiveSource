// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.impl.sql.execute.ConstraintInfo;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;

public final class FKConstraintDefinitionNode extends ConstraintDefinitionNode
{
    TableName refTableName;
    ResultColumnList refRcl;
    SchemaDescriptor refTableSd;
    int refActionDeleteRule;
    int refActionUpdateRule;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        super.init(o, ReuseFactory.getInteger(6), o3, null, null, null);
        this.refRcl = (ResultColumnList)o4;
        this.refTableName = (TableName)o2;
        this.refActionDeleteRule = ((int[])o5)[0];
        this.refActionUpdateRule = ((int[])o5)[1];
    }
    
    protected void bind(final DDLStatementNode ddlStatementNode, final DataDictionary dataDictionary) throws StandardException {
        super.bind(ddlStatementNode, dataDictionary);
        this.refTableSd = this.getSchemaDescriptor(this.refTableName.getSchemaName());
        if (this.refTableSd.isSystemSchema()) {
            throw StandardException.newException("42Y08");
        }
        if (this.refTableName.equals(ddlStatementNode.getObjectName())) {
            return;
        }
        final TableDescriptor tableDescriptor = this.getTableDescriptor(this.refTableName.getTableName(), this.refTableSd);
        if (tableDescriptor == null) {
            throw StandardException.newException("X0Y46.S", this.getConstraintMoniker(), this.refTableName.getTableName());
        }
        this.getCompilerContext().pushCurrentPrivType(this.getPrivType());
        this.getCompilerContext().createDependency(tableDescriptor);
        if (this.refRcl.size() == 0 && tableDescriptor.getPrimaryKey() != null) {
            final int[] referencedColumns = tableDescriptor.getPrimaryKey().getReferencedColumns();
            for (int i = 0; i < referencedColumns.length; ++i) {
                final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(referencedColumns[i]);
                columnDescriptor.setTableDescriptor(tableDescriptor);
                if (this.isPrivilegeCollectionRequired()) {
                    this.getCompilerContext().addRequiredColumnPriv(columnDescriptor);
                }
            }
        }
        else {
            for (int j = 0; j < this.refRcl.size(); ++j) {
                final ColumnDescriptor columnDescriptor2 = tableDescriptor.getColumnDescriptor(((ResultColumn)this.refRcl.elementAt(j)).getName());
                if (columnDescriptor2 != null) {
                    columnDescriptor2.setTableDescriptor(tableDescriptor);
                    if (this.isPrivilegeCollectionRequired()) {
                        this.getCompilerContext().addRequiredColumnPriv(columnDescriptor2);
                    }
                }
            }
        }
        this.getCompilerContext().popCurrentPrivType();
    }
    
    public ConstraintInfo getReferencedConstraintInfo() {
        return new ConstraintInfo(this.refTableName.getTableName(), this.refTableSd, this.refRcl.getColumnNames(), this.refActionDeleteRule, this.refActionUpdateRule);
    }
    
    public TableName getRefTableName() {
        return this.refTableName;
    }
    
    int getPrivType() {
        return 2;
    }
}
