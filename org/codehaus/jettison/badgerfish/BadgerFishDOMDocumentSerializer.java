// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.badgerfish;

import org.codehaus.jettison.AbstractXMLOutputFactory;
import java.io.OutputStream;
import org.codehaus.jettison.AbstractDOMDocumentSerializer;

public class BadgerFishDOMDocumentSerializer extends AbstractDOMDocumentSerializer
{
    public BadgerFishDOMDocumentSerializer(final OutputStream output) {
        super(output, new BadgerFishXMLOutputFactory());
    }
}
