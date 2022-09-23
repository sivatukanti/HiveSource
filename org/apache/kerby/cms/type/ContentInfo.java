// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Any;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class ContentInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public ContentInfo() {
        super(ContentInfo.fieldInfos);
    }
    
    public String getContentType() {
        return this.getFieldAsObjId(ContentInfoField.CONTENT_TYPE);
    }
    
    public void setContentType(final String contentType) {
        this.setFieldAsObjId(ContentInfoField.CONTENT_TYPE, contentType);
    }
    
    public <T extends Asn1Type> T getContentAs(final Class<T> t) {
        return this.getFieldAsAny(ContentInfoField.CONTENT, t);
    }
    
    public void setContent(final Asn1Type content) {
        this.setFieldAsAny(ContentInfoField.CONTENT, content);
    }
    
    static {
        ContentInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(ContentInfoField.CONTENT_TYPE, Asn1ObjectIdentifier.class), new ExplicitField(ContentInfoField.CONTENT, 0, Asn1Any.class) };
    }
    
    protected enum ContentInfoField implements EnumType
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
