// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.net.InetAddress;
import java.util.function.Predicate;
import java.util.Set;
import java.util.AbstractSet;

public class InetAddressSet extends AbstractSet<String> implements Set<String>, Predicate<InetAddress>
{
    private Map<String, InetPattern> _patterns;
    
    public InetAddressSet() {
        this._patterns = new HashMap<String, InetPattern>();
    }
    
    @Override
    public boolean add(final String pattern) {
        return this._patterns.put(pattern, this.newInetRange(pattern)) == null;
    }
    
    protected InetPattern newInetRange(final String pattern) {
        if (pattern == null) {
            return null;
        }
        final int slash = pattern.lastIndexOf(47);
        final int dash = pattern.lastIndexOf(45);
        try {
            if (slash >= 0) {
                return new CidrInetRange(pattern, InetAddress.getByName(pattern.substring(0, slash).trim()), StringUtil.toInt(pattern, slash + 1));
            }
            if (dash >= 0) {
                return new MinMaxInetRange(pattern, InetAddress.getByName(pattern.substring(0, dash).trim()), InetAddress.getByName(pattern.substring(dash + 1).trim()));
            }
            return new SingletonInetRange(pattern, InetAddress.getByName(pattern));
        }
        catch (Exception e) {
            try {
                if (slash < 0 && dash > 0) {
                    return new LegacyInetRange(pattern);
                }
            }
            catch (Exception e2) {
                e.addSuppressed(e2);
            }
            throw new IllegalArgumentException("Bad pattern: " + pattern, e);
        }
    }
    
    @Override
    public boolean remove(final Object pattern) {
        return this._patterns.remove(pattern) != null;
    }
    
    @Override
    public Iterator<String> iterator() {
        return this._patterns.keySet().iterator();
    }
    
    @Override
    public int size() {
        return this._patterns.size();
    }
    
    @Override
    public boolean test(final InetAddress address) {
        if (address == null) {
            return false;
        }
        final byte[] raw = address.getAddress();
        for (final InetPattern pattern : this._patterns.values()) {
            if (pattern.test(address, raw)) {
                return true;
            }
        }
        return false;
    }
    
    abstract static class InetPattern
    {
        final String _pattern;
        
        InetPattern(final String pattern) {
            this._pattern = pattern;
        }
        
        abstract boolean test(final InetAddress p0, final byte[] p1);
        
        @Override
        public String toString() {
            return this._pattern;
        }
    }
    
    static class SingletonInetRange extends InetPattern
    {
        final InetAddress _address;
        
        public SingletonInetRange(final String pattern, final InetAddress address) {
            super(pattern);
            this._address = address;
        }
        
        public boolean test(final InetAddress address, final byte[] raw) {
            return this._address.equals(address);
        }
    }
    
    static class MinMaxInetRange extends InetPattern
    {
        final int[] _min;
        final int[] _max;
        
        public MinMaxInetRange(final String pattern, final InetAddress min, final InetAddress max) {
            super(pattern);
            final byte[] raw_min = min.getAddress();
            final byte[] raw_max = max.getAddress();
            if (raw_min.length != raw_max.length) {
                throw new IllegalArgumentException("Cannot mix IPv4 and IPv6: " + pattern);
            }
            if (raw_min.length == 4) {
                int count = 0;
                for (final char c : pattern.toCharArray()) {
                    if (c == '.') {
                        ++count;
                    }
                }
                if (count != 6) {
                    throw new IllegalArgumentException("Legacy pattern: " + pattern);
                }
            }
            this._min = new int[raw_min.length];
            this._max = new int[raw_min.length];
            for (int i = 0; i < this._min.length; ++i) {
                this._min[i] = (0xFF & raw_min[i]);
                this._max[i] = (0xFF & raw_max[i]);
            }
            for (int i = 0; i < this._min.length; ++i) {
                if (this._min[i] > this._max[i]) {
                    throw new IllegalArgumentException("min is greater than max: " + pattern);
                }
                if (this._min[i] < this._max[i]) {
                    break;
                }
            }
        }
        
        public boolean test(final InetAddress item, final byte[] raw) {
            if (raw.length != this._min.length) {
                return false;
            }
            boolean min_ok = false;
            boolean max_ok = false;
            for (int i = 0; i < this._min.length; ++i) {
                final int r = 0xFF & raw[i];
                if (!min_ok) {
                    if (r < this._min[i]) {
                        return false;
                    }
                    if (r > this._min[i]) {
                        min_ok = true;
                    }
                }
                if (!max_ok) {
                    if (r > this._max[i]) {
                        return false;
                    }
                    if (r < this._max[i]) {
                        max_ok = true;
                    }
                }
                if (min_ok && max_ok) {
                    break;
                }
            }
            return true;
        }
    }
    
    static class CidrInetRange extends InetPattern
    {
        final byte[] _raw;
        final int _octets;
        final int _mask;
        final int _masked;
        
        public CidrInetRange(final String pattern, final InetAddress address, final int cidr) {
            super(pattern);
            this._raw = address.getAddress();
            this._octets = cidr / 8;
            this._mask = (0xFF & 255 << 8 - cidr % 8);
            this._masked = ((this._mask == 0) ? 0 : (this._raw[this._octets] & this._mask));
            if (cidr > this._raw.length * 8) {
                throw new IllegalArgumentException("CIDR too large: " + pattern);
            }
            if (this._mask != 0 && (0xFF & this._raw[this._octets]) != this._masked) {
                throw new IllegalArgumentException("CIDR bits non zero: " + pattern);
            }
            for (int o = this._octets + ((this._mask != 0) ? 1 : 0); o < this._raw.length; ++o) {
                if (this._raw[o] != 0) {
                    throw new IllegalArgumentException("CIDR bits non zero: " + pattern);
                }
            }
        }
        
        public boolean test(final InetAddress item, final byte[] raw) {
            if (raw.length != this._raw.length) {
                return false;
            }
            for (int o = 0; o < this._octets; ++o) {
                if (this._raw[o] != raw[o]) {
                    return false;
                }
            }
            return this._mask == 0 || (raw[this._octets] & this._mask) == this._masked;
        }
    }
    
    static class LegacyInetRange extends InetPattern
    {
        int[] _min;
        int[] _max;
        
        public LegacyInetRange(final String pattern) {
            super(pattern);
            this._min = new int[4];
            this._max = new int[4];
            final String[] parts = pattern.split("\\.");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Bad legacy pattern: " + pattern);
            }
            for (int i = 0; i < 4; ++i) {
                final String part = parts[i].trim();
                final int dash = part.indexOf(45);
                if (dash < 0) {
                    this._min[i] = (this._max[i] = Integer.parseInt(part));
                }
                else {
                    this._min[i] = ((dash == 0) ? 0 : StringUtil.toInt(part, 0));
                    this._max[i] = ((dash == part.length() - 1) ? 255 : StringUtil.toInt(part, dash + 1));
                }
                if (this._min[i] < 0 || this._min[i] > this._max[i] || this._max[i] > 255) {
                    throw new IllegalArgumentException("Bad legacy pattern: " + pattern);
                }
            }
        }
        
        public boolean test(final InetAddress item, final byte[] raw) {
            if (raw.length != 4) {
                return false;
            }
            for (int i = 0; i < 4; ++i) {
                if ((0xFF & raw[i]) < this._min[i] || (0xFF & raw[i]) > this._max[i]) {
                    return false;
                }
            }
            return true;
        }
    }
}
