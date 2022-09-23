// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hive.service.cli.thrift.TPrimitiveTypeEntry;
import java.util.List;
import org.apache.hive.service.cli.thrift.TTypeEntry;
import org.apache.hive.service.cli.thrift.TTypeDesc;

public class TypeDescriptor
{
    private final Type type;
    private String typeName;
    private TypeQualifiers typeQualifiers;
    
    public TypeDescriptor(final Type type) {
        this.typeName = null;
        this.typeQualifiers = null;
        this.type = type;
    }
    
    public TypeDescriptor(final TTypeDesc tTypeDesc) {
        this.typeName = null;
        this.typeQualifiers = null;
        final List<TTypeEntry> tTypeEntries = tTypeDesc.getTypes();
        final TPrimitiveTypeEntry top = tTypeEntries.get(0).getPrimitiveEntry();
        this.type = Type.getType(top.getType());
        if (top.isSetTypeQualifiers()) {
            this.setTypeQualifiers(TypeQualifiers.fromTTypeQualifiers(top.getTypeQualifiers()));
        }
    }
    
    public TypeDescriptor(final String typeName) {
        this.typeName = null;
        this.typeQualifiers = null;
        this.type = Type.getType(typeName);
        if (this.type.isComplexType()) {
            this.typeName = typeName;
        }
        else if (this.type.isQualifiedType()) {
            final PrimitiveTypeInfo pti = TypeInfoFactory.getPrimitiveTypeInfo(typeName);
            this.setTypeQualifiers(TypeQualifiers.fromTypeInfo(pti));
        }
    }
    
    public Type getType() {
        return this.type;
    }
    
    public TTypeDesc toTTypeDesc() {
        final TPrimitiveTypeEntry primitiveEntry = new TPrimitiveTypeEntry(this.type.toTType());
        if (this.getTypeQualifiers() != null) {
            primitiveEntry.setTypeQualifiers(this.getTypeQualifiers().toTTypeQualifiers());
        }
        final TTypeEntry entry = TTypeEntry.primitiveEntry(primitiveEntry);
        final TTypeDesc desc = new TTypeDesc();
        desc.addToTypes(entry);
        return desc;
    }
    
    public String getTypeName() {
        if (this.typeName != null) {
            return this.typeName;
        }
        return this.type.getName();
    }
    
    public TypeQualifiers getTypeQualifiers() {
        return this.typeQualifiers;
    }
    
    public void setTypeQualifiers(final TypeQualifiers typeQualifiers) {
        this.typeQualifiers = typeQualifiers;
    }
    
    public Integer getColumnSize() {
        if (this.type.isNumericType()) {
            return this.getPrecision();
        }
        switch (this.type) {
            case STRING_TYPE:
            case BINARY_TYPE: {
                return Integer.MAX_VALUE;
            }
            case CHAR_TYPE:
            case VARCHAR_TYPE: {
                return this.typeQualifiers.getCharacterMaximumLength();
            }
            case DATE_TYPE: {
                return 10;
            }
            case TIMESTAMP_TYPE: {
                return 29;
            }
            default: {
                return null;
            }
        }
    }
    
    public Integer getPrecision() {
        if (this.type == Type.DECIMAL_TYPE) {
            return this.typeQualifiers.getPrecision();
        }
        return this.type.getMaxPrecision();
    }
    
    public Integer getDecimalDigits() {
        switch (this.type) {
            case BOOLEAN_TYPE:
            case TINYINT_TYPE:
            case SMALLINT_TYPE:
            case INT_TYPE:
            case BIGINT_TYPE: {
                return 0;
            }
            case FLOAT_TYPE: {
                return 7;
            }
            case DOUBLE_TYPE: {
                return 15;
            }
            case DECIMAL_TYPE: {
                return this.typeQualifiers.getScale();
            }
            case TIMESTAMP_TYPE: {
                return 9;
            }
            default: {
                return null;
            }
        }
    }
}
