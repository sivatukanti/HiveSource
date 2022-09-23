// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import javax.xml.stream.Location;
import com.ctc.wstx.ent.EntityDecl;
import org.codehaus.stax2.XMLStreamReader2;

public interface StreamReaderImpl extends XMLStreamReader2
{
    EntityDecl getCurrentEntityDecl();
    
    Object withStartElement(final ElemCallback p0, final Location p1);
    
    boolean isNamespaceAware();
    
    AttributeCollector getAttributeCollector();
    
    InputElementStack getInputElementStack();
}
