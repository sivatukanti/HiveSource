// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamConstants;
import org.codehaus.stax2.util.StreamReader2Delegate;

public class Stax2FilteredStreamReader extends StreamReader2Delegate implements XMLStreamConstants
{
    final StreamFilter mFilter;
    
    public Stax2FilteredStreamReader(final XMLStreamReader xmlStreamReader, final StreamFilter mFilter) {
        super(Stax2ReaderAdapter.wrapIfNecessary(xmlStreamReader));
        this.mFilter = mFilter;
    }
    
    @Override
    public int next() throws XMLStreamException {
        int i;
        do {
            i = this.mDelegate2.next();
            if (this.mFilter.accept(this)) {
                break;
            }
        } while (i != 8);
        return i;
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        int nextTag;
        do {
            nextTag = this.mDelegate2.nextTag();
        } while (!this.mFilter.accept(this));
        return nextTag;
    }
}
