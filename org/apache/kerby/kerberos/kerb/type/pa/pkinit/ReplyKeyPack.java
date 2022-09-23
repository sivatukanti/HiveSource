// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.pkinit;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class ReplyKeyPack extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public ReplyKeyPack() {
        super(ReplyKeyPack.fieldInfos);
    }
    
    public EncryptionKey getReplyKey() {
        return this.getFieldAs(ReplyKeyPackField.REPLY_KEY, EncryptionKey.class);
    }
    
    public void setReplyKey(final EncryptionKey replyKey) {
        this.setFieldAs(ReplyKeyPackField.REPLY_KEY, replyKey);
    }
    
    public CheckSum getAsChecksum() {
        return this.getFieldAs(ReplyKeyPackField.AS_CHECKSUM, CheckSum.class);
    }
    
    public void setAsChecksum(final CheckSum checkSum) {
        this.setFieldAs(ReplyKeyPackField.AS_CHECKSUM, checkSum);
    }
    
    static {
        ReplyKeyPack.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(ReplyKeyPackField.REPLY_KEY, EncryptionKey.class), new ExplicitField(ReplyKeyPackField.AS_CHECKSUM, CheckSum.class) };
    }
    
    protected enum ReplyKeyPackField implements EnumType
    {
        REPLY_KEY, 
        AS_CHECKSUM;
        
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
