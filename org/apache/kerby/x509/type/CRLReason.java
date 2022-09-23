// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1Enumerated;

public class CRLReason extends Asn1Enumerated<CRLReasonEnum>
{
    public EnumType[] getAllEnumValues() {
        return CRLReasonEnum.values();
    }
}
