// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class UserNotice extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public UserNotice() {
        super(UserNotice.fieldInfos);
    }
    
    public NoticeReference getNoticeRef() {
        return this.getFieldAs(UserNoticeField.NOTICE_REF, NoticeReference.class);
    }
    
    public void setNoticeRef(final NoticeReference noticeRef) {
        this.setFieldAs(UserNoticeField.NOTICE_REF, noticeRef);
    }
    
    public DisplayText getExplicitText() {
        return this.getFieldAs(UserNoticeField.EXPLICIT_TEXT, DisplayText.class);
    }
    
    public void setExplicitText(final DisplayText explicitText) {
        this.setFieldAs(UserNoticeField.EXPLICIT_TEXT, explicitText);
    }
    
    static {
        UserNotice.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(UserNoticeField.NOTICE_REF, NoticeReference.class), new Asn1FieldInfo(UserNoticeField.EXPLICIT_TEXT, DisplayText.class) };
    }
    
    protected enum UserNoticeField implements EnumType
    {
        NOTICE_REF, 
        EXPLICIT_TEXT;
        
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
