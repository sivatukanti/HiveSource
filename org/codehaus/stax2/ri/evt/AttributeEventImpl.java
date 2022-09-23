// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

public class AttributeEventImpl extends BaseEventImpl implements Attribute
{
    final QName mName;
    final String mValue;
    final boolean mWasSpecified;
    
    public AttributeEventImpl(final Location location, final String localPart, String s, final String prefix, final String mValue, final boolean mWasSpecified) {
        super(location);
        this.mValue = mValue;
        if (prefix == null) {
            if (s == null) {
                this.mName = new QName(localPart);
            }
            else {
                this.mName = new QName(s, localPart);
            }
        }
        else {
            if (s == null) {
                s = "";
            }
            this.mName = new QName(s, localPart, prefix);
        }
        this.mWasSpecified = mWasSpecified;
    }
    
    public AttributeEventImpl(final Location location, final QName mName, final String mValue, final boolean mWasSpecified) {
        super(location);
        this.mName = mName;
        this.mValue = mValue;
        this.mWasSpecified = mWasSpecified;
    }
    
    @Override
    public int getEventType() {
        return 10;
    }
    
    @Override
    public boolean isAttribute() {
        return true;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        final String prefix = this.mName.getPrefix();
        try {
            if (prefix != null && prefix.length() > 0) {
                writer.write(prefix);
                writer.write(58);
            }
            writer.write(this.mName.getLocalPart());
            writer.write(61);
            writer.write(34);
            writeEscapedAttrValue(writer, this.mValue);
            writer.write(34);
        }
        catch (IOException ex) {
            this.throwFromIOE(ex);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 xmlStreamWriter2) throws XMLStreamException {
        final QName mName = this.mName;
        xmlStreamWriter2.writeAttribute(mName.getPrefix(), mName.getLocalPart(), mName.getNamespaceURI(), this.mValue);
    }
    
    public String getDTDType() {
        return "CDATA";
    }
    
    public QName getName() {
        return this.mName;
    }
    
    public String getValue() {
        return this.mValue;
    }
    
    public boolean isSpecified() {
        return this.mWasSpecified;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Attribute)) {
            return false;
        }
        final Attribute attribute = (Attribute)o;
        return this.mName.equals(attribute.getName()) && this.mValue.equals(attribute.getValue()) && this.isSpecified() == attribute.isSpecified() && BaseEventImpl.stringsWithNullsEqual(this.getDTDType(), attribute.getDTDType());
    }
    
    @Override
    public int hashCode() {
        return this.mName.hashCode() ^ this.mValue.hashCode();
    }
    
    protected static void writeEscapedAttrValue(final Writer writer, final String str) throws IOException {
        int i = 0;
        final int length = str.length();
        do {
            final int off = i;
            char char1 = '\0';
            while (i < length) {
                char1 = str.charAt(i);
                if (char1 == '<' || char1 == '&') {
                    break;
                }
                if (char1 == '\"') {
                    break;
                }
                ++i;
            }
            final int len = i - off;
            if (len > 0) {
                writer.write(str, off, len);
            }
            if (i < length) {
                if (char1 == '<') {
                    writer.write("&lt;");
                }
                else if (char1 == '&') {
                    writer.write("&amp;");
                }
                else {
                    if (char1 != '\"') {
                        continue;
                    }
                    writer.write("&quot;");
                }
            }
        } while (++i < length);
    }
}
