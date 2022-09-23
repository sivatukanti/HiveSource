// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1;

import org.apache.kerby.asn1.type.Asn1Type;

public class ImplicitField extends Asn1FieldInfo
{
    public ImplicitField(final EnumType index, final int tagNo, final Class<? extends Asn1Type> type) {
        super(index, tagNo, type, true);
    }
    
    public ImplicitField(final EnumType index, final Class<? extends Asn1Type> type) {
        super(index, index.getValue(), type, true);
    }
}
