// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import javax.jdo.metadata.Metadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.InterfaceMetaData;
import javax.jdo.metadata.InterfaceMetadata;

public class InterfaceMetadataImpl extends TypeMetadataImpl implements InterfaceMetadata
{
    public InterfaceMetadataImpl(final InterfaceMetaData internal) {
        super(internal);
    }
    
    @Override
    public AbstractMetadataImpl getParent() {
        if (this.parent == null) {
            this.parent = new PackageMetadataImpl(((InterfaceMetaData)this.internalMD).getPackageMetaData());
        }
        return super.getParent();
    }
}
