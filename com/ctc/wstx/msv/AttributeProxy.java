// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.msv;

import org.codehaus.stax2.validation.ValidationContext;
import org.xml.sax.Attributes;

final class AttributeProxy implements Attributes
{
    private final ValidationContext mContext;
    
    public AttributeProxy(final ValidationContext ctxt) {
        this.mContext = ctxt;
    }
    
    @Override
    public int getIndex(final String qName) {
        final int cix = qName.indexOf(58);
        final int acount = this.mContext.getAttributeCount();
        if (cix < 0) {
            for (int i = 0; i < acount; ++i) {
                if (qName.equals(this.mContext.getAttributeLocalName(i))) {
                    final String prefix = this.mContext.getAttributePrefix(i);
                    if (prefix == null || prefix.length() == 0) {
                        return i;
                    }
                }
            }
        }
        else {
            final String prefix2 = qName.substring(0, cix);
            final String ln = qName.substring(cix + 1);
            for (int j = 0; j < acount; ++j) {
                if (ln.equals(this.mContext.getAttributeLocalName(j))) {
                    final String p2 = this.mContext.getAttributePrefix(j);
                    if (p2 != null && prefix2.equals(p2)) {
                        return j;
                    }
                }
            }
        }
        return -1;
    }
    
    @Override
    public int getIndex(final String uri, final String localName) {
        return this.mContext.findAttributeIndex(uri, localName);
    }
    
    @Override
    public int getLength() {
        return this.mContext.getAttributeCount();
    }
    
    @Override
    public String getLocalName(final int index) {
        return this.mContext.getAttributeLocalName(index);
    }
    
    @Override
    public String getQName(final int index) {
        final String prefix = this.mContext.getAttributePrefix(index);
        final String ln = this.mContext.getAttributeLocalName(index);
        if (prefix == null || prefix.length() == 0) {
            return ln;
        }
        final StringBuilder sb = new StringBuilder(prefix.length() + 1 + ln.length());
        sb.append(prefix);
        sb.append(':');
        sb.append(ln);
        return sb.toString();
    }
    
    @Override
    public String getType(final int index) {
        return this.mContext.getAttributeType(index);
    }
    
    @Override
    public String getType(final String qName) {
        return this.getType(this.getIndex(qName));
    }
    
    @Override
    public String getType(final String uri, final String localName) {
        return this.getType(this.getIndex(uri, localName));
    }
    
    @Override
    public String getURI(final int index) {
        return this.mContext.getAttributeNamespace(index);
    }
    
    @Override
    public String getValue(final int index) {
        return this.mContext.getAttributeValue(index);
    }
    
    @Override
    public String getValue(final String qName) {
        return this.getValue(this.getIndex(qName));
    }
    
    @Override
    public String getValue(final String uri, final String localName) {
        return this.mContext.getAttributeValue(uri, localName);
    }
}
