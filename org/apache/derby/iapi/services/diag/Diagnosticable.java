// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.diag;

import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;

public interface Diagnosticable
{
    void init(final Object p0);
    
    String diag() throws StandardException;
    
    void diag_detail(final Properties p0) throws StandardException;
}
