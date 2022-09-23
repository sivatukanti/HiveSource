// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import org.mortbay.util.LazyList;
import org.mortbay.util.SingletonList;
import java.util.StringTokenizer;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.Set;
import java.util.List;
import org.mortbay.util.StringMap;
import java.io.Externalizable;
import java.util.HashMap;

public class PathMap extends HashMap implements Externalizable
{
    private static String __pathSpecSeparators;
    StringMap _prefixMap;
    StringMap _suffixMap;
    StringMap _exactMap;
    List _defaultSingletonList;
    Entry _prefixDefault;
    Entry _default;
    Set _entrySet;
    boolean _nodefault;
    
    public static void setPathSpecSeparators(final String s) {
        PathMap.__pathSpecSeparators = s;
    }
    
    public PathMap() {
        super(11);
        this._prefixMap = new StringMap();
        this._suffixMap = new StringMap();
        this._exactMap = new StringMap();
        this._defaultSingletonList = null;
        this._prefixDefault = null;
        this._default = null;
        this._nodefault = false;
        this._entrySet = this.entrySet();
    }
    
    public PathMap(final boolean nodefault) {
        super(11);
        this._prefixMap = new StringMap();
        this._suffixMap = new StringMap();
        this._exactMap = new StringMap();
        this._defaultSingletonList = null;
        this._prefixDefault = null;
        this._default = null;
        this._nodefault = false;
        this._entrySet = this.entrySet();
        this._nodefault = nodefault;
    }
    
    public PathMap(final int capacity) {
        super(capacity);
        this._prefixMap = new StringMap();
        this._suffixMap = new StringMap();
        this._exactMap = new StringMap();
        this._defaultSingletonList = null;
        this._prefixDefault = null;
        this._default = null;
        this._nodefault = false;
        this._entrySet = this.entrySet();
    }
    
    public PathMap(final Map m) {
        this._prefixMap = new StringMap();
        this._suffixMap = new StringMap();
        this._exactMap = new StringMap();
        this._defaultSingletonList = null;
        this._prefixDefault = null;
        this._default = null;
        this._nodefault = false;
        this.putAll(m);
        this._entrySet = this.entrySet();
    }
    
    public void writeExternal(final ObjectOutput out) throws IOException {
        final HashMap map = new HashMap(this);
        out.writeObject(map);
    }
    
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final HashMap map = (HashMap)in.readObject();
        this.putAll(map);
    }
    
    public synchronized Object put(final Object pathSpec, final Object object) {
        final StringTokenizer tok = new StringTokenizer(pathSpec.toString(), PathMap.__pathSpecSeparators);
        Object old = null;
        while (tok.hasMoreTokens()) {
            final String spec = tok.nextToken();
            if (!spec.startsWith("/") && !spec.startsWith("*.")) {
                throw new IllegalArgumentException("PathSpec " + spec + ". must start with '/' or '*.'");
            }
            old = super.put(spec, object);
            final Entry entry = new Entry(spec, object);
            if (!entry.getKey().equals(spec)) {
                continue;
            }
            if (spec.equals("/*")) {
                this._prefixDefault = entry;
            }
            else if (spec.endsWith("/*")) {
                final String mapped = spec.substring(0, spec.length() - 2);
                entry.setMapped(mapped);
                this._prefixMap.put(mapped, entry);
                this._exactMap.put(mapped, entry);
                this._exactMap.put(spec.substring(0, spec.length() - 1), entry);
            }
            else if (spec.startsWith("*.")) {
                this._suffixMap.put(spec.substring(2), entry);
            }
            else if (spec.equals("/")) {
                if (this._nodefault) {
                    this._exactMap.put(spec, entry);
                }
                else {
                    this._default = entry;
                    this._defaultSingletonList = SingletonList.newSingletonList(this._default);
                }
            }
            else {
                entry.setMapped(spec);
                this._exactMap.put(spec, entry);
            }
        }
        return old;
    }
    
    public Object match(final String path) {
        final Map.Entry entry = this.getMatch(path);
        if (entry != null) {
            return entry.getValue();
        }
        return null;
    }
    
    public Entry getMatch(final String path) {
        if (path == null) {
            return null;
        }
        final int l = path.length();
        Map.Entry entry = this._exactMap.getEntry(path, 0, l);
        if (entry != null) {
            return entry.getValue();
        }
        int i = l;
        while ((i = path.lastIndexOf(47, i - 1)) >= 0) {
            entry = this._prefixMap.getEntry(path, 0, i);
            if (entry != null) {
                return entry.getValue();
            }
        }
        if (this._prefixDefault != null) {
            return this._prefixDefault;
        }
        i = 0;
        while ((i = path.indexOf(46, i + 1)) > 0) {
            entry = this._suffixMap.getEntry(path, i + 1, l - i - 1);
            if (entry != null) {
                return entry.getValue();
            }
        }
        return this._default;
    }
    
    public Object getLazyMatches(final String path) {
        Object entries = null;
        if (path == null) {
            return LazyList.getList(entries);
        }
        final int l = path.length();
        Map.Entry entry = this._exactMap.getEntry(path, 0, l);
        if (entry != null) {
            entries = LazyList.add(entries, entry.getValue());
        }
        int i = l - 1;
        while ((i = path.lastIndexOf(47, i - 1)) >= 0) {
            entry = this._prefixMap.getEntry(path, 0, i);
            if (entry != null) {
                entries = LazyList.add(entries, entry.getValue());
            }
        }
        if (this._prefixDefault != null) {
            entries = LazyList.add(entries, this._prefixDefault);
        }
        i = 0;
        while ((i = path.indexOf(46, i + 1)) > 0) {
            entry = this._suffixMap.getEntry(path, i + 1, l - i - 1);
            if (entry != null) {
                entries = LazyList.add(entries, entry.getValue());
            }
        }
        if (this._default != null) {
            if (entries == null) {
                return this._defaultSingletonList;
            }
            entries = LazyList.add(entries, this._default);
        }
        return entries;
    }
    
    public List getMatches(final String path) {
        return LazyList.getList(this.getLazyMatches(path));
    }
    
    public boolean containsMatch(final String path) {
        final Entry match = this.getMatch(path);
        return match != null && !match.equals(this._default);
    }
    
    public synchronized Object remove(final Object pathSpec) {
        if (pathSpec != null) {
            final String spec = (String)pathSpec;
            if (spec.equals("/*")) {
                this._prefixDefault = null;
            }
            else if (spec.endsWith("/*")) {
                this._prefixMap.remove(spec.substring(0, spec.length() - 2));
                this._exactMap.remove(spec.substring(0, spec.length() - 1));
                this._exactMap.remove(spec.substring(0, spec.length() - 2));
            }
            else if (spec.startsWith("*.")) {
                this._suffixMap.remove(spec.substring(2));
            }
            else if (spec.equals("/")) {
                this._default = null;
                this._defaultSingletonList = null;
            }
            else {
                this._exactMap.remove(spec);
            }
        }
        return super.remove(pathSpec);
    }
    
    public void clear() {
        this._exactMap = new StringMap();
        this._prefixMap = new StringMap();
        this._suffixMap = new StringMap();
        this._default = null;
        this._defaultSingletonList = null;
        super.clear();
    }
    
    public static boolean match(final String pathSpec, final String path) throws IllegalArgumentException {
        return match(pathSpec, path, false);
    }
    
    public static boolean match(final String pathSpec, final String path, final boolean noDefault) throws IllegalArgumentException {
        final char c = pathSpec.charAt(0);
        if (c == '/') {
            if ((!noDefault && pathSpec.length() == 1) || pathSpec.equals(path)) {
                return true;
            }
            if (isPathWildcardMatch(pathSpec, path)) {
                return true;
            }
        }
        else if (c == '*') {
            return path.regionMatches(path.length() - pathSpec.length() + 1, pathSpec, 1, pathSpec.length() - 1);
        }
        return false;
    }
    
    private static boolean isPathWildcardMatch(final String pathSpec, final String path) {
        final int cpl = pathSpec.length() - 2;
        return pathSpec.endsWith("/*") && path.regionMatches(0, pathSpec, 0, cpl) && (path.length() == cpl || '/' == path.charAt(cpl));
    }
    
    public static String pathMatch(final String pathSpec, final String path) {
        final char c = pathSpec.charAt(0);
        if (c == '/') {
            if (pathSpec.length() == 1) {
                return path;
            }
            if (pathSpec.equals(path)) {
                return path;
            }
            if (isPathWildcardMatch(pathSpec, path)) {
                return path.substring(0, pathSpec.length() - 2);
            }
        }
        else if (c == '*' && path.regionMatches(path.length() - (pathSpec.length() - 1), pathSpec, 1, pathSpec.length() - 1)) {
            return path;
        }
        return null;
    }
    
    public static String pathInfo(final String pathSpec, final String path) {
        final char c = pathSpec.charAt(0);
        if (c == '/') {
            if (pathSpec.length() == 1) {
                return null;
            }
            final boolean wildcard = isPathWildcardMatch(pathSpec, path);
            if (pathSpec.equals(path) && !wildcard) {
                return null;
            }
            if (wildcard) {
                if (path.length() == pathSpec.length() - 2) {
                    return null;
                }
                return path.substring(pathSpec.length() - 2);
            }
        }
        return null;
    }
    
    public static String relativePath(final String base, final String pathSpec, String path) {
        String info = pathInfo(pathSpec, path);
        if (info == null) {
            info = path;
        }
        if (info.startsWith("./")) {
            info = info.substring(2);
        }
        if (base.endsWith("/")) {
            if (info.startsWith("/")) {
                path = base + info.substring(1);
            }
            else {
                path = base + info;
            }
        }
        else if (info.startsWith("/")) {
            path = base + info;
        }
        else {
            path = base + "/" + info;
        }
        return path;
    }
    
    static {
        PathMap.__pathSpecSeparators = System.getProperty("org.mortbay.http.PathMap.separators", ":,");
    }
    
    public static class Entry implements Map.Entry
    {
        private Object key;
        private Object value;
        private String mapped;
        private transient String string;
        
        Entry(final Object key, final Object value) {
            this.key = key;
            this.value = value;
        }
        
        public Object getKey() {
            return this.key;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public Object setValue(final Object o) {
            throw new UnsupportedOperationException();
        }
        
        public String toString() {
            if (this.string == null) {
                this.string = this.key + "=" + this.value;
            }
            return this.string;
        }
        
        public String getMapped() {
            return this.mapped;
        }
        
        void setMapped(final String mapped) {
            this.mapped = mapped;
        }
    }
}
