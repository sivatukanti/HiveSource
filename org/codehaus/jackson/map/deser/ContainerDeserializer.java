// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.map.deser;

import org.codehaus.jackson.map.deser.std.ContainerDeserializerBase;

@Deprecated
public abstract class ContainerDeserializer<T> extends ContainerDeserializerBase<T>
{
    protected ContainerDeserializer(final Class<?> selfType) {
        super(selfType);
    }
}
