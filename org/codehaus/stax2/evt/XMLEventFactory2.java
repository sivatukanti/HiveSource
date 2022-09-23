// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.evt;

import javax.xml.stream.XMLEventFactory;

public abstract class XMLEventFactory2 extends XMLEventFactory
{
    protected XMLEventFactory2() {
    }
    
    public abstract DTD2 createDTD(final String p0, final String p1, final String p2, final String p3);
    
    public abstract DTD2 createDTD(final String p0, final String p1, final String p2, final String p3, final Object p4);
}
