// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import javax.xml.stream.XMLStreamException;
import java.net.URL;

public interface DTDEventListener
{
    boolean dtdReportComments();
    
    void dtdProcessingInstruction(final String p0, final String p1);
    
    void dtdComment(final char[] p0, final int p1, final int p2);
    
    void dtdSkippedEntity(final String p0);
    
    void dtdNotationDecl(final String p0, final String p1, final String p2, final URL p3) throws XMLStreamException;
    
    void dtdUnparsedEntityDecl(final String p0, final String p1, final String p2, final String p3, final URL p4) throws XMLStreamException;
    
    void attributeDecl(final String p0, final String p1, final String p2, final String p3, final String p4);
    
    void dtdElementDecl(final String p0, final String p1);
    
    void dtdExternalEntityDecl(final String p0, final String p1, final String p2);
    
    void dtdInternalEntityDecl(final String p0, final String p1);
}
