// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
import java.util.AbstractList;

public class LazyStringArrayList extends AbstractList<String> implements LazyStringList, RandomAccess
{
    public static final LazyStringList EMPTY;
    private final List<Object> list;
    
    public LazyStringArrayList() {
        this.list = new ArrayList<Object>();
    }
    
    public LazyStringArrayList(final LazyStringList from) {
        this.list = new ArrayList<Object>(from.size());
        this.addAll(from);
    }
    
    public LazyStringArrayList(final List<String> from) {
        this.list = new ArrayList<Object>(from);
    }
    
    @Override
    public String get(final int index) {
        final Object o = this.list.get(index);
        if (o instanceof String) {
            return (String)o;
        }
        final ByteString bs = (ByteString)o;
        final String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
            this.list.set(index, s);
        }
        return s;
    }
    
    @Override
    public int size() {
        return this.list.size();
    }
    
    @Override
    public String set(final int index, final String s) {
        final Object o = this.list.set(index, s);
        return this.asString(o);
    }
    
    @Override
    public void add(final int index, final String element) {
        this.list.add(index, element);
        ++this.modCount;
    }
    
    @Override
    public boolean addAll(final Collection<? extends String> c) {
        return this.addAll(this.size(), c);
    }
    
    @Override
    public boolean addAll(final int index, final Collection<? extends String> c) {
        final Collection<?> collection = (c instanceof LazyStringList) ? ((LazyStringList)c).getUnderlyingElements() : c;
        final boolean ret = this.list.addAll(index, collection);
        ++this.modCount;
        return ret;
    }
    
    @Override
    public String remove(final int index) {
        final Object o = this.list.remove(index);
        ++this.modCount;
        return this.asString(o);
    }
    
    @Override
    public void clear() {
        this.list.clear();
        ++this.modCount;
    }
    
    public void add(final ByteString element) {
        this.list.add(element);
        ++this.modCount;
    }
    
    public ByteString getByteString(final int index) {
        final Object o = this.list.get(index);
        if (o instanceof String) {
            final ByteString b = ByteString.copyFromUtf8((String)o);
            this.list.set(index, b);
            return b;
        }
        return (ByteString)o;
    }
    
    private String asString(final Object o) {
        if (o instanceof String) {
            return (String)o;
        }
        return ((ByteString)o).toStringUtf8();
    }
    
    public List<?> getUnderlyingElements() {
        return Collections.unmodifiableList((List<?>)this.list);
    }
    
    static {
        EMPTY = new UnmodifiableLazyStringList(new LazyStringArrayList());
    }
}
