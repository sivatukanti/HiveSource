// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.mapped;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.Node;
import java.util.Iterator;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jettison.Convention;

public class MappedNamespaceConvention implements Convention
{
    private Map xnsToJns;
    private Map jnsToXns;
    private List attributesAsElements;
    private List jsonAttributesAsElements;
    private boolean supressAtAttributes;
    private String attributeKey;
    private TypeConverter typeConverter;
    
    public MappedNamespaceConvention() {
        this.xnsToJns = new HashMap();
        this.jnsToXns = new HashMap();
        this.attributeKey = "@";
        this.typeConverter = new DefaultConverter();
    }
    
    public MappedNamespaceConvention(final Configuration config) {
        this.xnsToJns = new HashMap();
        this.jnsToXns = new HashMap();
        this.attributeKey = "@";
        this.xnsToJns = config.getXmlToJsonNamespaces();
        this.attributesAsElements = config.getAttributesAsElements();
        this.supressAtAttributes = config.isSupressAtAttributes();
        this.attributeKey = config.getAttributeKey();
        for (final Map.Entry entry : this.xnsToJns.entrySet()) {
            this.jnsToXns.put(entry.getValue(), entry.getKey());
        }
        this.jsonAttributesAsElements = new ArrayList();
        if (this.attributesAsElements != null) {
            for (final QName q : this.attributesAsElements) {
                this.jsonAttributesAsElements.add(this.createAttributeKey(q.getPrefix(), q.getNamespaceURI(), q.getLocalPart()));
            }
        }
        this.typeConverter = config.getTypeConverter();
    }
    
    public void processAttributesAndNamespaces(final Node n, final JSONObject object) throws JSONException {
        final Iterator itr = object.keys();
        while (itr.hasNext()) {
            String k = itr.next();
            if (this.supressAtAttributes) {
                if (k.startsWith(this.attributeKey)) {
                    k = k.substring(1);
                }
                if (null == this.jsonAttributesAsElements) {
                    this.jsonAttributesAsElements = new ArrayList();
                }
                if (!this.jsonAttributesAsElements.contains(k)) {
                    this.jsonAttributesAsElements.add(k);
                }
            }
            if (k.startsWith(this.attributeKey)) {
                final String value = object.optString(k);
                k = k.substring(1);
                if (value != null) {
                    this.readAttribute(n, k, value);
                }
                else {
                    final JSONArray array = object.optJSONArray(k);
                    if (array != null) {
                        this.readAttribute(n, k, array);
                    }
                }
                itr.remove();
            }
            else if (this.jsonAttributesAsElements != null && this.jsonAttributesAsElements.contains(k)) {
                final String value = object.optString(k);
                if (value != null) {
                    this.readAttribute(n, k, value);
                }
                else {
                    final JSONArray array = object.optJSONArray(k);
                    if (array != null) {
                        this.readAttribute(n, k, array);
                    }
                }
                itr.remove();
            }
            else {
                final int dot = k.lastIndexOf(46);
                if (dot == -1) {
                    continue;
                }
                final String jns = k.substring(0, dot);
                n.setNamespace("", this.jnsToXns.get(jns));
            }
        }
    }
    
    public QName createQName(final String rootName, final Node node) {
        return this.createQName(rootName);
    }
    
    private void readAttribute(final Node n, final String k, final JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); ++i) {
            this.readAttribute(n, k, array.getString(i));
        }
    }
    
    private void readAttribute(final Node n, final String name, final String value) throws JSONException {
        final QName qname = this.createQName(name);
        n.getAttributes().put(qname, value);
    }
    
    private QName createQName(final String name) {
        int dot = name.lastIndexOf(46);
        QName qname = null;
        String local = name;
        if (dot == -1) {
            dot = 0;
        }
        else {
            local = local.substring(dot + 1);
        }
        final String jns = name.substring(0, dot);
        final String xns = this.jnsToXns.get(jns);
        if (xns == null) {
            qname = new QName(name);
        }
        else {
            qname = new QName(xns, local);
        }
        return qname;
    }
    
    public String createAttributeKey(final String p, final String ns, final String local) {
        final StringBuffer builder = new StringBuffer();
        if (!this.supressAtAttributes) {
            builder.append(this.attributeKey);
        }
        final String jns = this.getJSONNamespace(ns);
        if (jns != null && jns.length() != 0) {
            builder.append(jns).append('.');
        }
        return builder.append(local).toString();
    }
    
    private String getJSONNamespace(final String ns) {
        if (ns == null || ns.length() == 0) {
            return "";
        }
        final String jns = this.xnsToJns.get(ns);
        if (jns == null) {
            throw new IllegalStateException("Invalid JSON namespace: " + ns);
        }
        return jns;
    }
    
    public String createKey(final String p, final String ns, final String local) {
        final StringBuffer builder = new StringBuffer();
        final String jns = this.getJSONNamespace(ns);
        if (jns != null && jns.length() != 0) {
            builder.append(jns).append('.');
        }
        return builder.append(local).toString();
    }
    
    public boolean isElement(final String p, final String ns, final String local) {
        if (this.attributesAsElements == null) {
            return false;
        }
        for (final QName q : this.attributesAsElements) {
            if (q.getNamespaceURI().equals(ns) && q.getLocalPart().equals(local)) {
                return true;
            }
        }
        return false;
    }
    
    public Object convertToJSONPrimitive(final String text) {
        return this.typeConverter.convertToJSONPrimitive(text);
    }
}
