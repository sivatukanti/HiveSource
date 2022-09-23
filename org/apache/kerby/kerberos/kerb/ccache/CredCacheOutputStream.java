// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.ccache;

import org.apache.kerby.kerberos.kerb.type.ticket.TicketFlags;
import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationDataEntry;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationData;
import org.apache.kerby.kerberos.kerb.type.base.HostAddress;
import org.apache.kerby.kerberos.kerb.type.base.HostAddresses;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import java.io.OutputStream;
import org.apache.kerby.kerberos.kerb.KrbOutputStream;

public class CredCacheOutputStream extends KrbOutputStream
{
    public CredCacheOutputStream(final OutputStream out) {
        super(out);
    }
    
    @Override
    public void writePrincipal(final PrincipalName principal, final int version) throws IOException {
        final List<String> nameComponents = principal.getNameStrings();
        if (version != 1281) {
            this.writeInt(principal.getNameType().getValue());
        }
        int numComponents = nameComponents.size();
        if (version == 1281) {
            ++numComponents;
        }
        this.writeInt(numComponents);
        this.writeRealm(principal.getRealm());
        for (final String nameCom : nameComponents) {
            this.writeCountedString(nameCom);
        }
    }
    
    @Override
    public void writeKey(final EncryptionKey key, final int version) throws IOException {
        this.writeShort(key.getKeyType().getValue());
        if (version == 1283) {
            this.writeShort(key.getKeyType().getValue());
        }
        this.writeCountedOctets(key.getKeyData());
    }
    
    public void writeTimes(final KerberosTime[] times) throws IOException {
        for (int i = 0; i < times.length; ++i) {
            this.writeTime(times[i]);
        }
    }
    
    public void writeAddresses(final HostAddresses addrs) throws IOException {
        if (addrs == null) {
            this.writeInt(0);
        }
        else {
            final List<HostAddress> addresses = addrs.getElements();
            this.writeInt(addresses.size());
            for (final HostAddress addr : addresses) {
                this.writeAddress(addr);
            }
        }
    }
    
    public void writeAddress(final HostAddress address) throws IOException {
        this.write(address.getAddrType().getValue());
        this.write(address.getAddress().length);
        this.write(address.getAddress(), 0, address.getAddress().length);
    }
    
    public void writeAuthzData(final AuthorizationData authData) throws IOException {
        if (authData == null) {
            this.writeInt(0);
        }
        else {
            for (final AuthorizationDataEntry entry : authData.getElements()) {
                this.write(entry.getAuthzType().getValue());
                this.write(entry.getAuthzData().length);
                this.write(entry.getAuthzData());
            }
        }
    }
    
    public void writeTicket(final Ticket t) throws IOException {
        if (t == null) {
            this.writeInt(0);
        }
        else {
            final byte[] bytes = t.encode();
            this.writeInt(bytes.length);
            this.write(bytes);
        }
    }
    
    public void writeIsSkey(final boolean isEncInSKey) throws IOException {
        this.writeByte(isEncInSKey ? 1 : 0);
    }
    
    public void writeTicketFlags(final TicketFlags ticketFlags) throws IOException {
        this.writeInt(ticketFlags.getFlags());
    }
}
