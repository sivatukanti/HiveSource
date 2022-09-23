// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import java.io.StringWriter;
import javax.xml.stream.events.DTD;
import org.codehaus.stax2.XMLStreamWriter2;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.Location;
import org.codehaus.stax2.evt.DTD2;

public class DTDEventImpl extends BaseEventImpl implements DTD2
{
    final String mRootName;
    final String mSystemId;
    final String mPublicId;
    final String mInternalSubset;
    final Object mDTD;
    String mFullText;
    
    public DTDEventImpl(final Location location, final String mRootName, final String mSystemId, final String mPublicId, final String mInternalSubset, final Object mdtd) {
        super(location);
        this.mFullText = null;
        this.mRootName = mRootName;
        this.mSystemId = mSystemId;
        this.mPublicId = mPublicId;
        this.mInternalSubset = mInternalSubset;
        this.mFullText = null;
        this.mDTD = mdtd;
    }
    
    public DTDEventImpl(final Location location, final String s, final String s2) {
        this(location, s, null, null, s2, null);
    }
    
    public DTDEventImpl(final Location location, final String mFullText) {
        this(location, null, null, null, null, null);
        this.mFullText = mFullText;
    }
    
    public String getDocumentTypeDeclaration() {
        try {
            return this.doGetDocumentTypeDeclaration();
        }
        catch (XMLStreamException obj) {
            throw new RuntimeException("Internal error: " + obj);
        }
    }
    
    public List getEntities() {
        return null;
    }
    
    public List getNotations() {
        return null;
    }
    
    public Object getProcessedDTD() {
        return this.mDTD;
    }
    
    @Override
    public int getEventType() {
        return 11;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            if (this.mFullText != null) {
                writer.write(this.mFullText);
                return;
            }
            writer.write("<!DOCTYPE");
            if (this.mRootName != null) {
                writer.write(32);
                writer.write(this.mRootName);
            }
            if (this.mSystemId != null) {
                if (this.mPublicId != null) {
                    writer.write(" PUBLIC \"");
                    writer.write(this.mPublicId);
                    writer.write(34);
                }
                else {
                    writer.write(" SYSTEM");
                }
                writer.write(" \"");
                writer.write(this.mSystemId);
                writer.write(34);
            }
            if (this.mInternalSubset != null) {
                writer.write(" [");
                writer.write(this.mInternalSubset);
                writer.write(93);
            }
            writer.write(">");
        }
        catch (IOException ex) {
            this.throwFromIOE(ex);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 xmlStreamWriter2) throws XMLStreamException {
        if (this.mRootName != null) {
            xmlStreamWriter2.writeDTD(this.mRootName, this.mSystemId, this.mPublicId, this.mInternalSubset);
            return;
        }
        xmlStreamWriter2.writeDTD(this.doGetDocumentTypeDeclaration());
    }
    
    public String getRootName() {
        return this.mRootName;
    }
    
    public String getSystemId() {
        return this.mSystemId;
    }
    
    public String getPublicId() {
        return this.mPublicId;
    }
    
    public String getInternalSubset() {
        return this.mInternalSubset;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof DTD && BaseEventImpl.stringsWithNullsEqual(this.getDocumentTypeDeclaration(), ((DTD)o).getDocumentTypeDeclaration()));
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        if (this.mRootName != null) {
            n ^= this.mRootName.hashCode();
        }
        if (this.mSystemId != null) {
            n ^= this.mSystemId.hashCode();
        }
        if (this.mPublicId != null) {
            n ^= this.mPublicId.hashCode();
        }
        if (this.mInternalSubset != null) {
            n ^= this.mInternalSubset.hashCode();
        }
        if (this.mDTD != null) {
            n ^= this.mDTD.hashCode();
        }
        if (n == 0 && this.mFullText != null) {
            n ^= this.mFullText.hashCode();
        }
        return n;
    }
    
    protected String doGetDocumentTypeDeclaration() throws XMLStreamException {
        if (this.mFullText == null) {
            int initialSize = 60;
            if (this.mInternalSubset != null) {
                initialSize += this.mInternalSubset.length() + 4;
            }
            final StringWriter stringWriter = new StringWriter(initialSize);
            this.writeAsEncodedUnicode(stringWriter);
            this.mFullText = stringWriter.toString();
        }
        return this.mFullText;
    }
}
