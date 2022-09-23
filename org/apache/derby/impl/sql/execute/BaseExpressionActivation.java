// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.types.UserDefinedTypeIdImpl;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.catalog.types.BaseTypeIdImpl;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataValueDescriptor;

public abstract class BaseExpressionActivation
{
    BaseExpressionActivation() {
    }
    
    public static DataValueDescriptor minValue(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2, final DataValueDescriptor dataValueDescriptor3, final DataValueDescriptor dataValueDescriptor4, final int n, final int n2, final int n3, final int n4, final boolean b, final int n5, final int n6, final int n7) throws StandardException {
        DataValueDescriptor dataValueDescriptor5;
        if (n2 == -1) {
            dataValueDescriptor5 = new DataTypeDescriptor(new TypeId(n, null), n3, n4, b, n5, n6, n7).getNull();
        }
        else {
            dataValueDescriptor5 = new TypeId(n, new UserDefinedTypeIdImpl()).getNull();
        }
        DataValueDescriptor dataValueDescriptor6 = dataValueDescriptor;
        if (dataValueDescriptor2 != null && (dataValueDescriptor6.isNull() || dataValueDescriptor5.lessThan(dataValueDescriptor2, dataValueDescriptor6).equals(true))) {
            dataValueDescriptor6 = dataValueDescriptor2;
        }
        if (dataValueDescriptor3 != null && (dataValueDescriptor6.isNull() || dataValueDescriptor5.lessThan(dataValueDescriptor3, dataValueDescriptor6).equals(true))) {
            dataValueDescriptor6 = dataValueDescriptor3;
        }
        if (dataValueDescriptor4 != null && (dataValueDescriptor6.isNull() || dataValueDescriptor5.lessThan(dataValueDescriptor4, dataValueDescriptor6).equals(true))) {
            dataValueDescriptor6 = dataValueDescriptor4;
        }
        return dataValueDescriptor6;
    }
    
    public static DataValueDescriptor maxValue(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2, final DataValueDescriptor dataValueDescriptor3, final DataValueDescriptor dataValueDescriptor4, final int n, final int n2, final int n3, final int n4, final boolean b, final int n5, final int n6, final int n7) throws StandardException {
        DataValueDescriptor dataValueDescriptor5;
        if (n2 == -1) {
            dataValueDescriptor5 = new DataTypeDescriptor(new TypeId(n, null), n3, n4, b, n5, n6, n7).getNull();
        }
        else {
            dataValueDescriptor5 = new TypeId(n, new UserDefinedTypeIdImpl()).getNull();
        }
        DataValueDescriptor dataValueDescriptor6 = dataValueDescriptor;
        if (dataValueDescriptor2 != null && (dataValueDescriptor6.isNull() || dataValueDescriptor5.greaterThan(dataValueDescriptor2, dataValueDescriptor6).equals(true))) {
            dataValueDescriptor6 = dataValueDescriptor2;
        }
        if (dataValueDescriptor3 != null && (dataValueDescriptor6.isNull() || dataValueDescriptor5.greaterThan(dataValueDescriptor3, dataValueDescriptor6).equals(true))) {
            dataValueDescriptor6 = dataValueDescriptor3;
        }
        if (dataValueDescriptor4 != null && (dataValueDescriptor6.isNull() || dataValueDescriptor5.greaterThan(dataValueDescriptor4, dataValueDescriptor6).equals(true))) {
            dataValueDescriptor6 = dataValueDescriptor4;
        }
        return dataValueDescriptor6;
    }
}
