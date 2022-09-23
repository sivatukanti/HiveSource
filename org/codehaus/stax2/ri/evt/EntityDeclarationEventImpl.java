// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import java.io.StringWriter;
import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.EntityDeclaration;

public class EntityDeclarationEventImpl extends BaseEventImpl implements EntityDeclaration
{
    protected final String mName;
    
    public EntityDeclarationEventImpl(final Location location, final String mName) {
        super(location);
        this.mName = mName;
    }
    
    public String getBaseURI() {
        return "";
    }
    
    public String getName() {
        return this.mName;
    }
    
    public String getNotationName() {
        return null;
    }
    
    public String getPublicId() {
        return null;
    }
    
    public String getReplacementText() {
        return null;
    }
    
    public String getSystemId() {
        return null;
    }
    
    @Override
    public int getEventType() {
        return 15;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write("<!ENTITY ");
            writer.write(this.getName());
            writer.write(" \"");
            final String replacementText = this.getReplacementText();
            if (replacementText != null) {
                writer.write(replacementText);
            }
            writer.write("\">");
        }
        catch (IOException ex) {
            this.throwFromIOE(ex);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 xmlStreamWriter2) throws XMLStreamException {
        final StringWriter stringWriter = new StringWriter();
        this.writeAsEncodedUnicode(stringWriter);
        xmlStreamWriter2.writeRaw(stringWriter.toString());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof EntityDeclaration)) {
            return false;
        }
        final EntityDeclaration entityDeclaration = (EntityDeclaration)o;
        return BaseEventImpl.stringsWithNullsEqual(this.getName(), entityDeclaration.getName()) && BaseEventImpl.stringsWithNullsEqual(this.getBaseURI(), entityDeclaration.getBaseURI()) && BaseEventImpl.stringsWithNullsEqual(this.getNotationName(), entityDeclaration.getNotationName()) && BaseEventImpl.stringsWithNullsEqual(this.getPublicId(), entityDeclaration.getPublicId()) && BaseEventImpl.stringsWithNullsEqual(this.getReplacementText(), entityDeclaration.getReplacementText()) && BaseEventImpl.stringsWithNullsEqual(this.getSystemId(), entityDeclaration.getSystemId());
    }
    
    @Override
    public int hashCode() {
        return this.mName.hashCode();
    }
}
