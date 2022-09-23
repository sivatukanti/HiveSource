// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.x500.type.Name;
import org.apache.kerby.asn1.type.Asn1Any;
import org.apache.kerby.asn1.type.Asn1IA5String;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class GeneralName extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public GeneralName() {
        super(GeneralName.fieldInfos);
    }
    
    public OtherName getOtherName() {
        return this.getChoiceValueAs(GeneralNameField.OTHER_NAME, OtherName.class);
    }
    
    public void setOtherName(final OtherName otherName) {
        this.setChoiceValue(GeneralNameField.OTHER_NAME, otherName);
    }
    
    public Asn1IA5String getRfc822Name() {
        return this.getChoiceValueAs(GeneralNameField.RFC822_NAME, Asn1IA5String.class);
    }
    
    public void setRfc822Name(final Asn1IA5String rfc822Name) {
        this.setChoiceValue(GeneralNameField.RFC822_NAME, rfc822Name);
    }
    
    public Asn1IA5String getDNSName() {
        return this.getChoiceValueAs(GeneralNameField.DNS_NAME, Asn1IA5String.class);
    }
    
    public void setDNSName(final Asn1IA5String dnsName) {
        this.setChoiceValue(GeneralNameField.DNS_NAME, dnsName);
    }
    
    public Asn1Any getX400Address() {
        return this.getChoiceValueAs(GeneralNameField.X400_ADDRESS, Asn1Any.class);
    }
    
    public void setX400Address(final Asn1Any x400Address) {
        this.setChoiceValue(GeneralNameField.X400_ADDRESS, x400Address);
    }
    
    public Name getDirectoryName() {
        return this.getChoiceValueAs(GeneralNameField.DIRECTORY_NAME, Name.class);
    }
    
    public void setDirectoryName(final Name directoryName) {
        this.setChoiceValue(GeneralNameField.DIRECTORY_NAME, directoryName);
    }
    
    public EDIPartyName getEdiPartyName() {
        return this.getChoiceValueAs(GeneralNameField.EDI_PARTY_NAME, EDIPartyName.class);
    }
    
    public void setEdiPartyName(final EDIPartyName ediPartyName) {
        this.setChoiceValue(GeneralNameField.EDI_PARTY_NAME, ediPartyName);
    }
    
    public Asn1IA5String getUniformResourceIdentifier() {
        return this.getChoiceValueAs(GeneralNameField.UNIFORM_RESOURCE_IDENTIFIER, Asn1IA5String.class);
    }
    
    public void setUniformResourceIdentifier(final Asn1IA5String uniformResourceIdentifier) {
        this.setChoiceValue(GeneralNameField.UNIFORM_RESOURCE_IDENTIFIER, uniformResourceIdentifier);
    }
    
    public byte[] getIPAddress() {
        return this.getChoiceValueAsOctets(GeneralNameField.IP_ADDRESS);
    }
    
    public void setIpAddress(final byte[] ipAddress) {
        this.setChoiceValueAsOctets(GeneralNameField.IP_ADDRESS, ipAddress);
    }
    
    public Asn1ObjectIdentifier getRegisteredID() {
        return this.getChoiceValueAs(GeneralNameField.REGISTERED_ID, Asn1ObjectIdentifier.class);
    }
    
    public void setRegisteredID(final Asn1ObjectIdentifier registeredID) {
        this.setChoiceValue(GeneralNameField.REGISTERED_ID, registeredID);
    }
    
    static {
        GeneralName.fieldInfos = new Asn1FieldInfo[] { new ImplicitField(GeneralNameField.OTHER_NAME, OtherName.class), new ImplicitField(GeneralNameField.RFC822_NAME, Asn1IA5String.class), new ImplicitField(GeneralNameField.DNS_NAME, Asn1IA5String.class), new ImplicitField(GeneralNameField.X400_ADDRESS, Asn1Any.class), new ExplicitField(GeneralNameField.DIRECTORY_NAME, Name.class), new ImplicitField(GeneralNameField.EDI_PARTY_NAME, EDIPartyName.class), new ImplicitField(GeneralNameField.UNIFORM_RESOURCE_IDENTIFIER, Asn1IA5String.class), new ImplicitField(GeneralNameField.IP_ADDRESS, Asn1OctetString.class), new ImplicitField(GeneralNameField.REGISTERED_ID, Asn1ObjectIdentifier.class) };
    }
    
    protected enum GeneralNameField implements EnumType
    {
        OTHER_NAME, 
        RFC822_NAME, 
        DNS_NAME, 
        X400_ADDRESS, 
        DIRECTORY_NAME, 
        EDI_PARTY_NAME, 
        UNIFORM_RESOURCE_IDENTIFIER, 
        IP_ADDRESS, 
        REGISTERED_ID;
        
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
