// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.evt;

import com.ctc.wstx.sr.ElemAttrs;
import com.ctc.wstx.util.BaseNsContext;
import javax.xml.stream.util.XMLEventConsumer;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.ent.EntityDecl;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import org.codehaus.stax2.DTDInfo;
import javax.xml.stream.events.EntityDeclaration;
import com.ctc.wstx.exc.WstxException;
import com.ctc.wstx.cfg.ErrorConsts;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;
import javax.xml.stream.events.Namespace;
import java.util.ArrayList;
import org.codehaus.stax2.ri.evt.AttributeEventImpl;
import javax.xml.stream.events.Attribute;
import javax.xml.namespace.QName;
import java.util.LinkedHashMap;
import com.ctc.wstx.sr.StreamReaderImpl;
import org.codehaus.stax2.ri.evt.StartDocumentEventImpl;
import org.codehaus.stax2.ri.evt.ProcInstrEventImpl;
import org.codehaus.stax2.ri.evt.EndElementEventImpl;
import org.codehaus.stax2.ri.evt.EndDocumentEventImpl;
import com.ctc.wstx.dtd.DTDSubset;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.evt.CommentEventImpl;
import org.codehaus.stax2.ri.evt.CharactersEventImpl;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.util.XMLEventAllocator;
import com.ctc.wstx.sr.ElemCallback;

public class DefaultEventAllocator extends ElemCallback implements XMLEventAllocator, XMLStreamConstants
{
    static final DefaultEventAllocator sStdInstance;
    protected final boolean mAccurateLocation;
    protected Location mLastLocation;
    
    protected DefaultEventAllocator(final boolean accurateLocation) {
        this.mLastLocation = null;
        this.mAccurateLocation = accurateLocation;
    }
    
    public static DefaultEventAllocator getDefaultInstance() {
        return DefaultEventAllocator.sStdInstance;
    }
    
    public static DefaultEventAllocator getFastInstance() {
        return new DefaultEventAllocator(false);
    }
    
    @Override
    public XMLEvent allocate(final XMLStreamReader r) throws XMLStreamException {
        Location loc;
        if (this.mAccurateLocation) {
            loc = r.getLocation();
        }
        else {
            loc = this.mLastLocation;
            if (loc == null) {
                final Location location = r.getLocation();
                this.mLastLocation = location;
                loc = location;
            }
        }
        switch (r.getEventType()) {
            case 12: {
                return new CharactersEventImpl(loc, r.getText(), true);
            }
            case 4: {
                return new CharactersEventImpl(loc, r.getText(), false);
            }
            case 5: {
                return new CommentEventImpl(loc, r.getText());
            }
            case 11: {
                if (r instanceof XMLStreamReader2) {
                    final XMLStreamReader2 sr2 = (XMLStreamReader2)r;
                    final DTDInfo dtd = sr2.getDTDInfo();
                    return new WDTD(loc, dtd.getDTDRootName(), dtd.getDTDSystemId(), dtd.getDTDPublicId(), dtd.getDTDInternalSubset(), (DTDSubset)dtd.getProcessedDTD());
                }
                return new WDTD(loc, null, r.getText());
            }
            case 8: {
                return new EndDocumentEventImpl(loc);
            }
            case 2: {
                return new EndElementEventImpl(loc, r);
            }
            case 3: {
                return new ProcInstrEventImpl(loc, r.getPITarget(), r.getPIData());
            }
            case 6: {
                final CharactersEventImpl ch = new CharactersEventImpl(loc, r.getText(), false);
                ch.setWhitespaceStatus(true);
                return ch;
            }
            case 7: {
                return new StartDocumentEventImpl(loc, r);
            }
            case 1: {
                if (!(r instanceof StreamReaderImpl)) {
                    NamespaceContext nsCtxt = null;
                    if (r instanceof XMLStreamReader2) {
                        nsCtxt = ((XMLStreamReader2)r).getNonTransientNamespaceContext();
                    }
                    final int attrCount = r.getAttributeCount();
                    Map<QName, Attribute> attrs;
                    if (attrCount < 1) {
                        attrs = null;
                    }
                    else {
                        attrs = new LinkedHashMap<QName, Attribute>();
                        for (int i = 0; i < attrCount; ++i) {
                            final QName aname = r.getAttributeName(i);
                            attrs.put(aname, new AttributeEventImpl(loc, aname, r.getAttributeValue(i), r.isAttributeSpecified(i)));
                        }
                    }
                    final int nsCount = r.getNamespaceCount();
                    List<Namespace> ns;
                    if (nsCount < 1) {
                        ns = null;
                    }
                    else {
                        ns = new ArrayList<Namespace>(nsCount);
                        for (int j = 0; j < nsCount; ++j) {
                            ns.add(NamespaceEventImpl.constructNamespace(loc, r.getNamespacePrefix(j), r.getNamespaceURI(j)));
                        }
                    }
                    return SimpleStartElement.construct(loc, r.getName(), attrs, ns, nsCtxt);
                }
                final StreamReaderImpl sr3 = (StreamReaderImpl)r;
                final BaseStartElement be = (BaseStartElement)sr3.withStartElement(this, loc);
                if (be == null) {
                    throw new WstxException("Trying to create START_ELEMENT when current event is " + ErrorConsts.tokenTypeDesc(sr3.getEventType()), loc);
                }
                return be;
            }
            case 9: {
                if (!(r instanceof StreamReaderImpl)) {
                    return new WEntityReference(loc, r.getLocalName());
                }
                final EntityDecl ed = ((StreamReaderImpl)r).getCurrentEntityDecl();
                if (ed == null) {
                    return new WEntityReference(loc, r.getLocalName());
                }
                return new WEntityReference(loc, ed);
            }
            case 10:
            case 13:
            case 14:
            case 15: {
                throw new WstxException("Internal error: should not get " + ErrorConsts.tokenTypeDesc(r.getEventType()));
            }
            default: {
                throw new IllegalStateException("Unrecognized event type " + r.getEventType() + ".");
            }
        }
    }
    
    @Override
    public void allocate(final XMLStreamReader r, final XMLEventConsumer consumer) throws XMLStreamException {
        consumer.add(this.allocate(r));
    }
    
    @Override
    public XMLEventAllocator newInstance() {
        return new DefaultEventAllocator(this.mAccurateLocation);
    }
    
    @Override
    public Object withStartElement(final Location loc, final QName name, final BaseNsContext nsCtxt, final ElemAttrs attrs, final boolean wasEmpty) {
        return new CompactStartElement(loc, name, nsCtxt, attrs);
    }
    
    static {
        sStdInstance = new DefaultEventAllocator(true);
    }
}
