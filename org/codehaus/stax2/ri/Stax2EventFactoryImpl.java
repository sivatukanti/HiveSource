// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import org.codehaus.stax2.ri.evt.StartElementEventImpl;
import org.codehaus.stax2.evt.DTD2;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.ri.evt.StartDocumentEventImpl;
import javax.xml.stream.events.StartDocument;
import org.codehaus.stax2.ri.evt.ProcInstrEventImpl;
import javax.xml.stream.events.ProcessingInstruction;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;
import javax.xml.stream.events.Namespace;
import org.codehaus.stax2.ri.evt.EntityReferenceEventImpl;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.EntityDeclaration;
import org.codehaus.stax2.ri.evt.EndElementEventImpl;
import javax.xml.stream.events.EndElement;
import java.util.Iterator;
import org.codehaus.stax2.ri.evt.EndDocumentEventImpl;
import javax.xml.stream.events.EndDocument;
import org.codehaus.stax2.ri.evt.DTDEventImpl;
import javax.xml.stream.events.DTD;
import org.codehaus.stax2.ri.evt.CommentEventImpl;
import javax.xml.stream.events.Comment;
import org.codehaus.stax2.ri.evt.CharactersEventImpl;
import javax.xml.stream.events.Characters;
import org.codehaus.stax2.ri.evt.AttributeEventImpl;
import javax.xml.stream.events.Attribute;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import org.codehaus.stax2.evt.XMLEventFactory2;

public abstract class Stax2EventFactoryImpl extends XMLEventFactory2
{
    protected Location mLocation;
    
    @Override
    public Attribute createAttribute(final QName qName, final String s) {
        return new AttributeEventImpl(this.mLocation, qName, s, true);
    }
    
    @Override
    public Attribute createAttribute(final String s, final String s2) {
        return new AttributeEventImpl(this.mLocation, s, null, null, s2, true);
    }
    
    @Override
    public Attribute createAttribute(final String s, final String s2, final String s3, final String s4) {
        return new AttributeEventImpl(this.mLocation, s3, s2, s, s4, true);
    }
    
    @Override
    public Characters createCData(final String s) {
        return new CharactersEventImpl(this.mLocation, s, true);
    }
    
    @Override
    public Characters createCharacters(final String s) {
        return new CharactersEventImpl(this.mLocation, s, false);
    }
    
    @Override
    public Comment createComment(final String s) {
        return new CommentEventImpl(this.mLocation, s);
    }
    
    @Override
    public DTD createDTD(final String s) {
        return new DTDEventImpl(this.mLocation, s);
    }
    
    @Override
    public EndDocument createEndDocument() {
        return new EndDocumentEventImpl(this.mLocation);
    }
    
    @Override
    public EndElement createEndElement(final QName qName, final Iterator iterator) {
        return new EndElementEventImpl(this.mLocation, qName, iterator);
    }
    
    @Override
    public EndElement createEndElement(final String s, final String s2, final String s3) {
        return this.createEndElement(this.createQName(s2, s3, s), null);
    }
    
    @Override
    public EndElement createEndElement(final String s, final String s2, final String s3, final Iterator iterator) {
        return this.createEndElement(this.createQName(s2, s3, s), iterator);
    }
    
    @Override
    public EntityReference createEntityReference(final String s, final EntityDeclaration entityDeclaration) {
        return new EntityReferenceEventImpl(this.mLocation, entityDeclaration);
    }
    
    @Override
    public Characters createIgnorableSpace(final String s) {
        return CharactersEventImpl.createIgnorableWS(this.mLocation, s);
    }
    
    @Override
    public Namespace createNamespace(final String s) {
        return NamespaceEventImpl.constructDefaultNamespace(this.mLocation, s);
    }
    
    @Override
    public Namespace createNamespace(final String s, final String s2) {
        return NamespaceEventImpl.constructNamespace(this.mLocation, s, s2);
    }
    
    @Override
    public ProcessingInstruction createProcessingInstruction(final String s, final String s2) {
        return new ProcInstrEventImpl(this.mLocation, s, s2);
    }
    
    @Override
    public Characters createSpace(final String s) {
        return CharactersEventImpl.createNonIgnorableWS(this.mLocation, s);
    }
    
    @Override
    public StartDocument createStartDocument() {
        return new StartDocumentEventImpl(this.mLocation);
    }
    
    @Override
    public StartDocument createStartDocument(final String s) {
        return new StartDocumentEventImpl(this.mLocation, s);
    }
    
    @Override
    public StartDocument createStartDocument(final String s, final String s2) {
        return new StartDocumentEventImpl(this.mLocation, s, s2);
    }
    
    @Override
    public StartDocument createStartDocument(final String s, final String s2, final boolean b) {
        return new StartDocumentEventImpl(this.mLocation, s, s2, true, b);
    }
    
    @Override
    public StartElement createStartElement(final QName qName, final Iterator iterator, final Iterator iterator2) {
        return this.createStartElement(qName, iterator, iterator2, null);
    }
    
    @Override
    public StartElement createStartElement(final String s, final String s2, final String s3) {
        return this.createStartElement(this.createQName(s2, s3, s), null, null, null);
    }
    
    @Override
    public StartElement createStartElement(final String s, final String s2, final String s3, final Iterator iterator, final Iterator iterator2) {
        return this.createStartElement(this.createQName(s2, s3, s), iterator, iterator2, null);
    }
    
    @Override
    public StartElement createStartElement(final String s, final String s2, final String s3, final Iterator iterator, final Iterator iterator2, final NamespaceContext namespaceContext) {
        return this.createStartElement(this.createQName(s2, s3, s), iterator, iterator2, namespaceContext);
    }
    
    @Override
    public void setLocation(final Location mLocation) {
        this.mLocation = mLocation;
    }
    
    @Override
    public DTD2 createDTD(final String s, final String s2, final String s3, final String s4) {
        return new DTDEventImpl(this.mLocation, s, s2, s3, s4, null);
    }
    
    @Override
    public DTD2 createDTD(final String s, final String s2, final String s3, final String s4, final Object o) {
        return new DTDEventImpl(this.mLocation, s, s2, s3, s4, o);
    }
    
    protected abstract QName createQName(final String p0, final String p1);
    
    protected abstract QName createQName(final String p0, final String p1, final String p2);
    
    protected StartElement createStartElement(final QName qName, final Iterator<?> iterator, final Iterator<?> iterator2, final NamespaceContext namespaceContext) {
        return StartElementEventImpl.construct(this.mLocation, qName, iterator, iterator2, namespaceContext);
    }
}
