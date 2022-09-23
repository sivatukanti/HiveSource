// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1BmpString;
import org.apache.kerby.asn1.type.Asn1Utf8String;
import org.apache.kerby.asn1.type.Asn1UniversalString;
import org.apache.kerby.asn1.type.Asn1PrintableString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1T61String;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class DirectoryString extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public DirectoryString() {
        super(DirectoryString.fieldInfos);
    }
    
    public Asn1T61String getTeletexString() {
        return this.getChoiceValueAs(DirectoryStringField.TELETEX_STRING, Asn1T61String.class);
    }
    
    public void setTeletexString(final Asn1T61String teletexString) {
        this.setChoiceValue(DirectoryStringField.TELETEX_STRING, teletexString);
    }
    
    public Asn1PrintableString getPrintableString() {
        return this.getChoiceValueAs(DirectoryStringField.PRINTABLE_STRING, Asn1PrintableString.class);
    }
    
    public void setPrintableString(final Asn1PrintableString printableString) {
        this.setChoiceValue(DirectoryStringField.PRINTABLE_STRING, printableString);
    }
    
    public Asn1UniversalString getUniversalString() {
        return this.getChoiceValueAs(DirectoryStringField.UNIVERSAL_STRING, Asn1UniversalString.class);
    }
    
    public void setUniversalString(final Asn1UniversalString universalString) {
        this.setChoiceValue(DirectoryStringField.UNIVERSAL_STRING, universalString);
    }
    
    public Asn1Utf8String getUtf8String() {
        return this.getChoiceValueAs(DirectoryStringField.UTF8_STRING, Asn1Utf8String.class);
    }
    
    public void setUtf8String(final Asn1Utf8String utf8String) {
        this.setChoiceValue(DirectoryStringField.UTF8_STRING, utf8String);
    }
    
    public Asn1BmpString getBmpString() {
        return this.getChoiceValueAs(DirectoryStringField.BMP_STRING, Asn1BmpString.class);
    }
    
    public void setBmpString(final Asn1BmpString bmpString) {
        this.setChoiceValue(DirectoryStringField.BMP_STRING, bmpString);
    }
    
    static {
        DirectoryString.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(DirectoryStringField.TELETEX_STRING, Asn1T61String.class), new Asn1FieldInfo(DirectoryStringField.PRINTABLE_STRING, Asn1PrintableString.class), new Asn1FieldInfo(DirectoryStringField.UNIVERSAL_STRING, Asn1UniversalString.class), new Asn1FieldInfo(DirectoryStringField.UTF8_STRING, Asn1Utf8String.class), new Asn1FieldInfo(DirectoryStringField.BMP_STRING, Asn1BmpString.class) };
    }
    
    protected enum DirectoryStringField implements EnumType
    {
        TELETEX_STRING, 
        PRINTABLE_STRING, 
        UNIVERSAL_STRING, 
        UTF8_STRING, 
        BMP_STRING;
        
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
