// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.resolver;

import org.xml.sax.EntityResolver;

public interface EntityResolverSupport
{
    EntityResolver getEntityResolver();
    
    void setEntityResolver(final EntityResolver p0);
}
