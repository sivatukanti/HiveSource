// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidator;
import com.ctc.wstx.dtd.DTDEventListener;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import java.io.StringReader;
import java.io.CharArrayReader;
import java.io.Reader;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.codehaus.stax2.ri.typed.CharArrayBase64Decoder;
import org.codehaus.stax2.typed.Base64Variant;
import javax.xml.stream.Location;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import com.ctc.wstx.sr.InputProblemReporter;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import java.util.ArrayList;
import com.ctc.wstx.api.ReaderConfig;

public final class TextBuffer
{
    static final int DEF_INITIAL_BUFFER_SIZE = 500;
    static final int MAX_SEGMENT_LENGTH = 262144;
    static final int INT_SPACE = 32;
    private final ReaderConfig mConfig;
    private char[] mInputBuffer;
    private int mInputStart;
    private int mInputLen;
    private boolean mHasSegments;
    private ArrayList<char[]> mSegments;
    private int mSegmentSize;
    private char[] mCurrentSegment;
    private int mCurrentSize;
    private String mResultString;
    private char[] mResultArray;
    public static final int MAX_INDENT_SPACES = 32;
    public static final int MAX_INDENT_TABS = 8;
    private static final String sIndSpaces = "\n                                 ";
    private static final char[] sIndSpacesArray;
    private static final String[] sIndSpacesStrings;
    private static final String sIndTabs = "\n\t\t\t\t\t\t\t\t\t";
    private static final char[] sIndTabsArray;
    private static final String[] sIndTabsStrings;
    
    private TextBuffer(final ReaderConfig cfg) {
        this.mHasSegments = false;
        this.mConfig = cfg;
    }
    
    public static TextBuffer createRecyclableBuffer(final ReaderConfig cfg) {
        return new TextBuffer(cfg);
    }
    
    public static TextBuffer createTemporaryBuffer() {
        return new TextBuffer(null);
    }
    
    public void recycle(final boolean force) {
        if (this.mConfig != null && this.mCurrentSegment != null) {
            if (force) {
                this.resetWithEmpty();
            }
            else {
                if (this.mInputStart < 0 && this.mSegmentSize + this.mCurrentSize > 0) {
                    return;
                }
                if (this.mSegments != null && this.mSegments.size() > 0) {
                    this.mSegments.clear();
                    this.mSegmentSize = 0;
                }
            }
            final char[] buf = this.mCurrentSegment;
            this.mCurrentSegment = null;
            this.mConfig.freeMediumCBuffer(buf);
        }
    }
    
    public void resetWithEmpty() {
        this.mInputBuffer = null;
        this.mInputStart = -1;
        this.mInputLen = 0;
        this.mResultString = null;
        this.mResultArray = null;
        if (this.mHasSegments) {
            this.clearSegments();
        }
        this.mCurrentSize = 0;
    }
    
    public void resetWithEmptyString() {
        this.mInputBuffer = null;
        this.mInputStart = -1;
        this.mInputLen = 0;
        this.mResultString = "";
        this.mResultArray = null;
        if (this.mHasSegments) {
            this.clearSegments();
        }
        this.mCurrentSize = 0;
    }
    
    public void resetWithShared(final char[] buf, final int start, final int len) {
        this.mInputBuffer = buf;
        this.mInputStart = start;
        this.mInputLen = len;
        this.mResultString = null;
        this.mResultArray = null;
        if (this.mHasSegments) {
            this.clearSegments();
        }
    }
    
    public void resetWithCopy(final char[] buf, final int start, final int len) {
        this.mInputBuffer = null;
        this.mInputStart = -1;
        this.mInputLen = 0;
        this.mResultString = null;
        this.mResultArray = null;
        if (this.mHasSegments) {
            this.clearSegments();
        }
        else {
            if (this.mCurrentSegment == null) {
                this.mCurrentSegment = this.allocBuffer(len);
            }
            final int n = 0;
            this.mSegmentSize = n;
            this.mCurrentSize = n;
        }
        this.append(buf, start, len);
    }
    
    public void resetInitialized() {
        this.resetWithEmpty();
        if (this.mCurrentSegment == null) {
            this.mCurrentSegment = this.allocBuffer(0);
        }
    }
    
    private final char[] allocBuffer(final int needed) {
        final int size = Math.max(needed, 500);
        char[] buf = null;
        if (this.mConfig != null) {
            buf = this.mConfig.allocMediumCBuffer(size);
            if (buf != null) {
                return buf;
            }
        }
        return new char[size];
    }
    
    private final void clearSegments() {
        this.mHasSegments = false;
        this.mSegments.clear();
        final int n = 0;
        this.mSegmentSize = n;
        this.mCurrentSize = n;
    }
    
    public void resetWithIndentation(final int indCharCount, final char indChar) {
        this.mInputStart = 0;
        this.mInputLen = indCharCount + 1;
        String text;
        if (indChar == '\t') {
            this.mInputBuffer = TextBuffer.sIndTabsArray;
            text = TextBuffer.sIndTabsStrings[indCharCount];
            if (text == null) {
                text = (TextBuffer.sIndTabsStrings[indCharCount] = "\n\t\t\t\t\t\t\t\t\t".substring(0, this.mInputLen));
            }
        }
        else {
            this.mInputBuffer = TextBuffer.sIndSpacesArray;
            text = TextBuffer.sIndSpacesStrings[indCharCount];
            if (text == null) {
                text = (TextBuffer.sIndSpacesStrings[indCharCount] = "\n                                 ".substring(0, this.mInputLen));
            }
        }
        this.mResultString = text;
        this.mResultArray = null;
        if (this.mSegments != null && this.mSegments.size() > 0) {
            this.mSegments.clear();
            final int n = 0;
            this.mSegmentSize = n;
            this.mCurrentSize = n;
        }
    }
    
    public int size() {
        if (this.mInputStart >= 0) {
            return this.mInputLen;
        }
        return this.mSegmentSize + this.mCurrentSize;
    }
    
    public int getTextStart() {
        return (this.mInputStart >= 0) ? this.mInputStart : 0;
    }
    
    public char[] getTextBuffer() {
        if (this.mInputStart >= 0) {
            return this.mInputBuffer;
        }
        if (this.mSegments == null || this.mSegments.size() == 0) {
            return this.mCurrentSegment;
        }
        return this.contentsAsArray();
    }
    
    public void decode(final TypedValueDecoder tvd) throws IllegalArgumentException {
        char[] buf;
        int start;
        int end;
        if (this.mInputStart >= 0) {
            buf = this.mInputBuffer;
            start = this.mInputStart;
            end = start + this.mInputLen;
        }
        else {
            buf = this.getTextBuffer();
            start = 0;
            end = this.mSegmentSize + this.mCurrentSize;
        }
        while (start < end) {
            if (!StringUtil.isSpace(buf[start])) {
                while (--end > start && StringUtil.isSpace(buf[end])) {}
                tvd.decode(buf, start, end + 1);
                return;
            }
            ++start;
        }
        tvd.handleEmptyValue();
    }
    
    public int decodeElements(final TypedArrayDecoder tad, final InputProblemReporter rep) throws TypedXMLStreamException {
        int count = 0;
        if (this.mInputStart < 0) {
            if (this.mHasSegments) {
                this.mInputBuffer = this.buildResultArray();
                this.mInputLen = this.mInputBuffer.length;
                this.clearSegments();
            }
            else {
                this.mInputBuffer = this.mCurrentSegment;
                this.mInputLen = this.mCurrentSize;
            }
            this.mInputStart = 0;
        }
        int ptr = this.mInputStart;
        final int end = ptr + this.mInputLen;
        final char[] buf = this.mInputBuffer;
        int start = ptr;
        try {
        Label_0175:
            while (ptr < end) {
                while (buf[ptr] <= ' ') {
                    if (++ptr >= end) {
                        break Label_0175;
                    }
                }
                start = ptr;
                ++ptr;
                while (ptr < end && buf[ptr] > ' ') {
                    ++ptr;
                }
                ++count;
                final int tokenEnd = ptr;
                ++ptr;
                if (tad.decodeValue(buf, start, tokenEnd)) {
                    break;
                }
            }
        }
        catch (IllegalArgumentException iae) {
            final Location loc = rep.getLocation();
            final String lexical = new String(buf, start, ptr - start - 1);
            throw new TypedXMLStreamException(lexical, iae.getMessage(), loc, iae);
        }
        finally {
            this.mInputStart = ptr;
            this.mInputLen = end - ptr;
        }
        return count;
    }
    
    public void initBinaryChunks(final Base64Variant v, final CharArrayBase64Decoder dec, final boolean firstChunk) {
        if (this.mInputStart < 0) {
            dec.init(v, firstChunk, this.mCurrentSegment, 0, this.mCurrentSize, this.mSegments);
        }
        else {
            dec.init(v, firstChunk, this.mInputBuffer, this.mInputStart, this.mInputLen, null);
        }
    }
    
    public String contentsAsString() {
        if (this.mResultString == null) {
            if (this.mResultArray != null) {
                this.mResultString = new String(this.mResultArray);
            }
            else if (this.mInputStart >= 0) {
                if (this.mInputLen < 1) {
                    return this.mResultString = "";
                }
                this.mResultString = new String(this.mInputBuffer, this.mInputStart, this.mInputLen);
            }
            else {
                final int segLen = this.mSegmentSize;
                final int currLen = this.mCurrentSize;
                if (segLen == 0) {
                    this.mResultString = ((currLen == 0) ? "" : new String(this.mCurrentSegment, 0, currLen));
                }
                else {
                    final StringBuilder sb = new StringBuilder(segLen + currLen);
                    if (this.mSegments != null) {
                        for (int i = 0, len = this.mSegments.size(); i < len; ++i) {
                            final char[] curr = this.mSegments.get(i);
                            sb.append(curr, 0, curr.length);
                        }
                    }
                    sb.append(this.mCurrentSegment, 0, this.mCurrentSize);
                    this.mResultString = sb.toString();
                }
            }
        }
        return this.mResultString;
    }
    
    public StringBuilder contentsAsStringBuilder(final int extraSpace) {
        if (this.mResultString != null) {
            return new StringBuilder(this.mResultString);
        }
        if (this.mResultArray != null) {
            final StringBuilder sb = new StringBuilder(this.mResultArray.length + extraSpace);
            sb.append(this.mResultArray, 0, this.mResultArray.length);
            return sb;
        }
        if (this.mInputStart < 0) {
            final int segLen = this.mSegmentSize;
            final int currLen = this.mCurrentSize;
            final StringBuilder sb2 = new StringBuilder(segLen + currLen + extraSpace);
            if (this.mSegments != null) {
                for (int i = 0, len = this.mSegments.size(); i < len; ++i) {
                    final char[] curr = this.mSegments.get(i);
                    sb2.append(curr, 0, curr.length);
                }
            }
            sb2.append(this.mCurrentSegment, 0, currLen);
            return sb2;
        }
        if (this.mInputLen < 1) {
            return new StringBuilder();
        }
        final StringBuilder sb = new StringBuilder(this.mInputLen + extraSpace);
        sb.append(this.mInputBuffer, this.mInputStart, this.mInputLen);
        return sb;
    }
    
    public void contentsToStringBuilder(final StringBuilder sb) {
        if (this.mResultString != null) {
            sb.append(this.mResultString);
        }
        else if (this.mResultArray != null) {
            sb.append(this.mResultArray);
        }
        else if (this.mInputStart >= 0) {
            if (this.mInputLen > 0) {
                sb.append(this.mInputBuffer, this.mInputStart, this.mInputLen);
            }
        }
        else {
            if (this.mSegments != null) {
                for (int i = 0, len = this.mSegments.size(); i < len; ++i) {
                    final char[] curr = this.mSegments.get(i);
                    sb.append(curr, 0, curr.length);
                }
            }
            sb.append(this.mCurrentSegment, 0, this.mCurrentSize);
        }
    }
    
    public char[] contentsAsArray() {
        char[] result = this.mResultArray;
        if (result == null) {
            result = (this.mResultArray = this.buildResultArray());
        }
        return result;
    }
    
    public int contentsToArray(int srcStart, final char[] dst, int dstStart, int len) {
        if (this.mInputStart >= 0) {
            int amount = this.mInputLen - srcStart;
            if (amount > len) {
                amount = len;
            }
            else if (amount < 0) {
                amount = 0;
            }
            if (amount > 0) {
                System.arraycopy(this.mInputBuffer, this.mInputStart + srcStart, dst, dstStart, amount);
            }
            return amount;
        }
        int totalAmount = 0;
        if (this.mSegments != null) {
            for (int i = 0, segc = this.mSegments.size(); i < segc; ++i) {
                final char[] segment = this.mSegments.get(i);
                final int segLen = segment.length;
                final int amount2 = segLen - srcStart;
                if (amount2 < 1) {
                    srcStart -= segLen;
                }
                else {
                    if (amount2 >= len) {
                        System.arraycopy(segment, srcStart, dst, dstStart, len);
                        return totalAmount + len;
                    }
                    System.arraycopy(segment, srcStart, dst, dstStart, amount2);
                    totalAmount += amount2;
                    dstStart += amount2;
                    len -= amount2;
                    srcStart = 0;
                }
            }
        }
        if (len > 0) {
            final int maxAmount = this.mCurrentSize - srcStart;
            if (len > maxAmount) {
                len = maxAmount;
            }
            if (len > 0) {
                System.arraycopy(this.mCurrentSegment, srcStart, dst, dstStart, len);
                totalAmount += len;
            }
        }
        return totalAmount;
    }
    
    public int rawContentsTo(final Writer w) throws IOException {
        if (this.mResultArray != null) {
            w.write(this.mResultArray);
            return this.mResultArray.length;
        }
        if (this.mResultString != null) {
            w.write(this.mResultString);
            return this.mResultString.length();
        }
        if (this.mInputStart >= 0) {
            if (this.mInputLen > 0) {
                w.write(this.mInputBuffer, this.mInputStart, this.mInputLen);
            }
            return this.mInputLen;
        }
        int rlen = 0;
        if (this.mSegments != null) {
            for (int i = 0, len = this.mSegments.size(); i < len; ++i) {
                final char[] ch = this.mSegments.get(i);
                w.write(ch);
                rlen += ch.length;
            }
        }
        if (this.mCurrentSize > 0) {
            w.write(this.mCurrentSegment, 0, this.mCurrentSize);
            rlen += this.mCurrentSize;
        }
        return rlen;
    }
    
    public Reader rawContentsViaReader() throws IOException {
        if (this.mResultArray != null) {
            return new CharArrayReader(this.mResultArray);
        }
        if (this.mResultString != null) {
            return new StringReader(this.mResultString);
        }
        if (this.mInputStart >= 0) {
            if (this.mInputLen > 0) {
                return new CharArrayReader(this.mInputBuffer, this.mInputStart, this.mInputLen);
            }
            return new StringReader("");
        }
        else {
            if (this.mSegments == null || this.mSegments.size() == 0) {
                return new CharArrayReader(this.mCurrentSegment, 0, this.mCurrentSize);
            }
            return new BufferReader(this.mSegments, this.mCurrentSegment, this.mCurrentSize);
        }
    }
    
    public boolean isAllWhitespace() {
        if (this.mInputStart >= 0) {
            final char[] buf = this.mInputBuffer;
            for (int i = this.mInputStart, last = i + this.mInputLen; i < last; ++i) {
                if (buf[i] > ' ') {
                    return false;
                }
            }
            return true;
        }
        if (this.mSegments != null) {
            for (int j = 0, len = this.mSegments.size(); j < len; ++j) {
                final char[] buf2 = this.mSegments.get(j);
                for (int k = 0, len2 = buf2.length; k < len2; ++k) {
                    if (buf2[k] > ' ') {
                        return false;
                    }
                }
            }
        }
        final char[] buf = this.mCurrentSegment;
        for (int i = 0, len3 = this.mCurrentSize; i < len3; ++i) {
            if (buf[i] > ' ') {
                return false;
            }
        }
        return true;
    }
    
    public boolean endsWith(final String str) {
        if (this.mInputStart >= 0) {
            this.unshare(16);
        }
        int segIndex = (this.mSegments == null) ? 0 : this.mSegments.size();
        int inIndex = str.length() - 1;
        char[] buf = this.mCurrentSegment;
        int bufIndex = this.mCurrentSize - 1;
        while (inIndex >= 0) {
            if (str.charAt(inIndex) != buf[bufIndex]) {
                return false;
            }
            if (--inIndex == 0) {
                break;
            }
            if (--bufIndex >= 0) {
                continue;
            }
            if (--segIndex < 0) {
                return false;
            }
            buf = this.mSegments.get(segIndex);
            bufIndex = buf.length - 1;
        }
        return true;
    }
    
    public boolean equalsString(final String str) {
        final int expLen = str.length();
        if (this.mInputStart >= 0) {
            if (this.mInputLen != expLen) {
                return false;
            }
            for (int i = 0; i < expLen; ++i) {
                if (str.charAt(i) != this.mInputBuffer[this.mInputStart + i]) {
                    return false;
                }
            }
            return true;
        }
        else {
            if (expLen != this.size()) {
                return false;
            }
            char[] seg;
            if (this.mSegments == null || this.mSegments.size() == 0) {
                seg = this.mCurrentSegment;
            }
            else {
                seg = this.contentsAsArray();
            }
            for (int j = 0; j < expLen; ++j) {
                if (seg[j] != str.charAt(j)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public void fireSaxCharacterEvents(final ContentHandler h) throws SAXException {
        if (this.mResultArray != null) {
            h.characters(this.mResultArray, 0, this.mResultArray.length);
        }
        else if (this.mInputStart >= 0) {
            h.characters(this.mInputBuffer, this.mInputStart, this.mInputLen);
        }
        else {
            if (this.mSegments != null) {
                for (int i = 0, len = this.mSegments.size(); i < len; ++i) {
                    final char[] ch = this.mSegments.get(i);
                    h.characters(ch, 0, ch.length);
                }
            }
            if (this.mCurrentSize > 0) {
                h.characters(this.mCurrentSegment, 0, this.mCurrentSize);
            }
        }
    }
    
    public void fireSaxSpaceEvents(final ContentHandler h) throws SAXException {
        if (this.mResultArray != null) {
            h.ignorableWhitespace(this.mResultArray, 0, this.mResultArray.length);
        }
        else if (this.mInputStart >= 0) {
            h.ignorableWhitespace(this.mInputBuffer, this.mInputStart, this.mInputLen);
        }
        else {
            if (this.mSegments != null) {
                for (int i = 0, len = this.mSegments.size(); i < len; ++i) {
                    final char[] ch = this.mSegments.get(i);
                    h.ignorableWhitespace(ch, 0, ch.length);
                }
            }
            if (this.mCurrentSize > 0) {
                h.ignorableWhitespace(this.mCurrentSegment, 0, this.mCurrentSize);
            }
        }
    }
    
    public void fireSaxCommentEvent(final LexicalHandler h) throws SAXException {
        if (this.mResultArray != null) {
            h.comment(this.mResultArray, 0, this.mResultArray.length);
        }
        else if (this.mInputStart >= 0) {
            h.comment(this.mInputBuffer, this.mInputStart, this.mInputLen);
        }
        else if (this.mSegments != null && this.mSegments.size() > 0) {
            final char[] ch = this.contentsAsArray();
            h.comment(ch, 0, ch.length);
        }
        else {
            h.comment(this.mCurrentSegment, 0, this.mCurrentSize);
        }
    }
    
    public void fireDtdCommentEvent(final DTDEventListener l) {
        if (this.mResultArray != null) {
            l.dtdComment(this.mResultArray, 0, this.mResultArray.length);
        }
        else if (this.mInputStart >= 0) {
            l.dtdComment(this.mInputBuffer, this.mInputStart, this.mInputLen);
        }
        else if (this.mSegments != null && this.mSegments.size() > 0) {
            final char[] ch = this.contentsAsArray();
            l.dtdComment(ch, 0, ch.length);
        }
        else {
            l.dtdComment(this.mCurrentSegment, 0, this.mCurrentSize);
        }
    }
    
    public void validateText(final XMLValidator vld, final boolean lastSegment) throws XMLStreamException {
        if (this.mInputStart >= 0) {
            vld.validateText(this.mInputBuffer, this.mInputStart, this.mInputStart + this.mInputLen, lastSegment);
        }
        else {
            vld.validateText(this.contentsAsString(), lastSegment);
        }
    }
    
    public void ensureNotShared() {
        if (this.mInputStart >= 0) {
            this.unshare(16);
        }
    }
    
    public void append(final char c) {
        if (this.mInputStart >= 0) {
            this.unshare(16);
        }
        this.mResultString = null;
        this.mResultArray = null;
        char[] curr = this.mCurrentSegment;
        if (this.mCurrentSize >= curr.length) {
            this.expand(1);
            curr = this.mCurrentSegment;
        }
        curr[this.mCurrentSize++] = c;
    }
    
    public void append(final char[] c, int start, int len) {
        if (this.mInputStart >= 0) {
            this.unshare(len);
        }
        this.mResultString = null;
        this.mResultArray = null;
        final char[] curr = this.mCurrentSegment;
        final int max = curr.length - this.mCurrentSize;
        if (max >= len) {
            System.arraycopy(c, start, curr, this.mCurrentSize, len);
            this.mCurrentSize += len;
        }
        else {
            if (max > 0) {
                System.arraycopy(c, start, curr, this.mCurrentSize, max);
                start += max;
                len -= max;
            }
            this.expand(len);
            System.arraycopy(c, start, this.mCurrentSegment, 0, len);
            this.mCurrentSize = len;
        }
    }
    
    public void append(final String str) {
        int len = str.length();
        if (this.mInputStart >= 0) {
            this.unshare(len);
        }
        this.mResultString = null;
        this.mResultArray = null;
        final char[] curr = this.mCurrentSegment;
        final int max = curr.length - this.mCurrentSize;
        if (max >= len) {
            str.getChars(0, len, curr, this.mCurrentSize);
            this.mCurrentSize += len;
        }
        else {
            if (max > 0) {
                str.getChars(0, max, curr, this.mCurrentSize);
                len -= max;
            }
            this.expand(len);
            str.getChars(max, max + len, this.mCurrentSegment, 0);
            this.mCurrentSize = len;
        }
    }
    
    public char[] getCurrentSegment() {
        if (this.mInputStart >= 0) {
            this.unshare(1);
        }
        else {
            final char[] curr = this.mCurrentSegment;
            if (curr == null) {
                this.mCurrentSegment = this.allocBuffer(0);
            }
            else if (this.mCurrentSize >= curr.length) {
                this.expand(1);
            }
        }
        return this.mCurrentSegment;
    }
    
    public int getCurrentSegmentSize() {
        return this.mCurrentSize;
    }
    
    public void setCurrentLength(final int len) {
        this.mCurrentSize = len;
    }
    
    public char[] finishCurrentSegment() {
        if (this.mSegments == null) {
            this.mSegments = new ArrayList<char[]>();
        }
        this.mHasSegments = true;
        this.mSegments.add(this.mCurrentSegment);
        final int oldLen = this.mCurrentSegment.length;
        this.mSegmentSize += oldLen;
        final char[] curr = new char[this.calcNewSize(oldLen)];
        this.mCurrentSize = 0;
        return this.mCurrentSegment = curr;
    }
    
    private int calcNewSize(final int latestSize) {
        final int incr = (latestSize < 8000) ? latestSize : (latestSize >> 1);
        final int size = latestSize + incr;
        return Math.min(size, 262144);
    }
    
    @Override
    public String toString() {
        return this.contentsAsString();
    }
    
    public void unshare(final int needExtra) {
        final int len = this.mInputLen;
        this.mInputLen = 0;
        final char[] inputBuf = this.mInputBuffer;
        this.mInputBuffer = null;
        final int start = this.mInputStart;
        this.mInputStart = -1;
        final int needed = len + needExtra;
        if (this.mCurrentSegment == null || needed > this.mCurrentSegment.length) {
            this.mCurrentSegment = this.allocBuffer(needed);
        }
        if (len > 0) {
            System.arraycopy(inputBuf, start, this.mCurrentSegment, 0, len);
        }
        this.mSegmentSize = 0;
        this.mCurrentSize = len;
    }
    
    private void expand(final int roomNeeded) {
        if (this.mSegments == null) {
            this.mSegments = new ArrayList<char[]>();
        }
        char[] curr = this.mCurrentSegment;
        this.mHasSegments = true;
        this.mSegments.add(curr);
        final int oldLen = curr.length;
        this.mSegmentSize += oldLen;
        final int newSize = Math.max(roomNeeded, this.calcNewSize(oldLen));
        curr = new char[newSize];
        this.mCurrentSize = 0;
        this.mCurrentSegment = curr;
    }
    
    private char[] buildResultArray() {
        if (this.mResultString != null) {
            return this.mResultString.toCharArray();
        }
        char[] result;
        if (this.mInputStart >= 0) {
            if (this.mInputLen < 1) {
                return DataUtil.getEmptyCharArray();
            }
            result = new char[this.mInputLen];
            System.arraycopy(this.mInputBuffer, this.mInputStart, result, 0, this.mInputLen);
        }
        else {
            final int size = this.size();
            if (size < 1) {
                return DataUtil.getEmptyCharArray();
            }
            int offset = 0;
            result = new char[size];
            if (this.mSegments != null) {
                for (int i = 0, len = this.mSegments.size(); i < len; ++i) {
                    final char[] curr = this.mSegments.get(i);
                    final int currLen = curr.length;
                    System.arraycopy(curr, 0, result, offset, currLen);
                    offset += currLen;
                }
            }
            System.arraycopy(this.mCurrentSegment, 0, result, offset, this.mCurrentSize);
        }
        return result;
    }
    
    static {
        sIndSpacesArray = "\n                                 ".toCharArray();
        sIndSpacesStrings = new String[TextBuffer.sIndSpacesArray.length];
        sIndTabsArray = "\n\t\t\t\t\t\t\t\t\t".toCharArray();
        sIndTabsStrings = new String[TextBuffer.sIndTabsArray.length];
    }
    
    private static final class BufferReader extends Reader
    {
        ArrayList<char[]> _segments;
        char[] _currentSegment;
        final int _currentLength;
        int _segmentIndex;
        int _segmentOffset;
        int _currentOffset;
        
        public BufferReader(final ArrayList<char[]> segs, final char[] currSeg, final int currSegLen) {
            this._segments = segs;
            this._currentSegment = currSeg;
            this._currentLength = currSegLen;
            this._segmentIndex = 0;
            final int n = 0;
            this._currentOffset = n;
            this._segmentOffset = n;
        }
        
        @Override
        public void close() {
            this._segments = null;
            this._currentSegment = null;
        }
        
        @Override
        public void mark(final int x) throws IOException {
            throw new IOException("mark() not supported");
        }
        
        @Override
        public boolean markSupported() {
            return false;
        }
        
        @Override
        public int read(final char[] cbuf, int offset, int len) {
            if (len < 1) {
                return 0;
            }
            final int origOffset = offset;
            while (this._segments != null) {
                final char[] curr = this._segments.get(this._segmentIndex);
                final int max = curr.length - this._segmentOffset;
                if (len <= max) {
                    System.arraycopy(curr, this._segmentOffset, cbuf, offset, len);
                    this._segmentOffset += len;
                    offset += len;
                    return offset - origOffset;
                }
                if (max > 0) {
                    System.arraycopy(curr, this._segmentOffset, cbuf, offset, max);
                    offset += max;
                }
                if (++this._segmentIndex >= this._segments.size()) {
                    this._segments = null;
                }
                else {
                    this._segmentOffset = 0;
                }
            }
            if (len > 0 && this._currentSegment != null) {
                final int max2 = this._currentLength - this._currentOffset;
                if (len >= max2) {
                    len = max2;
                    System.arraycopy(this._currentSegment, this._currentOffset, cbuf, offset, len);
                    this._currentSegment = null;
                }
                else {
                    System.arraycopy(this._currentSegment, this._currentOffset, cbuf, offset, len);
                    this._currentOffset += len;
                }
                offset += len;
            }
            return (origOffset == offset) ? -1 : (offset - origOffset);
        }
        
        @Override
        public boolean ready() {
            return true;
        }
        
        @Override
        public void reset() throws IOException {
            throw new IOException("reset() not supported");
        }
        
        @Override
        public long skip(long amount) {
            if (amount < 0L) {
                return 0L;
            }
            final long origAmount = amount;
            while (this._segments != null) {
                final char[] curr = this._segments.get(this._segmentIndex);
                final int max = curr.length - this._segmentOffset;
                if (max >= amount) {
                    this._segmentOffset += (int)amount;
                    return origAmount;
                }
                amount -= max;
                if (++this._segmentIndex >= this._segments.size()) {
                    this._segments = null;
                }
                else {
                    this._segmentOffset = 0;
                }
            }
            if (amount > 0L && this._currentSegment != null) {
                final int max2 = this._currentLength - this._currentOffset;
                if (amount >= max2) {
                    amount -= max2;
                    this._currentSegment = null;
                }
                else {
                    amount = 0L;
                    this._currentOffset += (int)amount;
                }
            }
            return (amount == origAmount) ? -1L : (origAmount - amount);
        }
    }
}
