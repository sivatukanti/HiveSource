// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.map.deser;

import org.codehaus.jackson.map.deser.std.StdDeserializer;

@Deprecated
public abstract class StdScalarDeserializer<T> extends StdDeserializer<T>
{
    protected StdScalarDeserializer(final Class<?> vc) {
        super(vc);
    }
}
