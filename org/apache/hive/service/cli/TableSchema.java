// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import java.util.Collection;
import org.apache.hadoop.hive.metastore.api.Schema;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import java.util.Iterator;
import org.apache.hive.service.cli.thrift.TColumnDesc;
import org.apache.hive.service.cli.thrift.TTableSchema;
import java.util.ArrayList;
import java.util.List;

public class TableSchema
{
    private final List<ColumnDescriptor> columns;
    
    public TableSchema() {
        this.columns = new ArrayList<ColumnDescriptor>();
    }
    
    public TableSchema(final int numColumns) {
        this.columns = new ArrayList<ColumnDescriptor>();
    }
    
    public TableSchema(final TTableSchema tTableSchema) {
        this.columns = new ArrayList<ColumnDescriptor>();
        for (final TColumnDesc tColumnDesc : tTableSchema.getColumns()) {
            this.columns.add(new ColumnDescriptor(tColumnDesc));
        }
    }
    
    public TableSchema(final List<FieldSchema> fieldSchemas) {
        this.columns = new ArrayList<ColumnDescriptor>();
        int pos = 1;
        for (final FieldSchema field : fieldSchemas) {
            this.columns.add(new ColumnDescriptor(field, pos++));
        }
    }
    
    public TableSchema(final Schema schema) {
        this(schema.getFieldSchemas());
    }
    
    public List<ColumnDescriptor> getColumnDescriptors() {
        return new ArrayList<ColumnDescriptor>(this.columns);
    }
    
    public ColumnDescriptor getColumnDescriptorAt(final int pos) {
        return this.columns.get(pos);
    }
    
    public int getSize() {
        return this.columns.size();
    }
    
    public void clear() {
        this.columns.clear();
    }
    
    public TTableSchema toTTableSchema() {
        final TTableSchema tTableSchema = new TTableSchema();
        for (final ColumnDescriptor col : this.columns) {
            tTableSchema.addToColumns(col.toTColumnDesc());
        }
        return tTableSchema;
    }
    
    public Type[] toTypes() {
        final Type[] types = new Type[this.columns.size()];
        for (int i = 0; i < types.length; ++i) {
            types[i] = this.columns.get(i).getType();
        }
        return types;
    }
    
    public TableSchema addPrimitiveColumn(final String columnName, final Type columnType, final String columnComment) {
        this.columns.add(ColumnDescriptor.newPrimitiveColumnDescriptor(columnName, columnComment, columnType, this.columns.size() + 1));
        return this;
    }
    
    public TableSchema addStringColumn(final String columnName, final String columnComment) {
        this.columns.add(ColumnDescriptor.newPrimitiveColumnDescriptor(columnName, columnComment, Type.STRING_TYPE, this.columns.size() + 1));
        return this;
    }
}
