// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules.automata;

import java.util.Iterator;
import java.util.regex.Matcher;
import com.sun.jersey.api.uri.UriPattern;
import java.util.regex.Pattern;

public final class TrieNode<T>
{
    public static final Pattern PARAMETER_PATTERN;
    private static final char WILDCARD_CHAR = '\0';
    private TrieArc<T> firstArc;
    private TrieArc<T> lastArc;
    private int arcs;
    private TrieNodeValue<T> value;
    private UriPattern pattern;
    private boolean wildcard;
    
    protected void setWildcard(final boolean b) {
        this.wildcard = b;
    }
    
    protected void setValue(final T value, final UriPattern pattern) {
        this.value.set(value);
        this.pattern = pattern;
    }
    
    protected TrieNode() {
        this.arcs = 0;
        this.value = new TrieNodeValue<T>();
        this.wildcard = false;
    }
    
    protected TrieNode(final T value) {
        this.arcs = 0;
        this.value = new TrieNodeValue<T>();
        this.wildcard = false;
        this.value.set(value);
    }
    
    protected TrieArc<T> matchExitArc(final CharSequence seq, final int i) {
        for (TrieArc<T> arc = this.firstArc; arc != null; arc = arc.next) {
            if (arc.match(seq, i) > 0) {
                return arc;
            }
        }
        return null;
    }
    
    protected boolean hasValue() {
        return !this.value.isEmpty();
    }
    
    private void addArc(final TrieArc<T> arc) {
        if (this.firstArc == null) {
            this.firstArc = arc;
        }
        else {
            this.lastArc.next = arc;
        }
        this.lastArc = arc;
        ++this.arcs;
    }
    
    private boolean add(final CharSequence path, final int i, final T value, final UriPattern pattern) {
        if (i >= path.length()) {
            this.setValue(value, pattern);
            return true;
        }
        final char input = path.charAt(i);
        boolean added = false;
        for (TrieArc<T> arc = this.firstArc; arc != null; arc = arc.next) {
            if (arc.match(path, i) > 0) {
                added = arc.target.add(path, i + 1, value, pattern);
                if (added) {
                    return added;
                }
            }
        }
        if (input == '\0') {
            this.setWildcard(true);
            return this.add(path, i + 1, value, pattern);
        }
        final TrieNode<T> node = new TrieNode<T>();
        this.addArc(new TrieArc<T>(node, input));
        return node.add(path, i + 1, value, pattern);
    }
    
    protected void add(final String path, final T value, final UriPattern pattern) {
        final Matcher matcher = TrieNode.PARAMETER_PATTERN.matcher(path);
        final String uri = matcher.replaceAll(String.valueOf('\0'));
        if (uri.endsWith("/") && uri.length() > 1) {
            this.add(uri.substring(0, uri.length() - 1), 0, value, pattern);
        }
        this.add(uri, 0, value, pattern);
    }
    
    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder();
        this.toStringRepresentation(out, 0, new char[] { '\0' });
        return out.toString();
    }
    
    private void toStringRepresentation(final StringBuilder out, final int level, final char[] c) {
        for (int i = 0; i < level; ++i) {
            out.append(' ');
        }
        out.append("ARC(" + new String(c) + ") ->");
        out.append(this.getClass().getSimpleName() + (this.wildcard ? "*" : ""));
        out.append(" ");
        out.append(this.value);
        out.append('\n');
        for (TrieArc<T> arc = this.firstArc; arc != null; arc = arc.next) {
            arc.target.toStringRepresentation(out, level + 2, arc.code);
        }
    }
    
    public UriPattern getPattern() {
        return this.pattern;
    }
    
    public Iterator<T> getValue() {
        return this.value.getIterator();
    }
    
    protected boolean isWildcard() {
        return this.wildcard;
    }
    
    protected TrieArc<T> getFirstArc() {
        return this.firstArc;
    }
    
    public int getArcs() {
        return this.arcs;
    }
    
    public void pack() {
        for (TrieArc<T> arc = this.firstArc; arc != null; arc = arc.next) {
            arc.pack();
        }
    }
    
    static {
        PARAMETER_PATTERN = Pattern.compile("\\{([\\w-\\._~]+?)\\}");
    }
}
