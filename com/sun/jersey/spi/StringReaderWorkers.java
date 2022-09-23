// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface StringReaderWorkers
{
     <T> StringReader<T> getStringReader(final Class<T> p0, final Type p1, final Annotation[] p2);
}
