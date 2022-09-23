// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;

abstract class DDLStatementNode extends StatementNode
{
    public static final int UNKNOWN_TYPE = 0;
    public static final int ADD_TYPE = 1;
    public static final int DROP_TYPE = 2;
    public static final int MODIFY_TYPE = 3;
    public static final int LOCKING_TYPE = 4;
    public static final int UPDATE_STATISTICS = 5;
    public static final int DROP_STATISTICS = 6;
    private TableName objectName;
    private boolean initOk;
    boolean implicitCreateSchema;
    
    public void init(final Object o) throws StandardException {
        this.initAndCheck(o);
    }
    
    protected void initAndCheck(final Object o) throws StandardException {
        this.objectName = (TableName)o;
        this.initOk = true;
    }
    
    public boolean isAtomic() {
        return true;
    }
    
    public String getRelativeName() {
        return this.objectName.getTableName();
    }
    
    public String getFullName() {
        return this.objectName.getFullTableName();
    }
    
    public final TableName getObjectName() {
        return this.objectName;
    }
    
    public String toString() {
        return "";
    }
    
    int activationKind() {
        return 5;
    }
    
    public final void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.callMethod((short)185, null, "getDDLResultSet", "org.apache.derby.iapi.sql.ResultSet", 1);
    }
    
    protected final SchemaDescriptor getSchemaDescriptor() throws StandardException {
        return this.getSchemaDescriptor(true, true);
    }
    
    protected final SchemaDescriptor getSchemaDescriptor(final boolean b, final boolean b2) throws StandardException {
        final String schemaName = this.objectName.getSchemaName();
        SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor(schemaName, !this.implicitCreateSchema);
        final CompilerContext compilerContext = this.getCompilerContext();
        if (schemaDescriptor == null) {
            if (schemaName.startsWith("SYS")) {
                throw StandardException.newException("42X62", this.statementToString(), schemaName);
            }
            schemaDescriptor = new SchemaDescriptor(this.getDataDictionary(), schemaName, null, null, false);
            if (this.isPrivilegeCollectionRequired()) {
                compilerContext.addRequiredSchemaPriv(schemaName, null, 16);
            }
        }
        if (b && this.isPrivilegeCollectionRequired()) {
            compilerContext.addRequiredSchemaPriv(schemaDescriptor.getSchemaName(), null, 17);
        }
        if (b2 && schemaDescriptor.isSystemSchema()) {
            throw StandardException.newException("42X62", this.statementToString(), schemaDescriptor);
        }
        return schemaDescriptor;
    }
    
    protected final TableDescriptor getTableDescriptor() throws StandardException {
        return this.getTableDescriptor(this.objectName);
    }
    
    protected final TableDescriptor getTableDescriptor(final boolean b) throws StandardException {
        return this.checkTableDescriptor(this.justGetDescriptor(this.objectName), b);
    }
    
    protected final TableDescriptor getTableDescriptor(final UUID uuid) throws StandardException {
        return this.checkTableDescriptor(this.getDataDictionary().getTableDescriptor(uuid), true);
    }
    
    protected final TableDescriptor getTableDescriptor(final TableName tableName) throws StandardException {
        return this.checkTableDescriptor(this.justGetDescriptor(tableName), true);
    }
    
    private TableDescriptor justGetDescriptor(final TableName tableName) throws StandardException {
        final TableDescriptor tableDescriptor = this.getTableDescriptor(tableName.getTableName(), this.getSchemaDescriptor(tableName.getSchemaName()));
        if (tableDescriptor == null) {
            throw StandardException.newException("42Y55", this.statementToString(), tableName);
        }
        return tableDescriptor;
    }
    
    private TableDescriptor checkTableDescriptor(final TableDescriptor tableDescriptor, final boolean b) throws StandardException {
        String s = null;
        switch (tableDescriptor.getTableType()) {
            case 5: {
                s = "X0Y56.S";
                break;
            }
            case 1: {
                if (b) {
                    s = "X0Y56.S";
                    break;
                }
                return tableDescriptor;
            }
            case 0: {
                return this.lockTableForCompilation(tableDescriptor);
            }
            case 3: {
                return tableDescriptor;
            }
            case 2: {
                s = "42Y62";
                break;
            }
        }
        throw StandardException.newException(s, this.statementToString(), tableDescriptor.getQualifiedName());
    }
    
    void bindName(final DataDictionary dataDictionary) throws StandardException {
        if (this.objectName != null) {
            this.objectName.bind(dataDictionary);
        }
    }
    
    FromList makeFromList(final DataDictionary dataDictionary, final TableElementList list, final boolean b) throws StandardException {
        final TableName objectName = this.getObjectName();
        if (objectName.getSchemaName() == null) {
            objectName.setSchemaName(this.getSchemaDescriptor().getSchemaName());
        }
        final FromList list2 = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
        final FromBaseTable fromBaseTable = (FromBaseTable)this.getNodeFactory().getNode(135, objectName, null, null, null, this.getContextManager());
        if (b) {
            fromBaseTable.setTableNumber(0);
            list2.addFromTable(fromBaseTable);
            fromBaseTable.setResultColumns((ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager()));
        }
        else {
            list2.addFromTable(fromBaseTable);
            list2.bindTables(dataDictionary, (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()));
        }
        list.appendNewColumnsToRCL(fromBaseTable);
        return list2;
    }
}
