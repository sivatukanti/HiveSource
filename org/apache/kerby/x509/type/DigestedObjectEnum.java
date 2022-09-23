// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.EnumType;

enum DigestedObjectEnum implements EnumType
{
    PUBLIC_KEY, 
    PUBLIC_KEY_CERT, 
    OTHER_OBJECT_TYPES;
    
    @Override
    public int getValue() {
        return this.ordinal();
    }
    
    @Override
    public String getName() {
        return this.name();
    }
}
