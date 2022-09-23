// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class NoticeReference extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public NoticeReference() {
        super(NoticeReference.fieldInfos);
    }
    
    public DisplayText getOrganization() {
        return this.getFieldAs(NoticeReferenceField.ORGANIZATION, DisplayText.class);
    }
    
    public void setOrganization(final DisplayText organization) {
        this.setFieldAs(NoticeReferenceField.ORGANIZATION, organization);
    }
    
    public NoticeNumbers getNoticeNumbers() {
        return this.getFieldAs(NoticeReferenceField.NOTICE_NUMBERS, NoticeNumbers.class);
    }
    
    public void setNoticeNumbers(final NoticeNumbers noticeNumbers) {
        this.setFieldAs(NoticeReferenceField.NOTICE_NUMBERS, noticeNumbers);
    }
    
    static {
        NoticeReference.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(NoticeReferenceField.ORGANIZATION, DisplayText.class), new Asn1FieldInfo(NoticeReferenceField.NOTICE_NUMBERS, NoticeNumbers.class) };
    }
    
    protected enum NoticeReferenceField implements EnumType
    {
        ORGANIZATION, 
        NOTICE_NUMBERS;
        
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
