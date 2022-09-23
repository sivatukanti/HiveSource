// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.mapped;

import org.codehaus.jettison.AbstractXMLInputFactory;
import org.codehaus.jettison.AbstractDOMDocumentParser;

public class MappedDOMDocumentParser extends AbstractDOMDocumentParser
{
    public MappedDOMDocumentParser(final Configuration con) {
        super(new MappedXMLInputFactory(con));
    }
}
