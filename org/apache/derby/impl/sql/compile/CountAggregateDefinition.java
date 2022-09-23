// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;

public class CountAggregateDefinition implements AggregateDefinition
{
    public final DataTypeDescriptor getAggregator(final DataTypeDescriptor dataTypeDescriptor, final StringBuffer sb) {
        sb.append("org.apache.derby.impl.sql.execute.CountAggregator");
        return DataTypeDescriptor.getBuiltInDataTypeDescriptor(4, false);
    }
}
