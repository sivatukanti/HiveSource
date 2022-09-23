// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import java.util.List;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.XMLStreamWriter2;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.Attribute;
import java.util.Iterator;
import org.codehaus.stax2.ri.EmptyNamespaceContext;
import javax.xml.stream.Location;
import javax.xml.namespace.NamespaceContext;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

public class StartElementEventImpl extends BaseEventImpl implements StartElement
{
    protected final QName mName;
    protected final ArrayList mAttrs;
    protected final ArrayList mNsDecls;
    protected NamespaceContext mParentNsCtxt;
    NamespaceContext mActualNsCtxt;
    
    protected StartElementEventImpl(final Location location, final QName mName, final ArrayList mAttrs, final ArrayList mNsDecls, final NamespaceContext namespaceContext) {
        super(location);
        this.mActualNsCtxt = null;
        this.mName = mName;
        this.mAttrs = mAttrs;
        this.mNsDecls = mNsDecls;
        this.mParentNsCtxt = ((namespaceContext == null) ? EmptyNamespaceContext.getInstance() : namespaceContext);
    }
    
    public static StartElementEventImpl construct(final Location location, final QName qName, final Iterator iterator, final Iterator iterator2, final NamespaceContext namespaceContext) {
        ArrayList<Attribute> list;
        if (iterator == null || !iterator.hasNext()) {
            list = null;
        }
        else {
            list = new ArrayList<Attribute>();
            do {
                list.add(iterator.next());
            } while (iterator.hasNext());
        }
        ArrayList<Namespace> list2;
        if (iterator2 == null || !iterator2.hasNext()) {
            list2 = null;
        }
        else {
            list2 = new ArrayList<Namespace>();
            do {
                list2.add(iterator2.next());
            } while (iterator2.hasNext());
        }
        return new StartElementEventImpl(location, qName, list, list2, namespaceContext);
    }
    
    @Override
    public StartElement asStartElement() {
        return this;
    }
    
    @Override
    public int getEventType() {
        return 1;
    }
    
    @Override
    public boolean isStartElement() {
        return true;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write(60);
            final String prefix = this.mName.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                writer.write(prefix);
                writer.write(58);
            }
            writer.write(this.mName.getLocalPart());
            if (this.mNsDecls != null) {
                for (int i = 0; i < this.mNsDecls.size(); ++i) {
                    writer.write(32);
                    ((Namespace)this.mNsDecls.get(i)).writeAsEncodedUnicode(writer);
                }
            }
            if (this.mAttrs != null) {
                for (int j = 0; j < this.mAttrs.size(); ++j) {
                    final Attribute attribute = this.mAttrs.get(j);
                    if (attribute.isSpecified()) {
                        writer.write(32);
                        attribute.writeAsEncodedUnicode(writer);
                    }
                }
            }
            writer.write(62);
        }
        catch (IOException th) {
            throw new XMLStreamException(th);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 xmlStreamWriter2) throws XMLStreamException {
        final QName mName = this.mName;
        xmlStreamWriter2.writeStartElement(mName.getPrefix(), mName.getLocalPart(), mName.getNamespaceURI());
        if (this.mNsDecls != null) {
            for (int i = 0; i < this.mNsDecls.size(); ++i) {
                final Namespace namespace = this.mNsDecls.get(i);
                final String prefix = namespace.getPrefix();
                final String namespaceURI = namespace.getNamespaceURI();
                if (prefix == null || prefix.length() == 0) {
                    xmlStreamWriter2.writeDefaultNamespace(namespaceURI);
                }
                else {
                    xmlStreamWriter2.writeNamespace(prefix, namespaceURI);
                }
            }
        }
        if (this.mAttrs != null) {
            for (int j = 0; j < this.mAttrs.size(); ++j) {
                final Attribute attribute = this.mAttrs.get(j);
                if (attribute.isSpecified()) {
                    final QName name = attribute.getName();
                    xmlStreamWriter2.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attribute.getValue());
                }
            }
        }
    }
    
    public final QName getName() {
        return this.mName;
    }
    
    public Iterator getNamespaces() {
        return (this.mNsDecls == null) ? EmptyIterator.getInstance() : this.mNsDecls.iterator();
    }
    
    public NamespaceContext getNamespaceContext() {
        if (this.mActualNsCtxt == null) {
            if (this.mNsDecls == null) {
                this.mActualNsCtxt = this.mParentNsCtxt;
            }
            else {
                this.mActualNsCtxt = MergedNsContext.construct(this.mParentNsCtxt, this.mNsDecls);
            }
        }
        return this.mActualNsCtxt;
    }
    
    public String getNamespaceURI(String s) {
        if (this.mNsDecls != null) {
            if (s == null) {
                s = "";
            }
            for (int i = 0; i < this.mNsDecls.size(); ++i) {
                final Namespace namespace = this.mNsDecls.get(i);
                String prefix = namespace.getPrefix();
                if (prefix == null) {
                    prefix = "";
                }
                if (s.equals(prefix)) {
                    return namespace.getNamespaceURI();
                }
            }
        }
        return null;
    }
    
    public Attribute getAttributeByName(final QName qName) {
        if (this.mAttrs == null) {
            return null;
        }
        final String localPart = qName.getLocalPart();
        final String namespaceURI = qName.getNamespaceURI();
        final int size = this.mAttrs.size();
        final boolean b = namespaceURI == null || namespaceURI.length() == 0;
        for (int i = 0; i < size; ++i) {
            final Attribute attribute = this.mAttrs.get(i);
            final QName name = attribute.getName();
            if (name.getLocalPart().equals(localPart)) {
                final String namespaceURI2 = name.getNamespaceURI();
                if (b) {
                    if (namespaceURI2 == null || namespaceURI2.length() == 0) {
                        return attribute;
                    }
                }
                else if (namespaceURI.equals(namespaceURI2)) {
                    return attribute;
                }
            }
        }
        return null;
    }
    
    public Iterator getAttributes() {
        if (this.mAttrs == null) {
            return EmptyIterator.getInstance();
        }
        return this.mAttrs.iterator();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof StartElement)) {
            return false;
        }
        final StartElement startElement = (StartElement)o;
        return this.mName.equals(startElement.getName()) && BaseEventImpl.iteratedEquals(this.getNamespaces(), startElement.getNamespaces()) && BaseEventImpl.iteratedEquals(this.getAttributes(), startElement.getAttributes());
    }
    
    @Override
    public int hashCode() {
        return BaseEventImpl.addHash(this.getAttributes(), BaseEventImpl.addHash(this.getNamespaces(), this.mName.hashCode()));
    }
}
