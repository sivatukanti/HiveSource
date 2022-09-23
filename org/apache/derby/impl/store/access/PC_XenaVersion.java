// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.services.io.Formatable;

public class PC_XenaVersion implements Formatable
{
    private static final int XENA_MAJOR_VERSION = 1;
    private static final int XENA_MINOR_VERSION_0 = 0;
    private int minorVersion;
    
    public PC_XenaVersion() {
        this.minorVersion = 0;
    }
    
    private boolean isUpgradeNeeded(final PC_XenaVersion pc_XenaVersion) {
        return pc_XenaVersion == null || this.getMajorVersionNumber() != pc_XenaVersion.getMajorVersionNumber();
    }
    
    public void upgradeIfNeeded(final TransactionController transactionController, final PropertyConglomerate propertyConglomerate, final Properties properties) throws StandardException {
        final PC_XenaVersion pc_XenaVersion = (PC_XenaVersion)propertyConglomerate.getProperty(transactionController, "PropertyConglomerateVersion");
        if (this.isUpgradeNeeded(pc_XenaVersion)) {
            throw StandardException.newException("XCW00.D", pc_XenaVersion, this);
        }
    }
    
    public int getMajorVersionNumber() {
        return 1;
    }
    
    public int getMinorVersionNumber() {
        return this.minorVersion;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.getMajorVersionNumber());
        objectOutput.writeInt(this.getMinorVersionNumber());
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        objectInput.readInt();
        this.minorVersion = objectInput.readInt();
    }
    
    public int getTypeFormatId() {
        return 15;
    }
    
    public String toString() {
        return this.getMajorVersionNumber() + "." + this.getMinorVersionNumber();
    }
}
