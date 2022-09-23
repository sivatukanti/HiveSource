// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import org.codehaus.stax2.ri.EmptyIterator;
import javax.xml.stream.events.Namespace;
import java.util.Iterator;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.Location;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;

public class EndElementEventImpl extends BaseEventImpl implements EndElement
{
    final QName mName;
    final ArrayList mNamespaces;
    
    public EndElementEventImpl(final Location location, final XMLStreamReader xmlStreamReader) {
        super(location);
        this.mName = xmlStreamReader.getName();
        final int namespaceCount = xmlStreamReader.getNamespaceCount();
        if (namespaceCount == 0) {
            this.mNamespaces = null;
        }
        else {
            final ArrayList mNamespaces = new ArrayList<NamespaceEventImpl>(namespaceCount);
            for (int i = 0; i < namespaceCount; ++i) {
                mNamespaces.add(NamespaceEventImpl.constructNamespace(location, xmlStreamReader.getNamespacePrefix(i), xmlStreamReader.getNamespaceURI(i)));
            }
            this.mNamespaces = mNamespaces;
        }
    }
    
    public EndElementEventImpl(final Location location, final QName mName, final Iterator iterator) {
        super(location);
        this.mName = mName;
        if (iterator == null || !iterator.hasNext()) {
            this.mNamespaces = null;
        }
        else {
            final ArrayList<Namespace> mNamespaces = new ArrayList<Namespace>();
            while (iterator.hasNext()) {
                mNamespaces.add(iterator.next());
            }
            this.mNamespaces = mNamespaces;
        }
    }
    
    public QName getName() {
        return this.mName;
    }
    
    public Iterator getNamespaces() {
        return (this.mNamespaces == null) ? EmptyIterator.getInstance() : this.mNamespaces.iterator();
    }
    
    @Override
    public EndElement asEndElement() {
        return this;
    }
    
    @Override
    public int getEventType() {
        return 2;
    }
    
    @Override
    public boolean isEndElement() {
        return true;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write("</");
            final String prefix = this.mName.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                writer.write(prefix);
                writer.write(58);
            }
            writer.write(this.mName.getLocalPart());
            writer.write(62);
        }
        catch (IOException ex) {
            this.throwFromIOE(ex);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 xmlStreamWriter2) throws XMLStreamException {
        xmlStreamWriter2.writeEndElement();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof EndElement && this.getName().equals(((EndElement)o).getName()));
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
