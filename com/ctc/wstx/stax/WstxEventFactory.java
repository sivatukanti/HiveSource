// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.stax;

import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.Attribute;
import com.ctc.wstx.evt.SimpleStartElement;
import javax.xml.stream.events.StartElement;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import com.ctc.wstx.compat.QNameCreator;
import javax.xml.namespace.QName;
import com.ctc.wstx.evt.WDTD;
import javax.xml.stream.events.DTD;
import org.codehaus.stax2.ri.Stax2EventFactoryImpl;

public final class WstxEventFactory extends Stax2EventFactoryImpl
{
    @Override
    public DTD createDTD(final String dtd) {
        return new WDTD(this.mLocation, dtd);
    }
    
    @Override
    protected QName createQName(final String nsURI, final String localName) {
        return new QName(nsURI, localName);
    }
    
    @Override
    protected QName createQName(final String nsURI, final String localName, final String prefix) {
        return QNameCreator.create(nsURI, localName, prefix);
    }
    
    @Override
    protected StartElement createStartElement(final QName name, final Iterator<?> attr, final Iterator<?> ns, final NamespaceContext ctxt) {
        return SimpleStartElement.construct(this.mLocation, name, (Iterator<Attribute>)attr, (Iterator<Namespace>)ns, ctxt);
    }
}
