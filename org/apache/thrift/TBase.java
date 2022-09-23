// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import org.apache.thrift.protocol.TProtocol;
import java.io.Serializable;

public interface TBase<T extends TBase<?, ?>, F extends TFieldIdEnum> extends Comparable<T>, Serializable
{
    void read(final TProtocol p0) throws TException;
    
    void write(final TProtocol p0) throws TException;
    
    F fieldForId(final int p0);
    
    boolean isSet(final F p0);
    
    Object getFieldValue(final F p0);
    
    void setFieldValue(final F p0, final Object p1);
    
    TBase<T, F> deepCopy();
    
    void clear();
}
