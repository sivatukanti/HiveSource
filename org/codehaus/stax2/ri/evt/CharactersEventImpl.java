// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.evt;

import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.Characters;

public class CharactersEventImpl extends BaseEventImpl implements Characters
{
    final String mContent;
    final boolean mIsCData;
    final boolean mIgnorableWS;
    boolean mWhitespaceChecked;
    boolean mIsWhitespace;
    
    public CharactersEventImpl(final Location location, final String mContent, final boolean mIsCData) {
        super(location);
        this.mWhitespaceChecked = false;
        this.mIsWhitespace = false;
        this.mContent = mContent;
        this.mIsCData = mIsCData;
        this.mIgnorableWS = false;
    }
    
    private CharactersEventImpl(final Location location, final String mContent, final boolean mIsCData, final boolean mIsWhitespace, final boolean mIgnorableWS) {
        super(location);
        this.mWhitespaceChecked = false;
        this.mIsWhitespace = false;
        this.mContent = mContent;
        this.mIsCData = mIsCData;
        this.mIsWhitespace = mIsWhitespace;
        if (mIsWhitespace) {
            this.mWhitespaceChecked = true;
            this.mIgnorableWS = mIgnorableWS;
        }
        else {
            this.mWhitespaceChecked = false;
            this.mIgnorableWS = false;
        }
    }
    
    public static final CharactersEventImpl createIgnorableWS(final Location location, final String s) {
        return new CharactersEventImpl(location, s, false, true, true);
    }
    
    public static final CharactersEventImpl createNonIgnorableWS(final Location location, final String s) {
        return new CharactersEventImpl(location, s, false, true, false);
    }
    
    @Override
    public Characters asCharacters() {
        return this;
    }
    
    @Override
    public int getEventType() {
        return this.mIsCData ? 12 : 4;
    }
    
    @Override
    public boolean isCharacters() {
        return true;
    }
    
    @Override
    public void writeAsEncodedUnicode(final Writer writer) throws XMLStreamException {
        try {
            if (this.mIsCData) {
                writer.write("<![CDATA[");
                writer.write(this.mContent);
                writer.write("]]>");
            }
            else {
                writeEscapedXMLText(writer, this.mContent);
            }
        }
        catch (IOException ex) {
            this.throwFromIOE(ex);
        }
    }
    
    @Override
    public void writeUsing(final XMLStreamWriter2 xmlStreamWriter2) throws XMLStreamException {
        if (this.mIsCData) {
            xmlStreamWriter2.writeCData(this.mContent);
        }
        else {
            xmlStreamWriter2.writeCharacters(this.mContent);
        }
    }
    
    public String getData() {
        return this.mContent;
    }
    
    public boolean isCData() {
        return this.mIsCData;
    }
    
    public boolean isIgnorableWhiteSpace() {
        return this.mIgnorableWS;
    }
    
    public boolean isWhiteSpace() {
        if (!this.mWhitespaceChecked) {
            this.mWhitespaceChecked = true;
            String mContent;
            int index;
            int length;
            for (mContent = this.mContent, index = 0, length = mContent.length(); index < length && mContent.charAt(index) <= ' '; ++index) {}
            this.mIsWhitespace = (index == length);
        }
        return this.mIsWhitespace;
    }
    
    public void setWhitespaceStatus(final boolean mIsWhitespace) {
        this.mWhitespaceChecked = true;
        this.mIsWhitespace = mIsWhitespace;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Characters)) {
            return false;
        }
        final Characters characters = (Characters)o;
        return this.mContent.equals(characters.getData()) && this.isCData() == characters.isCData();
    }
    
    @Override
    public int hashCode() {
        return this.mContent.hashCode();
    }
    
    protected static void writeEscapedXMLText(final Writer writer, final String str) throws IOException {
        for (int length = str.length(), i = 0; i < length; ++i) {
            final int off = i;
            char char1 = '\0';
            while (i < length) {
                char1 = str.charAt(i);
                if (char1 == '<') {
                    break;
                }
                if (char1 == '&') {
                    break;
                }
                if (char1 == '>' && i >= 2 && str.charAt(i - 1) == ']' && str.charAt(i - 2) == ']') {
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
                else if (char1 == '>') {
                    writer.write("&gt;");
                }
            }
        }
    }
}
