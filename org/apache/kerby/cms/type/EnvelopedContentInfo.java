// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;

public class EnvelopedContentInfo extends ContentInfo
{
    public EnvelopedContentInfo() {
        this.setAnyFieldValueType(ContentInfoField.CONTENT, EnvelopedData.class);
    }
    
    public EnvelopedData getEnvelopedData() {
        return this.getFieldAsAny(ContentInfoField.CONTENT, EnvelopedData.class);
    }
    
    public void setEnvelopedData(final EnvelopedData signedData) {
        this.setFieldAsAny(ContentInfoField.CONTENT, signedData);
    }
}
