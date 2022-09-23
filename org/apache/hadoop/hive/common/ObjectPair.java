// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

public class ObjectPair<F, S>
{
    private F first;
    private S second;
    
    public ObjectPair() {
    }
    
    public static <T1, T2> ObjectPair<T1, T2> create(final T1 f, final T2 s) {
        return new ObjectPair<T1, T2>(f, s);
    }
    
    public ObjectPair(final F first, final S second) {
        this.first = first;
        this.second = second;
    }
    
    public F getFirst() {
        return this.first;
    }
    
    public void setFirst(final F first) {
        this.first = first;
    }
    
    public S getSecond() {
        return this.second;
    }
    
    public void setSecond(final S second) {
        this.second = second;
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ObjectPair && this.equals((ObjectPair)that);
    }
    
    public boolean equals(final ObjectPair<F, S> that) {
        return that != null && this.getFirst().equals(that.getFirst()) && this.getSecond().equals(that.getSecond());
    }
    
    @Override
    public String toString() {
        return this.first + ":" + this.second;
    }
}
