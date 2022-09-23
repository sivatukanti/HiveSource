// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.mapped;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public class NullNamespaceContext implements NamespaceContext
{
    public String getNamespaceURI(final String arg0) {
        return null;
    }
    
    public String getPrefix(final String arg0) {
        return null;
    }
    
    public Iterator getPrefixes(final String arg0) {
        return null;
    }
}
