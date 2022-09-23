// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public interface Convention
{
    void processAttributesAndNamespaces(final Node p0, final JSONObject p1) throws JSONException, XMLStreamException;
    
    QName createQName(final String p0, final Node p1) throws XMLStreamException;
}
