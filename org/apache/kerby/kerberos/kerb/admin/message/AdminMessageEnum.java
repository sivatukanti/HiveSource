// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.message;

import org.apache.kerby.xdr.EnumType;
import org.apache.kerby.xdr.type.XdrEnumerated;

public class AdminMessageEnum extends XdrEnumerated<AdminMessageType>
{
    public AdminMessageEnum() {
        super((EnumType)null);
    }
    
    public AdminMessageEnum(final AdminMessageType value) {
        super(value);
    }
    
    @Override
    protected EnumType[] getAllEnumValues() {
        return AdminMessageType.values();
    }
}
