// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import java.io.Serializable;

public interface TypeConverter<X, Y> extends Serializable
{
    public static final Localiser LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    
    Y toDatastoreType(final X p0);
    
    X toMemberType(final Y p0);
}
