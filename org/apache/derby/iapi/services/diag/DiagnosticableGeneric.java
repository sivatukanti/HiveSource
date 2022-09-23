// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.diag;

import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;

public class DiagnosticableGeneric implements Diagnosticable
{
    protected Object diag_object;
    
    public DiagnosticableGeneric() {
        this.diag_object = null;
    }
    
    public void init(final Object diag_object) {
        this.diag_object = diag_object;
    }
    
    public String diag() throws StandardException {
        return this.diag_object.toString();
    }
    
    public void diag_detail(final Properties properties) throws StandardException {
    }
}
