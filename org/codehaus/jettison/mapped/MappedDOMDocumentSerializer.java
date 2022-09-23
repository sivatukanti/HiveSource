// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.mapped;

import org.codehaus.jettison.AbstractXMLOutputFactory;
import java.io.OutputStream;
import org.codehaus.jettison.AbstractDOMDocumentSerializer;

public class MappedDOMDocumentSerializer extends AbstractDOMDocumentSerializer
{
    public MappedDOMDocumentSerializer(final OutputStream output, final Configuration con) {
        super(output, new MappedXMLOutputFactory(con));
    }
}
