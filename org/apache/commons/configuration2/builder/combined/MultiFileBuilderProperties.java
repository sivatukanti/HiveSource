// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import org.apache.commons.configuration2.builder.BuilderParameters;

public interface MultiFileBuilderProperties<T>
{
    T setFilePattern(final String p0);
    
    T setManagedBuilderParameters(final BuilderParameters p0);
}
