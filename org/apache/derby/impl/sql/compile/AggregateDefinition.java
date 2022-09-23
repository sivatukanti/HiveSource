// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;

interface AggregateDefinition
{
    DataTypeDescriptor getAggregator(final DataTypeDescriptor p0, final StringBuffer p1) throws StandardException;
}
