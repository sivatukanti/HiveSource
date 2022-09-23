// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.depend;

import java.io.ObjectOutput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.FormatableHashtable;
import java.io.ObjectInput;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.ProviderInfo;

public class BasicProviderInfo implements ProviderInfo
{
    public UUID uuid;
    public DependableFinder dFinder;
    public String providerName;
    
    public BasicProviderInfo() {
    }
    
    public BasicProviderInfo(final UUID uuid, final DependableFinder dFinder, final String providerName) {
        this.uuid = uuid;
        this.dFinder = dFinder;
        this.providerName = providerName;
    }
    
    public DependableFinder getDependableFinder() {
        return this.dFinder;
    }
    
    public UUID getObjectId() {
        return this.uuid;
    }
    
    public String getProviderName() {
        return this.providerName;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final FormatableHashtable formatableHashtable = (FormatableHashtable)objectInput.readObject();
        this.uuid = formatableHashtable.get("uuid");
        this.dFinder = (DependableFinder)formatableHashtable.get("dFinder");
        this.providerName = (String)formatableHashtable.get("providerName");
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        final FormatableHashtable formatableHashtable = new FormatableHashtable();
        formatableHashtable.put("uuid", this.uuid);
        formatableHashtable.put("dFinder", this.dFinder);
        formatableHashtable.put("providerName", this.providerName);
        objectOutput.writeObject(formatableHashtable);
    }
    
    public int getTypeFormatId() {
        return 359;
    }
    
    public String toString() {
        return "";
    }
}
