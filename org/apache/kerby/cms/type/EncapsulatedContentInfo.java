// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class EncapsulatedContentInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public EncapsulatedContentInfo() {
        super(EncapsulatedContentInfo.fieldInfos);
    }
    
    public String getContentType() {
        return this.getFieldAsObjId(EncapsulatedContentInfoField.CONTENT_TYPE);
    }
    
    public void setContentType(final String contentType) {
        this.setFieldAsObjId(EncapsulatedContentInfoField.CONTENT_TYPE, contentType);
    }
    
    public byte[] getContent() {
        return this.getFieldAsOctets(EncapsulatedContentInfoField.CONTENT);
    }
    
    public void setContent(final byte[] content) {
        this.setFieldAsOctets(EncapsulatedContentInfoField.CONTENT, content);
    }
    
    static {
        EncapsulatedContentInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(EncapsulatedContentInfoField.CONTENT_TYPE, Asn1ObjectIdentifier.class), new ExplicitField(EncapsulatedContentInfoField.CONTENT, 0, Asn1OctetString.class) };
    }
    
    protected enum EncapsulatedContentInfoField implements EnumType
    {
        CONTENT_TYPE, 
        CONTENT;
        
        @Override
        public int getValue() {
            return this.ordinal();
        }
        
        @Override
        public String getName() {
            return this.name();
        }
    }
}
