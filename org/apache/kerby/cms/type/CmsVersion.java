// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Integer;

public class CmsVersion extends Asn1Integer
{
    public CmsVersion() {
        this(CmsVersionEnum.V0);
    }
    
    public CmsVersion(final CmsVersionEnum version) {
        super(Integer.valueOf(version.getValue()));
    }
}
