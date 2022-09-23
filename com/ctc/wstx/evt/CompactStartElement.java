// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.evt;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.AttributeEventImpl;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import com.ctc.wstx.io.TextEscaper;
import java.io.Writer;
import com.ctc.wstx.util.DataUtil;
import java.util.Iterator;
import com.ctc.wstx.util.BaseNsContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Attribute;
import java.util.ArrayList;
import com.ctc.wstx.sr.ElemAttrs;

public class CompactStartElement extends BaseStartElement
{
    private static final int OFFSET_NS_URI = 1;
    private static final int OFFSET_NS_PREFIX = 2;
    private static final int OFFSET_VALUE = 3;
    final ElemAttrs mAttrs;
    final String[] mRawAttrs;
    private ArrayList<Attribute> mAttrList;
    
    protected CompactStartElement(final Location loc, final QName name, final BaseNsContext nsCtxt, final ElemAttrs attrs) {
        super(loc, name, nsCtxt);
        this.mAttrList = null;
        this.mAttrs = attrs;
        this.mRawAttrs = (String[])((attrs == null) ? null : attrs.getRawAttrs());
    }
    
    @Override
    public Attribute getAttributeByName(final QName name) {
        if (this.mAttrs == null) {
            return null;
        }
        final int ix = this.mAttrs.findIndex(name);
        if (ix < 0) {
            return null;
        }
        return this.constructAttr(this.mRawAttrs, ix, !this.mAttrs.isDefault(ix));
    }
    
    @Override
    public Iterator<Attribute> getAttributes() {
        if (this.mAttrList == null) {
            if (this.mAttrs == null) {
                return DataUtil.emptyIterator();
            }
            final String[] rawAttrs = this.mRawAttrs;
            final int rawLen = rawAttrs.length;
            final int defOffset = this.mAttrs.getFirstDefaultOffset();
            if (rawLen == 4) {
                return DataUtil.singletonIterator(this.constructAttr(rawAttrs, 0, defOffset == 0));
            }
            final ArrayList<Attribute> l = new ArrayList<Attribute>(rawLen >> 2);
            for (int i = 0; i < rawLen; i += 4) {
                l.add(this.constructAttr(rawAttrs, i, i >= defOffset));
            }
            this.mAttrList = l;
        }
        return this.mAttrList.iterator();
    }
    
    @Override
    protected void outputNsAndAttr(final Writer w) throws IOException {
        if (this.mNsCtxt != null) {
            this.mNsCtxt.outputNamespaceDeclarations(w);
        }
        final String[] raw = this.mRawAttrs;
        if (raw != null) {
            for (int i = 0, len = raw.length; i < len; i += 4) {
                w.write(32);
                final String prefix = raw[i + 2];
                if (prefix != null && prefix.length() > 0) {
                    w.write(prefix);
                    w.write(58);
                }
                w.write(raw[i]);
                w.write("=\"");
                TextEscaper.writeEscapedAttrValue(w, raw[i + 3]);
                w.write(34);
            }
        }
    }
    
    @Override
    protected void outputNsAndAttr(final XMLStreamWriter w) throws XMLStreamException {
        if (this.mNsCtxt != null) {
            this.mNsCtxt.outputNamespaceDeclarations(w);
        }
        final String[] raw = this.mRawAttrs;
        if (raw != null) {
            for (int i = 0, len = raw.length; i < len; i += 4) {
                final String ln = raw[i];
                final String prefix = raw[i + 2];
                final String nsURI = raw[i + 1];
                w.writeAttribute(prefix, nsURI, ln, raw[i + 3]);
            }
        }
    }
    
    public Attribute constructAttr(final String[] raw, final int rawIndex, final boolean isDef) {
        return new AttributeEventImpl(this.mLocation, raw[rawIndex], raw[rawIndex + 1], raw[rawIndex + 2], raw[rawIndex + 3], isDef);
    }
}
