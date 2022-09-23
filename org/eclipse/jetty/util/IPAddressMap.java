// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.BitSet;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class IPAddressMap<TYPE> extends HashMap<String, TYPE>
{
    private final HashMap<String, IPAddrPattern> _patterns;
    
    public IPAddressMap() {
        super(11);
        this._patterns = new HashMap<String, IPAddrPattern>();
    }
    
    public IPAddressMap(final int capacity) {
        super(capacity);
        this._patterns = new HashMap<String, IPAddrPattern>();
    }
    
    @Override
    public TYPE put(final String addrSpec, final TYPE object) throws IllegalArgumentException {
        if (addrSpec == null || addrSpec.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid IP address pattern: " + addrSpec);
        }
        final String spec = addrSpec.trim();
        if (this._patterns.get(spec) == null) {
            this._patterns.put(spec, new IPAddrPattern(spec));
        }
        return super.put(spec, object);
    }
    
    @Override
    public TYPE get(final Object key) {
        return super.get(key);
    }
    
    public TYPE match(final String addr) {
        final Map.Entry<String, TYPE> entry = this.getMatch(addr);
        return (entry == null) ? null : entry.getValue();
    }
    
    public Map.Entry<String, TYPE> getMatch(final String addr) {
        if (addr != null) {
            for (final Map.Entry<String, TYPE> entry : super.entrySet()) {
                if (this._patterns.get(entry.getKey()).match(addr)) {
                    return entry;
                }
            }
        }
        return null;
    }
    
    public Object getLazyMatches(final String addr) {
        if (addr == null) {
            return LazyList.getList(super.entrySet());
        }
        Object entries = null;
        for (final Map.Entry<String, TYPE> entry : super.entrySet()) {
            if (this._patterns.get(entry.getKey()).match(addr)) {
                entries = LazyList.add(entries, entry);
            }
        }
        return entries;
    }
    
    private static class IPAddrPattern
    {
        private final OctetPattern[] _octets;
        
        public IPAddrPattern(final String value) throws IllegalArgumentException {
            this._octets = new OctetPattern[4];
            if (value == null || value.trim().length() == 0) {
                throw new IllegalArgumentException("Invalid IP address pattern: " + value);
            }
            try {
                final StringTokenizer parts = new StringTokenizer(value, ".");
                for (int idx = 0; idx < 4; ++idx) {
                    final String part = parts.hasMoreTokens() ? parts.nextToken().trim() : "0-255";
                    final int len = part.length();
                    if (len == 0 && parts.hasMoreTokens()) {
                        throw new IllegalArgumentException("Invalid IP address pattern: " + value);
                    }
                    this._octets[idx] = new OctetPattern((len == 0) ? "0-255" : part);
                }
            }
            catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid IP address pattern: " + value, ex);
            }
        }
        
        public boolean match(final String value) throws IllegalArgumentException {
            if (value == null || value.trim().length() == 0) {
                throw new IllegalArgumentException("Invalid IP address: " + value);
            }
            try {
                final StringTokenizer parts = new StringTokenizer(value, ".");
                boolean result = true;
                for (int idx = 0; idx < 4; ++idx) {
                    if (!parts.hasMoreTokens()) {
                        throw new IllegalArgumentException("Invalid IP address: " + value);
                    }
                    if (!(result &= this._octets[idx].match(parts.nextToken()))) {
                        break;
                    }
                }
                return result;
            }
            catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid IP address: " + value, ex);
            }
        }
    }
    
    private static class OctetPattern extends BitSet
    {
        private final BitSet _mask;
        
        public OctetPattern(final String octetSpec) throws IllegalArgumentException {
            this._mask = new BitSet(256);
            try {
                if (octetSpec != null) {
                    final String spec = octetSpec.trim();
                    if (spec.length() == 0) {
                        this._mask.set(0, 255);
                    }
                    else {
                        final StringTokenizer parts = new StringTokenizer(spec, ",");
                        while (parts.hasMoreTokens()) {
                            final String part = parts.nextToken().trim();
                            if (part.length() > 0) {
                                if (part.indexOf(45) < 0) {
                                    final Integer value = Integer.valueOf(part);
                                    this._mask.set(value);
                                }
                                else {
                                    int low = 0;
                                    int high = 255;
                                    final String[] bounds = part.split("-", -2);
                                    if (bounds.length != 2) {
                                        throw new IllegalArgumentException("Invalid octet spec: " + octetSpec);
                                    }
                                    if (bounds[0].length() > 0) {
                                        low = Integer.parseInt(bounds[0]);
                                    }
                                    if (bounds[1].length() > 0) {
                                        high = Integer.parseInt(bounds[1]);
                                    }
                                    if (low > high) {
                                        throw new IllegalArgumentException("Invalid octet spec: " + octetSpec);
                                    }
                                    this._mask.set(low, high + 1);
                                }
                            }
                        }
                    }
                }
            }
            catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid octet spec: " + octetSpec, ex);
            }
        }
        
        public boolean match(final String value) throws IllegalArgumentException {
            if (value == null || value.trim().length() == 0) {
                throw new IllegalArgumentException("Invalid octet: " + value);
            }
            try {
                final int number = Integer.parseInt(value);
                return this.match(number);
            }
            catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid octet: " + value);
            }
        }
        
        public boolean match(final int number) throws IllegalArgumentException {
            if (number < 0 || number > 255) {
                throw new IllegalArgumentException("Invalid octet: " + number);
            }
            return this._mask.get(number);
        }
    }
}
