// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.badgerfish;

import org.codehaus.jettison.AbstractXMLInputFactory;
import org.codehaus.jettison.AbstractDOMDocumentParser;

public class BadgerFishDOMDocumentParser extends AbstractDOMDocumentParser
{
    public BadgerFishDOMDocumentParser() {
        super(new BadgerFishXMLInputFactory());
    }
}
