// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import java.util.Arrays;
import org.apache.kerby.asn1.EnumType;
import java.net.InetAddress;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class HostAddress extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public HostAddress() {
        super(HostAddress.fieldInfos);
    }
    
    public HostAddress(final InetAddress inetAddress) {
        super(HostAddress.fieldInfos);
        this.setAddrType(HostAddrType.ADDRTYPE_INET);
        this.setAddress(inetAddress.getAddress());
    }
    
    public HostAddrType getAddrType() {
        final Integer value = this.getFieldAsInteger(HostAddressField.ADDR_TYPE);
        return HostAddrType.fromValue(value);
    }
    
    public void setAddrType(final HostAddrType addrType) {
        this.setField(HostAddressField.ADDR_TYPE, addrType);
    }
    
    public byte[] getAddress() {
        return this.getFieldAsOctets(HostAddressField.ADDRESS);
    }
    
    public void setAddress(final byte[] address) {
        this.setFieldAsOctets(HostAddressField.ADDRESS, address);
    }
    
    public boolean equalsWith(final InetAddress address) {
        if (address == null) {
            return false;
        }
        final HostAddress that = new HostAddress(address);
        return this.equals(that);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof HostAddress)) {
            return false;
        }
        final HostAddress that = (HostAddress)other;
        return this.getAddrType() == that.getAddrType() && Arrays.equals(this.getAddress(), that.getAddress());
    }
    
    @Override
    public int hashCode() {
        int hash = 17 + this.getAddrType().getValue() * 31;
        if (this.getAddress() != null) {
            hash = 31 * hash + Arrays.hashCode(this.getAddress());
        }
        return hash;
    }
    
    static {
        HostAddress.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(HostAddressField.ADDR_TYPE, Asn1Integer.class), new ExplicitField(HostAddressField.ADDRESS, Asn1OctetString.class) };
    }
    
    protected enum HostAddressField implements EnumType
    {
        ADDR_TYPE, 
        ADDRESS;
        
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
