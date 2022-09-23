// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;

public interface ConstantAction
{
    void executeConstantAction(final Activation p0) throws StandardException;
}
