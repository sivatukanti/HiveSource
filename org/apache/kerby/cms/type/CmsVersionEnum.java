// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.EnumType;

enum CmsVersionEnum implements EnumType
{
    V0, 
    V1, 
    V2, 
    V3, 
    V4, 
    V5;
    
    @Override
    public int getValue() {
        return this.ordinal();
    }
    
    @Override
    public String getName() {
        return this.name();
    }
}
