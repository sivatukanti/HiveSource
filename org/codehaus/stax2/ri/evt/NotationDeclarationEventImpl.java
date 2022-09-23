// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import org.codehaus.stax2.evt.NotationDeclaration2;

public class NotationDeclarationEventImpl extends BaseEventImpl implements NotationDeclaration2
{
    final String mName;
    final String mPublicId;
    final String mSystemId;
    
    public NotationDeclarationEventImpl(final Location location, final String mName, final String mPublicId, final String mSystemId) {
        super(location);
        this.mName = mName;
        this.mPublicId = mPublicId;
        this.mSystemId = mSystemId;
    }
    
    public String getName() {
        return this.mName;
    }
    
    public String getPublicId() {
        return this.mPublicId;
    }
    
    public String getSystemId() {
        return this.mSystemId;
    }
    
    public String getBaseURI() {
        return "";
    }
    
    @Override
    public int getEventType() {
        return 14;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write("<!NOTATION ");
            writer.write(this.mName);
            if (this.mPublicId != null) {
                writer.write("PUBLIC \"");
                writer.write(this.mPublicId);
                writer.write(34);
            }
            else {
                writer.write("SYSTEM");
            }
            if (this.mSystemId != null) {
                writer.write(" \"");
                writer.write(this.mSystemId);
                writer.write(34);
            }
            writer.write(62);
        }
        catch (IOException ex) {
            this.throwFromIOE(ex);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 xmlStreamWriter2) throws XMLStreamException {
        throw new XMLStreamException("Can not write notation declarations using an XMLStreamWriter");
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof NotationDeclaration2)) {
            return false;
        }
        final NotationDeclaration2 notationDeclaration2 = (NotationDeclaration2)o;
        return BaseEventImpl.stringsWithNullsEqual(this.getName(), notationDeclaration2.getName()) && BaseEventImpl.stringsWithNullsEqual(this.getPublicId(), notationDeclaration2.getPublicId()) && BaseEventImpl.stringsWithNullsEqual(this.getSystemId(), notationDeclaration2.getSystemId()) && BaseEventImpl.stringsWithNullsEqual(this.getBaseURI(), notationDeclaration2.getBaseURI());
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        if (this.mName != null) {
            n ^= this.mName.hashCode();
        }
        if (this.mPublicId != null) {
            n ^= this.mPublicId.hashCode();
        }
        if (this.mSystemId != null) {
            n ^= this.mSystemId.hashCode();
        }
        return n;
    }
}
