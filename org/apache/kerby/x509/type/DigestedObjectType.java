// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1Enumerated;

public class DigestedObjectType extends Asn1Enumerated<DigestedObjectEnum>
{
    public EnumType[] getAllEnumValues() {
        return DigestedObjectEnum.values();
    }
}
