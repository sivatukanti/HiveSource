// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth;

import java.util.Iterator;
import org.apache.kerby.kerberos.kerb.KrbException;
import java.util.ArrayList;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;

public class PreauthContext
{
    private boolean preauthRequired;
    private PaData inputPaData;
    private PaData outputPaData;
    private PaData errorPaData;
    private UserResponser userResponser;
    private PaDataType allowedPaType;
    private final List<PaDataType> triedPaTypes;
    private final List<PreauthHandle> handles;
    
    public PreauthContext() {
        this.preauthRequired = true;
        this.userResponser = new UserResponser();
        this.triedPaTypes = new ArrayList<PaDataType>(1);
        this.handles = new ArrayList<PreauthHandle>(5);
        this.allowedPaType = PaDataType.NONE;
        this.outputPaData = new PaData();
    }
    
    public void reset() {
        this.outputPaData = new PaData();
    }
    
    public boolean isPreauthRequired() {
        return this.preauthRequired;
    }
    
    public void setPreauthRequired(final boolean preauthRequired) {
        this.preauthRequired = preauthRequired;
    }
    
    public UserResponser getUserResponser() {
        return this.userResponser;
    }
    
    public boolean isPaTypeAllowed(final PaDataType paType) {
        return this.allowedPaType == PaDataType.NONE || this.allowedPaType == paType;
    }
    
    public PaData getOutputPaData() throws KrbException {
        return this.outputPaData;
    }
    
    public boolean hasInputPaData() {
        return this.inputPaData != null && !this.inputPaData.isEmpty();
    }
    
    public PaData getInputPaData() {
        return this.inputPaData;
    }
    
    public void setInputPaData(final PaData inputPaData) {
        this.inputPaData = inputPaData;
    }
    
    public PaData getErrorPaData() {
        return this.errorPaData;
    }
    
    public void setErrorPaData(final PaData errorPaData) {
        this.errorPaData = errorPaData;
    }
    
    public void setAllowedPaType(final PaDataType paType) {
        this.allowedPaType = paType;
    }
    
    public List<PreauthHandle> getHandles() {
        return this.handles;
    }
    
    public PaDataType getAllowedPaType() {
        return this.allowedPaType;
    }
    
    public boolean checkAndPutTried(final PaDataType paType) {
        for (final PaDataType pt : this.triedPaTypes) {
            if (pt == paType) {
                return true;
            }
        }
        this.triedPaTypes.add(paType);
        return false;
    }
}
