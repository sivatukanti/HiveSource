// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.evt;

import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.ctc.wstx.exc.WstxIOException;
import java.io.Writer;
import javax.xml.namespace.NamespaceContext;
import com.ctc.wstx.util.DataUtil;
import javax.xml.stream.events.Namespace;
import java.util.Iterator;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.Location;
import com.ctc.wstx.util.BaseNsContext;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

abstract class BaseStartElement extends BaseEventImpl implements StartElement
{
    protected final QName mName;
    protected final BaseNsContext mNsCtxt;
    
    protected BaseStartElement(final Location loc, final QName name, final BaseNsContext nsCtxt) {
        super(loc);
        this.mName = name;
        this.mNsCtxt = nsCtxt;
    }
    
    @Override
    public abstract Attribute getAttributeByName(final QName p0);
    
    @Override
    public abstract Iterator<Attribute> getAttributes();
    
    @Override
    public final QName getName() {
        return this.mName;
    }
    
    @Override
    public Iterator<Namespace> getNamespaces() {
        if (this.mNsCtxt == null) {
            return DataUtil.emptyIterator();
        }
        return this.mNsCtxt.getNamespaces();
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.mNsCtxt;
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        return (this.mNsCtxt == null) ? null : this.mNsCtxt.getNamespaceURI(prefix);
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
    public void writeAsEncodedUnicode(final Writer w) throws XMLStreamException {
        try {
            w.write(60);
            final String prefix = this.mName.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                w.write(prefix);
                w.write(58);
            }
            w.write(this.mName.getLocalPart());
            this.outputNsAndAttr(w);
            w.write(62);
        }
        catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 w) throws XMLStreamException {
        final QName n = this.mName;
        w.writeStartElement(n.getPrefix(), n.getLocalPart(), n.getNamespaceURI());
        this.outputNsAndAttr(w);
    }
    
    protected abstract void outputNsAndAttr(final Writer p0) throws IOException;
    
    protected abstract void outputNsAndAttr(final XMLStreamWriter p0) throws XMLStreamException;
    
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
        final StartElement other = (StartElement)o;
        return this.mName.equals(other.getName()) && BaseEventImpl.iteratedEquals(this.getNamespaces(), other.getNamespaces()) && BaseEventImpl.iteratedEquals(this.getAttributes(), other.getAttributes());
    }
    
    @Override
    public int hashCode() {
        int hash = this.mName.hashCode();
        hash = BaseEventImpl.addHash(this.getNamespaces(), hash);
        hash = BaseEventImpl.addHash(this.getAttributes(), hash);
        return hash;
    }
}
