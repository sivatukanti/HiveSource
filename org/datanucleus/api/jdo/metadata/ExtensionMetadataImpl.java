// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.ExtensionMetaData;
import javax.jdo.metadata.ExtensionMetadata;

public class ExtensionMetadataImpl implements ExtensionMetadata
{
    ExtensionMetaData extmd;
    
    public ExtensionMetadataImpl(final String vendor, final String key, final String value) {
        this.extmd = new ExtensionMetaData(vendor, key, value);
    }
    
    public ExtensionMetadataImpl(final ExtensionMetaData extmd) {
        this.extmd = extmd;
    }
    
    public String getKey() {
        return this.extmd.getKey();
    }
    
    public String getValue() {
        return this.extmd.getValue();
    }
    
    public String getVendorName() {
        return this.extmd.getVendorName();
    }
}
