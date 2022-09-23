// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.fast;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class PaFxFastReply extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public PaFxFastReply() {
        super(PaFxFastReply.fieldInfos);
    }
    
    public KrbFastArmoredRep getFastArmoredRep() {
        return this.getChoiceValueAs(PaFxFastReplyField.ARMORED_DATA, KrbFastArmoredRep.class);
    }
    
    public void setFastArmoredRep(final KrbFastArmoredRep fastArmoredRep) {
        this.setChoiceValue(PaFxFastReplyField.ARMORED_DATA, fastArmoredRep);
    }
    
    static {
        PaFxFastReply.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(PaFxFastReplyField.ARMORED_DATA, KrbFastArmoredRep.class) };
    }
    
    protected enum PaFxFastReplyField implements EnumType
    {
        ARMORED_DATA;
        
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
