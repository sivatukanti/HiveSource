// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface Buffer extends Cloneable
{
    public static final int IMMUTABLE = 0;
    public static final int READONLY = 1;
    public static final int READWRITE = 2;
    public static final boolean VOLATILE = true;
    public static final boolean NON_VOLATILE = false;
    
    byte[] array();
    
    byte[] asArray();
    
    Buffer buffer();
    
    Buffer asNonVolatileBuffer();
    
    Buffer asReadOnlyBuffer();
    
    Buffer asImmutableBuffer();
    
    Buffer asMutableBuffer();
    
    int capacity();
    
    int space();
    
    void clear();
    
    void compact();
    
    byte get();
    
    int get(final byte[] p0, final int p1, final int p2);
    
    Buffer get(final int p0);
    
    int getIndex();
    
    boolean hasContent();
    
    boolean equalsIgnoreCase(final Buffer p0);
    
    boolean isImmutable();
    
    boolean isReadOnly();
    
    boolean isVolatile();
    
    int length();
    
    void mark();
    
    void mark(final int p0);
    
    int markIndex();
    
    byte peek();
    
    byte peek(final int p0);
    
    Buffer peek(final int p0, final int p1);
    
    int peek(final int p0, final byte[] p1, final int p2, final int p3);
    
    int poke(final int p0, final Buffer p1);
    
    void poke(final int p0, final byte p1);
    
    int poke(final int p0, final byte[] p1, final int p2, final int p3);
    
    int put(final Buffer p0);
    
    void put(final byte p0);
    
    int put(final byte[] p0, final int p1, final int p2);
    
    int put(final byte[] p0);
    
    int putIndex();
    
    void reset();
    
    void setGetIndex(final int p0);
    
    void setMarkIndex(final int p0);
    
    void setPutIndex(final int p0);
    
    int skip(final int p0);
    
    Buffer slice();
    
    Buffer sliceFromMark();
    
    Buffer sliceFromMark(final int p0);
    
    String toDetailString();
    
    void writeTo(final OutputStream p0) throws IOException;
    
    int readFrom(final InputStream p0, final int p1) throws IOException;
    
    public interface CaseInsensitve
    {
    }
}
