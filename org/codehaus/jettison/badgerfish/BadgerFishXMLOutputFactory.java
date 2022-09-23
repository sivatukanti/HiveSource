// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.badgerfish;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import org.codehaus.jettison.AbstractXMLOutputFactory;

public class BadgerFishXMLOutputFactory extends AbstractXMLOutputFactory
{
    public XMLStreamWriter createXMLStreamWriter(final Writer writer) throws XMLStreamException {
        return new BadgerFishXMLStreamWriter(writer);
    }
}
