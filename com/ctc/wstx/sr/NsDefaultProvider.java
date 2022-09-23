// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import javax.xml.stream.XMLStreamException;

public interface NsDefaultProvider
{
    boolean mayHaveNsDefaults(final String p0, final String p1);
    
    void checkNsDefaults(final InputElementStack p0) throws XMLStreamException;
}
