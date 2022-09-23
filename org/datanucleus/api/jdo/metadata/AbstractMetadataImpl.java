// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.ExtensionMetaData;
import javax.jdo.metadata.ExtensionMetadata;
import org.datanucleus.metadata.MetaData;
import javax.jdo.metadata.Metadata;

public class AbstractMetadataImpl implements Metadata
{
    AbstractMetadataImpl parent;
    MetaData internalMD;
    
    public AbstractMetadataImpl(final MetaData internal) {
        this.internalMD = internal;
    }
    
    @Override
    public String toString() {
        return this.internalMD.toString("", "    ");
    }
    
    public ExtensionMetadata[] getExtensions() {
        final ExtensionMetaData[] exts = this.internalMD.getExtensions();
        if (exts == null) {
            return null;
        }
        final ExtensionMetadata[] extensions = new ExtensionMetadata[exts.length];
        for (int i = 0; i < extensions.length; ++i) {
            extensions[i] = new ExtensionMetadataImpl(exts[i]);
        }
        return extensions;
    }
    
    public int getNumberOfExtensions() {
        return this.internalMD.getNoOfExtensions();
    }
    
    public ExtensionMetadata newExtensionMetadata(final String vendor, final String key, final String value) {
        return new ExtensionMetadataImpl(this.internalMD.newExtensionMetaData(vendor, key, value));
    }
    
    public AbstractMetadataImpl getParent() {
        return this.parent;
    }
}
