// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.badgerfish;

import org.codehaus.jettison.json.JSONException;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.json.JSONObject;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.jettison.json.JSONTokener;
import org.codehaus.jettison.AbstractXMLInputFactory;

public class BadgerFishXMLInputFactory extends AbstractXMLInputFactory
{
    public XMLStreamReader createXMLStreamReader(final JSONTokener tokener) throws XMLStreamException {
        try {
            final JSONObject root = new JSONObject(tokener);
            return new BadgerFishXMLStreamReader(root);
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }
}
