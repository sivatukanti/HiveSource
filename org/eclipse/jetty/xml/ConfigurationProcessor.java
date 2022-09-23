// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.xml;

import java.net.URL;

public interface ConfigurationProcessor
{
    void init(final URL p0, final XmlParser.Node p1, final XmlConfiguration p2);
    
    Object configure(final Object p0) throws Exception;
    
    Object configure() throws Exception;
}
