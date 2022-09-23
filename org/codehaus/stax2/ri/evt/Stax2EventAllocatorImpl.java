// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import org.codehaus.stax2.ri.EmptyIterator;
import java.util.ArrayList;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.XMLStreamReader2;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.util.XMLEventConsumer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.util.XMLEventAllocator;

public class Stax2EventAllocatorImpl implements XMLEventAllocator, XMLStreamConstants
{
    public XMLEvent allocate(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        final Location location = this.getLocation(xmlStreamReader);
        switch (xmlStreamReader.getEventType()) {
            case 12: {
                return new CharactersEventImpl(location, xmlStreamReader.getText(), true);
            }
            case 4: {
                return new CharactersEventImpl(location, xmlStreamReader.getText(), false);
            }
            case 5: {
                return new CommentEventImpl(location, xmlStreamReader.getText());
            }
            case 11: {
                return this.createDTD(xmlStreamReader, location);
            }
            case 8: {
                return new EndDocumentEventImpl(location);
            }
            case 2: {
                return new EndElementEventImpl(location, xmlStreamReader);
            }
            case 3: {
                return new ProcInstrEventImpl(location, xmlStreamReader.getPITarget(), xmlStreamReader.getPIData());
            }
            case 6: {
                final CharactersEventImpl charactersEventImpl = new CharactersEventImpl(location, xmlStreamReader.getText(), false);
                charactersEventImpl.setWhitespaceStatus(true);
                return charactersEventImpl;
            }
            case 7: {
                return new StartDocumentEventImpl(location, xmlStreamReader);
            }
            case 1: {
                return this.createStartElement(xmlStreamReader, location);
            }
            case 9: {
                return this.createEntityReference(xmlStreamReader, location);
            }
            default: {
                throw new XMLStreamException("Unrecognized event type " + xmlStreamReader.getEventType() + ".");
            }
        }
    }
    
    public void allocate(final XMLStreamReader xmlStreamReader, final XMLEventConsumer xmlEventConsumer) throws XMLStreamException {
        xmlEventConsumer.add(this.allocate(xmlStreamReader));
    }
    
    public XMLEventAllocator newInstance() {
        return new Stax2EventAllocatorImpl();
    }
    
    protected Location getLocation(final XMLStreamReader xmlStreamReader) {
        return xmlStreamReader.getLocation();
    }
    
    protected EntityReference createEntityReference(final XMLStreamReader xmlStreamReader, final Location location) throws XMLStreamException {
        return new EntityReferenceEventImpl(location, xmlStreamReader.getLocalName());
    }
    
    protected DTD createDTD(final XMLStreamReader xmlStreamReader, final Location location) throws XMLStreamException {
        if (xmlStreamReader instanceof XMLStreamReader2) {
            final DTDInfo dtdInfo = ((XMLStreamReader2)xmlStreamReader).getDTDInfo();
            return new DTDEventImpl(location, dtdInfo.getDTDRootName(), dtdInfo.getDTDSystemId(), dtdInfo.getDTDPublicId(), dtdInfo.getDTDInternalSubset(), dtdInfo.getProcessedDTD());
        }
        return new DTDEventImpl(location, null, xmlStreamReader.getText());
    }
    
    protected StartElement createStartElement(final XMLStreamReader xmlStreamReader, final Location location) throws XMLStreamException {
        NamespaceContext nonTransientNamespaceContext = null;
        if (xmlStreamReader instanceof XMLStreamReader2) {
            nonTransientNamespaceContext = ((XMLStreamReader2)xmlStreamReader).getNonTransientNamespaceContext();
        }
        final int attributeCount = xmlStreamReader.getAttributeCount();
        ArrayList list;
        if (attributeCount < 1) {
            list = null;
        }
        else {
            list = new ArrayList<Object>(attributeCount);
            for (int i = 0; i < attributeCount; ++i) {
                list.add(new AttributeEventImpl(location, xmlStreamReader.getAttributeName(i), xmlStreamReader.getAttributeValue(i), xmlStreamReader.isAttributeSpecified(i)));
            }
        }
        final int namespaceCount = xmlStreamReader.getNamespaceCount();
        ArrayList list2;
        if (namespaceCount < 1) {
            list2 = null;
        }
        else {
            list2 = new ArrayList<Object>(namespaceCount);
            for (int j = 0; j < namespaceCount; ++j) {
                list2.add(NamespaceEventImpl.constructNamespace(location, xmlStreamReader.getNamespacePrefix(j), xmlStreamReader.getNamespaceURI(j)));
            }
        }
        return StartElementEventImpl.construct(location, xmlStreamReader.getName(), (list == null) ? EmptyIterator.getInstance() : list.iterator(), (list2 == null) ? EmptyIterator.getInstance() : list2.iterator(), nonTransientNamespaceContext);
    }
}
