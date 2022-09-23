// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hive.service.cli.thrift.TColumnDesc;

public class ColumnDescriptor
{
    private final String name;
    private final String comment;
    private final TypeDescriptor type;
    private final int position;
    
    public ColumnDescriptor(final String name, final String comment, final TypeDescriptor type, final int position) {
        this.name = name;
        this.comment = comment;
        this.type = type;
        this.position = position;
    }
    
    public ColumnDescriptor(final TColumnDesc tColumnDesc) {
        this.name = tColumnDesc.getColumnName();
        this.comment = tColumnDesc.getComment();
        this.type = new TypeDescriptor(tColumnDesc.getTypeDesc());
        this.position = tColumnDesc.getPosition();
    }
    
    public ColumnDescriptor(final FieldSchema column, final int position) {
        this.name = column.getName();
        this.comment = column.getComment();
        this.type = new TypeDescriptor(column.getType());
        this.position = position;
    }
    
    public static ColumnDescriptor newPrimitiveColumnDescriptor(final String name, final String comment, final Type type, final int position) {
        return new ColumnDescriptor(name, comment, new TypeDescriptor(type), position);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public TypeDescriptor getTypeDescriptor() {
        return this.type;
    }
    
    public int getOrdinalPosition() {
        return this.position;
    }
    
    public TColumnDesc toTColumnDesc() {
        final TColumnDesc tColumnDesc = new TColumnDesc();
        tColumnDesc.setColumnName(this.name);
        tColumnDesc.setComment(this.comment);
        tColumnDesc.setTypeDesc(this.type.toTTypeDesc());
        tColumnDesc.setPosition(this.position);
        return tColumnDesc;
    }
    
    public Type getType() {
        return this.type.getType();
    }
    
    public boolean isPrimitive() {
        return this.type.getType().isPrimitiveType();
    }
    
    public String getTypeName() {
        return this.type.getTypeName();
    }
}
