// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class RecipientInfo extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public RecipientInfo() {
        super(RecipientInfo.fieldInfos);
    }
    
    public KeyTransRecipientInfo getKtri() {
        return this.getChoiceValueAs(RecipientInfoField.KTRI, KeyTransRecipientInfo.class);
    }
    
    public void setKtri(final KeyTransRecipientInfo ktri) {
        this.setChoiceValue(RecipientInfoField.KTRI, ktri);
    }
    
    public KeyAgreeRecipientInfo getKari() {
        return this.getChoiceValueAs(RecipientInfoField.KARI, KeyAgreeRecipientInfo.class);
    }
    
    public void setKari(final KeyAgreeRecipientInfo kari) {
        this.setChoiceValue(RecipientInfoField.KARI, kari);
    }
    
    public KEKRecipientInfo getKekri() {
        return this.getChoiceValueAs(RecipientInfoField.KEKRI, KEKRecipientInfo.class);
    }
    
    public void setKekri(final KEKRecipientInfo kekri) {
        this.setChoiceValue(RecipientInfoField.KEKRI, kekri);
    }
    
    public PasswordRecipientInfo getPwri() {
        return this.getChoiceValueAs(RecipientInfoField.PWRI, PasswordRecipientInfo.class);
    }
    
    public void setPwri(final PasswordRecipientInfo pwri) {
        this.setChoiceValue(RecipientInfoField.PWRI, pwri);
    }
    
    public OtherRecipientInfo getori() {
        return this.getChoiceValueAs(RecipientInfoField.ORI, OtherRecipientInfo.class);
    }
    
    public void setOri(final OtherRecipientInfo ori) {
        this.setChoiceValue(RecipientInfoField.ORI, ori);
    }
    
    static {
        RecipientInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(RecipientInfoField.KTRI, KeyTransRecipientInfo.class), new ImplicitField(RecipientInfoField.KARI, KeyAgreeRecipientInfo.class), new ImplicitField(RecipientInfoField.KEKRI, KEKRecipientInfo.class), new ImplicitField(RecipientInfoField.PWRI, PasswordRecipientInfo.class), new ImplicitField(RecipientInfoField.ORI, OtherRecipientInfo.class) };
    }
    
    protected enum RecipientInfoField implements EnumType
    {
        KTRI, 
        KARI, 
        KEKRI, 
        PWRI, 
        ORI;
        
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
