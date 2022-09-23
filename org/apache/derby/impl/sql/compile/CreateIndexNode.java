// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.util.HashSet;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import java.util.List;
import java.util.Properties;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;

public class CreateIndexNode extends DDLStatementNode
{
    boolean unique;
    DataDictionary dd;
    Properties properties;
    String indexType;
    TableName indexName;
    TableName tableName;
    List columnNameList;
    String[] columnNames;
    boolean[] isAscending;
    int[] boundColumnIDs;
    TableDescriptor td;
    
    public CreateIndexNode() {
        this.dd = null;
        this.columnNames = null;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) throws StandardException {
        this.initAndCheck(o3);
        this.unique = (boolean)o;
        this.indexType = (String)o2;
        this.indexName = (TableName)o3;
        this.tableName = (TableName)o4;
        this.columnNameList = (List)o5;
        this.properties = (Properties)o6;
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "CREATE INDEX";
    }
    
    public boolean getUniqueness() {
        return this.unique;
    }
    
    public String getIndexType() {
        return this.indexType;
    }
    
    public TableName getIndexName() {
        return this.indexName;
    }
    
    public UUID getBoundTableID() {
        return this.td.getUUID();
    }
    
    public Properties getProperties() {
        return this.properties;
    }
    
    public TableName getIndexTableName() {
        return this.tableName;
    }
    
    public String[] getColumnNames() {
        return this.columnNames;
    }
    
    public int[] getKeyColumnIDs() {
        return this.boundColumnIDs;
    }
    
    public boolean[] getIsAscending() {
        return this.isAscending;
    }
    
    public void bindStatement() throws StandardException {
        this.getCompilerContext();
        this.getSchemaDescriptor();
        this.td = this.getTableDescriptor(this.tableName);
        if (this.td.getTableType() == 3) {
            throw StandardException.newException("42995");
        }
        if (this.td.getTotalNumberOfIndexes() > 32767) {
            throw StandardException.newException("42Z9F", String.valueOf(this.td.getTotalNumberOfIndexes()), this.tableName, String.valueOf(32767));
        }
        this.verifyAndGetUniqueNames();
        final int length = this.columnNames.length;
        this.boundColumnIDs = new int[length];
        for (int i = 0; i < length; ++i) {
            final ColumnDescriptor columnDescriptor = this.td.getColumnDescriptor(this.columnNames[i]);
            if (columnDescriptor == null) {
                throw StandardException.newException("42X14", this.columnNames[i], this.tableName);
            }
            this.boundColumnIDs[i] = columnDescriptor.getPosition();
            if (!columnDescriptor.getType().getTypeId().orderable(this.getClassFactory())) {
                throw StandardException.newException("X0X67.S", columnDescriptor.getType().getTypeId().getSQLTypeName());
            }
        }
        if (length > 16) {
            throw StandardException.newException("54008");
        }
        this.getCompilerContext().createDependency(this.td);
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.isSessionSchema(this.td.getSchemaName());
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor();
        final int length = this.columnNames.length;
        int n = 0;
        for (int i = 0; i < length; ++i) {
            final DataTypeDescriptor type = this.td.getColumnDescriptor(this.columnNames[i]).getType();
            n += type.getTypeId().getApproximateLengthInBytes(type);
        }
        if (n > 1024 && (this.properties == null || this.properties.get("derby.storage.pageSize") == null) && PropertyUtil.getServiceProperty(this.getLanguageConnectionContext().getTransactionCompile(), "derby.storage.pageSize") == null) {
            if (this.properties == null) {
                this.properties = new Properties();
            }
            this.properties.put("derby.storage.pageSize", "32768");
        }
        return this.getGenericConstantActionFactory().getCreateIndexConstantAction(false, this.unique, false, this.indexType, schemaDescriptor.getSchemaName(), this.indexName.getTableName(), this.tableName.getTableName(), this.td.getUUID(), this.columnNames, this.isAscending, false, null, this.properties);
    }
    
    private void verifyAndGetUniqueNames() throws StandardException {
        final int size = this.columnNameList.size();
        final HashSet set = new HashSet<String>(size + 2, 0.999f);
        this.columnNames = new String[size];
        this.isAscending = new boolean[size];
        for (int i = 0; i < size; ++i) {
            this.columnNames[i] = (String)this.columnNameList.get(i);
            if (this.columnNames[i].endsWith(" ")) {
                this.columnNames[i] = this.columnNames[i].substring(0, this.columnNames[i].length() - 1);
                this.isAscending[i] = false;
            }
            else {
                this.isAscending[i] = true;
            }
            if (!set.add(this.columnNames[i])) {
                throw StandardException.newException("42X66", this.columnNames[i]);
            }
        }
    }
}
