// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.services.io.Formatable;

public interface ExecAggregator extends Formatable
{
    void setup(final ClassFactory p0, final String p1, final DataTypeDescriptor p2);
    
    void accumulate(final DataValueDescriptor p0, final Object p1) throws StandardException;
    
    void merge(final ExecAggregator p0) throws StandardException;
    
    DataValueDescriptor getResult() throws StandardException;
    
    ExecAggregator newAggregator();
    
    boolean didEliminateNulls();
}
