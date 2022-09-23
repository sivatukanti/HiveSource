// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import javax.xml.stream.Location;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLStreamReader2;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.XMLStreamConstants;
import org.codehaus.stax2.XMLEventReader2;

public abstract class Stax2EventReaderImpl implements XMLEventReader2, XMLStreamConstants
{
    protected static final int STATE_INITIAL = 1;
    protected static final int STATE_END_OF_INPUT = 2;
    protected static final int STATE_CONTENT = 3;
    protected static final int ERR_GETELEMTEXT_NOT_START_ELEM = 1;
    protected static final int ERR_GETELEMTEXT_NON_TEXT_EVENT = 2;
    protected static final int ERR_NEXTTAG_NON_WS_TEXT = 3;
    protected static final int ERR_NEXTTAG_WRONG_TYPE = 4;
    protected final XMLEventAllocator mAllocator;
    protected final XMLStreamReader2 mReader;
    private XMLEvent mPeekedEvent;
    protected int mState;
    protected int mPrePeekEvent;
    
    protected Stax2EventReaderImpl(final XMLEventAllocator mAllocator, final XMLStreamReader2 mReader) {
        this.mPeekedEvent = null;
        this.mState = 1;
        this.mPrePeekEvent = 7;
        this.mAllocator = mAllocator;
        this.mReader = mReader;
    }
    
    public abstract boolean isPropertySupported(final String p0);
    
    public abstract boolean setProperty(final String p0, final Object p1);
    
    protected abstract String getErrorDesc(final int p0, final int p1);
    
    public void close() throws XMLStreamException {
        this.mReader.close();
    }
    
    public String getElementText() throws XMLStreamException {
        if (this.mPeekedEvent == null) {
            return this.mReader.getElementText();
        }
        XMLEvent xmlEvent = this.mPeekedEvent;
        this.mPeekedEvent = null;
        if (this.mPrePeekEvent != 1) {
            this.reportProblem(this.findErrorDesc(1, this.mPrePeekEvent));
        }
        String str = null;
        StringBuffer sb = null;
        while (!xmlEvent.isEndElement()) {
            final int eventType = xmlEvent.getEventType();
            if (eventType != 5) {
                if (eventType != 3) {
                    if (!xmlEvent.isCharacters()) {
                        this.reportProblem(this.findErrorDesc(2, eventType));
                    }
                    final String data = xmlEvent.asCharacters().getData();
                    if (str == null) {
                        str = data;
                    }
                    else {
                        if (sb == null) {
                            sb = new StringBuffer(str.length() + data.length());
                            sb.append(str);
                        }
                        sb.append(data);
                    }
                }
            }
            xmlEvent = this.nextEvent();
        }
        if (sb != null) {
            return sb.toString();
        }
        return (str == null) ? "" : str;
    }
    
    public Object getProperty(final String s) {
        return this.mReader.getProperty(s);
    }
    
    public boolean hasNext() {
        return this.mState != 2;
    }
    
    public XMLEvent nextEvent() throws XMLStreamException {
        if (this.mState == 2) {
            this.throwEndOfInput();
        }
        else if (this.mState == 1) {
            this.mState = 3;
            return this.createStartDocumentEvent();
        }
        if (this.mPeekedEvent != null) {
            final XMLEvent mPeekedEvent = this.mPeekedEvent;
            this.mPeekedEvent = null;
            if (mPeekedEvent.isEndDocument()) {
                this.mState = 2;
            }
            return mPeekedEvent;
        }
        return this.createNextEvent(true, this.mReader.next());
    }
    
    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException ex) {
            this.throwUnchecked(ex);
            return null;
        }
    }
    
    public XMLEvent nextTag() throws XMLStreamException {
        if (this.mPeekedEvent != null) {
            final XMLEvent mPeekedEvent = this.mPeekedEvent;
            this.mPeekedEvent = null;
            final int eventType = mPeekedEvent.getEventType();
            switch (eventType) {
                case 8: {
                    return null;
                }
                case 7: {
                    break;
                }
                case 6: {
                    break;
                }
                case 3:
                case 5: {
                    break;
                }
                case 4:
                case 12: {
                    if (((Characters)mPeekedEvent).isWhiteSpace()) {
                        break;
                    }
                    this.reportProblem(this.findErrorDesc(3, eventType));
                    break;
                }
                case 1:
                case 2: {
                    return mPeekedEvent;
                }
                default: {
                    this.reportProblem(this.findErrorDesc(4, eventType));
                    break;
                }
            }
        }
        else if (this.mState == 1) {
            this.mState = 3;
        }
        while (true) {
            final int next = this.mReader.next();
            switch (next) {
                case 8: {
                    return null;
                }
                case 3:
                case 5:
                case 6: {
                    continue;
                }
                case 4:
                case 12: {
                    if (this.mReader.isWhiteSpace()) {
                        continue;
                    }
                    this.reportProblem(this.findErrorDesc(3, next));
                    continue;
                }
                case 1:
                case 2: {
                    return this.createNextEvent(false, next);
                }
                default: {
                    this.reportProblem(this.findErrorDesc(4, next));
                    continue;
                }
            }
        }
    }
    
    public XMLEvent peek() throws XMLStreamException {
        if (this.mPeekedEvent == null) {
            if (this.mState == 2) {
                return null;
            }
            if (this.mState == 1) {
                this.mPrePeekEvent = 7;
                this.mPeekedEvent = this.createStartDocumentEvent();
                this.mState = 3;
            }
            else {
                this.mPrePeekEvent = this.mReader.getEventType();
                this.mPeekedEvent = this.createNextEvent(false, this.mReader.next());
            }
        }
        return this.mPeekedEvent;
    }
    
    public void remove() {
        throw new UnsupportedOperationException("Can not remove events from XMLEventReader.");
    }
    
    public boolean hasNextEvent() throws XMLStreamException {
        return this.mState != 2;
    }
    
    protected XMLEvent createNextEvent(final boolean b, final int n) throws XMLStreamException {
        try {
            final XMLEvent allocate = this.mAllocator.allocate(this.mReader);
            if (b && n == 8) {
                this.mState = 2;
            }
            return allocate;
        }
        catch (RuntimeException ex) {
            for (Throwable t = ex.getCause(); t != null; t = t.getCause()) {
                if (t instanceof XMLStreamException) {
                    throw (XMLStreamException)t;
                }
            }
            throw ex;
        }
    }
    
    protected XMLEvent createStartDocumentEvent() throws XMLStreamException {
        return this.mAllocator.allocate(this.mReader);
    }
    
    private void throwEndOfInput() {
        throw new NoSuchElementException();
    }
    
    protected void throwUnchecked(final XMLStreamException ex) {
        final Throwable cause = (ex.getNestedException() == null) ? ex : ex.getNestedException();
        if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
        }
        if (cause instanceof Error) {
            throw (Error)cause;
        }
        throw new RuntimeException("[was " + cause.getClass() + "] " + cause.getMessage(), cause);
    }
    
    protected void reportProblem(final String s) throws XMLStreamException {
        this.reportProblem(s, this.mReader.getLocation());
    }
    
    protected void reportProblem(final String s, final Location location) throws XMLStreamException {
        if (location == null) {
            throw new XMLStreamException(s);
        }
        throw new XMLStreamException(s, location);
    }
    
    protected XMLStreamReader getStreamReader() {
        return this.mReader;
    }
    
    private final String findErrorDesc(final int i, final int n) {
        final String errorDesc = this.getErrorDesc(i, n);
        if (errorDesc != null) {
            return errorDesc;
        }
        switch (i) {
            case 1: {
                return "Current state not START_ELEMENT when calling getElementText()";
            }
            case 2: {
                return "Expected a text token";
            }
            case 3: {
                return "Only all-whitespace CHARACTERS/CDATA (or SPACE) allowed for nextTag()";
            }
            case 4: {
                return "Should only encounter START_ELEMENT/END_ELEMENT, SPACE, or all-white-space CHARACTERS";
            }
            default: {
                return "Internal error (unrecognized error type: " + i + ")";
            }
        }
    }
}
