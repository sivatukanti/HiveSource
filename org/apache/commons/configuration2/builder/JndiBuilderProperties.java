// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import javax.naming.Context;

public interface JndiBuilderProperties<T>
{
    T setContext(final Context p0);
    
    T setPrefix(final String p0);
}
