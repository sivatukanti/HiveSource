// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;

public class SignedContentInfo extends ContentInfo
{
    public SignedContentInfo() {
        this.setAnyFieldValueType(ContentInfoField.CONTENT, SignedData.class);
    }
    
    public SignedData getSignedData() {
        return this.getFieldAsAny(ContentInfoField.CONTENT, SignedData.class);
    }
    
    public void setSignedData(final SignedData signedData) {
        this.setFieldAsAny(ContentInfoField.CONTENT, signedData);
    }
}
