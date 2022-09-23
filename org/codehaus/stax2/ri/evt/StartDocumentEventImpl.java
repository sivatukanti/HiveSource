// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.Location;
import javax.xml.stream.events.StartDocument;

public class StartDocumentEventImpl extends BaseEventImpl implements StartDocument
{
    private final boolean mStandaloneSet;
    private final boolean mIsStandalone;
    private final String mVersion;
    private final boolean mEncodingSet;
    private final String mEncodingScheme;
    private final String mSystemId;
    
    public StartDocumentEventImpl(final Location location, final XMLStreamReader xmlStreamReader) {
        super(location);
        this.mStandaloneSet = xmlStreamReader.standaloneSet();
        this.mIsStandalone = xmlStreamReader.isStandalone();
        String version = xmlStreamReader.getVersion();
        if (version == null || version.length() == 0) {
            version = "1.0";
        }
        this.mVersion = version;
        this.mEncodingScheme = xmlStreamReader.getCharacterEncodingScheme();
        this.mEncodingSet = (this.mEncodingScheme != null && this.mEncodingScheme.length() > 0);
        this.mSystemId = ((location != null) ? location.getSystemId() : "");
    }
    
    public StartDocumentEventImpl(final Location location) {
        this(location, (String)null);
    }
    
    public StartDocumentEventImpl(final Location location, final String s) {
        this(location, s, null);
    }
    
    public StartDocumentEventImpl(final Location location, final String s, final String s2) {
        this(location, s, s2, false, false);
    }
    
    public StartDocumentEventImpl(final Location location, final String mEncodingScheme, final String mVersion, final boolean mStandaloneSet, final boolean mIsStandalone) {
        super(location);
        this.mEncodingScheme = mEncodingScheme;
        this.mEncodingSet = (mEncodingScheme != null && mEncodingScheme.length() > 0);
        this.mVersion = mVersion;
        this.mStandaloneSet = mStandaloneSet;
        this.mIsStandalone = mIsStandalone;
        this.mSystemId = "";
    }
    
    public boolean encodingSet() {
        return this.mEncodingSet;
    }
    
    public String getCharacterEncodingScheme() {
        return this.mEncodingScheme;
    }
    
    public String getSystemId() {
        return this.mSystemId;
    }
    
    public String getVersion() {
        return this.mVersion;
    }
    
    public boolean isStandalone() {
        return this.mIsStandalone;
    }
    
    public boolean standaloneSet() {
        return this.mStandaloneSet;
    }
    
    @Override
    public int getEventType() {
        return 7;
    }
    
    @Override
    public boolean isStartDocument() {
        return true;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write("<?xml version=\"");
            if (this.mVersion == null || this.mVersion.length() == 0) {
                writer.write("1.0");
            }
            else {
                writer.write(this.mVersion);
            }
            writer.write(34);
            if (this.mEncodingSet) {
                writer.write(" encoding=\"");
                writer.write(this.mEncodingScheme);
                writer.write(34);
            }
            if (this.mStandaloneSet) {
                if (this.mIsStandalone) {
                    writer.write(" standalone=\"yes\"");
                }
                else {
                    writer.write(" standalone=\"no\"");
                }
            }
            writer.write(" ?>");
        }
        catch (IOException ex) {
            this.throwFromIOE(ex);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 xmlStreamWriter2) throws XMLStreamException {
        xmlStreamWriter2.writeStartDocument();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof StartDocument)) {
            return false;
        }
        final StartDocument startDocument = (StartDocument)o;
        return this.encodingSet() == startDocument.encodingSet() && this.isStandalone() == startDocument.isStandalone() && this.standaloneSet() == startDocument.standaloneSet() && BaseEventImpl.stringsWithNullsEqual(this.getCharacterEncodingScheme(), startDocument.getCharacterEncodingScheme()) && BaseEventImpl.stringsWithNullsEqual(this.getSystemId(), startDocument.getSystemId()) && BaseEventImpl.stringsWithNullsEqual(this.getVersion(), startDocument.getVersion());
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        if (this.encodingSet()) {
            ++n;
        }
        if (this.isStandalone()) {
            --n;
        }
        if (this.standaloneSet()) {
            n ^= 0x1;
        }
        if (this.mVersion != null) {
            n ^= this.mVersion.hashCode();
        }
        if (this.mEncodingScheme != null) {
            n ^= this.mEncodingScheme.hashCode();
        }
        if (this.mSystemId != null) {
            n ^= this.mSystemId.hashCode();
        }
        return n;
    }
}
