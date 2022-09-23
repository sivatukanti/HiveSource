// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication.net;

import java.net.UnknownHostException;
import java.net.InetAddress;

public class SlaveAddress
{
    private final InetAddress hostAddress;
    private final int portNumber;
    public static final int DEFAULT_PORT_NO = 4851;
    
    public SlaveAddress(final String host, final int portNumber) throws UnknownHostException {
        this.hostAddress = InetAddress.getByName(host);
        if (portNumber > 0) {
            this.portNumber = portNumber;
        }
        else {
            this.portNumber = 4851;
        }
    }
    
    public InetAddress getHostAddress() {
        return this.hostAddress;
    }
    
    public int getPortNumber() {
        return this.portNumber;
    }
}
