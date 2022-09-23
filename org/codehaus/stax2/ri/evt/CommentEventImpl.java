// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.Comment;

public class CommentEventImpl extends BaseEventImpl implements Comment
{
    final String mContent;
    
    public CommentEventImpl(final Location location, final String mContent) {
        super(location);
        this.mContent = mContent;
    }
    
    public String getText() {
        return this.mContent;
    }
    
    @Override
    public int getEventType() {
        return 5;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write("<!--");
            writer.write(this.mContent);
            writer.write("-->");
        }
        catch (IOException ex) {
            this.throwFromIOE(ex);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 xmlStreamWriter2) throws XMLStreamException {
        xmlStreamWriter2.writeComment(this.mContent);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof Comment && this.mContent.equals(((Comment)o).getText()));
    }
    
    @Override
    public int hashCode() {
        return this.mContent.hashCode();
    }
}
