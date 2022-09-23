// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import java.util.Iterator;
import java.net.InetAddress;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceOfType;

public class HostAddresses extends KrbSequenceOfType<HostAddress>
{
    public boolean contains(final InetAddress address) {
        for (final HostAddress hostAddress : this.getElements()) {
            if (hostAddress.equalsWith(address)) {
                return true;
            }
        }
        return false;
    }
}
