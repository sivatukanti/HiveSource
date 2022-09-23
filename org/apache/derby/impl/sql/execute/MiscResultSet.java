// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;

class MiscResultSet extends NoRowsResultSetImpl
{
    MiscResultSet(final Activation activation) {
        super(activation);
    }
    
    public void open() throws StandardException {
        this.setup();
        this.activation.getConstantAction().executeConstantAction(this.activation);
        this.close();
    }
    
    public void cleanUp() {
    }
}
