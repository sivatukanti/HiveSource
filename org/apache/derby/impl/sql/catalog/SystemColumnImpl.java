// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.SystemColumn;

class SystemColumnImpl implements SystemColumn
{
    private final String name;
    private final DataTypeDescriptor type;
    
    static SystemColumn getColumn(final String s, final int n, final boolean b) {
        return new SystemColumnImpl(s, DataTypeDescriptor.getBuiltInDataTypeDescriptor(n, b));
    }
    
    static SystemColumn getColumn(final String s, final int n, final boolean b, final int n2) {
        return new SystemColumnImpl(s, DataTypeDescriptor.getBuiltInDataTypeDescriptor(n, b, n2));
    }
    
    static SystemColumn getIdentifierColumn(final String s, final boolean b) {
        return new SystemColumnImpl(s, DataTypeDescriptor.getBuiltInDataTypeDescriptor(12, b, 128));
    }
    
    static SystemColumn getUUIDColumn(final String s, final boolean b) {
        return new SystemColumnImpl(s, DataTypeDescriptor.getBuiltInDataTypeDescriptor(1, b, 36));
    }
    
    static SystemColumn getIndicatorColumn(final String s) {
        return new SystemColumnImpl(s, DataTypeDescriptor.getBuiltInDataTypeDescriptor(1, false, 1));
    }
    
    static SystemColumn getJavaColumn(final String s, final String s2, final boolean b) throws StandardException {
        return new SystemColumnImpl(s, new DataTypeDescriptor(TypeId.getUserDefinedTypeId(s2), b));
    }
    
    private SystemColumnImpl(final String name, final DataTypeDescriptor type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return this.name;
    }
    
    public DataTypeDescriptor getType() {
        return this.type;
    }
}
