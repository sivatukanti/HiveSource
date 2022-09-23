// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.ProcessingInstruction;

public class ProcInstrEventImpl extends BaseEventImpl implements ProcessingInstruction
{
    final String mTarget;
    final String mData;
    
    public ProcInstrEventImpl(final Location location, final String mTarget, final String mData) {
        super(location);
        this.mTarget = mTarget;
        this.mData = mData;
    }
    
    public String getData() {
        return this.mData;
    }
    
    public String getTarget() {
        return this.mTarget;
    }
    
    @Override
    public int getEventType() {
        return 3;
    }
    
    @Override
    public boolean isProcessingInstruction() {
        return true;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            writer.write("<?");
            writer.write(this.mTarget);
            if (this.mData != null && this.mData.length() > 0) {
                writer.write(this.mData);
            }
            writer.write("?>");
        }
        catch (IOException ex) {
            this.throwFromIOE(ex);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 xmlStreamWriter2) throws XMLStreamException {
        if (this.mData != null && this.mData.length() > 0) {
            xmlStreamWriter2.writeProcessingInstruction(this.mTarget, this.mData);
        }
        else {
            xmlStreamWriter2.writeProcessingInstruction(this.mTarget);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof ProcessingInstruction)) {
            return false;
        }
        final ProcessingInstruction processingInstruction = (ProcessingInstruction)o;
        return this.mTarget.equals(processingInstruction.getTarget()) && BaseEventImpl.stringsWithNullsEqual(this.mData, processingInstruction.getData());
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.mTarget.hashCode();
        if (this.mData != null) {
            hashCode ^= this.mData.hashCode();
        }
        return hashCode;
    }
}
