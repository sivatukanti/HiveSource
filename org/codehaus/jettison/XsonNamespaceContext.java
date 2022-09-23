// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison;

import java.util.Iterator;
import org.codehaus.jettison.util.FastStack;
import javax.xml.namespace.NamespaceContext;

public class XsonNamespaceContext implements NamespaceContext
{
    private FastStack nodes;
    
    public XsonNamespaceContext(final FastStack nodes) {
        this.nodes = nodes;
    }
    
    public String getNamespaceURI(final String prefix) {
        for (final Node node : this.nodes) {
            final String uri = node.getNamespaceURI(prefix);
            if (uri != null) {
                return uri;
            }
        }
        return null;
    }
    
    public String getPrefix(final String namespaceURI) {
        for (final Node node : this.nodes) {
            final String prefix = node.getNamespacePrefix(namespaceURI);
            if (prefix != null) {
                return prefix;
            }
        }
        return null;
    }
    
    public Iterator getPrefixes(final String namespaceURI) {
        return null;
    }
}
