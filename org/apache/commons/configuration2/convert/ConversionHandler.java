// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.convert;

import java.util.Collection;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;

public interface ConversionHandler
{
     <T> T to(final Object p0, final Class<T> p1, final ConfigurationInterpolator p2);
    
    Object toArray(final Object p0, final Class<?> p1, final ConfigurationInterpolator p2);
    
     <T> void toCollection(final Object p0, final Class<T> p1, final ConfigurationInterpolator p2, final Collection<T> p3);
}
