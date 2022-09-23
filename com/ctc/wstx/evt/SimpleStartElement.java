// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.evt;

import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import com.ctc.wstx.io.TextEscaper;
import java.io.Writer;
import com.ctc.wstx.util.DataUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.events.Namespace;
import java.util.List;
import com.ctc.wstx.util.BaseNsContext;
import javax.xml.stream.Location;
import javax.xml.stream.events.Attribute;
import javax.xml.namespace.QName;
import java.util.Map;

public class SimpleStartElement extends BaseStartElement
{
    final Map<QName, Attribute> mAttrs;
    
    protected SimpleStartElement(final Location loc, final QName name, final BaseNsContext nsCtxt, final Map<QName, Attribute> attr) {
        super(loc, name, nsCtxt);
        this.mAttrs = attr;
    }
    
    public static SimpleStartElement construct(final Location loc, final QName name, final Map<QName, Attribute> attrs, final List<Namespace> ns, final NamespaceContext nsCtxt) {
        final BaseNsContext myCtxt = MergedNsContext.construct(nsCtxt, ns);
        return new SimpleStartElement(loc, name, myCtxt, attrs);
    }
    
    public static SimpleStartElement construct(final Location loc, final QName name, final Iterator<Attribute> attrs, final Iterator<Namespace> ns, final NamespaceContext nsCtxt) {
        Map<QName, Attribute> attrMap;
        if (attrs == null || !attrs.hasNext()) {
            attrMap = null;
        }
        else {
            attrMap = new LinkedHashMap<QName, Attribute>();
            do {
                final Attribute attr = attrs.next();
                attrMap.put(attr.getName(), attr);
            } while (attrs.hasNext());
        }
        BaseNsContext myCtxt;
        if (ns != null && ns.hasNext()) {
            final ArrayList<Namespace> l = new ArrayList<Namespace>();
            do {
                l.add(ns.next());
            } while (ns.hasNext());
            myCtxt = MergedNsContext.construct(nsCtxt, l);
        }
        else if (nsCtxt == null) {
            myCtxt = null;
        }
        else if (nsCtxt instanceof BaseNsContext) {
            myCtxt = (BaseNsContext)nsCtxt;
        }
        else {
            myCtxt = MergedNsContext.construct(nsCtxt, null);
        }
        return new SimpleStartElement(loc, name, myCtxt, attrMap);
    }
    
    @Override
    public Attribute getAttributeByName(final QName name) {
        if (this.mAttrs == null) {
            return null;
        }
        return this.mAttrs.get(name);
    }
    
    @Override
    public Iterator<Attribute> getAttributes() {
        if (this.mAttrs == null) {
            return DataUtil.emptyIterator();
        }
        return this.mAttrs.values().iterator();
    }
    
    @Override
    protected void outputNsAndAttr(final Writer w) throws IOException {
        if (this.mNsCtxt != null) {
            this.mNsCtxt.outputNamespaceDeclarations(w);
        }
        if (this.mAttrs != null && this.mAttrs.size() > 0) {
            for (final Attribute attr : this.mAttrs.values()) {
                if (!attr.isSpecified()) {
                    continue;
                }
                w.write(32);
                final QName name = attr.getName();
                final String prefix = name.getPrefix();
                if (prefix != null && prefix.length() > 0) {
                    w.write(prefix);
                    w.write(58);
                }
                w.write(name.getLocalPart());
                w.write("=\"");
                final String val = attr.getValue();
                if (val != null && val.length() > 0) {
                    TextEscaper.writeEscapedAttrValue(w, val);
                }
                w.write(34);
            }
        }
    }
    
    @Override
    protected void outputNsAndAttr(final XMLStreamWriter w) throws XMLStreamException {
        if (this.mNsCtxt != null) {
            this.mNsCtxt.outputNamespaceDeclarations(w);
        }
        if (this.mAttrs != null && this.mAttrs.size() > 0) {
            for (final Attribute attr : this.mAttrs.values()) {
                if (!attr.isSpecified()) {
                    continue;
                }
                final QName name = attr.getName();
                final String prefix = name.getPrefix();
                final String ln = name.getLocalPart();
                final String nsURI = name.getNamespaceURI();
                w.writeAttribute(prefix, nsURI, ln, attr.getValue());
            }
        }
    }
}
