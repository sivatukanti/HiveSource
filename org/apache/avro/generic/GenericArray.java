// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.generic;

import java.util.List;

public interface GenericArray<T> extends List<T>, GenericContainer
{
    T peek();
    
    void reverse();
}
