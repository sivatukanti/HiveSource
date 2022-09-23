// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;

public class CompressedContentInfo extends ContentInfo
{
    public CompressedContentInfo() {
        this.setAnyFieldValueType(ContentInfoField.CONTENT, CompressedData.class);
    }
    
    public CompressedData getCompressedData() {
        return this.getFieldAsAny(ContentInfoField.CONTENT, CompressedData.class);
    }
    
    public void setCompressedData(final CompressedData signedData) {
        this.setFieldAsAny(ContentInfoField.CONTENT, signedData);
    }
}
