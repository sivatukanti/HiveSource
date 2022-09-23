// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.badgerfish;

import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.json.JSONException;
import javax.xml.namespace.QName;
import java.util.Iterator;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.Node;
import org.codehaus.jettison.Convention;

public class BadgerFishConvention implements Convention
{
    public void processAttributesAndNamespaces(final Node n, final JSONObject object) throws JSONException, XMLStreamException {
        final Iterator itr = object.keys();
        while (itr.hasNext()) {
            String k = itr.next();
            if (k.startsWith("@")) {
                final Object o = object.opt(k);
                k = k.substring(1);
                if (k.equals("xmlns")) {
                    if (o instanceof JSONObject) {
                        final JSONObject jo = (JSONObject)o;
                        final Iterator pitr = jo.keys();
                        while (pitr.hasNext()) {
                            String prefix = pitr.next();
                            final String uri = jo.getString(prefix);
                            if (prefix.equals("$")) {
                                prefix = "";
                            }
                            n.setNamespace(prefix, uri);
                        }
                    }
                }
                else {
                    final String strValue = (String)o;
                    final QName name = this.createQName(k, n);
                    n.setAttribute(name, strValue);
                }
                itr.remove();
            }
        }
    }
    
    public QName createQName(final String rootName, final Node node) throws XMLStreamException {
        final int idx = rootName.indexOf(58);
        if (idx != -1) {
            final String prefix = rootName.substring(0, idx);
            final String local = rootName.substring(idx + 1);
            final String uri = node.getNamespaceURI(prefix);
            if (uri == null) {
                throw new XMLStreamException("Invalid prefix " + prefix + " on element " + rootName);
            }
            return new QName(uri, local, prefix);
        }
        else {
            final String uri2 = node.getNamespaceURI("");
            if (uri2 != null) {
                return new QName(uri2, rootName);
            }
            return new QName(rootName);
        }
    }
}
