// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.auth;

import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.ServerCnxn;

public class IPAuthenticationProvider implements AuthenticationProvider
{
    @Override
    public String getScheme() {
        return "ip";
    }
    
    @Override
    public KeeperException.Code handleAuthentication(final ServerCnxn cnxn, final byte[] authData) {
        final String id = cnxn.getRemoteSocketAddress().getAddress().getHostAddress();
        cnxn.addAuthInfo(new Id(this.getScheme(), id));
        return KeeperException.Code.OK;
    }
    
    private byte[] addr2Bytes(final String addr) {
        final byte[] b = this.v4addr2Bytes(addr);
        return b;
    }
    
    private byte[] v4addr2Bytes(final String addr) {
        final String[] parts = addr.split("\\.", -1);
        if (parts.length != 4) {
            return null;
        }
        final byte[] b = new byte[4];
        for (int i = 0; i < 4; ++i) {
            try {
                final int v = Integer.parseInt(parts[i]);
                if (v < 0 || v > 255) {
                    return null;
                }
                b[i] = (byte)v;
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return b;
    }
    
    private void mask(final byte[] b, final int bits) {
        int start = bits / 8;
        int startMask = (1 << 8 - bits % 8) - 1;
        startMask ^= -1;
        while (start < b.length) {
            final int n = start;
            b[n] &= (byte)startMask;
            startMask = 0;
            ++start;
        }
    }
    
    @Override
    public boolean matches(final String id, final String aclExpr) {
        final String[] parts = aclExpr.split("/", 2);
        final byte[] aclAddr = this.addr2Bytes(parts[0]);
        if (aclAddr == null) {
            return false;
        }
        int bits = aclAddr.length * 8;
        if (parts.length == 2) {
            try {
                bits = Integer.parseInt(parts[1]);
                if (bits < 0 || bits > aclAddr.length * 8) {
                    return false;
                }
            }
            catch (NumberFormatException e) {
                return false;
            }
        }
        this.mask(aclAddr, bits);
        final byte[] remoteAddr = this.addr2Bytes(id);
        if (remoteAddr == null) {
            return false;
        }
        this.mask(remoteAddr, bits);
        for (int i = 0; i < remoteAddr.length; ++i) {
            if (remoteAddr[i] != aclAddr[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isAuthenticated() {
        return false;
    }
    
    @Override
    public boolean isValid(final String id) {
        return this.addr2Bytes(id) != null;
    }
}
