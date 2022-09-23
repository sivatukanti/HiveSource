// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.evt;

import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.exc.WstxParsingException;
import javax.xml.stream.Location;
import com.ctc.wstx.cfg.ErrorConsts;
import org.codehaus.stax2.XMLStreamReader2;
import javax.xml.stream.util.XMLEventAllocator;
import org.codehaus.stax2.ri.Stax2EventReaderImpl;

public class WstxEventReader extends Stax2EventReaderImpl
{
    public WstxEventReader(final XMLEventAllocator a, final XMLStreamReader2 r) {
        super(a, r);
    }
    
    @Override
    protected String getErrorDesc(final int errorType, final int currEvent) {
        switch (errorType) {
            case 1: {
                return ErrorConsts.ERR_STATE_NOT_STELEM + ", got " + ErrorConsts.tokenTypeDesc(currEvent);
            }
            case 2: {
                return "Expected a text token, got " + ErrorConsts.tokenTypeDesc(currEvent);
            }
            case 3: {
                return "Only all-whitespace CHARACTERS/CDATA (or SPACE) allowed for nextTag(), got " + ErrorConsts.tokenTypeDesc(currEvent);
            }
            case 4: {
                return "Got " + ErrorConsts.tokenTypeDesc(currEvent) + ", instead of START_ELEMENT, END_ELEMENT or SPACE";
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public boolean isPropertySupported(final String name) {
        return ((XMLStreamReader2)this.getStreamReader()).isPropertySupported(name);
    }
    
    @Override
    public boolean setProperty(final String name, final Object value) {
        return ((XMLStreamReader2)this.getStreamReader()).setProperty(name, value);
    }
    
    @Override
    protected void reportProblem(final String msg, final Location loc) throws XMLStreamException {
        throw new WstxParsingException(msg, loc);
    }
}
