// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public interface Receiver
{
    void receive(final UnmarshallingContext.State p0, final Object p1) throws SAXException;
}
