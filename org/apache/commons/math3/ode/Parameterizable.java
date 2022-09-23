// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.ode;

import java.util.Collection;

public interface Parameterizable
{
    Collection<String> getParametersNames();
    
    boolean isSupported(final String p0);
}
