// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1;

import org.apache.kerby.asn1.type.Asn1Type;

public class ExplicitField extends Asn1FieldInfo
{
    public ExplicitField(final EnumType index, final int tagNo, final Class<? extends Asn1Type> type) {
        super(index, tagNo, type, false);
    }
    
    public ExplicitField(final EnumType index, final Class<? extends Asn1Type> type) {
        super(index, index.getValue(), type, false);
    }
}
