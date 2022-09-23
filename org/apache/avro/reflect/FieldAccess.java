// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.reflect;

import java.lang.reflect.Field;

abstract class FieldAccess
{
    protected abstract FieldAccessor getAccessor(final Field p0);
}
