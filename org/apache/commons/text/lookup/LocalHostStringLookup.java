// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.text.lookup;

import java.net.UnknownHostException;
import java.net.InetAddress;

final class LocalHostStringLookup extends AbstractStringLookup
{
    static final LocalHostStringLookup INSTANCE;
    
    private LocalHostStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        switch (key) {
            case "name": {
                try {
                    return InetAddress.getLocalHost().getHostName();
                }
                catch (UnknownHostException e) {
                    return null;
                }
            }
            case "canonical-name": {
                try {
                    return InetAddress.getLocalHost().getCanonicalHostName();
                }
                catch (UnknownHostException e) {
                    return null;
                }
            }
            case "address": {
                try {
                    return InetAddress.getLocalHost().getHostAddress();
                }
                catch (UnknownHostException e) {
                    return null;
                }
                break;
            }
        }
        throw new IllegalArgumentException(key);
    }
    
    static {
        INSTANCE = new LocalHostStringLookup();
    }
}
