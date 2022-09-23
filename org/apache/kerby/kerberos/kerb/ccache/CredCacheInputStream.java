// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.ccache;

import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;
import org.apache.kerby.kerberos.kerb.type.ticket.TicketFlags;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationType;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationDataEntry;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationData;
import org.apache.kerby.kerberos.kerb.type.base.HostAddrType;
import org.apache.kerby.kerberos.kerb.type.base.HostAddress;
import org.apache.kerby.kerberos.kerb.type.base.HostAddresses;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.apache.kerby.kerberos.kerb.type.base.NameType;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import java.io.InputStream;
import org.apache.kerby.kerberos.kerb.KrbInputStream;

public class CredCacheInputStream extends KrbInputStream
{
    public CredCacheInputStream(final InputStream in) {
        super(in);
    }
    
    @Override
    public PrincipalName readPrincipal(final int version) throws IOException {
        NameType nameType = NameType.NT_UNKNOWN;
        if (version != 1281) {
            final int typeValue = this.readInt();
            nameType = NameType.fromValue(typeValue);
        }
        int numComponents = this.readInt();
        if (version == 1281) {
            --numComponents;
        }
        final String realm = this.readCountedString();
        final List<String> nameStrings = new ArrayList<String>();
        for (int i = 0; i < numComponents; ++i) {
            final String component = this.readCountedString();
            nameStrings.add(component);
        }
        final PrincipalName principal = new PrincipalName(nameStrings, nameType);
        principal.setRealm(realm);
        return principal;
    }
    
    public EncryptionKey readKey(final int version) throws IOException {
        if (version == 1283) {
            this.readShort();
        }
        return super.readKey();
    }
    
    public KerberosTime[] readTimes() throws IOException {
        final KerberosTime[] times = new KerberosTime[4];
        for (int i = 0; i < times.length; ++i) {
            times[i] = this.readTime();
        }
        return times;
    }
    
    public boolean readIsSkey() throws IOException {
        final int value = this.readByte();
        return value == 1;
    }
    
    public HostAddresses readAddr() throws IOException {
        final int numAddresses = this.readInt();
        if (numAddresses <= 0) {
            return null;
        }
        final HostAddress[] addresses = new HostAddress[numAddresses];
        for (int i = 0; i < numAddresses; ++i) {
            addresses[i] = this.readAddress();
        }
        final HostAddresses result = new HostAddresses();
        result.addElements(addresses);
        return result;
    }
    
    public HostAddress readAddress() throws IOException {
        final int typeValue = this.readShort();
        final HostAddrType addrType = HostAddrType.fromValue(typeValue);
        if (addrType == HostAddrType.NONE) {
            throw new IOException("Invalid host address type");
        }
        final byte[] addrData = this.readCountedOctets();
        if (addrData == null) {
            throw new IOException("Invalid host address data");
        }
        final HostAddress addr = new HostAddress();
        addr.setAddrType(addrType);
        addr.setAddress(addrData);
        return addr;
    }
    
    public AuthorizationData readAuthzData() throws IOException {
        final int numEntries = this.readInt();
        if (numEntries <= 0) {
            return null;
        }
        final AuthorizationDataEntry[] authzData = new AuthorizationDataEntry[numEntries];
        for (int i = 0; i < numEntries; ++i) {
            authzData[i] = this.readAuthzDataEntry();
        }
        final AuthorizationData result = new AuthorizationData();
        result.addElements(authzData);
        return result;
    }
    
    public AuthorizationDataEntry readAuthzDataEntry() throws IOException {
        final int typeValue = this.readShort();
        final AuthorizationType authzType = AuthorizationType.fromValue(typeValue);
        if (authzType == AuthorizationType.NONE) {
            throw new IOException("Invalid authorization data type");
        }
        final byte[] authzData = this.readCountedOctets();
        if (authzData == null) {
            throw new IOException("Invalid authorization data");
        }
        final AuthorizationDataEntry authzEntry = new AuthorizationDataEntry();
        authzEntry.setAuthzType(authzType);
        authzEntry.setAuthzData(authzData);
        return authzEntry;
    }
    
    @Override
    public int readOctetsCount() throws IOException {
        return this.readInt();
    }
    
    public TicketFlags readTicketFlags() throws IOException {
        final int flags = this.readInt();
        final TicketFlags tktFlags = new TicketFlags(flags);
        return tktFlags;
    }
    
    public Ticket readTicket() throws IOException {
        final byte[] ticketData = this.readCountedOctets();
        if (ticketData == null) {
            return null;
        }
        final Ticket ticket = new Ticket();
        ticket.decode(ticketData);
        return ticket;
    }
}
