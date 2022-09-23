// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.preauth;

import org.apache.kerby.kerberos.kerb.KrbException;
import java.util.ArrayList;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import java.util.List;

public class PreauthContext
{
    private boolean preauthRequired;
    private List<PreauthHandle> handles;
    private PaData outputPaData;
    
    public PreauthContext() {
        this.preauthRequired = true;
        this.handles = new ArrayList<PreauthHandle>(5);
        this.outputPaData = new PaData();
    }
    
    public boolean isPreauthRequired() {
        return this.preauthRequired;
    }
    
    public void setPreauthRequired(final boolean preauthRequired) {
        this.preauthRequired = preauthRequired;
    }
    
    public List<PreauthHandle> getHandles() {
        return this.handles;
    }
    
    public void reset() {
        this.outputPaData = new PaData();
    }
    
    public PaData getOutputPaData() throws KrbException {
        return this.outputPaData;
    }
}
