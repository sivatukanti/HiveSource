// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.List;

public interface LazyStringList extends List<String>
{
    ByteString getByteString(final int p0);
    
    void add(final ByteString p0);
    
    List<?> getUnderlyingElements();
}
