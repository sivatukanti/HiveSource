// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import java.util.NoSuchElementException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class DefaultHttpHeaders extends HttpHeaders
{
    private static final int BUCKET_SIZE = 17;
    private static final Set<String> KNOWN_NAMES;
    private static final Set<String> KNOWN_VALUES;
    private final HeaderEntry[] entries;
    private final HeaderEntry head;
    protected final boolean validate;
    
    private static Set<String> createSet(final Class<?> clazz) {
        final Set<String> set = new HashSet<String>();
        final Field[] arr$;
        final Field[] fields = arr$ = clazz.getDeclaredFields();
        for (final Field f : arr$) {
            final int m = f.getModifiers();
            if (Modifier.isPublic(m) && Modifier.isStatic(m) && Modifier.isFinal(m) && f.getType().isAssignableFrom(String.class)) {
                try {
                    set.add((String)f.get(null));
                }
                catch (Throwable t) {}
            }
        }
        return set;
    }
    
    private static int hash(final String name, final boolean validate) {
        int h = 0;
        for (int i = name.length() - 1; i >= 0; --i) {
            char c = name.charAt(i);
            if (validate) {
                HttpHeaders.valideHeaderNameChar(c);
            }
            c = toLowerCase(c);
            h = 31 * h + c;
        }
        if (h > 0) {
            return h;
        }
        if (h == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
        }
        return -h;
    }
    
    private static boolean eq(final String name1, final String name2) {
        if (name1 == name2) {
            return true;
        }
        final int nameLen = name1.length();
        if (nameLen != name2.length()) {
            return false;
        }
        for (int i = nameLen - 1; i >= 0; --i) {
            final char c1 = name1.charAt(i);
            final char c2 = name2.charAt(i);
            if (c1 != c2 && toLowerCase(c1) != toLowerCase(c2)) {
                return false;
            }
        }
        return true;
    }
    
    private static char toLowerCase(char c) {
        if (c >= 'A' && c <= 'Z') {
            c += ' ';
        }
        return c;
    }
    
    private static int index(final int hash) {
        return hash % 17;
    }
    
    public DefaultHttpHeaders() {
        this(true);
    }
    
    public DefaultHttpHeaders(final boolean validate) {
        this.entries = new HeaderEntry[17];
        this.head = new HeaderEntry(-1, null, null);
        final HeaderEntry head = this.head;
        final HeaderEntry head2 = this.head;
        final HeaderEntry head3 = this.head;
        head2.after = head3;
        head.before = head3;
        this.validate = validate;
    }
    
    void validateHeaderValue0(final String headerValue) {
        if (DefaultHttpHeaders.KNOWN_VALUES.contains(headerValue)) {
            return;
        }
        HttpHeaders.validateHeaderValue(headerValue);
    }
    
    @Override
    public HttpHeaders add(final String name, final Object value) {
        final String strVal = toString(value);
        boolean validateName = false;
        if (this.validate) {
            this.validateHeaderValue0(strVal);
            validateName = !DefaultHttpHeaders.KNOWN_NAMES.contains(name);
        }
        final int h = hash(name, validateName);
        final int i = index(h);
        this.add0(h, i, name, strVal);
        return this;
    }
    
    @Override
    public HttpHeaders add(final String name, final Iterable<?> values) {
        boolean validateName = false;
        if (this.validate) {
            validateName = !DefaultHttpHeaders.KNOWN_NAMES.contains(name);
        }
        final int h = hash(name, validateName);
        final int i = index(h);
        for (final Object v : values) {
            final String vstr = toString(v);
            if (this.validate) {
                this.validateHeaderValue0(vstr);
            }
            this.add0(h, i, name, vstr);
        }
        return this;
    }
    
    private void add0(final int h, final int i, final String name, final String value) {
        final HeaderEntry e = this.entries[i];
        final HeaderEntry newEntry = this.entries[i] = new HeaderEntry(h, name, value);
        newEntry.next = e;
        newEntry.addBefore(this.head);
    }
    
    @Override
    public HttpHeaders remove(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        final int h = hash(name, false);
        final int i = index(h);
        this.remove0(h, i, name);
        return this;
    }
    
    private void remove0(final int h, final int i, final String name) {
        HeaderEntry e = this.entries[i];
        if (e == null) {
            return;
        }
        while (e.hash == h && eq(name, e.key)) {
            e.remove();
            final HeaderEntry next = e.next;
            if (next == null) {
                this.entries[i] = null;
                return;
            }
            this.entries[i] = next;
            e = next;
        }
        while (true) {
            final HeaderEntry next = e.next;
            if (next == null) {
                break;
            }
            if (next.hash == h && eq(name, next.key)) {
                e.next = next.next;
                next.remove();
            }
            else {
                e = next;
            }
        }
    }
    
    @Override
    public HttpHeaders set(final String name, final Object value) {
        final String strVal = toString(value);
        boolean validateName = false;
        if (this.validate) {
            this.validateHeaderValue0(strVal);
            validateName = !DefaultHttpHeaders.KNOWN_NAMES.contains(name);
        }
        final int h = hash(name, validateName);
        final int i = index(h);
        this.remove0(h, i, name);
        this.add0(h, i, name, strVal);
        return this;
    }
    
    @Override
    public HttpHeaders set(final String name, final Iterable<?> values) {
        if (values == null) {
            throw new NullPointerException("values");
        }
        boolean validateName = false;
        if (this.validate) {
            validateName = !DefaultHttpHeaders.KNOWN_NAMES.contains(name);
        }
        final int h = hash(name, validateName);
        final int i = index(h);
        this.remove0(h, i, name);
        for (final Object v : values) {
            if (v == null) {
                break;
            }
            final String strVal = toString(v);
            if (this.validate) {
                this.validateHeaderValue0(strVal);
            }
            this.add0(h, i, name, strVal);
        }
        return this;
    }
    
    @Override
    public HttpHeaders clear() {
        Arrays.fill(this.entries, null);
        final HeaderEntry head = this.head;
        final HeaderEntry head2 = this.head;
        final HeaderEntry head3 = this.head;
        head2.after = head3;
        head.before = head3;
        return this;
    }
    
    @Override
    public String get(final String name) {
        return this.get(name, false);
    }
    
    private String get(final String name, final boolean last) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        final int h = hash(name, false);
        final int i = index(h);
        HeaderEntry e = this.entries[i];
        String value = null;
        while (e != null) {
            if (e.hash == h && eq(name, e.key)) {
                value = e.value;
                if (last) {
                    break;
                }
            }
            e = e.next;
        }
        return value;
    }
    
    @Override
    public List<String> getAll(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        final LinkedList<String> values = new LinkedList<String>();
        final int h = hash(name, false);
        final int i = index(h);
        for (HeaderEntry e = this.entries[i]; e != null; e = e.next) {
            if (e.hash == h && eq(name, e.key)) {
                values.addFirst(e.value);
            }
        }
        return values;
    }
    
    @Override
    public List<Map.Entry<String, String>> entries() {
        final List<Map.Entry<String, String>> all = new LinkedList<Map.Entry<String, String>>();
        for (HeaderEntry e = this.head.after; e != this.head; e = e.after) {
            all.add(e);
        }
        return all;
    }
    
    public Iterator<Map.Entry<String, String>> iterator() {
        return new HeaderIterator();
    }
    
    @Override
    public boolean contains(final String name) {
        return this.get(name, true) != null;
    }
    
    @Override
    public boolean isEmpty() {
        return this.head == this.head.after;
    }
    
    @Override
    public boolean contains(final String name, final String value, final boolean ignoreCaseValue) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        final int h = hash(name, false);
        final int i = index(h);
        for (HeaderEntry e = this.entries[i]; e != null; e = e.next) {
            if (e.hash == h && eq(name, e.key)) {
                if (ignoreCaseValue) {
                    if (e.value.equalsIgnoreCase(value)) {
                        return true;
                    }
                }
                else if (e.value.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public Set<String> names() {
        final Set<String> names = new LinkedHashSet<String>();
        for (HeaderEntry e = this.head.after; e != this.head; e = e.after) {
            names.add(e.key);
        }
        return names;
    }
    
    private static String toString(final Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String)value;
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Date) {
            return HttpHeaderDateFormat.get().format((Date)value);
        }
        if (value instanceof Calendar) {
            return HttpHeaderDateFormat.get().format(((Calendar)value).getTime());
        }
        return value.toString();
    }
    
    static {
        KNOWN_NAMES = createSet(Names.class);
        KNOWN_VALUES = createSet(Values.class);
    }
    
    private final class HeaderIterator implements Iterator<Map.Entry<String, String>>
    {
        private HeaderEntry current;
        
        private HeaderIterator() {
            this.current = DefaultHttpHeaders.this.head;
        }
        
        public boolean hasNext() {
            return this.current.after != DefaultHttpHeaders.this.head;
        }
        
        public Map.Entry<String, String> next() {
            this.current = this.current.after;
            if (this.current == DefaultHttpHeaders.this.head) {
                throw new NoSuchElementException();
            }
            return this.current;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private final class HeaderEntry implements Map.Entry<String, String>
    {
        final int hash;
        final String key;
        String value;
        HeaderEntry next;
        HeaderEntry before;
        HeaderEntry after;
        
        HeaderEntry(final int hash, final String key, final String value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }
        
        void remove() {
            this.before.after = this.after;
            this.after.before = this.before;
        }
        
        void addBefore(final HeaderEntry e) {
            this.after = e;
            this.before = e.before;
            this.before.after = this;
            this.after.before = this;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public String setValue(final String value) {
            if (value == null) {
                throw new NullPointerException("value");
            }
            if (DefaultHttpHeaders.this.validate) {
                DefaultHttpHeaders.this.validateHeaderValue0(value);
            }
            final String oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        @Override
        public String toString() {
            return this.key + '=' + this.value;
        }
    }
}
