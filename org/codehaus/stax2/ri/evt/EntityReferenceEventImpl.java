// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;

public class EntityReferenceEventImpl extends BaseEventImpl implements EntityReference
{
    protected final EntityDeclaration mDecl;
    
    public EntityReferenceEventImpl(final Location location, final EntityDeclaration mDecl) {
        super(location);
        this.mDecl = mDecl;
    }
    
    public EntityReferenceEventImpl(final Location location, final String s) {
        super(location);
        this.mDecl = new EntityDeclarationEventImpl(location, s);
    }
    
    public EntityDeclaration getDeclaration() {
        return this.mDecl;
    }
    
    public String getName() {
        return this.mDecl.getName();
    }
    
    @Override
    public int getEventType() {
        return 9;
    }
    
    @Override
    public boolean isEntityReference() {
        return true;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write(38);
            writer.write(this.getName());
            writer.write(59);
        }
        catch (IOException ex) {
            this.throwFromIOE(ex);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 xmlStreamWriter2) throws XMLStreamException {
        xmlStreamWriter2.writeEntityRef(this.getName());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof EntityReference && this.getName().equals(((EntityReference)o).getName()));
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
