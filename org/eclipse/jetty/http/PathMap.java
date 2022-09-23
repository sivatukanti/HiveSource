// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import org.eclipse.jetty.util.ArrayTernaryTrie;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.util.Trie;
import java.util.HashMap;

@Deprecated
public class PathMap<O> extends HashMap<String, O>
{
    private static String __pathSpecSeparators;
    Trie<MappedEntry<O>> _prefixMap;
    Trie<MappedEntry<O>> _suffixMap;
    final Map<String, MappedEntry<O>> _exactMap;
    List<MappedEntry<O>> _defaultSingletonList;
    MappedEntry<O> _prefixDefault;
    MappedEntry<O> _default;
    boolean _nodefault;
    
    public static void setPathSpecSeparators(final String s) {
        PathMap.__pathSpecSeparators = s;
    }
    
    public PathMap() {
        this(11);
    }
    
    public PathMap(final boolean noDefault) {
        this(11, noDefault);
    }
    
    public PathMap(final int capacity) {
        this(capacity, false);
    }
    
    private PathMap(final int capacity, final boolean noDefault) {
        super(capacity);
        this._prefixMap = new ArrayTernaryTrie<MappedEntry<O>>(false);
        this._suffixMap = new ArrayTernaryTrie<MappedEntry<O>>(false);
        this._exactMap = new HashMap<String, MappedEntry<O>>();
        this._defaultSingletonList = null;
        this._prefixDefault = null;
        this._default = null;
        this._nodefault = false;
        this._nodefault = noDefault;
    }
    
    public PathMap(final Map<String, ? extends O> dictMap) {
        this._prefixMap = new ArrayTernaryTrie<MappedEntry<O>>(false);
        this._suffixMap = new ArrayTernaryTrie<MappedEntry<O>>(false);
        this._exactMap = new HashMap<String, MappedEntry<O>>();
        this._defaultSingletonList = null;
        this._prefixDefault = null;
        this._default = null;
        this._nodefault = false;
        this.putAll(dictMap);
    }
    
    @Override
    public O put(final String pathSpec, final O object) {
        if ("".equals(pathSpec.trim())) {
            final MappedEntry<O> entry = new MappedEntry<O>("", object);
            entry.setMapped("");
            this._exactMap.put("", entry);
            return super.put("", object);
        }
        final StringTokenizer tok = new StringTokenizer(pathSpec, PathMap.__pathSpecSeparators);
        O old = null;
        while (tok.hasMoreTokens()) {
            final String spec = tok.nextToken();
            if (!spec.startsWith("/") && !spec.startsWith("*.")) {
                throw new IllegalArgumentException("PathSpec " + spec + ". must start with '/' or '*.'");
            }
            old = super.put(spec, object);
            final MappedEntry<O> entry2 = new MappedEntry<O>(spec, object);
            if (!entry2.getKey().equals(spec)) {
                continue;
            }
            if (spec.equals("/*")) {
                this._prefixDefault = entry2;
            }
            else if (spec.endsWith("/*")) {
                final String mapped = spec.substring(0, spec.length() - 2);
                entry2.setMapped(mapped);
                while (!this._prefixMap.put(mapped, entry2)) {
                    this._prefixMap = new ArrayTernaryTrie<MappedEntry<O>>((ArrayTernaryTrie)this._prefixMap, 1.5);
                }
            }
            else if (spec.startsWith("*.")) {
                final String suffix = spec.substring(2);
                while (!this._suffixMap.put(suffix, entry2)) {
                    this._suffixMap = new ArrayTernaryTrie<MappedEntry<O>>((ArrayTernaryTrie)this._suffixMap, 1.5);
                }
            }
            else if (spec.equals("/")) {
                if (this._nodefault) {
                    this._exactMap.put(spec, entry2);
                }
                else {
                    this._default = entry2;
                    this._defaultSingletonList = Collections.singletonList(this._default);
                }
            }
            else {
                entry2.setMapped(spec);
                this._exactMap.put(spec, entry2);
            }
        }
        return old;
    }
    
    public O match(final String path) {
        final MappedEntry<O> entry = this.getMatch(path);
        if (entry != null) {
            return entry.getValue();
        }
        return null;
    }
    
    public MappedEntry<O> getMatch(final String path) {
        if (path == null) {
            return null;
        }
        final int l = path.length();
        MappedEntry<O> entry = null;
        if (l == 1 && path.charAt(0) == '/') {
            entry = this._exactMap.get("");
            if (entry != null) {
                return entry;
            }
        }
        entry = this._exactMap.get(path);
        if (entry != null) {
            return entry;
        }
        int i = l;
        final Trie<MappedEntry<O>> prefix_map = this._prefixMap;
        while (i >= 0) {
            entry = prefix_map.getBest(path, 0, i);
            if (entry == null) {
                break;
            }
            final String key = entry.getKey();
            if (key.length() - 2 >= path.length() || path.charAt(key.length() - 2) == '/') {
                return entry;
            }
            i = key.length() - 3;
        }
        if (this._prefixDefault != null) {
            return this._prefixDefault;
        }
        i = 0;
        final Trie<MappedEntry<O>> suffix_map = this._suffixMap;
        while ((i = path.indexOf(46, i + 1)) > 0) {
            entry = suffix_map.get(path, i + 1, l - i - 1);
            if (entry != null) {
                return entry;
            }
        }
        return this._default;
    }
    
    public List<? extends Map.Entry<String, O>> getMatches(final String path) {
        final List<MappedEntry<O>> entries = new ArrayList<MappedEntry<O>>();
        if (path == null) {
            return entries;
        }
        if (path.length() == 0) {
            return this._defaultSingletonList;
        }
        MappedEntry<O> entry = this._exactMap.get(path);
        if (entry != null) {
            entries.add(entry);
        }
        int i;
        final int l = i = path.length();
        final Trie<MappedEntry<O>> prefix_map = this._prefixMap;
        while (i >= 0) {
            entry = prefix_map.getBest(path, 0, i);
            if (entry == null) {
                break;
            }
            final String key = entry.getKey();
            if (key.length() - 2 >= path.length() || path.charAt(key.length() - 2) == '/') {
                entries.add(entry);
            }
            i = key.length() - 3;
        }
        if (this._prefixDefault != null) {
            entries.add(this._prefixDefault);
        }
        i = 0;
        final Trie<MappedEntry<O>> suffix_map = this._suffixMap;
        while ((i = path.indexOf(46, i + 1)) > 0) {
            entry = suffix_map.get(path, i + 1, l - i - 1);
            if (entry != null) {
                entries.add(entry);
            }
        }
        if ("/".equals(path)) {
            entry = this._exactMap.get("");
            if (entry != null) {
                entries.add(entry);
            }
        }
        if (this._default != null) {
            entries.add(this._default);
        }
        return entries;
    }
    
    public boolean containsMatch(final String path) {
        final MappedEntry<?> match = this.getMatch(path);
        return match != null && !match.equals(this._default);
    }
    
    @Override
    public O remove(final Object pathSpec) {
        if (pathSpec != null) {
            final String spec = (String)pathSpec;
            if (spec.equals("/*")) {
                this._prefixDefault = null;
            }
            else if (spec.endsWith("/*")) {
                this._prefixMap.remove(spec.substring(0, spec.length() - 2));
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
    
    @Override
    public void clear() {
        this._exactMap.clear();
        this._prefixMap = new ArrayTernaryTrie<MappedEntry<O>>(false);
        this._suffixMap = new ArrayTernaryTrie<MappedEntry<O>>(false);
        this._default = null;
        this._defaultSingletonList = null;
        this._prefixDefault = null;
        super.clear();
    }
    
    public static boolean match(final String pathSpec, final String path) {
        return match(pathSpec, path, false);
    }
    
    public static boolean match(final String pathSpec, final String path, final boolean noDefault) {
        if (pathSpec.length() == 0) {
            return "/".equals(path);
        }
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
        if ("".equals(pathSpec)) {
            return path;
        }
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
        PathMap.__pathSpecSeparators = ":,";
    }
    
    public static class MappedEntry<O> implements Map.Entry<String, O>
    {
        private final String key;
        private final O value;
        private String mapped;
        
        MappedEntry(final String key, final O value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public String getKey() {
            return this.key;
        }
        
        @Override
        public O getValue() {
            return this.value;
        }
        
        @Override
        public O setValue(final O o) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
        
        public String getMapped() {
            return this.mapped;
        }
        
        void setMapped(final String mapped) {
            this.mapped = mapped;
        }
    }
    
    public static class PathSet extends AbstractSet<String> implements Predicate<String>
    {
        private final PathMap<Boolean> _map;
        
        public PathSet() {
            this._map = new PathMap<Boolean>();
        }
        
        @Override
        public Iterator<String> iterator() {
            return this._map.keySet().iterator();
        }
        
        @Override
        public int size() {
            return this._map.size();
        }
        
        @Override
        public boolean add(final String item) {
            return this._map.put(item, Boolean.TRUE) == null;
        }
        
        @Override
        public boolean remove(final Object item) {
            return this._map.remove(item) != null;
        }
        
        @Override
        public boolean contains(final Object o) {
            return this._map.containsKey(o);
        }
        
        @Override
        public boolean test(final String s) {
            return this._map.containsMatch(s);
        }
        
        public boolean containsMatch(final String s) {
            return this._map.containsMatch(s);
        }
    }
}
