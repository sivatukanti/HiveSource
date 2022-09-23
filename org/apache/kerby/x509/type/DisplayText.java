// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Utf8String;
import org.apache.kerby.asn1.type.Asn1BmpString;
import org.apache.kerby.asn1.type.Asn1VisibleString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1IA5String;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class DisplayText extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public DisplayText() {
        super(DisplayText.fieldInfos);
    }
    
    public Asn1IA5String getIA5String() {
        return this.getChoiceValueAs(DisplayTextField.IA5_STRING, Asn1IA5String.class);
    }
    
    public void setIA5String(final Asn1IA5String ia5String) {
        this.setChoiceValue(DisplayTextField.IA5_STRING, ia5String);
    }
    
    public Asn1VisibleString getVisibleString() {
        return this.getChoiceValueAs(DisplayTextField.VISIBLE_STRING, Asn1VisibleString.class);
    }
    
    public void setVisibleString(final Asn1VisibleString visibleString) {
        this.setChoiceValue(DisplayTextField.VISIBLE_STRING, visibleString);
    }
    
    public Asn1BmpString getBmpString() {
        return this.getChoiceValueAs(DisplayTextField.BMP_STRING, Asn1BmpString.class);
    }
    
    public void setBmpString(final Asn1BmpString bmpString) {
        this.setChoiceValue(DisplayTextField.BMP_STRING, bmpString);
    }
    
    public Asn1Utf8String getUtf8String() {
        return this.getChoiceValueAs(DisplayTextField.UTF8_STRING, Asn1Utf8String.class);
    }
    
    public void setUtf8String(final Asn1Utf8String utf8String) {
        this.setChoiceValue(DisplayTextField.UTF8_STRING, utf8String);
    }
    
    static {
        DisplayText.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(DisplayTextField.IA5_STRING, Asn1IA5String.class), new Asn1FieldInfo(DisplayTextField.VISIBLE_STRING, Asn1VisibleString.class), new Asn1FieldInfo(DisplayTextField.BMP_STRING, Asn1BmpString.class), new Asn1FieldInfo(DisplayTextField.UTF8_STRING, Asn1BmpString.class) };
    }
    
    protected enum DisplayTextField implements EnumType
    {
        IA5_STRING, 
        VISIBLE_STRING, 
        BMP_STRING, 
        UTF8_STRING;
        
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
