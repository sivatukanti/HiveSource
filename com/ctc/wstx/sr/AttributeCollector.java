// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import java.io.IOException;
import com.ctc.wstx.sw.XmlWriter;
import java.util.Arrays;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.util.StringVector;
import com.ctc.wstx.util.DataUtil;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;
import javax.xml.stream.Location;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import java.util.List;
import org.codehaus.stax2.ri.typed.CharArrayBase64Decoder;
import org.codehaus.stax2.typed.Base64Variant;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import javax.xml.namespace.QName;
import com.ctc.wstx.util.StringUtil;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.util.TextBuilder;
import com.ctc.wstx.util.InternCache;

public final class AttributeCollector
{
    static final int INT_SPACE = 32;
    protected static final int LONG_ATTR_LIST_LEN = 4;
    protected static final int EXP_ATTR_COUNT = 12;
    protected static final int EXP_NS_COUNT = 6;
    protected static final int XMLID_IX_DISABLED = -2;
    protected static final int XMLID_IX_NONE = -1;
    protected static final InternCache sInternCache;
    final String mXmlIdPrefix;
    final String mXmlIdLocalName;
    protected Attribute[] mAttributes;
    protected int mAttrCount;
    protected int mNonDefCount;
    protected Attribute[] mNamespaces;
    protected int mNsCount;
    protected boolean mDefaultNsDeclared;
    protected int mXmlIdAttrIndex;
    protected TextBuilder mValueBuilder;
    private final TextBuilder mNamespaceBuilder;
    protected int[] mAttrMap;
    protected int mAttrHashSize;
    protected int mAttrSpillEnd;
    protected int mMaxAttributesPerElement;
    protected int mMaxAttributeSize;
    
    protected AttributeCollector(final ReaderConfig cfg, final boolean nsAware) {
        this.mDefaultNsDeclared = false;
        this.mValueBuilder = null;
        this.mNamespaceBuilder = new TextBuilder(6);
        this.mAttrMap = null;
        this.mXmlIdAttrIndex = (cfg.willDoXmlIdTyping() ? -1 : -2);
        if (nsAware) {
            this.mXmlIdPrefix = "xml";
            this.mXmlIdLocalName = "id";
        }
        else {
            this.mXmlIdPrefix = null;
            this.mXmlIdLocalName = "xml:id";
        }
        this.mMaxAttributesPerElement = cfg.getMaxAttributesPerElement();
        this.mMaxAttributeSize = cfg.getMaxAttributeSize();
    }
    
    public void reset() {
        if (this.mNsCount > 0) {
            this.mNamespaceBuilder.reset();
            this.mDefaultNsDeclared = false;
            this.mNsCount = 0;
        }
        if (this.mAttrCount > 0) {
            this.mValueBuilder.reset();
            this.mAttrCount = 0;
            if (this.mXmlIdAttrIndex >= 0) {
                this.mXmlIdAttrIndex = -1;
            }
        }
    }
    
    public void normalizeSpacesInValue(final int index) {
        final char[] attrCB = this.mValueBuilder.getCharBuffer();
        final String normValue = StringUtil.normalizeSpaces(attrCB, this.getValueStartOffset(index), this.getValueStartOffset(index + 1));
        if (normValue != null) {
            this.mAttributes[index].setValue(normValue);
        }
    }
    
    protected int getNsCount() {
        return this.mNsCount;
    }
    
    public boolean hasDefaultNs() {
        return this.mDefaultNsDeclared;
    }
    
    public final int getCount() {
        return this.mAttrCount;
    }
    
    public int getSpecifiedCount() {
        return this.mNonDefCount;
    }
    
    public String getNsPrefix(final int index) {
        if (index < 0 || index >= this.mNsCount) {
            this.throwIndex(index);
        }
        return this.mNamespaces[index].mLocalName;
    }
    
    public String getNsURI(final int index) {
        if (index < 0 || index >= this.mNsCount) {
            this.throwIndex(index);
        }
        return this.mNamespaces[index].mNamespaceURI;
    }
    
    public String getPrefix(final int index) {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        return this.mAttributes[index].mPrefix;
    }
    
    public String getLocalName(final int index) {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        return this.mAttributes[index].mLocalName;
    }
    
    public String getURI(final int index) {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        return this.mAttributes[index].mNamespaceURI;
    }
    
    public QName getQName(final int index) {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        return this.mAttributes[index].getQName();
    }
    
    public final String getValue(int index) {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        final String full = this.mValueBuilder.getAllValues();
        final Attribute attr = this.mAttributes[index];
        if (++index < this.mAttrCount) {
            final int endOffset = this.mAttributes[index].mValueStartOffset;
            return attr.getValue(full, endOffset);
        }
        return attr.getValue(full);
    }
    
    public String getValue(String nsURI, final String localName) {
        final int hashSize = this.mAttrHashSize;
        if (hashSize == 0) {
            return null;
        }
        int hash = localName.hashCode();
        if (nsURI != null) {
            if (nsURI.length() == 0) {
                nsURI = null;
            }
            else {
                hash ^= nsURI.hashCode();
            }
        }
        int ix = this.mAttrMap[hash & hashSize - 1];
        if (ix == 0) {
            return null;
        }
        --ix;
        if (this.mAttributes[ix].hasQName(nsURI, localName)) {
            return this.getValue(ix);
        }
        for (int i = hashSize, len = this.mAttrSpillEnd; i < len; i += 2) {
            if (this.mAttrMap[i] == hash) {
                ix = this.mAttrMap[i + 1];
                if (this.mAttributes[ix].hasQName(nsURI, localName)) {
                    return this.getValue(ix);
                }
            }
        }
        return null;
    }
    
    public int getMaxAttributesPerElement() {
        return this.mMaxAttributesPerElement;
    }
    
    public void setMaxAttributesPerElement(final int maxAttributesPerElement) {
        this.mMaxAttributesPerElement = maxAttributesPerElement;
    }
    
    public int findIndex(final String localName) {
        return this.findIndex(null, localName);
    }
    
    public int findIndex(String nsURI, final String localName) {
        final int hashSize = this.mAttrHashSize;
        if (hashSize == 0) {
            return -1;
        }
        int hash = localName.hashCode();
        if (nsURI != null) {
            if (nsURI.length() == 0) {
                nsURI = null;
            }
            else {
                hash ^= nsURI.hashCode();
            }
        }
        int ix = this.mAttrMap[hash & hashSize - 1];
        if (ix == 0) {
            return -1;
        }
        --ix;
        if (this.mAttributes[ix].hasQName(nsURI, localName)) {
            return ix;
        }
        for (int i = hashSize, len = this.mAttrSpillEnd; i < len; i += 2) {
            if (this.mAttrMap[i] == hash) {
                ix = this.mAttrMap[i + 1];
                if (this.mAttributes[ix].hasQName(nsURI, localName)) {
                    return ix;
                }
            }
        }
        return -1;
    }
    
    public final boolean isSpecified(final int index) {
        return index < this.mNonDefCount;
    }
    
    public final int getXmlIdAttrIndex() {
        return this.mXmlIdAttrIndex;
    }
    
    public final void decodeValue(final int index, final TypedValueDecoder tvd) throws IllegalArgumentException {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        final char[] buf = this.mValueBuilder.getCharBuffer();
        for (int start = this.mAttributes[index].mValueStartOffset, end = this.getValueStartOffset(index + 1); start < end; ++start) {
            if (!StringUtil.isSpace(buf[start])) {
                while (--end > start && StringUtil.isSpace(buf[end])) {}
                tvd.decode(buf, start, end + 1);
                return;
            }
        }
        tvd.handleEmptyValue();
    }
    
    public final int decodeValues(final int index, final TypedArrayDecoder tad, final InputProblemReporter rep) throws XMLStreamException {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        return decodeValues(tad, rep, this.mValueBuilder.getCharBuffer(), this.mAttributes[index].mValueStartOffset, this.getValueStartOffset(index + 1));
    }
    
    public final byte[] decodeBinary(final int index, final Base64Variant v, final CharArrayBase64Decoder dec, final InputProblemReporter rep) throws XMLStreamException {
        if (index < 0 || index >= this.mAttrCount) {
            this.throwIndex(index);
        }
        final Attribute attr = this.mAttributes[index];
        final char[] cbuf = this.mValueBuilder.getCharBuffer();
        final int start = attr.mValueStartOffset;
        final int end = this.getValueStartOffset(index + 1);
        final int len = end - start;
        dec.init(v, true, cbuf, start, len, null);
        try {
            return dec.decodeCompletely();
        }
        catch (IllegalArgumentException iae) {
            final String lexical = new String(cbuf, start, len);
            throw new TypedXMLStreamException(lexical, iae.getMessage(), rep.getLocation(), iae);
        }
    }
    
    private static final int decodeValues(final TypedArrayDecoder tad, final InputProblemReporter rep, final char[] buf, int ptr, final int end) throws XMLStreamException {
        int start = ptr;
        int count = 0;
        try {
        Label_0092:
            while (ptr < end) {
                while (buf[ptr] <= ' ') {
                    if (++ptr >= end) {
                        break Label_0092;
                    }
                }
                start = ptr;
                ++ptr;
                while (ptr < end && buf[ptr] > ' ') {
                    ++ptr;
                }
                final int tokenEnd = ptr;
                ++ptr;
                ++count;
                if (tad.decodeValue(buf, start, tokenEnd) && !checkExpand(tad)) {
                    break;
                }
            }
        }
        catch (IllegalArgumentException iae) {
            final Location loc = rep.getLocation();
            final String lexical = new String(buf, start, ptr - start);
            throw new TypedXMLStreamException(lexical, iae.getMessage(), loc, iae);
        }
        return count;
    }
    
    private static final boolean checkExpand(final TypedArrayDecoder tad) {
        if (tad instanceof ValueDecoderFactory.BaseArrayDecoder) {
            ((ValueDecoderFactory.BaseArrayDecoder)tad).expand();
            return true;
        }
        return false;
    }
    
    protected int getValueStartOffset(final int index) {
        if (index < this.mAttrCount) {
            return this.mAttributes[index].mValueStartOffset;
        }
        return this.mValueBuilder.getCharSize();
    }
    
    protected char[] getSharedValueBuffer() {
        return this.mValueBuilder.getCharBuffer();
    }
    
    protected Attribute resolveNamespaceDecl(int index, final boolean internURI) {
        final Attribute ns = this.mNamespaces[index];
        final String full = this.mNamespaceBuilder.getAllValues();
        String uri;
        if (this.mNsCount == 0) {
            uri = full;
        }
        else if (++index < this.mNsCount) {
            final int endOffset = this.mNamespaces[index].mValueStartOffset;
            uri = ns.getValue(full, endOffset);
        }
        else {
            uri = ns.getValue(full);
        }
        if (internURI && uri.length() > 0) {
            uri = AttributeCollector.sInternCache.intern(uri);
        }
        ns.mNamespaceURI = uri;
        return ns;
    }
    
    public ElemAttrs buildAttrOb() {
        final int count = this.mAttrCount;
        if (count == 0) {
            return null;
        }
        final String[] raw = new String[count << 2];
        for (int i = 0; i < count; ++i) {
            final Attribute attr = this.mAttributes[i];
            final int ix = i << 2;
            raw[ix] = attr.mLocalName;
            raw[ix + 1] = attr.mNamespaceURI;
            raw[ix + 2] = attr.mPrefix;
            raw[ix + 3] = this.getValue(i);
        }
        if (count < 4) {
            return new ElemAttrs(raw, this.mNonDefCount);
        }
        final int amapLen = this.mAttrMap.length;
        final int[] amap = new int[amapLen];
        System.arraycopy(this.mAttrMap, 0, amap, 0, amapLen);
        return new ElemAttrs(raw, this.mNonDefCount, amap, this.mAttrHashSize, this.mAttrSpillEnd);
    }
    
    protected void validateAttribute(final int index, final XMLValidator vld) throws XMLStreamException {
        final Attribute attr = this.mAttributes[index];
        final String normValue = vld.validateAttribute(attr.mLocalName, attr.mNamespaceURI, attr.mPrefix, this.mValueBuilder.getCharBuffer(), this.getValueStartOffset(index), this.getValueStartOffset(index + 1));
        if (normValue != null) {
            attr.setValue(normValue);
        }
    }
    
    public final TextBuilder getAttrBuilder(final String attrPrefix, final String attrLocalName) throws XMLStreamException {
        if (this.mAttrCount == 0) {
            if (this.mAttributes == null) {
                this.allocBuffers();
            }
            this.mAttributes[0] = new Attribute(attrPrefix, attrLocalName, 0);
        }
        else {
            final int valueStart = this.mValueBuilder.getCharSize();
            if (this.mAttrCount >= this.mAttributes.length) {
                if (this.mAttrCount + this.mNsCount >= this.mMaxAttributesPerElement) {
                    throw new XMLStreamException("Attribute limit (" + this.mMaxAttributesPerElement + ") exceeded");
                }
                this.mAttributes = (Attribute[])DataUtil.growArrayBy50Pct(this.mAttributes);
            }
            final Attribute curr = this.mAttributes[this.mAttrCount];
            if (curr == null) {
                this.mAttributes[this.mAttrCount] = new Attribute(attrPrefix, attrLocalName, valueStart);
            }
            else {
                curr.reset(attrPrefix, attrLocalName, valueStart);
            }
        }
        ++this.mAttrCount;
        if (attrLocalName == this.mXmlIdLocalName && attrPrefix == this.mXmlIdPrefix && this.mXmlIdAttrIndex != -2) {
            this.mXmlIdAttrIndex = this.mAttrCount - 1;
        }
        return this.mValueBuilder;
    }
    
    public int addDefaultAttribute(final String localName, final String uri, final String prefix, final String value) throws XMLStreamException {
        final int attrIndex = this.mAttrCount;
        if (attrIndex < 1) {
            this.initHashArea();
        }
        int hash = localName.hashCode();
        if (uri != null && uri.length() > 0) {
            hash ^= uri.hashCode();
        }
        final int index = hash & this.mAttrHashSize - 1;
        int[] map = this.mAttrMap;
        if (map[index] == 0) {
            map[index] = attrIndex + 1;
        }
        else {
            final int currIndex = map[index] - 1;
            int spillIndex = this.mAttrSpillEnd;
            map = this.spillAttr(uri, localName, map, currIndex, spillIndex, hash, this.mAttrHashSize);
            if (map == null) {
                return -1;
            }
            map[++spillIndex] = attrIndex;
            this.mAttrMap = map;
            this.mAttrSpillEnd = ++spillIndex;
        }
        this.getAttrBuilder(prefix, localName);
        final Attribute attr = this.mAttributes[this.mAttrCount - 1];
        attr.mNamespaceURI = uri;
        attr.setValue(value);
        return this.mAttrCount - 1;
    }
    
    public final void setNormalizedValue(final int index, final String value) {
        this.mAttributes[index].setValue(value);
    }
    
    public TextBuilder getDefaultNsBuilder() throws XMLStreamException {
        if (this.mDefaultNsDeclared) {
            return null;
        }
        this.mDefaultNsDeclared = true;
        return this.getNsBuilder(null);
    }
    
    public TextBuilder getNsBuilder(final String prefix) throws XMLStreamException {
        if (this.mNsCount == 0) {
            if (this.mNamespaces == null) {
                this.mNamespaces = new Attribute[6];
            }
            this.mNamespaces[0] = new Attribute(null, prefix, 0);
        }
        else {
            final int len = this.mNsCount;
            if (prefix != null) {
                for (int i = 0; i < len; ++i) {
                    if (prefix == this.mNamespaces[i].mLocalName) {
                        return null;
                    }
                }
            }
            if (len >= this.mNamespaces.length) {
                if (this.mAttrCount + this.mNsCount >= this.mMaxAttributesPerElement) {
                    throw new XMLStreamException("Attribute limit (" + this.mMaxAttributesPerElement + ") exceeded");
                }
                this.mNamespaces = (Attribute[])DataUtil.growArrayBy50Pct(this.mNamespaces);
            }
            final int uriStart = this.mNamespaceBuilder.getCharSize();
            final Attribute curr = this.mNamespaces[len];
            if (curr == null) {
                this.mNamespaces[len] = new Attribute(null, prefix, uriStart);
            }
            else {
                curr.reset(null, prefix, uriStart);
            }
        }
        ++this.mNsCount;
        return this.mNamespaceBuilder;
    }
    
    public int resolveNamespaces(final InputProblemReporter rep, final StringVector ns) throws XMLStreamException {
        final int attrCount = this.mAttrCount;
        this.mNonDefCount = attrCount;
        if (attrCount < 1) {
            final int n = 0;
            this.mAttrSpillEnd = n;
            this.mAttrHashSize = n;
            return this.mXmlIdAttrIndex;
        }
        for (int i = 0; i < attrCount; ++i) {
            final Attribute attr = this.mAttributes[i];
            final String prefix = attr.mPrefix;
            if (prefix != null) {
                if (prefix == "xml") {
                    attr.mNamespaceURI = "http://www.w3.org/XML/1998/namespace";
                }
                else {
                    final String uri = ns.findLastFromMap(prefix);
                    if (uri == null) {
                        rep.throwParseError(ErrorConsts.ERR_NS_UNDECLARED_FOR_ATTR, prefix, attr.mLocalName);
                    }
                    attr.mNamespaceURI = uri;
                }
            }
        }
        int[] map = this.mAttrMap;
        int hashCount = 4;
        for (int min = attrCount + (attrCount >> 2); hashCount < min; hashCount += hashCount) {}
        this.mAttrHashSize = hashCount;
        final int min = hashCount + (hashCount >> 4);
        if (map == null || map.length < min) {
            map = new int[min];
        }
        else {
            Arrays.fill(map, 0, hashCount, 0);
        }
        final int mask = hashCount - 1;
        int spillIndex = hashCount;
        for (int j = 0; j < attrCount; ++j) {
            final Attribute attr2 = this.mAttributes[j];
            final String name = attr2.mLocalName;
            int hash = name.hashCode();
            final String uri2 = attr2.mNamespaceURI;
            if (uri2 != null) {
                hash ^= uri2.hashCode();
            }
            final int index = hash & mask;
            if (map[index] == 0) {
                map[index] = j + 1;
            }
            else {
                final int currIndex = map[index] - 1;
                map = this.spillAttr(uri2, name, map, currIndex, spillIndex, hash, hashCount);
                if (map == null) {
                    this.throwDupAttr(rep, currIndex);
                }
                else {
                    map[++spillIndex] = j;
                    ++spillIndex;
                }
            }
        }
        this.mAttrSpillEnd = spillIndex;
        this.mAttrMap = map;
        return this.mXmlIdAttrIndex;
    }
    
    protected void throwIndex(final int index) {
        throw new IllegalArgumentException("Invalid index " + index + "; current element has only " + this.getCount() + " attributes");
    }
    
    @Deprecated
    public void writeAttribute(final int index, final XmlWriter xw) throws IOException, XMLStreamException {
        this.writeAttribute(index, xw, null);
    }
    
    public void writeAttribute(final int index, final XmlWriter xw, final XMLValidator validator) throws IOException, XMLStreamException {
        final Attribute attr = this.mAttributes[index];
        final String ln = attr.mLocalName;
        final String prefix = attr.mPrefix;
        final String value = this.getValue(index);
        if (prefix == null || prefix.length() == 0) {
            xw.writeAttribute(ln, value);
        }
        else {
            xw.writeAttribute(prefix, ln, value);
        }
        if (validator != null) {
            validator.validateAttribute(ln, attr.mNamespaceURI, prefix, value);
        }
    }
    
    protected final void allocBuffers() {
        if (this.mAttributes == null) {
            this.mAttributes = new Attribute[8];
        }
        if (this.mValueBuilder == null) {
            this.mValueBuilder = new TextBuilder(12);
        }
    }
    
    private int[] spillAttr(final String uri, final String name, int[] map, int currIndex, final int spillIndex, final int hash, final int hashCount) {
        final Attribute oldAttr = this.mAttributes[currIndex];
        if (oldAttr.mLocalName == name) {
            final String currURI = oldAttr.mNamespaceURI;
            if (currURI == uri || (currURI != null && currURI.equals(uri))) {
                return null;
            }
        }
        if (spillIndex + 1 >= map.length) {
            map = DataUtil.growArrayBy(map, 8);
        }
        for (int j = hashCount; j < spillIndex; j += 2) {
            if (map[j] == hash) {
                currIndex = map[j + 1];
                final Attribute attr = this.mAttributes[currIndex];
                if (attr.mLocalName == name) {
                    final String currURI2 = attr.mNamespaceURI;
                    if (currURI2 == uri || (currURI2 != null && currURI2.equals(uri))) {
                        return null;
                    }
                }
            }
        }
        map[spillIndex] = hash;
        return map;
    }
    
    private void initHashArea() {
        final int n = 4;
        this.mAttrSpillEnd = n;
        this.mAttrHashSize = n;
        if (this.mAttrMap == null || this.mAttrMap.length < this.mAttrHashSize) {
            this.mAttrMap = new int[this.mAttrHashSize + 1];
        }
        final int[] mAttrMap = this.mAttrMap;
        final int n2 = 0;
        final int[] mAttrMap2 = this.mAttrMap;
        final int n3 = 1;
        final int[] mAttrMap3 = this.mAttrMap;
        final int n4 = 2;
        final int[] mAttrMap4 = this.mAttrMap;
        final int n5 = 3;
        final int n6 = 0;
        mAttrMap3[n4] = (mAttrMap4[n5] = n6);
        mAttrMap[n2] = (mAttrMap2[n3] = n6);
        this.allocBuffers();
    }
    
    protected void throwDupAttr(final InputProblemReporter rep, final int index) throws XMLStreamException {
        rep.throwParseError("Duplicate attribute '" + this.getQName(index) + "'.");
    }
    
    static {
        sInternCache = InternCache.getInstance();
    }
}
